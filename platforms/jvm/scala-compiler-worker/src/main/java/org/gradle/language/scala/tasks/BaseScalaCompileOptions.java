/*
 * Copyright 2021 the original author or authors.
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

package org.gradle.language.scala.tasks;

import org.gradle.api.Action;
import org.gradle.api.Incubating;
import org.gradle.internal.instrumentation.api.annotations.EagerSetter;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.scala.IncrementalCompileOptions;
import org.gradle.api.tasks.scala.ScalaForkOptions;
import org.gradle.internal.instrumentation.api.annotations.ReplacesEagerProperty;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Options for Scala platform compilation.
 */
public abstract class BaseScalaCompileOptions implements Serializable {

    private static final long serialVersionUID = 0;

    private final ScalaForkOptions forkOptions = getObjectFactory().newInstance(ScalaForkOptions.class);

    private final IncrementalCompileOptions incrementalOptions = getObjectFactory().newInstance(IncrementalCompileOptions.class);

    @Inject
    public BaseScalaCompileOptions() {
        getFailOnError().convention(true);
        getDeprecation().convention(true);
        getUnchecked().convention(true);
        getOptimize().convention(false);
        getForce().convention(false);
        getListFiles().convention(false);
    }

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    /**
     * Fail the build on compilation errors.
     */
    @Input
    public abstract Property<Boolean> getFailOnError();

    /** Eager forwarder; see {@link #getFailOnError()}. */
    @EagerSetter
    public void setFailOnError(boolean failOnError) {
        getFailOnError().set(failOnError);
    }

    @Internal
    public Property<Boolean> getIsFailOnError() {
        return getFailOnError();
    }

    /**
     * Generate deprecation information.
     */
    @Console
    public abstract Property<Boolean> getDeprecation();

    /** Eager forwarder; see {@link #getDeprecation()}. */
    @EagerSetter
    public void setDeprecation(boolean deprecation) {
        getDeprecation().set(deprecation);
    }

    @Internal
    public Property<Boolean> getIsDeprecation() {
        return getDeprecation();
    }

    /**
     * Generate unchecked information.
     */
    @Console
    public abstract Property<Boolean> getUnchecked();

    /** Eager forwarder; see {@link #getUnchecked()}. */
    @EagerSetter
    public void setUnchecked(boolean unchecked) {
        getUnchecked().set(unchecked);
    }

    @Internal
    public Property<Boolean> getIsUnchecked() {
        return getUnchecked();
    }

    /**
     * Generate debugging information.
     * Legal values: none, source, line, vars, notailcalls
     */
    @Optional
    @Input
    public abstract Property<String> getDebugLevel();

    /** Eager forwarder; see {@link #getDebugLevel()}. */
    @EagerSetter
    public void setDebugLevel(String debugLevel) {
        getDebugLevel().set(debugLevel);
    }

    /**
     * Run optimizations.
     */
    @Input
    public abstract Property<Boolean> getOptimize();

    /** Eager forwarder; see {@link #getOptimize()}. */
    @EagerSetter
    public void setOptimize(boolean optimize) {
        getOptimize().set(optimize);
    }

    @Internal
    public Property<Boolean> getIsOptimize() {
        return getOptimize();
    }

    /**
     * Encoding of source files.
     */
    @Optional
    @Input
    public abstract Property<String> getEncoding();

    /** Eager forwarder; see {@link #getEncoding()}. */
    @EagerSetter
    public void setEncoding(String encoding) {
        getEncoding().set(encoding);
    }

    /**
     * Whether to force the compilation of all files.
     * Legal values:
     * - false (only compile modified files)
     * - true (always recompile all files)
     */
    @Input
    public abstract Property<Boolean> getForce();

    /** Eager forwarder; see {@link #getForce()}. */
    @EagerSetter
    public void setForce(boolean force) {
        getForce().set(force);
    }

    @Internal
    public Property<Boolean> getIsForce() {
        return getForce();
    }

    /**
     * Additional parameters passed to the compiler.
     * Each parameter must start with '-'.
     *
     * @return The list of additional parameters.
     */
    @Optional
    @Input
    @ReplacesEagerProperty
    public abstract ListProperty<String> getAdditionalParameters();

    /**
     * List files to be compiled.
     */
    @Console
    public abstract Property<Boolean> getListFiles();

    /** Eager forwarder; see {@link #getListFiles()}. */
    @EagerSetter
    public void setListFiles(boolean listFiles) {
        getListFiles().set(listFiles);
    }

    @Internal
    public Property<Boolean> getIsListFiles() {
        return getListFiles();
    }

    /**
     * Specifies the amount of logging.
     * Legal values:  none, verbose, debug
     */
    @Console
    public abstract Property<String> getLoggingLevel();

    /** Eager forwarder; see {@link #getLoggingLevel()}. */
    @EagerSetter
    public void setLoggingLevel(String loggingLevel) {
        getLoggingLevel().set(loggingLevel);
    }

    /**
     * Phases of the compiler to log.
     * Legal values: namer, typer, pickler, uncurry, tailcalls, transmatch, explicitouter, erasure,
     * lambdalift, flatten, constructors, mixin, icode, jvm, terminal.
     */
    @Console
    public abstract ListProperty<String> getLoggingPhases();

    /** Eager forwarder; see {@link #getLoggingPhases()}. */
    @EagerSetter
    public void setLoggingPhases(List<String> loggingPhases) {
        getLoggingPhases().set(loggingPhases);
    }

    /**
     * Options for running the Scala compiler in a separate process.
     */
    @Nested
    public ScalaForkOptions getForkOptions() {
        return forkOptions;
    }

    /**
     * Configure options for running the Scala compiler in a separate process.
     *
     * @since 8.11
     */
    public void forkOptions(Action<? super ScalaForkOptions> action) {
        action.execute(forkOptions);
    }

    /**
     * Options for incremental compilation of Scala code.
     */
    @Nested
    public IncrementalCompileOptions getIncrementalOptions() {
        return incrementalOptions;
    }

    /**
     * Configure options for incremental compilation of Scala code.
     *
     * @since 8.11
     */
    public void incrementalOptions(Action<? super IncrementalCompileOptions> action) {
        action.execute(incrementalOptions);
    }

    /**
     * Keeps Scala compiler daemon alive across builds for faster build times
     *
     * @since 7.6
     */
    @Incubating
    @Input
    public abstract Property<KeepAliveMode> getKeepAliveMode();
}
