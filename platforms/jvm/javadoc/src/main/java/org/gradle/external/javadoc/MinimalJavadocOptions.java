/*
 * Copyright 2009 the original author or authors.
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

package org.gradle.external.javadoc;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.internal.instrumentation.api.annotations.EagerSetter;
import org.gradle.internal.instrumentation.api.annotations.ReplacesEagerProperty;
import org.gradle.internal.instrumentation.api.annotations.ReplacedAccessor;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.gradle.internal.instrumentation.api.annotations.ReplacedAccessor.AccessorType.GETTER;

/**
 * Provides the core Javadoc options.
 */
public interface MinimalJavadocOptions {
    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getOverview"))
    Property<String> getOverview();

    /** Eager forwarder; see {@link #getOverview()}. */
    @EagerSetter
    default void setOverview(String overview) {
        getOverview().set(overview);
    }

    MinimalJavadocOptions overview(String overview);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getMemberLevel"))
    Property<JavadocMemberLevel> getMemberLevel();

    /** Eager forwarder; see {@link #getMemberLevel()}. */
    @EagerSetter
    default void setMemberLevel(JavadocMemberLevel memberLevel) {
        getMemberLevel().set(memberLevel);
    }

    MinimalJavadocOptions showFromPublic();

    MinimalJavadocOptions showFromProtected();

    MinimalJavadocOptions showFromPackage();

    MinimalJavadocOptions showFromPrivate();

    MinimalJavadocOptions showAll();

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getDoclet"))
    Property<String> getDoclet();

    /** Eager forwarder; see {@link #getDoclet()}. */
    @EagerSetter
    default void setDoclet(String doclet) {
        getDoclet().set(doclet);
    }

    MinimalJavadocOptions doclet(String docletClass);

