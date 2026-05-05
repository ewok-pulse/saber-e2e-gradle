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

import java.util.function.Supplier;

/**
 * No-op {@link IsolatedProjectsApiAccessReporter} used when Isolated Projects is disabled.
 */
public class NoOpIsolatedProjectsApiAccessReporter implements IsolatedProjectsApiAccessReporter {
    @Override
    public boolean onProjectGetProperties() {
        // No IP violation outside Isolated Projects: caller still emits the
        // standard deprecation warning.
        return false;
    }

    @Override
    public <T> T runIgnoringProblemsOnCurrentThread(Supplier<T> action) {
        return action.get();
    }
}
