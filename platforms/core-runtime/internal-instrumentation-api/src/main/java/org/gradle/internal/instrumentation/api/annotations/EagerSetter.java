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

package org.gradle.internal.instrumentation.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a setter that exists solely as an eager forwarder over a lazy-property
 * getter declared on the same type. The annotated method is expected to delegate
 * to that getter (typically {@code getFoo().set(value)} for {@link org.gradle.api.provider.Property}
 * or {@code getFoo().setFrom(value)} for {@link org.gradle.api.file.ConfigurableFileCollection}).
 *
 * <p>Such setters are retained for source-compatibility with the pre-lazy API and
 * for IDE discoverability (auto-completion, refactor support). They are intended
 * to be deprecated in a future release; this annotation provides a stable hook
 * that tooling (e.g. an IntelliJ inspection) can use to flag direct invocations
 * while excluding Groovy property-assignment (`x = v`) call sites.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EagerSetter {
}
