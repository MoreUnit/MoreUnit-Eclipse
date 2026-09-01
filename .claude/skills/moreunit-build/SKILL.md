---
name: moreunit-build
description: Build and test the MoreUnit-Eclipse project (Eclipse RCP / Tycho / OSGi). Use whenever the user asks to build, compile, run tests, verify a change, or launch the test suite for this repo — e.g. "build the project", "run the tests", "verify the fix", "lance le build", "lance les tests". This skill documents the non-obvious build commands (it is NOT a plain `mvn clean install`) and how to read the results.
---

# MoreUnit-Eclipse Build & Test

This project is an **Eclipse RCP / OSGi** application built with **Eclipse Tycho** (not a standard Maven project). Tycho resolves a full Eclipse target platform; the reactor is declared in a nested build module.

## Where to run from

The Maven reactor lives in the **`org.moreunit.build`** module. All build commands must be launched **from that directory**:

```bash
cd org.moreunit.build
```

## Build commands

### Full build + all tests (offline)

```bash
cd org.moreunit.build && mvn -o verify -fae
```

- `-o`: offline mode (use local Maven/Tycho cache only). First-ever run needs network for the target platform.
- `verify`: runs everything including tests.
- `-fae` (fail-at-end): continues past broken modules.

### Build single module + dependencies

Tycho **does** support `-pl` with standard Maven syntax:

```bash
# Build core tests only (fastest signal for core changes)
mvn -o -pl ../org.moreunit.core.test -am verify -Dtests.use.ui=false -fae

# Build plugin + mock unit tests (need a display / X server)
DISPLAY=:0 mvn -o -pl ../org.moreunit.test,../org.moreunit.mock.test -am verify -fae

# Build SWTBot test (needs a display)
DISPLAY=:0 mvn -o -pl ../org.moreunit.swtbot.test -am verify -fae
```

`-am` = also make (builds upstream dependencies). `-rf :<artifactId>` resumes from a specific module.

### Run a specific test class

```bash
mvn -o -pl ../org.moreunit.swtbot.test -am verify -Dtest=RunTestSWTBotTest -fae
```

The `-Dtest=` filter applies Tycho-surefire's `test` parameter. Upstream modules with no matching class gracefully skip (0 tests).

## Tycho 5 gotchas (verified experimentally, 2026-09)

### Surefire binds to `integration-test`, not `test`

With Tycho 5, `tycho-surefire:test` is bound to the **`integration-test` phase**:

```bash
mvn -o test -pl ../org.moreunit.swtbot.test   # ⚠️ BUILD SUCCESS but NO tests run (silent!)
mvn -o verify -pl ../org.moreunit.swtbot.test # ✅ runs the tests
```

A `mvn test` that succeeds in a few seconds without any `Tests run:` line is a red flag: the tests were silently skipped.

### `-pl` selectors must use the right groupId

Test/product modules use **two different groupIds** — a wrong groupId fails with `Could not find the selected project in the reactor`:

- `org.moreunit` → `org.moreunit.core`, `org.moreunit.core.test`, `org.moreunit.test.dependencies`? no → **`org.moreunit` group only for core + core.test + the aggregator**
- `org.moreunit.plugins` → `org.moreunit` (plugin, dir `org.moreunit.plugin`), `org.moreunit.mock`, `org.moreunit.mock.test`, `org.moreunit.mock.it`, `org.moreunit.test`, `org.moreunit.test.dependencies`, `org.moreunit.swtbot.test`

Example: `mvn -pl org.moreunit.plugins:org.moreunit.swtbot.test verify`

### Tycho resolves `Require-Bundle` from the target platform, not from Maven reactor deps

`-am` for a test module pulls **almost nothing**: `mvn -pl org.moreunit.plugins:org.moreunit.swtbot.test -am validate` = parent + swtbot.test only. To run a single test module in isolation, the product bundles it requires must already be **installed in the local repo**:

```bash
# 1. install the product bundles required by the test module (tests skipped)
mvn -o install -DskipTests -pl org.moreunit:org.moreunit.core,org.moreunit.plugins:org.moreunit,org.moreunit.plugins:org.moreunit.test.dependencies
# 2. run the test module alone (Tycho resolves Require-Bundle from installed local artifacts)
mvn -o verify -pl org.moreunit.plugins:org.moreunit.swtbot.test -Dtarget.platform.classifier=eclipse-latest
```

