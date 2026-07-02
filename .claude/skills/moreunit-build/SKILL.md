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
