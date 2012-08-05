package org.moreunit.test.workspace;

import static org.fest.assertions.Assertions.assertThat;

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
        assertThat(normalizeSpaces(actualSource)).isEqualTo(normalizeSpaces(expectedSource));
        return this;
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
