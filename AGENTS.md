# Repository Guidelines

## Project Structure & Module Organization

- `backend/` contains Spring Boot modules: `onboard-provider`, `onboard-lending`, `onboard-core`, `onboard-registration`.
- Source code follows standard Java layout under `src/main/java` (example: `backend/onboard-provider/src/main/java/com/onboard/provider/Application.java`).
- Build outputs land in module-level `build/` directories.

## Build, Test, and Development Commands

Preferred workflow uses Taskfile:

- `task dev` starts Spring Boot with the `dev` profile.
- `task run` starts Spring Boot with the default profile.
- `task test` runs backend tests via Gradle.
- `task verify` runs a clean build and tests.

Direct Gradle/Maven alternatives:

- `./gradlew :backend:onboard-provider:bootRun`
- `./gradlew test`
- `./mvnw -pl backend/onboard-provider spring-boot:run`

## Quality Checks During Development

- Run `task check` before opening a PR to execute the full Gradle `check` lifecycle.
- Run `task checkstyle` (or `task checkstyle-main` / `task checkstyle-test`) for style and Javadoc rules.
- Run `task spotbugs` for static analysis and `task check-format` for formatting validation.
- Use `task format` to auto-fix formatting issues.
- Taskfile Gradle commands are configured with `--rerun-tasks`, so quality tasks always execute instead of relying on cached up-to-date checks.

## Coding Style & Naming Conventions

- Java 21 is the target toolchain.
- Follow standard Spring/Java conventions: package names lower-case (`com.onboard.*`), classes `PascalCase`, methods/fields `camelCase`.
- Match existing formatting in modules (current Java files use 2-space indentation).

## Testing Guidelines

- Tests, if added, should follow the standard Gradle layout under `src/test/java`.
- Use clear, behavior-focused names (e.g., `ProductDetailServiceTest`).
- For domain models and value objects (VOs) that override `equals`, add unit tests that verify the `equals`/`hashCode` contract: if `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` must also be `true`.
- Run all tests with `task test` or `./gradlew test`.

## Commit & Pull Request Guidelines

- Git history currently contains a single `init` commit, so no established convention exists.
- Use concise, imperative commit messages (e.g., `Add lending service endpoints`).
- PRs should include a short summary, testing notes, and any relevant configuration changes.

## Configuration Tips

- Gradle resolves dependencies via Maven Central and an optional Nexus repo.
- Configure Nexus with `NEXUS_URL`, `NEXUS_USERNAME`, `NEXUS_PASSWORD`, `NEXUS_ALLOW_INSECURE` if needed.
