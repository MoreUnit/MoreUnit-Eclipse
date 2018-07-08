package org.moreunit.test.workspace;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

public class CompilationUnitAssertions
{
    private final CompilationUnitHandler cuHandler;

    public CompilationUnitAssertions(CompilationUnitHandler compilationUnitHandler)
    {
        this.cuHandler = compilationUnitHandler;
    }

    public final CompilationUnitAssertions hasSameSourceAsIn(String expectedSourceFile)
    {
        SourceFolderHandler srcFolderHandler = cuHandler.getSourceFolderHandler();
        String actualSource = cuHandler.getActualSource();
        String expectedSource = getSource(srcFolderHandler, expectedSourceFile);
        System.out.println("Actual: " + actualSource);
        System.out.println("Expected: " + expectedSource);
        assertThat(normalizeSpaces(ignoreJdkDependentImports(actualSource))).isEqualTo(normalizeSpaces(expectedSource));
        return this;
    }

    private String ignoreJdkDependentImports(String source)
    {
        // Some classnames are not unique within some JVMs while unique in case of others.
        // For example, there is a single class named Callable in Java 8, but there are two in Java 10.
        // Save actions will not add imports for ambiguous classes, therefore to avoid making the test
        // JDK dependent, we should ignore them during th compare
        return source.replaceAll("^import java\\.util\\.concurrent\\.Callable;$", "");
    }

    protected String getSource(SourceFolderHandler srcFolderHandler, String expectedSourceFile)
    {
        return srcFolderHandler.createSource(expectedSourceFile).getSource();
    }

    private String normalizeSpaces(String source)
    {
        return source.replaceAll("\t+", "").replaceAll("[ ]+", " ").replaceAll("[\r\n]+", System.getProperty("line.separator")).trim();
    }

    public CompilationUnitAssertions hasPrimaryType(IType expectedType)
    {
        cuHandler.getPrimaryTypeHandler().assertThat().isEqualTo(expectedType);
        return this;
    }

    public CompilationUnitAssertions isEqualTo(ICompilationUnit expectedCompilationUnit)
    {
        assertThat(cuHandler.get()).isEqualTo(expectedCompilationUnit);
        return this;
    }

    public CompilationUnitAssertions isInSourceFolder(String sourceFolderName)
    {
        assertThat(cuHandler.getSourceFolderHandler().getName()).isEqualTo(sourceFolderName);
        return this;
    }

    public CompilationUnitAssertions isInProject(ProjectHandler project)
    {
        assertThat(cuHandler.getSourceFolderHandler().getProjectHandler()).isEqualTo(project);
        return this;
    }
}
