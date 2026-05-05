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

package org.gradle.api.internal.project;

import org.gradle.internal.service.scopes.Scope;
import org.gradle.internal.service.scopes.ServiceScope;

import java.util.function.Supplier;

/**
 * Bridge that lets call sites in {@code :core} report Isolated Projects
 * violations for selected Project APIs without depending on the
 * {@code :configuration-problems-base} module directly.
 * <p>
 * The default implementation is a no-op; the Isolated Projects-aware
 * implementation is wired in {@code :configuration-cache} when IP is
 * enabled.
 */
@ServiceScope(Scope.BuildTree.class)
public interface IsolatedProjectsApiAccessReporter {

    /**
     * Reports that {@code Project.getProperties()} (or its script-level
     * dynamic equivalent) was invoked, which is forbidden under
     * Isolated Projects.
     *
     * @return {@code true} if an Isolated Projects violation was reported;
     * the caller should suppress any companion deprecation warning. Returns
     * {@code false} otherwise (IP is off, or the call is wrapped in
     * {@link org.gradle.internal.deprecation.DeprecationLogger#whileDisabled}),
     * meaning the caller may emit its deprecation warning as usual.
     */
    boolean onProjectGetProperties();

    /**
     * Runs the given action while suppressing further IP problems that
     * would otherwise be reported on the calling thread.
     * <p>
     * Used to avoid reporting downstream cross-project violations after
     * a higher-level violation has already been reported for the same
     * call (e.g. a {@link #onProjectGetProperties()} above a parent
     * dynamic-property walk).
     */
    <T> T runIgnoringProblemsOnCurrentThread(Supplier<T> action);
}
