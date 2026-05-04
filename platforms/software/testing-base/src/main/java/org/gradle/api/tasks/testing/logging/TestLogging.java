/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.tasks.testing.logging;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.internal.instrumentation.api.annotations.EagerSetter;
import org.gradle.internal.instrumentation.api.annotations.ReplacedAccessor;
import org.gradle.internal.instrumentation.api.annotations.ReplacedAccessor.AccessorType;
import org.gradle.internal.instrumentation.api.annotations.ReplacesEagerProperty;
import org.gradle.util.internal.GUtil;

import java.util.EnumSet;
import java.util.Set;

import static org.gradle.internal.instrumentation.api.annotations.ReplacedAccessor.AccessorType.GETTER;

/**
 * Options that determine which test events get logged, and at which detail.
 */
public interface TestLogging {

    /**
     * Returns the events to be logged.
     *
     * @return the events to be logged
     */
    @Internal
    @ReplacesEagerProperty(adapter = TestLoggingAdapters.EventsAdapter.class)
    SetProperty<TestLogEvent> getEvents();

    /**
     * Sets the events to be logged.
     *
     * @param events the events to be logged
     * @since 4.0
     */
    @EagerSetter
    default void setEvents(Set<TestLogEvent> events) {
        getEvents().set(events);
    }

    /**
     * Sets the events to be logged.
     *
     * @param events the events to be logged
     */
    @EagerSetter
    default void setEvents(Iterable<?> events) {
        Set<TestLogEvent> set = EnumSet.noneOf(TestLogEvent.class);
        for (Object event : events) {
            set.add(GUtil.toEnum(TestLogEvent.class, event));
        }
        getEvents().set(set);
    }

    /**
     * Sets the events to be logged. Events can be passed as enum values (e.g. {@link TestLogEvent#FAILED}) or Strings (e.g. "failed").
     *
     * @param events the events to be logged
     */
    void events(Object... events);

    /**
     * Returns the minimum granularity of the events to be logged. Typically, 0 corresponds to events from the Gradle-generated test suite for the whole test run, 1 corresponds to the Gradle-generated test suite
     * for a particular test JVM, 2 corresponds to a test class, and 3 corresponds to a test method. These values may extend higher if user-defined suites or parameterized test methods are executed.  Events
     * from levels lower than the specified granularity will be ignored.
     * <p>The default granularity is -1, which specifies that test events from only the most granular level should be logged.  In other words, if a test method is not parameterized, only events
     * from the test method will be logged and events from the test class and lower will be ignored.  On the other hand, if a test method is parameterized, then events from the iterations of that test
     * method will be logged and events from the test method and lower will be ignored.
     *
     * @return the minimum granularity of the events to be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getMinGranularity", originalType = int.class))
    Property<Integer> getMinGranularity();

    /**
     * Sets the minimum granularity of the events to be logged. Typically, 0 corresponds to events from the Gradle-generated test suite for the whole test run, 1 corresponds to the Gradle-generated test suite
     * for a particular test JVM, 2 corresponds to a test class, and 3 corresponds to a test method. These values may extend higher if user-defined suites or parameterized test methods are executed.  Events
     * from levels lower than the specified granularity will be ignored.
     * <p>The default granularity is -1, which specifies that test events from only the most granular level should be logged.  In other words, if a test method is not parameterized, only events
     * from the test method will be logged and events from the test class and lower will be ignored.  On the other hand, if a test method is parameterized, then events from the iterations of that test
     * method will be logged and events from the test method and lower will be ignored.
     *
     * @param minGranularity the minimum granularity of the events to be logged
     */
    @EagerSetter
    default void setMinGranularity(int minGranularity) {
        getMinGranularity().set(minGranularity);
    }

