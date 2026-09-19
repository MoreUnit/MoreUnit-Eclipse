package org.moreunit.elements;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.log.LogHandler;
import org.moreunit.util.SearchTools;

/**
 * Finds the tests which correspond to a set of changed Java files: a changed
 * test file is run as is, and for a changed class under test, all its
 * corresponding test cases are run.
 */
public class ChangedTestsFinder
{
    public ChangedTests findTests(Collection<ICompilationUnit> changedFiles)
    {
        final Collection<IType> tests = new LinkedHashSet<>();
        final Collection<ICompilationUnit> filesWithoutTests = new LinkedHashSet<>();

        for (final ICompilationUnit changedFile : changedFiles)
        {
            final IType type = changedFile.findPrimaryType();
            if(type == null)
            {
                // e.g. package-info.java, module-info.java
                continue;
            }

            if(TypeFacade.isTestCase(type))
            {
                tests.addAll(runnableTestCasesOf(type));
            }
            else
            {
                final Collection<IType> correspondingTests = new ClassTypeFacade(changedFile).getCorrespondingTestCases();
                if(correspondingTests.isEmpty())
                {
                    filesWithoutTests.add(changedFile);
                }
                for (final IType correspondingTest : correspondingTests)
                {
                    tests.addAll(runnableTestCasesOf(correspondingTest));
                }
            }
        }
        return new ChangedTests(tests, filesWithoutTests, changedFiles.size());
    }

    /**
     * Abstract test cases and test interfaces cannot be run: their concrete
     * subclasses are run instead.
     */
    private Collection<IType> runnableTestCasesOf(IType testCase)
    {
        try
        {
            if(! Flags.isAbstract(testCase.getFlags()) && ! testCase.isInterface())
            {
                return Collections.singleton(testCase);
            }
            return SearchTools.findConcreteSubclasses(testCase);
        }
        catch (final JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
            return Collections.emptyList();
        }
    }
}