- Tycho includes locally installed artifacts (`~/.m2/repository/...`) in the target platform by default (verified: step 2 works in offline mode after step 1).
- `-DskipTests` **is** respected by tycho-surefire (verified) — useful for the step 1 above.

### Running a single module while excluding test modules from a full reactor

Exclusions are useful to skip unwanted test modules in one pass (Maven ≥3.2 syntax):

```bash
mvn clean install -pl '!org.moreunit.plugins:org.moreunit.swtbot.test'        # everything but swtbot
mvn clean install -pl '!org.moreunit:org.moreunit.core.test,!org.moreunit:org.moreunit.test,!org.moreunit.plugins:org.moreunit.mock.test,!org.moreunit.plugins:org.moreunit.mock.it'  # product + swtbot only
```

### Run a single SWTBot test class in isolation (validation recipe)

Full-module SWTBot runs are unstable on a shared desktop display (workbench dies mid-run → cascading `activeShell is null` / `Workspace is already closed` errors, even on unmodified code). For A/B test validation, run **one class at a time**:

```bash
mvn -o -pl ../org.moreunit.swtbot.test verify -Dtest='PreferencesTest' -Dmaven.test.failure.ignore=true
```

## CI performance knowledge (GitHub Actions, windows-latest runners)

### Composite actions: checkout comes first

A **local composite action cannot contain the checkout step** — the repo must
already be checked out for the action to be found (`Can't find 'action.yml'
... Did you forget to run actions/checkout before running your local action?`).
Pattern that works:

```yaml
- name: Checkout 🛎
  uses: actions/checkout@v7      # explicit step, cannot be factored out
- uses: ./.github/actions/setup-build-env   # composite: JDK + Maven only
```

Share the rest (e.g. common Maven flags) via a workflow-level `env:` (like
`MVN_ARGS`) — keep the value free of internal quotes: it is expanded unquoted
into the shell command line, and bash/pwsh do not re-parse quotes inside
expanded variables.


Measured on master builds (useful when optimizing CI time):

