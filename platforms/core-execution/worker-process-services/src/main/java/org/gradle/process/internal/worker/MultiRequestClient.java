/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.process.internal.worker;

import org.gradle.api.problems.internal.InternalProblems;
import org.jspecify.annotations.Nullable;

public interface MultiRequestClient<IN, OUT> extends RequestHandler<IN, OUT>, WorkerControl {

    /**
     * Binds the given problems service for the next job executed by this client.
     * If {@code null}, any problems reported by the worker will be silently dropped.
     */
    void bindProblems(@Nullable InternalProblems problems);
}
