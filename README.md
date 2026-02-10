## Java Spring 4.x Gradle Multi-module Setup Template

> Why gradle? It's 2026 and I don't want to look at XMLs anymore.

### Create a repository from this template (GitHub CLI)

```bash
gh repo create \
  --template ngnhng/spring-boot-4-gradle-monorepo-template \
  --private \
  --clone <your-repo-name>
```

- In scope:
  - This repository will try to combine aspects of layered architecture, domain-driven and hexagonal (ports and adapters) to try to produce a readable and maintainable codebase.

- Out of scope:
  - Not for enterprise-grade projects

### Structure

#### Overall module structure

The template is organized into multiple domain-specific modules at the root level:

```
backend/
├── build-logic/              # Shared Gradle convention plugins (quality, Java defaults)
├── integration-tests/        # Cross-module integration test suite
├── onboard-provider/         # Provider-facing application module
├── onboard-registration/     # Registration domain module
├── onboard-loan-origination/ # Loan origination domain module
└── onboard-core/             # Shared domain primitives and cross-cutting logic
```

#### Standard module structure

```text
<module-name>/
├── src/
│   ├── main/
│   │   ├── java/com/<app>/<bounded-context>/
│   │   │   ├── application/              # Use cases / service orchestration
│   │   │   ├── domain/                   # Domain model (entities, VOs, rules)
│   │   │   ├── adapters/
│   │   │   │   ├── in/                   # Inbound adapters (Service-to-service, REST, EDA, etc.)
│   │   │   │   └── out/                  # Outbound adapters (DB, clients)
│   │   │   └── config/                   # (Optional) Spring configuration
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   └── test/
│       ├── java/com/<app>/<bounded-context>/
│       └── resources/
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

A single module is usually the best default when the codebase is small, the team is small,
and release boundaries are simple. A multi-module setup is useful when domain boundaries are
clear and you need stronger build-time separation between concerns.

Use a **single module** when:

- The project is still in early discovery and architecture changes frequently.
- Most code changes touch the same area, so split boundaries add little value.
- You want faster onboarding and lower build/tooling complexity.

Use a **multi-module project** when:

- Domains are stable (`registration`, `loan-origination`, `provider`) and should evolve independently.
- You want explicit dependency direction (e.g., domain modules should not depend on application modules).
- You need reusable shared modules (e.g., `onboard-core`) across multiple services.
- You want module-level quality gates and architecture tests to enforce boundaries.

Trade-offs:

- Multi-module improves maintainability and dependency hygiene, but increases build setup and refactoring overhead.
- Single module keeps velocity high early on, but can become harder to scale as coupling grows.

#### When to create a module

Create a new module only when at least one of these is true:

- The package has a distinct business capability and a clear owner.
- It needs an independent dependency set or different runtime integration.
- You need to enforce compile-time isolation from other domains.
- It is reused by multiple modules and should not be duplicated.

Avoid creating a module when:

- The split is only for folder organization.
- The boundaries are still unclear and likely to be merged again.
- The team cannot maintain the additional build and dependency complexity yet.

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

##### Audit

The hierarchy of auditing patterns in modern software architecture, ranging from simple entity metadata to complex, immutable event streams.

Level 1: Basic Entity Metadata (The "Who & When")

This is the bare minimum for any production database. It tracks **state**, but not **history**.

- **Concept:** Every table has columns for `created_at`, `created_by`, `updated_at`, `updated_by`.
- **Implementation:**
  - **Spring Boot:** `@EnableJpaAuditing` with `@CreatedDate`, `@LastModifiedDate`, etc.
  - **Database:** Default values (`DEFAULT CURRENT_TIMESTAMP`) or Triggers.
- **What it answers:** "When was this row last touched, and by whom?"
- **Limitation:** It is destructive. We cannot see what the value was _before_ the update. We only see the current state.

Level 2: Shadow Tables / Snapshots (The "Envers" Approach)

This creates a full historical record of every change by copying the data to a parallel table.

- **Concept:** For every table `users`, there is a `users_audit` (or `users_history`) table. On every `INSERT/UPDATE/DELETE`, a copy of the row is inserted into the audit table with a revision number and type (ADD, MOD, DEL).
- **Implementation:**
  - **Spring Boot:** **Hibernate Envers** (`@Audited` on the entity). Zero boilerplate code.
  - **Database:** Triggers that copy `NEW.*` to the history table.
- **What it answers:** "What did the Product look like last Tuesday at 2 PM?"
- **Limitation:** Storage heavy. If we change one column in a row with 50 columns, Envers duplicates all 50 columns in the audit table.

Level 3: Field-Level Diffs (The "Javers" Approach)

Instead of storing the _whole row_, we store only the _delta_ (what changed).

- **Concept:** A centralized `audit_log` table stores JSON payloads representing the diff.
  - _Example:_ `{"field": "status", "old": "PENDING", "new": "APPROVED"}`
- **Implementation:**
  - **Library:** **Javers** (Java library for object diffing).
  - **Custom:** An EntityListener that compares `state` vs `oldState` and writes to a MongoDB or JSONB column in Postgres.
- **What it answers:** "Show me exactly which fields changed in this transaction."
- **Pros:** Efficient storage; very easy to render a "Change Log" UI for users.

Level 4: Domain / Activity Auditing (The "Business Intent")

Levels 1-3 track _data_. Level 4 tracks _intent_.

- **Concept:** Technical audits show "status changed from 1 to 2". Domain audits show "User A approved the Invoice". This is often required for compliance (SOC2, HIPAA, Banking).
- **Implementation:**
  - **Explicit Service Calls:** `auditService.log("INVOICE_APPROVED", invoiceId, user);`
  - **AOP:** Custom annotation `@LogActivity(action="APPROVE_INVOICE")` on service methods.
- **What it answers:** "Why did this data change?" or "Who tried to access this sensitive record?"
- **Pros:** meaningful to non-technical staff/auditors.

Level 5: Asynchronous / Enterprise Auditing (CDC)

In high-scale systems, writing audit logs synchronously (in the same transaction) slows down the application.

- **Concept:** The application writes to the DB as normal. A separate process reads the **Database Transaction Log (WAL)** and generates audit events.
- **Implementation:**
  - **Pattern:** **CDC (Change Data Capture)**.
  - **Tools:** **Debezium** + Kafka. Debezium listens to Postgres WAL, pushes changes to Kafka, and an Audit Service consumes them to write to Elasticsearch/Snowflake.
- **Pros:** Zero performance impact on the main application. Decoupled.
- **Cons:** High infrastructure complexity. Eventual consistency (audit log might lag by 100ms).

Level 6: Event Sourcing (The "Ultimate" Audit)

Here, the audit log **is** the database.

- **Concept:** We do not store the "Current State" (e.g., `Wallet Balance: $100`). We store the transactions (`Credit $50`, `Credit $50`).
- **Implementation:**
  - **Architecture:** **CQRS + Event Sourcing**.
  - **Tools:** Axon Framework, Kafka Streams, or EventStoreDB.
- **How it works:** To get the current state, we replay all events. We cannot "delete" or "overwrite" data, we can only append a "Correction Event".
- **What it answers:** absolute mathematical proof of how the system arrived at the current state.
- **Cons:** Extremely complex to develop and maintain. Overkill for 95% of CRUD applications.
