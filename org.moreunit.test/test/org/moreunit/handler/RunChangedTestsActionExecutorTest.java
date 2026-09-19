package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.ChangedTests;
import org.moreunit.elements.ChangedTestsFinder;
import org.moreunit.git.GitSupport;
import org.moreunit.launch.TestLauncher;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;

@org.moreunit.test.context.Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class RunChangedTestsActionExecutorTest extends ContextTestCase
{
    @Test
    public void computeTests_should_find_the_tests_of_changed_files()
    {
        final IJavaProject project = context.getProjectHandler().get();
        final RunChangedTestsActionExecutor executor = executorWith(mock(TestLauncher.class), pathOf(context.getCompilationUnit("com.Foo")));

        final ChangedTests result = executor.computeTests(List.of(project));

        assertEquals(Set.of("com.FooTest"), namesOf(result.getTests()));
        assertEquals(1, result.getChangedFileCount());
    }

    @Test
    public void computeTests_should_return_nothing_when_no_file_changed()
    {
        final IJavaProject project = context.getProjectHandler().get();
        final RunChangedTestsActionExecutor executor = executorWith(mock(TestLauncher.class));

        final ChangedTests result = executor.computeTests(List.of(project));

        assertTrue(result.isEmpty());
        assertEquals(0, result.getChangedFileCount());
    }

    @Test
    public void computeTests_should_ignore_changed_files_outside_of_the_workspace()
    {
        final IJavaProject project = context.getProjectHandler().get();
        final RunChangedTestsActionExecutor executor = executorWith(mock(TestLauncher.class), Path.of("/somewhere/else/Foo.java"));

        final ChangedTests result = executor.computeTests(List.of(project));

        assertTrue(result.isEmpty());
        assertEquals(0, result.getChangedFileCount());
    }

    @Test
    public void launchTests_should_launch_tests_grouped_by_test_type()
    {
        final TestLauncher testLauncher = mock(TestLauncher.class);
        final RunChangedTestsActionExecutor executor = executorWith(testLauncher);
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        final String expectedTestType = Preferences.getInstance().getTestType(context.getProjectHandler().get());

        executor.launchTests(List.of(fooTest), ILaunchManager.RUN_MODE);

        verify(testLauncher).launch(expectedTestType, Set.of(fooTest), ILaunchManager.RUN_MODE);
    }

    @Test
    public void execute_should_launch_the_tests_of_changed_files() throws Exception
    {
        final TestLauncher testLauncher = mock(TestLauncher.class);
        final AtomicReference<Collection< ? extends IJavaElement>> launchedMembers = new AtomicReference<>();
        doAnswer(invocation -> {
            launchedMembers.set(invocation.getArgument(1));
            return null;
        }).when(testLauncher).launch(anyString(), anyCollection(), anyString());

        final RunChangedTestsActionExecutor executor = executorWith(testLauncher, pathOf(context.getCompilationUnit("com.Foo")));

        executor.execute(List.of(context.getProjectHandler().get()), ILaunchManager.RUN_MODE);

        awaitLaunch(launchedMembers);
        assertEquals(Set.of("com.FooTest"), namesOf(launchedMembers.get()));
    }

    @Test
    public void nothingToRunMessage_should_mention_git_when_no_file_was_seen()
    {
        assertTrue(RunChangedTestsActionExecutor.nothingToRunMessage(new ChangedTests(Set.of(), Set.of(), 0)).contains("Git"));
    }

    @Test
    public void nothingToRunMessage_should_mention_the_eclipse_git_implementation_when_jgit_is_missing()
    {
        // JGit is part of the target platform, so the message about its absence
        // is only checked when it is not installed
        assumeFalse(GitSupport.isAvailable(), "JGit is installed in the test runtime");

        assertTrue(RunChangedTestsActionExecutor.nothingToRunMessage(new ChangedTests(Set.of(), Set.of(), 0)).contains("EGit"));
    }

    @Test
    public void nothingToRunMessage_should_mention_the_number_of_changed_files()
    {
        assertTrue(RunChangedTestsActionExecutor.nothingToRunMessage(new ChangedTests(Set.of(), Set.of(), 3)).contains("3 Java file(s)"));
    }

    private RunChangedTestsActionExecutor executorWith(TestLauncher testLauncher, Path... changedFiles)
    {
        return new RunChangedTestsActionExecutor(testLauncher, project -> List.of(changedFiles), new ChangedTestsFinder());
    }
    private static Path pathOf(ICompilationUnit compilationUnit)
    {
        return Path.of(compilationUnit.getResource().getLocation().toOSString());
    }

    private static Set<String> namesOf(Collection< ? extends IJavaElement> elements)
    {
        return elements.stream().map(element -> element instanceof final IType type ? type.getFullyQualifiedName() : element.getElementName()).collect(java.util.stream.Collectors.toSet());
    }

    private static void awaitLaunch(AtomicReference< ? > launchedMembers) throws InterruptedException
    {
        final Display display = Display.getDefault();
        final long deadline = System.currentTimeMillis() + 30_000;
        while(launchedMembers.get() == null && System.currentTimeMillis() < deadline)
        {
            display.readAndDispatch();
            Thread.sleep(10);
        }
        assertNotNull(launchedMembers.get(), "TestLauncher.launch was not called");
    }
}
