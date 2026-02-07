## Java Spring 4.x Gradle Multi-module Setup Template

### Multi-module project versus Single module project

### Structure

### Taskfile

- Windows vs. Linux Shell Problem: The problem with the classic Makefile is that it actually does not run command, but rather passes them to the system shell (command-line interface for user to interact with OS).
  - On Linux/Unix: It passes them to `/bin/sh`
  - On Windows: `cmd.exe` or PowerShell

- The Taskfile Solution:
  Taskfile includes a built-in Go-based shell interpreter (mvdan.cc/sh).
  It allows us to write standard Unix-like commands (cp, rm, mv, cat) inside the Taskfile, and Task translates them automatically to work on Windows.

### Build system

#### Gradle versus Maven

#### Gradle Kotlin DSL

In the gradle setup, we have `settings.gradle.kts` and `build.gradle` at root of project

### Maven

#### Jar vs War

### Quality gate strategy

Current backend quality gates are centralized in the `java-quality` convention plugin
(`backend/build-logic/src/main/kotlin/java-quality.gradle.kts`) and applied in backend modules.

#### Level 1: Auto-formatting (Spotless)

> Goal: Keep Java and Gradle Kotlin DSL formatting consistent.
>
> Run: `task backend:format` (fix), `task backend:check-format` (verify).

#### Level 2: Compile-time checks (Error Prone)

> Goal: Catch bug-prone code patterns during compilation.
>
> Run: included in `task backend:check` and `task backend:verify`.

#### Level 3: Static analysis (SpotBugs)

> Goal: Detect likely runtime bugs from bytecode analysis.
>
> Run: `task backend:spotbugs` or full lifecycle via `task backend:check` / `task backend:verify`.
