## Java Spring Multi-module Setup (Gradle Kotlin DSL + Maven)

### Multi-module project versus Single module project

### Structure

### Taskfile

- Windows vs. Linux Shell Problem: The problem with the classic Makefile is that it actually does not run command, but rather passes them to the system shell (command-line interface for user to interact with OS).
  - On Linux/Unix: It passes them to /bin/sh
  - On Windows: cmd.exe or PowerShell

- The Taskfile Solution:
  Taskfile includes a built-in Go-based shell interpreter (mvdan.cc/sh).
  It allows you to write standard Unix-like commands (cp, rm, mv, cat) inside the Taskfile, and Task translates them automatically to work on Windows.

### Build system

#### Gradle versus Maven

#### Gradle Kotlin DSL

In the gradle setup, we have `settings.gradle.kts` and `build.gradle` at root of project

### Maven

#### Jar vs War

### Quality gate strategy

#### Level 1: Auto-Formatting (Spotless)

> Goal: Zero mental energy spent on formatting.
> Action: Runs automatically on git commit.

#### Level 2: Compile-Time Safety (Error Prone / NullAway)

> Goal: Catch NPEs and common bugs while typing.
> Action: Runs inside the compiler.

#### Level 3: Deep Analysis (SpotBugs + FindSecBugs)

> Goal: Catch resource leaks and security flaws.
> Action: Runs in the CI pipeline (Docker build).

#### Level 4: Architecture Safety (ArchUnit)

> Goal: Prevent "Spaghetti Code."
> Action: Runs as standard JUnit tests.
