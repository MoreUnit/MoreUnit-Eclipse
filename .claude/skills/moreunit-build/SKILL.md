---
name: moreunit-build
description: Build and test the MoreUnit-Eclipse project (Eclipse RCP / Tycho / OSGi). Use whenever the user asks to build, compile, run tests, verify a change, or launch the test suite for this repo — e.g. "build the project", "run the tests", "verify the fix", "lance le build", "lance les tests". This skill documents the non-obvious build commands (it is NOT a plain `mvn clean install`) and how to read the results.
---

# MoreUnit-Eclipse Build & Test

This project is an **Eclipse RCP / OSGi** application built with **Eclipse Tycho** (not a standard Maven project). The build is **not** a simple `mvn clean install` from the repo root — Tycho resolves a full Eclipse target platform and the reactor is declared in a nested build module.

## Where to run from

The Maven reactor lives in the **`org.moreunit.build`** module (a child of the repo root). All build commands must be launched **from that directory**, not the repo root:

```bash
cd org.moreunit.build
```

The parent POM (`org.moreunit.build/pom.xml`) aggregates every module (`org.moreunit.core`, `org.moreunit.plugin`, `org.moreunit.core.test`, `org.moreunit.test`, `org.moreunit.swtbot.test`, features, update site, …).

## Build commands

### Full build + all tests (offline, recommended first try)

```bash
cd org.moreunit.build && mvn -o verify -fae
```

- `-o` / **offline**: use only the local Maven/Tycho cache. The first ever run needs network to fetch the Eclipse target platform; afterwards prefer offline to avoid slow re-resolution.
- **`verify`** (not `install`): runs compilation **and** the Tycho surefire tests. Plain `mvn clean` or `compile` alone would skip the tests.
- **`-fae`** (fail-at-end): keep going through all modules even if one fails, so you see the full picture instead of stopping at the first broken module.

### Build a single module and its dependencies

Tycho doesn't accept `-pl` the same way as classic Maven. To scope roughly, build from `org.moreunit.build` and resume, or just run the full reactor (it's the reliable path). If a module fails and you want to re-run from it:

```bash
mvn -rf :<artifactId> verify -fae
```

### Skip the UI/SWTBot tests (fastest feedback on core logic)

The headless environment often flaks on SWTBot (`WidgetNotFound`, menu-bar timeouts). To get fast signal on the pure-Java core:

```bash
mvn -o verify -fae -Dskip.swtbot.tests=true
```

(Check the parent POM properties for the exact skip property name in use; if none, just run the full reactor and ignore SWTBot noise.)

## Reading the results

Tycho surefire writes per-module reports that are the **source of truth** — Maven's own log is terse (especially with `-q`). After a run, inspect:

```
<module>/target/surefire-reports/<TestClass>.txt     # human summary
<module>/target/surefire-reports/TEST-<TestClass>.xml # machine details
```

Example for the core matching tests:

```bash
cat org.moreunit.core.test/target/surefire-reports/org.moreunit.core.matching.SearchEngineTest.txt
```

A passing test set reads `Tests run: N, Failures: 0, Errors: 0, Skipped: 0`.

## Known flaky / environment failures (do not chase these)

In a headless/offline run, these commonly fail **unrelated to your change** — they are SWTBot/UI timing issues:
- `org.moreunit.swtbot.test` — `BestMatchJumpTest`, `PropertiesTest` (`WidgetNotFound ... Could not find menu bar for shell`, `Could not find node with text: test`).

When verifying a fix, **focus on the surefire report of the module you touched**, not the SWTBot suite. Confirm your target module is green; treat SWTBot failures as pre-existing environment noise unless your change touched UI code.

## Sanity check workflow

1. `cd org.moreunit.build`
2. `mvn -o verify -fae`
3. Grep the surefire reports of the module you care for `Failures: 0, Errors: 0`.
4. If green there → your change is verified, regardless of SWTBot noise elsewhere.
