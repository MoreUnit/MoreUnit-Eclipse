package org.moreunit.elements;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

/**
 * The test cases which correspond to a set of changed Java files, and the
 * changed files for which no test was found.
 */
public class ChangedTests
{
    private final Collection<IType> tests;
    private final Collection<ICompilationUnit> filesWithoutTests;
    private final int changedFileCount;

    public ChangedTests(Collection<IType> tests, Collection<ICompilationUnit> filesWithoutTests, int changedFileCount)
    {
        this.tests = new LinkedHashSet<>(tests);
        this.filesWithoutTests = new LinkedHashSet<>(filesWithoutTests);
        this.changedFileCount = changedFileCount;
    }

    /**
     * @return the tests which should be run, once abstract test cases have
     *         been replaced by their concrete subclasses
     */
    public Collection<IType> getTests()
    {
        return tests;
    }

    /**
     * @return the changed files which have no corresponding test
     */
    public Collection<ICompilationUnit> getFilesWithoutTests()
    {
        return filesWithoutTests;
    }

    /**
     * @return the number of changed files which have been inspected
     */
    public int getChangedFileCount()
    {
        return changedFileCount;
    }

    public boolean isEmpty()
    {
        return tests.isEmpty();
    }
}