    /**
     * Returns the maximum granularity of the events to be logged. Typically, 0 corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to the Gradle-generated test suite
     * for a particular test JVM, 2 corresponds to a test class, and 3 corresponds to a test method. These values may extend higher if user-defined suites or parameterized test methods are executed.  Events
     * from levels higher than the specified granularity will be ignored.
     * <p>The default granularity is -1, which specifies that test events from only the most granular level should be logged.  Setting this value to something lower will cause events
     * from a higher level to be ignored.  For example, setting the value to 3 will cause only events from the test method level to be logged and any events from iterations of a parameterized test method
     * will be ignored.
     *
     * @return the maximum granularity of the events to be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getMaxGranularity", originalType = int.class))
    Property<Integer> getMaxGranularity();

    /**
     * Returns the maximum granularity of the events to be logged. Typically, 0 corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to the Gradle-generated test suite
     * for a particular test JVM, 2 corresponds to a test class, and 3 corresponds to a test method. These values may extend higher if user-defined suites or parameterized test methods are executed.  Events
     * from levels higher than the specified granularity will be ignored.
     * <p>The default granularity is -1, which specifies that test events from only the most granular level should be logged.  Setting this value to something lower will cause events
     * from a higher level to be ignored.  For example, setting the value to 3 will cause only events from the test method level to be logged and any events from iterations of a parameterized test method
     * will be ignored.
     *<p>Note that since the default value of {@link #getMinGranularity()} is -1 (the highest level of granularity) it only makes sense to configure the maximum granularity while also setting the
     * minimum granularity to a value greater than -1.
     *
     * @param maxGranularity the maximum granularity of the events to be logged
     */
    @EagerSetter
    default void setMaxGranularity(int maxGranularity) {
        getMaxGranularity().set(maxGranularity);
    }

    /**
     * Returns the display granularity of the events to be logged. For example, if set to 0, a method-level event will be displayed as "Test Run &gt; Test Worker x &gt; org.SomeClass &gt; org.someMethod". If
     * set to 2, the same event will be displayed as "org.someClass &gt; org.someMethod". <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @return the display granularity of the events to be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getDisplayGranularity", originalType = int.class))
    Property<Integer> getDisplayGranularity();

    /**
     * Sets the display granularity of the events to be logged. For example, if set to 0, a method-level event will be displayed as "Test Run &gt; Test Worker x &gt; org.SomeClass &gt; org.someMethod". If set
     * to 2, the same event will be displayed as "org.someClass &gt; org.someMethod". <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @param displayGranularity the display granularity of the events to be logged
     */
    @EagerSetter
    default void setDisplayGranularity(int displayGranularity) {
        getDisplayGranularity().set(displayGranularity);
    }

    /**
     * Tells whether exceptions that occur during test execution will be logged. Typically these exceptions coincide with a "failed" event.  Defaults to true.
     *
     * @return whether exceptions that occur during test execution will be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = AccessorType.GETTER, name = "getShowExceptions", originalType = boolean.class))
    Property<Boolean> getShowExceptions();

    /**
     * Sets whether exceptions that occur during test execution will be logged.  Defaults to true.
     *
     * @param showExceptions whether exceptions that occur during test execution will be logged
     */
    @EagerSetter
    default void setShowExceptions(boolean showExceptions) {
        getShowExceptions().set(showExceptions);
    }

    /**
     * Tells whether causes of exceptions that occur during test execution will be logged. Only relevant if {@code showExceptions} is {@code true}.  Defaults to true.
     *
     * @return whether causes of exceptions that occur during test execution will be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = AccessorType.GETTER, name = "getShowCauses", originalType = boolean.class))
    Property<Boolean> getShowCauses();

    /**
     * Sets whether causes of exceptions that occur during test execution will be logged. Only relevant if {@code showExceptions} is {@code true}. Defaults to true.
     *
     * @param showCauses whether causes of exceptions that occur during test execution will be logged
     */
    @EagerSetter
    default void setShowCauses(boolean showCauses) {
        getShowCauses().set(showCauses);
    }

    /**
     * Tells whether stack traces of exceptions that occur during test execution will be logged.  Defaults to true.
     *
     * @return whether stack traces of exceptions that occur during test execution will be logged
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = AccessorType.GETTER, name = "getShowStackTraces", originalType = boolean.class))
    Property<Boolean> getShowStackTraces();

    /**
     * Sets whether stack traces of exceptions that occur during test execution will be logged.  Defaults to true.
     *
     * @param showStackTraces whether stack traces of exceptions that occur during test execution will be logged
     */
    @EagerSetter
    default void setShowStackTraces(boolean showStackTraces) {
        getShowStackTraces().set(showStackTraces);
    }

