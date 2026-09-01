---
name: moreunit-test-patterns
description: How to write and locate tests in the MoreUnit-Eclipse project. Covers unit test structure, SWTBot UI tests, test doubles, JaCoCo coverage analysis, and common patterns. Use when the user asks to write tests, find what's untested, increase coverage, or understand the test infrastructure.
---

# MoreUnit-Eclipse Test Patterns

## Test module layout

Each main module has a corresponding test module. Test source folders vary:

| Module | Test source folders | Notes |
|---|---|---|
| `org.moreunit.core.test` | `test/` (tests) + `src/` (test infra: InMemoryWorkspace, etc.) + `classes/` (fixture classes) | Fragment of `org.moreunit.core` |
| `org.moreunit.test` | `test/` (all tests) | Fragment of `org.moreunit` (plugin) |
| `org.moreunit.mock.test` | `test/` (all tests) | Fragment of `org.moreunit.mock` |
| `org.moreunit.swtbot.test` | `test/` (all SWTBot tests) | Needs display |

## Test framework & libraries

All test modules use:
- **JUnit 5** (`org.junit.jupiter.api`, `org.junit.jupiter.engine`)
- **Mockito** (`org.mockito.mockito-core`, `org.mockito.junit-jupiter`)
- Tests use static imports: `assert*` from `org.junit.jupiter.api.Assertions`, `mock`/`when`/`verify` from `org.mockito.Mockito`

### Naming convention

Test classes follow `{ClassName}Test.java`. Test method names follow `should_<description>` or `should_<action>_<condition>` style.

## Unit test patterns (core.test)

### Test doubles for resource classes

The `org.moreunit.core.resources` package has a complete **in-memory implementation** used as test doubles:

| Double | Implements | Purpose |
|---|---|---|
| `InMemoryWorkspace` | `Workspace`, `ResourceContainer` | Full workspace with projects, folders, files |
| `InMemoryProject` | `Project`, `InMemoryResourceContainer` | Java-like project |
| `InMemoryFolder` | `Folder`, `InMemoryResourceContainer` | Folder within a project |
| `InMemoryFile` | `File`, `InMemoryResource` | File with content |
| `InMemoryPath` | `Path` (`Iterable<String>`) | Path parsing and manipulation |
| `InMemoryResource` | `Resource` | Base resource |
| `InMemoryResourceContainer` | `ResourceContainer` | Container for children |

These are in `org.moreunit.core.test/src/org/moreunit/core/resources/`. Usage example:

```java
InMemoryWorkspace workspace = new InMemoryWorkspace();
Path path = workspace.path("/proj/src/foo");
InMemoryFolder folder = workspace.getFolder("/proj/src");
```

### Mocking Eclipse interfaces

For "pure logic + mockable Eclipse interfaces" pattern (most common for core/modifable classes):

```java
// Mock an interface like IFile, IResource, IPreferenceStore, IMethod, etc.
IFile file = mock(IFile.class);
when(file.toString()).thenReturn("path/to/file");

// Verify interactions
verify(file).delete(true, null);
verify(parent, never()).delete(anyBoolean(), any());
```

### Mocking via InOrder

For verifying call order (e.g., `ContainerCreation`):

```java
InOrder order = inOrder(parent, container);
order.verify(parent).create();
order.verify(container).create();
```

## SWTBot test patterns

### SWTBot performance rules (CI cost, measured 2026-09)

SWTBot tests dominate CI time (~10 min of a ~15 min build before optimization). Key facts:

- `TestContextRule` creates **a full Java project per test method** (`beforeEach` → `initWorkspace`, `afterEach` → `clearWorkspace`): every `@Test` method pays project creation + JDT build. Fewer test methods = less overhead.
- Opening the Eclipse **Preferences** dialog costs ~19 s per opening on Windows CI (it loads every preference page of the IDE). The project-scoped **Properties** dialog is ~1 s.
- Therefore: **group preference changes into as few dialog sessions as possible** (see `PreferencesTest`: one session per theme of assertions, radios kept separate since each radio change needs save+reopen). Never write one test = one dialog opening.
- On Eclipse 4.x Windows, preference-page validation messages are often **not discoverable SWT widgets** — validation checks must be *best-effort* (search `label` then `clabel`, catch, never fail the test) with short timeouts (5 s), because a full timeout is pure waste when the message isn't rendered.
- Full-module SWTBot runs are unstable (workbench can die mid-run); when diagnosing, run single classes (`-Dtest=...`).

### Base class
All SWTBot tests extend `JavaProjectSWTBotTestHelper` (in `org.moreunit.swtbot.test/test/org/moreunit/`), which provides:
- `bot` (static `SWTWorkbenchBot`) — the SWTBot API entry point
- `context` (`@RegisterExtension TestContextRule`) — manages the workspace
- `openResource("SomeClass.java")` — opens a file in the editor via "Navigate > Open Resource..."
- `getShortcutStrategy()` — returns platform-specific shortcut strategy
- `testSimpleJump(original, target)` — common jump test helper
- `selectAndReturnJavaProjectFromPackageExplorer()` — selects project in Package Explorer
- Automatic editor cleanup between tests (`@BeforeEach`/`@AfterEach` closes all editors)

### Project setup via annotations

SWTBot tests use the `@Context` or `@Project` annotations to define the workspace.

