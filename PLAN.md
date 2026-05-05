# Plan: Add renamed lazy getters for File→Property migrations

## Goal

For 14 selected `File`-typed properties already migrated to `DirectoryProperty`/`RegularFileProperty`, rename the lazy getter to a clearer name (e.g. `getOutputDir` → `getOutputDirectory`) and restore the original eager API from master.

End state per property:
- Original eager methods restored from master (no `@Deprecated` yet — Phase 2).
- New lazy method with the better name.
- Eager methods delegate to the lazy property.
- `@ReplacesEagerProperty` + companion adapter removed.

## Scope (14 properties)

### Dir → Directory (8)

| Property | New name | Callers |
| --- | --- | ---: |
| `CodeQualityExtension.getReportsDir` | `getReportsDirectory` | ~4 |
| `CreateStartScripts.getOutputDir` | `getOutputDirectory` | ~3 |
| `GroovyCompileOptions.getStubDir` | `getStubDirectory` | ~3 |
| `Groovydoc.getDestinationDir` | `getDestinationDirectory` | ~20 |
| `JacocoTaskExtension.getClassDumpDir` | `getClassDumpDirectory` | 0 |
| `Javadoc.getDestinationDir` | `getDestinationDirectory` | ~20 |
| `ProcessForkOptions.getWorkingDir` | `getWorkingDirectory` | ~30 (interface) |
| `ScalaDoc.getDestinationDir` | `getDestinationDirectory` | ~20 |

### Add File / distinguishing suffix (4)

| Property | New name | Callers |
| --- | --- | ---: |
| `GenerateIvyDescriptor.getDestination` | `getDestinationFile` | ~12 |
| `GenerateMavenPom.getDestination` | `getDestinationFile` | ~12 |
| `CreateStartScripts.getUnixScript` | `getUnixScriptFile` | 0 |
| `CreateStartScripts.getWindowsScript` | `getWindowsScriptFile` | 0 |

### Optional (2)

| Property | New name | Callers |
| --- | --- | ---: |
| `GroovyCompileOptions.getConfigurationScript` | `getConfigurationScriptFile` | ~2 |
| `War.getWebXml` | `getWebXmlFile` | 0 |

## Per-property recipe

One commit per property.

1. Restore master's eager API: `File getXxxDir()`, every `setXxxDir(...)` overload, and DSL chaining methods. Not `@Deprecated`.
2. Wire eager → lazy: read via `getXxxDirectory().getAsFile().getOrNull()`; write via `getXxxDirectory().set(...)`. For `Object`-overload setters, port master's `Project#file(Object)` resolution.
3. Rename the lazy getter to the new name; rename the backing field for readability.
4. Drop `@ReplacesEagerProperty` (and any `@ReplacedAccessor`/`@ReplacedDeprecation`). Delete the adapter class if one exists; grep for references first.
5. Update internal callers — name-only, mechanical.
6. Interface case (`ProcessForkOptions`, `CodeQualityExtension`): keep eager bridges abstract on the interface; delegate to the lazy property in every concrete implementation. No `default` methods.
7. Add `@since` Javadoc on each new lazy getter.

## Ordering (low risk → high)

1. Zero callers: `JacocoTaskExtension.getClassDumpDir`, `War.getWebXml`, `CreateStartScripts.getUnixScript`/`getWindowsScript`.
2. Few callers, single class: `GroovyCompileOptions.getConfigurationScript`/`getStubDir`, `CreateStartScripts.getOutputDir`, `CodeQualityExtension.getReportsDir`.
3. Medium: `GenerateIvyDescriptor.getDestination`, `GenerateMavenPom.getDestination`.
4. `SourceTask` doc cluster: `Javadoc`, `Groovydoc`, `ScalaDoc`.
5. Last: `ProcessForkOptions.getWorkingDir` (interface + adapter; touches `Exec`/`JavaExec`/`Test`).

## Risks

- `ProcessForkOptions` adapter deletion: confirm no external instrumentation references before removing.
- `Object`-overload setter semantics: must preserve master's path-resolution behavior.
- Groovy/Kotlin DSL assignment must still work via the restored eager setters.
- Phase 1 leaves both names visible without a deprecation signal — accepted as interim.

## Validation

Per commit: compile + unit + `embeddedIntegTest` for affected subprojects (run from `/Users/asodja/workspace/agents`).
After all commits: full `embeddedIntegTest` + `gradleception` build.

## Migrated but excluded from rename

### Skipped this iteration (revisit later)

| Property | Reason |
| --- | --- |
| `AntTarget.getBaseDir` | Skipped by request |
| `DirectoryBuildCache.getDirectory` | Skipped by request (proposed name was `getLocation`) |

### Already well-named — leave as-is (9)

Names already end in `Directory`/`File`, no clean rename:

- `AntlrTask.getOutputDirectory`
- `MinimalJavadocOptions.getDestinationDirectory`
- `TestNGOptions.getOutputDirectory`
- `ConventionReportTask.getOutputFile`
- `JacocoTaskExtension.getDestinationFile`
- `StandardJavadocDocletOptions.getHelpFile`
- `StandardJavadocDocletOptions.getStylesheetFile`
- `Wrapper.getJarFile`
- `Wrapper.getScriptFile`

### Already renamed (done — for reference)

- `AbstractCompile.getDestinationDirectory` (was `getDestinationDir`)
- `TestReport.getDestinationDirectory` (was `getDestinationDir`)
- `CompileOptions.getGeneratedSourceOutputDirectory` (was `getAnnotationProcessorGeneratedSourcesDirectory`)

## Phase 2 (deferred)

Add `@Deprecated` + `DeprecationLogger` nag to all restored eager methods, with deprecation tests and upgrade-guide entries.

## Open Slack-thread questions (non-blocking)

- Symmetry: should the 9 "leave as-is" properties also get an eager bridge?
- Holistic rename pass beyond `File`-typed properties (Balint).
- Final word on dropping upgrade-infrastructure entirely vs. retaining it for plugin compat (Pavlos).
