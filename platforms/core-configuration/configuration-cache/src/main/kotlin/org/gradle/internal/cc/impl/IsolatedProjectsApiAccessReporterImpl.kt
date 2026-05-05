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

package org.gradle.internal.cc.impl

import org.gradle.api.internal.project.IsolatedProjectsApiAccessReporter
import org.gradle.internal.configuration.problems.IsolatedProjectsProblemsReporter
import org.gradle.internal.deprecation.DeprecationLogger
import java.util.function.Supplier


internal
class IsolatedProjectsApiAccessReporterImpl(
    private val ipProblems: IsolatedProjectsProblemsReporter
) : IsolatedProjectsApiAccessReporter {

    @Suppress("ThrowingExceptionsWithoutMessageOrCause")
    override fun onProjectGetProperties(): Boolean {
        // Defer to DeprecationLogger.whileDisabled when internal Gradle code
        // intentionally invokes this deprecated API (e.g. the built-in
        // `properties` task). The caller's nagUser() is itself a no-op in
        // that scope, so neither signal fires.
        if (!DeprecationLogger.isEnabled()) {
            return false
        }
        ipProblems.report {
            problem {
                text("use of ")
                reference("Project.getProperties()")
                text(" is not allowed with Isolated Projects")
            }
                .exception()
                .build()
        }
        return true
    }

    override fun <T : Any> runIgnoringProblemsOnCurrentThread(action: Supplier<T>): T =
        ipProblems.runIgnoringProblemsOnCurrentThread { action.get() }
}