**Using a pre-configured project class:**
```java
@ExtendWith(SWTBotJunit5Extension.class)
public class MyTest extends JavaProjectSWTBotTestHelper {
    @Test
    @Context(SimpleJUnit4Project.class)
    public void my_test() {
        openResource("SomeClass.java");
        getShortcutStrategy().pressJumpShortcut();
        // ...
    }
}
```

**Inline project definition:**
```java
@Test
@Project(
    mainCls = "org:SomeClass",
    testCls = "org:SomeClassTest",
    properties = @Properties(
        testType = TestType.JUNIT4,
        testClassNameTemplate = "${srcFile}Test"))
public void my_test() { ... }
```

**From source files (resources):**
```java
@Test
@Project(
    mainSrc = "MyClass_with_method.txt",
    testSrc = "MyClassTest_with_testmethod.txt",
    properties = @Properties(...))
public void my_test() { ... }
```

The `.txt` files are resolved as resources relative to the test class's package.

### Available `@Context` project configs

All in `org.moreunit.test.dependencies/src/org/moreunit/test/context/configs/`:

| Config class | Test type | Source/test classes |
|---|---|---|
| `SimpleJUnit3Project` | JUnit 3 | SomeClass / SomeClassTest |
| `SimpleJUnit4Project` | JUnit 4 | SomeClass / SomeClassTest |
| `SimpleJUnit5Project` | JUnit 5 | SomeClass / SomeClassTest |
| `SimpleTestNGProject` | TestNG | SomeClass / SomeClassTest |
| `SimpleSpockProject` | Spock | SomeClass / SomeClassSpec |
| `SimpleMavenJUnit4Project` | JUnit 4 + Maven structure | SomeClass / SomeClassTest |

### Key keyboard shortcuts

Defined in `ShortcutStrategy` and `plugin.xml`:

| Action | Shortcut | Method |
|---|---|---|
| Jump to Test / Test Subject | `Ctrl+Shift+J` | `pressJumpShortcut()` |
| Generate Test / Create Test | `Ctrl+U` | `pressGenerateShortcut()` |
| Run Test | `Ctrl+R` | (via `KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL, 'r')`) |
| Debug Test | `Ctrl+Alt+D` | (not in helper) |

### Launch-based assertion (Run/Debug test)

To assert a test was actually launched, check the LaunchManager directly. Requires `org.eclipse.debug.core` in `Require-Bundle` of `swtbot.test/META-INF/MANIFEST.MF`:

```java
int launchesBefore = DebugPlugin.getDefault().getLaunchManager().getLaunches().length;
KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL, 'r');
bot.waitUntil(new DefaultCondition() {
    @Override
    public boolean test() {
        return DebugPlugin.getDefault().getLaunchManager().getLaunches().length > launchesBefore;
    }
    // ...
});
```

## Coverage analysis (finding what's untested)

### Quick: parse the JaCoCo CSV

After a coverage run with `-Pcoverage`:

```bash
cd org.moreunit.build/target/site/jacoco-aggregate

# Completely untested classes (instr covered = 0), sorted by impact
awk -F, 'NR>1 && ($1 ~ /\/org\.moreunit\.core$/ || $1 ~ /\/org\.moreunit\.mock$/ || $1 == "org.moreunit.report/org.moreunit") && $5==0 {print $4"\t"$2"."$3}' jacoco.csv | sort -rn

# Classes with any coverage, sorted by % covered (ascending, worst first)
awk -F, 'NR>1 && ($1 ~ /\/org\.moreunit\.core$/ || $1 ~ /\/org\.moreunit\.mock$/ || $1 == "org.moreunit.report/org.moreunit") && ($4+$5)>0 {pct=$5/($4+$5)*100; printf "%5.1f%%\t%s.%s\n", pct, $2, $3}' jacoco.csv | sort -n
```

The GROUP column format is `org.moreunit.report/<bundle-symbolic-name>`.

Main bundles (not tests): `org.moreunit.report/org.moreunit.core`, `org.moreunit.report/org.moreunit`, `org.moreunit.report/org.moreunit.mock`.

### Verify a specific test file is genuinely new

Before writing a new test, check the file does NOT already exist on master:

```bash
git cat-file -e HEAD:"org.moreunit.core.test/test/org/moreunit/core/my/MyClassTest.java" 2>/dev/null && echo "EXISTS" || echo "NEW"
```

Or check via `git ls-files`:

```bash
git ls-files '*/test/*MyClassTest.java'
```

## Access restrictions (when your test won't compile)

Test bundles may not import packages used by the production code. Common restrictions:

| Restricted API | Bundle | In which test module | Fix |
|---|---|---|---|
| `org.eclipse.jface.text.Position` (type + fields) | `org.eclipse.text` | `org.moreunit.test` | Avoid referencing Position; or add `org.eclipse.text` to Require-Bundle |
| `org.eclipse.debug.core.DebugPlugin` | `org.eclipse.debug.core` | `org.moreunit.swtbot.test` | Add `org.eclipse.debug.core` to Require-Bundle |
| `org.eclipse.debug.core.ILaunch` / `ILaunchManager` | `org.eclipse.debug.core` | `org.moreunit.swtbot.test` | Same as above |

## Adding a new dependency to a test bundle

Edit the `Require-Bundle` entry in the test module's `META-INF/MANIFEST.MF`. Add on a new line (comma-terminated):

```
Require-Bundle: ...,
 org.eclipse.debug.core,
```