- Full build ≈ 15 min Maven; `org.moreunit.swtbot.test` ≈ **10:30 (≈ 2/3 of the build)**.
- SWTBot cost breakdown: opening the Eclipse **Preferences dialog ≈ 19 s per opening** on Windows CI (it loads every preference page of the IDE). `PreferencesTest`/`PreferencesPageSWTBotTest` were the worst offenders — group preference changes into few dialog sessions (see PR #370: swtbot 10:30 → 6:45).
- The Preferences **Properties** dialog (project-scoped) is much cheaper (~1 s/opening).
- Module timings are in the job log under `Reactor Summary` (`SUCCESS [XX s]` lines) and per-test `Time elapsed`; parse the job log with `gh api repos/<org>/<repo>/actions/jobs/<job-id>/logs`.
- Full SWTBot module runs are flaky on CI too; prefer class-level runs for diagnosis.

## UI / headless execution matrix

| Module | Needs display? | Needs Workbench? | Default UI harness | How to run |
|---|---|---|---|---|
| `org.moreunit.core.test` | No (pure logic) | No | `tests.use.ui=true` | `-Dtests.use.ui=false` for headless (UI tests fail, pure tests pass) |
| `org.moreunit.test` | **Yes** | **Yes** (plugin activator calls PlatformUI) | `tests.use.ui=true` | `DISPLAY=:0 mvn ...` |
| `org.moreunit.mock.test` | **Yes** | **Yes** (transitive via mock → plugin) | `tests.use.ui=true` | `DISPLAY=:0 mvn ...` |
| `org.moreunit.swtbot.test` | **Yes** | **Yes** (SWTBot workbench) | `useUIHarness=true` | `DISPLAY=:0 mvn ...` |

### Pre-existing UI-test failures (ignore when validating non-UI changes)

- **headless run** (`-Dtests.use.ui=false`): `JumpActionHandlerTest`, `JumpActionExecutorTest` — need a Workbench.
- **SWTBot run** (headless or flaky): `BestMatchJumpTest`, `PropertiesTest` — timing/menu issues.
- **UI unit run**: `JumperExtensionManagerTest`, `LanguageExtensionManagerTest` — may fail intermittently in some environments.

## Coverage (JaCoCo)

### Run coverage and generate aggregate report

```bash
DISPLAY=:0 mvn -o -Pcoverage verify -fae -Dmaven.test.failure.ignore=true
```

This runs ALL tests (including SWTBot), produces per-module `jacoco.exec`, merges them, and generates an **aggregate HTML + CSV + XML report** at:

```
org.moreunit.build/target/site/jacoco-aggregate/
```

Key files:
- `jacoco.csv` — easy to parse (columns: GROUP, PACKAGE, CLASS, INSTRUCTION_MISSED, INSTRUCTION_COVERED, ...)
- `jacoco.xml` — detailed XML (method-level data)
- `index.html` — browsable HTML report

### Find completely untested classes (instruction covered = 0) in main bundles

```bash
cd org.moreunit.build/target/site/jacoco-aggregate
awk -F, 'NR>1 && ($1 ~ /\/org\.moreunit\.core$/ || $1 ~ /\/org\.moreunit\.mock$/ || $1 == "org.moreunit.report/org.moreunit") && $5==0 {print $4"\t"$2"."$3}' jacoco.csv | sort -rn | head -40
```

The GROUP column in jacoco.csv has the format `org.moreunit.report/<bundle-symbolic-name>` (e.g. `org.moreunit.report/org.moreunit.core`).

### Coverage setup details

- JaCoCo `prepare-agent` execution (in the `coverage` profile) correctly sets `tycho.testArgLine` (not plain `argLine`) for Tycho compatibility — agent is injected into the test JVM.
- The `merge-all` execution (aggregator, `inherited=false`) collects `**/target/jacoco.exec` from all child modules.
- `org.moreunit.report` module runs `report-aggregate` to produce the unified report covering all bundles.
- Use `-Dmaven.test.failure.ignore=true` to ensure all modules package even if tests fail, so the report module's dependencies resolve.

## Module structure

```
org.moreunit.build/          ← build aggregator (pom.xml)
org.moreunit.core/           ← core logic (matching, resources, preferences, config)
org.moreunit.core.test/      ← tests for core (JUnit + Mockito)
org.moreunit.plugin/         ← Eclipse UI plugin (handlers, actions, refactoring, etc.)
org.moreunit.test/           ← unit tests for plugin (JUnit + Mockito)
org.moreunit.mock/           ← mock generation support
org.moreunit.mock.test/      ← tests for mock (fragment, can access package-private host classes)
org.moreunit.test.dependencies/ ← shared test infrastructure (TestContextRule, @Project, configs, test doubles)
org.moreunit.swtbot.test/    ← UI tests (SWTBot, needs display)
org.moreunit.report/         ← JaCoCo aggregate reporting
```

## Key dependencies for test modules

- All test modules: `junit-jupiter-api`, `org.mockito.mockito-core`, `org.mockito.junit-jupiter`
- `org.moreunit.core.test`: `org.eclipse.ui`, `org.eclipse.core.resources` (needed for test doubles)
- `org.moreunit.test`: `org.eclipse.jdt.core`, `org.moreunit.test.dependencies`
- `org.moreunit.mock.test`: fragment of `org.moreunit.mock` (access to package-private host classes)
- `org.moreunit.swtbot.test`: `org.eclipse.swtbot.go` / `swtbot.junit5_x`

### Access restrictions (common gotchas)

Test bundles may not import packages that the production bundles use. This causes `Access restriction` compile errors:
- `org.eclipse.jface.text.Position` — restricted in `org.moreunit.test` (org.eclipse.text bundle). **Fix:** avoid referencing `Position` type in tests, or add the package to Require-Bundle (test infra change).
- `org.eclipse.debug.core.DebugPlugin` — restricted in `org.moreunit.swtbot.test`. **Fix:** add `org.eclipse.debug.core` to swtbot.test MANIFEST Require-Bundle.
- `org.eclipse.debug.core.ILaunch` — same restriction as DebugPlugin.
