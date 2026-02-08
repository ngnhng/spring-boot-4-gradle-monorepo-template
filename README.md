## Java Spring 4.x Gradle Multi-module Setup Template

- In scope:
  - This repository will try to combine aspects of layered architecture, domain-driven and hexagonal (ports and adapters) to try to produce a readable and maintainable codebase.

- Out of scope:
  - Not for enterprise-grade projects

### Structure

#### Overall module structure

The template is organized into multiple domain-specific modules at the root level:

```

```

#### Standard module structure

```
<module-name>/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── build.gradle.kts
```

#### Package Organization and Layering

Within each module, code is organized under `com.example.onboarding` with domain-specific sub-packages. The typical package structure includes:

### Quality gate strategy

Current backend quality gates are centralized in the `java-quality` convention plugin
(`backend/build-logic/src/main/kotlin/java-quality.gradle.kts`) and applied in backend modules.

#### Level 1: Auto-formatting (Spotless)

> Goal: Keep Java and Gradle Kotlin DSL formatting consistent.
>
> Run: `task format` (fix), `task check-format` (verify).

#### Level 2: Compile-time checks (Error Prone)

> Goal: Catch bug-prone code patterns during compilation.
>
> Run: included in `task check` and `task verify`.

#### Level 3: Static analysis (SpotBugs)

> Goal: Detect likely runtime bugs from bytecode analysis.
>
> Run: `task spotbugs` or full lifecycle via `task check` / `task verify`.

#### Level 4: Style checks (Checkstyle)

> Goal: Enforce source-level conventions (including Javadoc requirements).
>
> Run: `task checkstyle`, or scoped tasks `task checkstyle-main` / `task checkstyle-test`.

#### Level 5: Null-safety checks (JSpecify + NullAway)

> Goal: Enforce null contracts at compile time for null-marked packages.

Setup:

- `@NullMarked` is declared in package-level files:
  - `backend/onboard-provider/src/main/java/com/onboard/provider/package-info.java`
  - `backend/onboard-core/src/main/java/com/onboard/core/domain/package-info.java`
  - `backend/onboard-loan-origination/src/main/java/com/onboard/loan-origination/package-info.java`
- Example null-safe API:
  - `backend/onboard-core/src/main/java/com/onboard/core/domain/NullSafetyDemo.java`

Run: included in Java compilation, so it is exercised by `task check`, `task verify`, or `./gradlew compileJava --rerun-tasks`.

Quality gate behavior:

- NullAway is configured as a build-breaking check (`error("NullAway")`) in
  `backend/build-logic/src/main/kotlin/java-quality.gradle.kts`.
- Checks are scoped to null-marked code (`NullAway:OnlyNullMarked=true`), and JSpecify
  annotations are interpreted via `NullAway:JSpecifyMode=true`.
- This gate verifies nullability contract violations inside `@NullMarked` packages.
- This gate does not enforce that every package must declare `@NullMarked`; packages without it are simply out of scope for NullAway in current configuration.

---

> [!WARNING]
>
> Below are my ramblings

---

### Multi-module project versus Single module project

#### When to create a module

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

#### Separate Pure Domain from Entity

#### CQRS, maker-checker and audit trail