    /**
     * Returns the format to be used for logging test exceptions. Only relevant if {@code showStackTraces} is {@code true}.  Defaults to {@link TestExceptionFormat#FULL} for
     * the INFO and DEBUG log levels and {@link TestExceptionFormat#SHORT} for the LIFECYCLE log level.
     *
     * @return the format to be used for logging test exceptions
     */
    @Internal
    @ReplacesEagerProperty(adapter = TestLoggingAdapters.ExceptionFormatAdapter.class)
    Property<TestExceptionFormat> getExceptionFormat();

    /**
     * Sets the format to be used for logging test exceptions. Only relevant if {@code showStackTraces} is {@code true}.  Defaults to {@link TestExceptionFormat#FULL} for
     * the INFO and DEBUG log levels and {@link TestExceptionFormat#SHORT} for the LIFECYCLE log level.
     *
     * @param exceptionFormat the format to be used for logging test exceptions
     * @since 4.0
     */
    @EagerSetter
    default void setExceptionFormat(TestExceptionFormat exceptionFormat) {
        getExceptionFormat().set(exceptionFormat);
    }

    /**
     * Sets the format to be used for logging test exceptions. Only relevant if {@code showStackTraces} is {@code true}.  Defaults to {@link TestExceptionFormat#FULL} for
     * the INFO and DEBUG log levels and {@link TestExceptionFormat#SHORT} for the LIFECYCLE log level.
     *
     * @param exceptionFormat the format to be used for logging test exceptions
     */
    @EagerSetter
    default void setExceptionFormat(Object exceptionFormat) {
        getExceptionFormat().set(GUtil.toEnum(TestExceptionFormat.class, exceptionFormat));
    }

    /**
     * Returns the set of filters to be used for sanitizing test stack traces.
     *
     * @return the set of filters to be used for sanitizing test stack traces
     */
    @Internal
    @ReplacesEagerProperty(adapter = TestLoggingAdapters.StackTraceFiltersAdapter.class)
    SetProperty<TestStackTraceFilter> getStackTraceFilters();

    /**
     * Sets the set of filters to be used for sanitizing test stack traces.
     *
     * @param stackTraces the set of filters to be used for sanitizing test stack traces
     * @since 4.0
     */
    @EagerSetter
    default void setStackTraceFilters(Set<TestStackTraceFilter> stackTraces) {
        getStackTraceFilters().set(stackTraces);
    }

    /**
     * Sets the set of filters to be used for sanitizing test stack traces.
     *
     * @param stackTraces the set of filters to be used for sanitizing test stack traces
     */
    @EagerSetter
    default void setStackTraceFilters(Iterable<?> stackTraces) {
        Set<TestStackTraceFilter> set = EnumSet.noneOf(TestStackTraceFilter.class);
        for (Object trace : stackTraces) {
            set.add(GUtil.toEnum(TestStackTraceFilter.class, trace));
        }
        getStackTraceFilters().set(set);
    }

    /**
     * Convenience method for {@link #getStackTraceFilters()}. Accepts both enum values and Strings.
     */
    void stackTraceFilters(Object... stackTraces);

    /**
     * Tells whether output on standard out and standard error will be logged. Equivalent to checking if both log events {@code TestLogEvent.STANDARD_OUT} and {@code TestLogEvent.STANDARD_ERROR} are
     * set.
     */
    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = AccessorType.GETTER, name = "getShowStandardStreams", originalType = boolean.class))
    Property<Boolean> getShowStandardStreams();

    /**
     * Sets whether output on standard out and standard error will be logged. Equivalent to setting log events {@code TestLogEvent.STANDARD_OUT} and {@code TestLogEvent.STANDARD_ERROR}.
     */
    @EagerSetter
    default TestLogging setShowStandardStreams(boolean showStandardStreams) {
        getShowStandardStreams().set(showStandardStreams);
        return this;
    }
}
