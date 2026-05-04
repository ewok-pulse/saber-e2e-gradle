/*
 * Copyright 2026 the original author or authors.
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

package org.gradle.internal.serialize.graph

import org.gradle.internal.configuration.problems.PropertyKind
import org.gradle.internal.configuration.problems.PropertyTrace


/**
 * Returns a human-readable description of the task currently being serialized,
 * for use in error messages. Falls back to the full property trace when no
 * task frame is on the trace.
 */
fun PropertyTrace.taskDescription(): String =
    sequence
        .filterIsInstance<PropertyTrace.Task>()
        .firstOrNull()
        ?.let { "task ${it.path} of type ${it.type.simpleName}" }
        ?: toString()


/**
 * Reports a serialization failure as a configuration cache problem, attaching
 * [exception] as the cause and emitting the standard "failed to serialize
 * value of ..." structured message under the given property trace frame.
 */
suspend fun WriteContext.reportSerializationError(
    propertyKind: PropertyKind,
    fieldName: String,
    exception: Exception
) {
    withPropertyTrace(propertyKind, fieldName) {
        onError(exception) {
            text("failed to serialize value of ")
            reference(trace.toString())
        }
    }
}
