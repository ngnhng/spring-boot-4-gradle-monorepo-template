// Shared Java quality convention for backend modules.
//
// This file is a precompiled Gradle convention plugin (plugin id: "java-quality").
// Any module that applies `id("java-quality")` will receive all rules below.
//
// Why a convention plugin?
// - Single source of truth for formatting and static analysis.
// - Consistent behavior across modules.
// - Easier upgrades (plugin/tool versions and defaults in one place).
//
// Important note for maintainers:
// - This file is compiled by the build-logic project, so keep it deterministic.
// - Prefer explicit versions/coordinates here unless version-catalog access is known
//   to be available in this specific precompiled-script context.

// 1) Apply quality-related Gradle plugins that this convention composes.
//
// - `java`: gives Java source sets/tasks (`compileJava`, `test`, etc.).
// - `com.diffplug.spotless`: source formatting and style enforcement.
// - `com.github.spotbugs`: bytecode-level bug pattern analysis.
// - `net.ltgt.errorprone`: compiler-integrated static checks.
//
// To extend:
// - Add more quality plugins here.
// - Ensure corresponding plugin classpath dependencies exist in
//   backend/build-logic/build.gradle.kts.

import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("com.diffplug.spotless")
    id("com.github.spotbugs")
    id("net.ltgt.errorprone")

    // https://docs.gradle.org/current/userguide/checkstyle_plugin.html
    checkstyle
}

// 2) Checkstyle configuration (source-level style and convention checks).
//
// Current policy:
// - Uses a pinned Checkstyle engine version for deterministic CI/local runs.
//
// Configuration file:
// - The repository-level rules live in `config/checkstyle/checkstyle.xml`.
// - Gradle's default location (`config/checkstyle/checkstyle.xml`) is used,
//   so no explicit `configFile` wiring is required here.
//
// To adjust:
// - Bump `toolVersion` when upgrading Checkstyle.
// - Update `config/checkstyle/checkstyle.xml` to evolve style rules.
checkstyle {
    toolVersion = "13.2.0"
}

// 3) Source formatting policy (Spotless).
//
// Current scope:
// - Applies to Java files under `src/**/*.java` in each consuming module.
//
// Current policy:
// - `googleJavaFormat().reflowLongStrings()` keeps code machine-formatted and
//   stable in CI; `reflowLongStrings` allows formatter-controlled wrapping.
// - remove/trim/newline/import ordering rules keep diffs clean and predictable.
//
// To adjust:
// - Relax or tighten format behavior by editing this block.
// - If your team standard changes, update here once rather than per module.
spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat().reflowLongStrings()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// 4) SpotBugs configuration (bug-finding on compiled classes).
// - See: https://spotbugs.readthedocs.io/en/stable/ant.html#parameters
//
// - `toolVersion`: engine version used by SpotBugs tasks.
// - `ignoreFailures = false`: fails the build on findings (quality gate).
// - `effort = MAX`: deeper analysis, slower but catches more issues.
// - `reportLevel = HIGH`: only high-confidence findings to reduce noise.
//
// To adjust:
// - For stricter CI, keep as-is and add exclusion filters if needed.
// - For faster local iteration, lower `effort` or tune report level.
// - Add `tasks.withType<com.github.spotbugs.snom.SpotBugsTask>()` if you need
//   custom reports (HTML/XML) or per-task settings.
spotbugs {
    toolVersion.set("4.9.8")
    ignoreFailures = false
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.HIGH)
}

// 5) Error Prone compiler dependency.
//
// The errorprone plugin adds an `errorprone` configuration. We attach the core
// checker artifact so Java compilation runs Error Prone checks.
//
// To adjust:
// - Bump version here when upgrading Error Prone.
// - Keep plugin version (catalog) and checker version compatible.
dependencies {
    add("errorprone", "com.google.errorprone:error_prone_core:2.47.0")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}


// 6) JDK/compiler compatibility knobs for Error Prone.
//
// `-XDaddTypeAnnotationsToSymbol=true` is required for Error Prone on JDK 21.
// Without it, compile tasks fail before any useful diagnostics are reported.
//
// To extend:
// - Add more shared compiler args here.
// - For module-specific overrides, configure `JavaCompile` in that module after
//   applying `java-quality`.
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Critical for modern JDKs (21+)
    options.compilerArgs.add("-XDaddTypeAnnotationsToSymbol=true")
    options.compilerArgs.add("-XDcompilePolicy=simple")
    // Critical for Lombok/MapStruct support
    options.errorprone.disableWarningsInGeneratedCode.set(true)
    // Exclude specific generated folders from ErrorProne explicitly if needed
    options.errorprone.excludedPaths.set(".*/build/generated/.*")
}