    @Classpath
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.DocletpathAdapter.class)
    ConfigurableFileCollection getDocletpath();

    /** Eager forwarder; see {@link #getDocletpath()}. */
    @EagerSetter
    default void setDocletpath(List<File> docletpath) {
        getDocletpath().setFrom(docletpath);
    }

    MinimalJavadocOptions docletpath(File... docletpath);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getSource"))
    Property<String> getSource();

    /** Eager forwarder; see {@link #getSource()}. */
    @EagerSetter
    default void setSource(String source) {
        getSource().set(source);
    }

    MinimalJavadocOptions source(String source);

    @Internal
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.ClasspathAdapter.class)
    ConfigurableFileCollection getClasspath();

    /** Eager forwarder; see {@link #getClasspath()}. */
    @EagerSetter
    default void setClasspath(List<File> classpath) {
        getClasspath().setFrom(classpath);
    }

    /**
     * The --module-path.
     *
     * @since 6.4
     */
    @Internal
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.ModulePath.class)
    ConfigurableFileCollection getModulePath();

    /**
     * The --module-path.
     *
     * @since 6.4
     */
    @EagerSetter
    default void setModulePath(List<File> modulePath) {
        getBootClasspath().setFrom(modulePath);
    }

    /**
     * The --module-path.
     *
     * @since 6.4
     */
    MinimalJavadocOptions modulePath(List<File> classpath);

    MinimalJavadocOptions classpath(List<File> classpath);

    MinimalJavadocOptions classpath(File... classpath);

    @Classpath
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.BootclasspathAdapter.class)
    ConfigurableFileCollection getBootClasspath();

    /** Eager forwarder; see {@link #getBootClasspath()}. */
    @EagerSetter
    default void setBootClasspath(List<File> bootClasspath) {
        getBootClasspath().setFrom(bootClasspath);
    }

    MinimalJavadocOptions bootClasspath(File... bootClasspath);

    @InputFiles
    @Optional
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.ExtDirsAdapter.class)
    ConfigurableFileCollection getExtDirs();

    /** Eager forwarder; see {@link #getExtDirs()}. */
    @EagerSetter
    default void setExtDirs(List<File> extDirs) {
        getExtDirs().setFrom(extDirs);
    }

    MinimalJavadocOptions extDirs(File... extDirs);

    @Console
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getOutputLevel"))
    Property<JavadocOutputLevel> getOutputLevel();

    /** Eager forwarder; see {@link #getOutputLevel()}. */
    @EagerSetter
    default void setOutputLevel(JavadocOutputLevel outputLevel) {
        getOutputLevel().set(outputLevel);
    }

    MinimalJavadocOptions verbose();

    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "isVerbose", originalType = boolean.class))
    Provider<Boolean> getVerbose();

    /**
     * This method exists only for Kotlin source backward compatibility.
     */
    @Internal
    Provider<Boolean> getIsVerbose();

    MinimalJavadocOptions quiet();

    @Input
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "isBreakIterator", originalType = boolean.class))
    Property<Boolean> getBreakIterator();

    /** Eager forwarder; see {@link #getBreakIterator()}. */
    @EagerSetter
    default void setBreakIterator(boolean breakIterator) {
        getBreakIterator().set(breakIterator);
    }

    /**
     * This method exists only for Kotlin source backward compatibility.
     */
    @Internal
    Property<Boolean> getIsBreakIterator();

    MinimalJavadocOptions breakIterator(boolean breakIterator);

    MinimalJavadocOptions breakIterator();

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getLocale"))
    Property<String> getLocale();

    /** Eager forwarder; see {@link #getLocale()}. */
    @EagerSetter
    default void setLocale(String locale) {
        getLocale().set(locale);
    }

    MinimalJavadocOptions locale(String locale);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getEncoding"))
    Property<String> getEncoding();

    /** Eager forwarder; see {@link #getEncoding()}. */
    @EagerSetter
    default void setEncoding(String encoding) {
        getEncoding().set(encoding);
    }

    MinimalJavadocOptions encoding(String encoding);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getJFlags"))
    ListProperty<String> getJFlags();

    /** Eager forwarder; see {@link #getJFlags()}. */
    @EagerSetter
    default void setJFlags(List<String> jFlags) {
        getJFlags().set(jFlags);
    }

    MinimalJavadocOptions jFlags(String... jFlags);

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.NONE)
    @ReplacesEagerProperty(adapter = MinimalJavadocOptionsAdapters.OptionFilesAdapter.class)
    ConfigurableFileCollection getOptionFiles();

    /** Eager forwarder; see {@link #getOptionFiles()}. */
    @EagerSetter
    default void setOptionFiles(List<File> optionFiles) {
        getOptionFiles().setFrom(optionFiles);
    }

    MinimalJavadocOptions optionFiles(File... argumentFiles);

    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getDestinationDirectory"))
    DirectoryProperty getDestinationDirectory();

    /** Eager forwarder; see {@link #getDestinationDirectory()}. */
    @EagerSetter
    default void setDestinationDirectory(File destinationDirectory) {
        getDestinationDirectory().set(destinationDirectory);
    }

    MinimalJavadocOptions destinationDirectory(File directory);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getWindowTitle"))
    Property<String> getWindowTitle();

    /** Eager forwarder; see {@link #getWindowTitle()}. */
    @EagerSetter
    default void setWindowTitle(String windowTitle) {
        getWindowTitle().set(windowTitle);
    }

    StandardJavadocDocletOptions windowTitle(String windowTitle);

    @Input
    @Optional
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getHeader"))
    Property<String> getHeader();

    /** Eager forwarder; see {@link #getHeader()}. */
    @EagerSetter
    default void setHeader(String header) {
        getHeader().set(header);
    }

    StandardJavadocDocletOptions header(String header);

    void write(File outputFile) throws IOException;

    @Internal
    @ReplacesEagerProperty(replacedAccessors = @ReplacedAccessor(value = GETTER, name = "getSourceNames"))
    ListProperty<String> getSourceNames();

    /** Eager forwarder; see {@link #getSourceNames()}. */
    @EagerSetter
    default void setSourceNames(List<String> sourceNames) {
        getSourceNames().set(sourceNames);
    }

    MinimalJavadocOptions sourceNames(String... sourceNames);

    void contributeCommandLineOptions(ExecSpec execHandleBuilder);
}
