package org.moreunit.handler;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.moreunit.core.util.Jobs;
import org.moreunit.elements.ChangedTests;
import org.moreunit.elements.ChangedTestsFinder;
import org.moreunit.launch.TestLauncher;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * Runs the tests which correspond to the files that changed since the last
 * commit, as reported by Git.
 */
public class RunChangedTestsActionExecutor
{
    private static RunChangedTestsActionExecutor instance;

    private final TestLauncher testLauncher;
    private final ChangedFilesProvider changedFilesProvider;
    private final ChangedTestsFinder changedTestsFinder;

    public RunChangedTestsActionExecutor()
    {
        this(new TestLauncher(), new GitChangedFilesProvider(), new ChangedTestsFinder());
    }

    public RunChangedTestsActionExecutor(TestLauncher testLauncher, ChangedFilesProvider changedFilesProvider, ChangedTestsFinder changedTestsFinder)
    {
        this.testLauncher = testLauncher;
        this.changedFilesProvider = changedFilesProvider;
        this.changedTestsFinder = changedTestsFinder;
    }

    public static synchronized RunChangedTestsActionExecutor getInstance()
    {
        if(instance == null)
        {
            instance = new RunChangedTestsActionExecutor();
        }
        return instance;
    }

    /**
     * Finds the tests of the changed files in a background job, then runs
     * them, or tells the user that there is nothing to run.
     */
    public void execute(Collection<IJavaProject> projects, String launchMode)
    {
        if(projects.isEmpty())
        {
            return;
        }

        Jobs.waitForIndexExecuteAndRunInUI("Looking for tests of changed files ...", () -> computeTests(projects), result -> {
            if(result.isEmpty())
            {
                showNothingToRun(result);
            }
            else
            {
                launchTests(result.getTests(), launchMode);
            }
        });
    }

    /**
     * Maps the files changed in the given projects to the tests to run. Can be
     * called from any thread.
     */
    public ChangedTests computeTests(Collection<IJavaProject> projects)
    {
        final Collection<ICompilationUnit> changedFiles = new LinkedHashSet<>();
        for (final IJavaProject project : projects)
        {
            changedFiles.addAll(compilationUnitsOf(changedFilesProvider.changedJavaFiles(project)));
        }
        return changedTestsFinder.findTests(changedFiles);
    }

    /**
     * Runs the given tests, grouping them by test type so that each group can
     * be run by the proper test runner. Must be called from the UI thread.
     */
    public void launchTests(Collection<IType> tests, String launchMode)
    {
        final Map<String, Collection<IType>> testsByType = new LinkedHashMap<>();
        for (final IType test : tests)
        {
            final String testType = Preferences.getInstance().getTestType(test.getJavaProject());
            testsByType.computeIfAbsent(testType, type -> new LinkedHashSet<>()).add(test);
        }

        LogHandler.getInstance().handleInfoLog("Running " + tests.size() + " test class(es) for the changed files");
        for (final Map.Entry<String, Collection<IType>> testsOfType : testsByType.entrySet())
        {
            testLauncher.launch(testsOfType.getKey(), testsOfType.getValue(), launchMode);
        }
    }

    private Collection<ICompilationUnit> compilationUnitsOf(Collection<Path> changedFiles)
    {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final Collection<ICompilationUnit> compilationUnits = new LinkedHashSet<>();

        for (final Path changedFile : changedFiles)
        {
            final IFile file = workspaceRoot.getFileForLocation(org.eclipse.core.runtime.Path.fromOSString(changedFile.toString()));
            if(file == null)
            {
                continue;
            }
            final ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
            if(compilationUnit != null && compilationUnit.exists())
            {
                compilationUnits.add(compilationUnit);
            }
        }
        return compilationUnits;
    }

    private void showNothingToRun(ChangedTests result)
    {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "MoreUnit", nothingToRunMessage(result));
    }

    /**
     * The message shown when the changed files have no test to run.
     */
    public static String nothingToRunMessage(ChangedTests result)
    {
        if(result.getChangedFileCount() == 0)
        {
            return "No changed Java file was found.\n\nMake sure that the projects are in a Git working tree and that Git is installed.";
        }
        return result.getChangedFileCount() + " Java file(s) changed since the last commit, but no corresponding test was found.";
    }
}
