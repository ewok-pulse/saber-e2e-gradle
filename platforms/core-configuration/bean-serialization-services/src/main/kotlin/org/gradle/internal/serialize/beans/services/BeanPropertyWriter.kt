/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.serialize.beans.services

import com.google.common.primitives.Primitives.wrap
import org.gradle.api.internal.IConventionAware
import org.gradle.internal.configuration.problems.PropertyKind
import org.gradle.internal.extensions.stdlib.uncheckedCast
import org.gradle.internal.reflect.UnsupportedTypeException
import org.gradle.internal.serialize.graph.BeanStateWriter
import org.gradle.internal.serialize.graph.WriteContext
import org.gradle.internal.serialize.graph.codecs.NarrowingCodec
import org.gradle.internal.serialize.graph.codecs.findIncompatibleNarrowing
import org.gradle.internal.serialize.graph.reportSerializationError
import org.gradle.internal.serialize.graph.taskDescription
import org.gradle.internal.serialize.graph.withDebugFrame
import org.gradle.internal.serialize.graph.writePropertyValue
import java.lang.reflect.Field


class BeanPropertyWriter(
    beanType: Class<*>
) : BeanStateWriter {

    private
    val relevantFields = relevantStateOf(beanType)

    /**
     * Serializes a bean by serializing the value of each of its fields.
     */
    override suspend fun WriteContext.writeStateOf(bean: Any) {
        for (relevantField in relevantFields) {
            val field = relevantField.field
            val fieldName = field.name
            val fieldValue =
                when (val isExplicitValue = relevantField.isExplicitValueField) {
                    null -> field.get(bean)
                    else -> conventionValueOf(bean, field, isExplicitValue)
                }
            checkForIncompatibleRoundtrip(field, fieldName, fieldValue)
            checkKotlinDelegateForUnsupportedValue(field, fieldName, fieldValue)
            withDebugFrame({ field.debugFrameName() }) {
                writePropertyValue(PropertyKind.Field, fieldName, fieldValue)
            }
        }
    }

    /**
     * Rejects values whose codec will narrow to a type the field cannot accept.
     *
     * For each non-null field value, resolves the codec that will handle its
     * runtime type via [WriteContext.codecForRuntimeType]. If the codec
     * implements [NarrowingCodec] and its decodedType is not assignable to
     * the field's declared type, throws [UnsupportedTypeException] so the
     * author is informed at store time, not after a broken cache load.
     */
    internal
    suspend fun WriteContext.checkForIncompatibleRoundtrip(field: Field, fieldName: String, fieldValue: Any?) {
        if (fieldValue == null) return
        val narrowing = findIncompatibleNarrowing(field.type, fieldValue.javaClass) ?: return
        // Pass when the field's declared type is a subtype of the codec's decoded type:
        // the codec may produce a concrete instance of that subtype at runtime (codecs
        // declare a broad interface but generally construct via a factory that yields
        // the expected concrete class). Only flag when the types share no subtyping
        // relation at all - then reassignment is definitely impossible.
        if (narrowing.decodedType.isAssignableFrom(field.type)) return
        val exception = UnsupportedTypeException(
            "Cannot serialize value of type ${fieldValue.javaClass.name} into field " +
                "${field.name} of ${field.declaringClass.name} in ${trace.taskDescription()}: " +
                "its codec produces ${narrowing.publicDecodedType.name} on load, " +
                "which cannot be assigned to a field of type ${field.type.name}.",
            listOf(narrowing.narrowingResolution)
        )
        reportSerializationError(PropertyKind.Field, fieldName, exception)
    }

    /**
     * Inspects the value of a Kotlin property delegate field for unsupported types.
     *
     * Kotlin `by`-delegates (e.g. `by lazy { … }`) create a backing field whose
     * declared type is the delegate class, hiding the actual value type from
     * the NarrowingCodec-driven check in [checkForIncompatibleRoundtrip]. This
     * method looks inside the delegate and
     * reports a configuration cache error when the wrapped value is an
     * unsupported type (such as [org.gradle.api.artifacts.Configuration]).
     *
     * Only reports when the Kotlin property type (the getter return type) is itself
     * an unsupported type.  When the user explicitly narrows the property type to
     * a safe supertype (e.g. `val x: FileCollection by lazy { … }`), the
     * round-trip through serialization succeeds and no error is reported.
     *
     * The error is routed through [reportSerializationError] so that it appears
     * in the failure cause chain (for `assertHasCause`) and in the CC problems report.
     */
    private
    suspend fun WriteContext.checkKotlinDelegateForUnsupportedValue(field: Field, fieldName: String, fieldValue: Any?) {
        if (!KotlinDelegateInspector.isKotlinDelegate(fieldValue)) return
        val delegateValue = KotlinDelegateInspector.extractValue(fieldValue!!) ?: return
        val kotlinGetterReturnType = KotlinDelegateInspector.kotlinPropertyGetterReturnType(field)
        val narrowing = findIncompatibleNarrowing(kotlinGetterReturnType, delegateValue.javaClass) ?: return
        reportUnsupportedKotlinDelegateType(field, fieldName, fieldValue, narrowing, kotlinGetterReturnType)
    }

    /**
     * Reports a configuration cache error for a Kotlin delegate whose result type
     * is unsupported.  The error is routed through [reportSerializationError] so
     * that it appears in both the failure cause chain and the CC problems report.
     */
    private
    suspend fun WriteContext.reportUnsupportedKotlinDelegateType(
        field: Field,
        fieldName: String,
        delegate: Any,
        narrowing: NarrowingCodec<*>,
        kotlinGetterReturnType: Class<*>
    ) {
        val delegateKind = KotlinDelegateInspector.delegateKindName(delegate)
        val propertyName = field.name.removeSuffix("\$delegate")
        val exception = UnsupportedTypeException(
            "Cannot serialize $delegateKind delegate for property '$propertyName: ${kotlinGetterReturnType.simpleName}' in ${trace.taskDescription()}. " +
                "The codec for the delegate's value produces ${narrowing.publicDecodedType.name} on load, " +
                "which cannot be assigned to a property of type ${kotlinGetterReturnType.name}.",
            listOf(narrowing.narrowingResolution)
        )
        reportSerializationError(PropertyKind.Field, fieldName, exception)
    }

    private
    fun conventionValueOf(bean: Any, field: Field, isExplicitValue: Field) =
        field.get(bean).let { fieldValue ->
            if (isExplicitValue.get(bean).uncheckedCast()) {
                fieldValue
            } else {
                getConventionValue(bean, field, fieldValue)
                    ?.takeIf { conventionValue ->
                        // Prevent convention value to be assigned to a field of incompatible type
                        // A common cause is a regular field type being promoted to a Property/Provider type.
                        conventionValue.isAssignableTo(field.type)
                    } ?: fieldValue
            }
        }

    private
    fun getConventionValue(bean: Any, field: Field, fieldValue: Any?): Any? =
        bean.uncheckedCast<IConventionAware>()
            .conventionMapping
            .getConventionValue(fieldValue, field.name, false)

    private
    fun Field.debugFrameName() =
        "${declaringClass.typeName}.$name"

    private
    fun Any?.isAssignableTo(type: Class<*>) =
        (if (type.isPrimitive) wrap(type) else type)
            .isInstance(this)
}
