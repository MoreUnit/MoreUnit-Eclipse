package org.moreunit.test.workspace;

import static org.moreunit.core.util.Preconditions.checkState;

import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

public class CompilationUnitHandler implements ElementHandler<ICompilationUnit, CompilationUnitAssertions>
{
    static final Pattern PACKAGE_PATTERN = Pattern.compile(".*package\\s+([^;]+);.*", Pattern.DOTALL);
    static final Pattern CLASSNAME_PATTERN = Pattern.compile(".*public\\s+class\\s+(\\w+).*", Pattern.DOTALL);

    private final Source source;
    private final ICompilationUnit compilationUnit;
    private final SourceFolderHandler sourceFolderHandler;

    public CompilationUnitHandler(ICompilationUnit compilationUnit, Source source)
    {
        this.compilationUnit = compilationUnit;
        this.source = source;
        this.sourceFolderHandler = source.getSourceFolderHandler();
    }

    public CompilationUnitHandler(SourceFolderHandler sourceFolderHandler, ICompilationUnit compilationUnit)
    {
        this.compilationUnit = compilationUnit;
        this.source = null;
        this.sourceFolderHandler = sourceFolderHandler;
    }

    public String getInitialSource()
    {
        checkState(source != null, "This compilation was not initially created by your test, therefore it has no initial source.");
        return source.getSource();
    }

    public ICompilationUnit get()
    {
        return compilationUnit;
    }

    public WorkspaceHandler getWorkspaceHandler()
    {
        return sourceFolderHandler.getWorkspaceHandler();
    }

    public String getName()
    {
        return compilationUnit.findPrimaryType().getFullyQualifiedName();
    }

    public String getActualSource()
    {
        try
        {
            return compilationUnit.getSource();
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException("Could not load source of " + compilationUnit.getElementName(), e);
        }
    }

    public SourceFolderHandler getSourceFolderHandler()
    {
        return sourceFolderHandler;
    }

    public TypeHandler getPrimaryTypeHandler()
    {
        return new TypeHandler(this, compilationUnit.findPrimaryType());
    }

    public CompilationUnitAssertions assertThat()
    {
        return new CompilationUnitAssertions(this);
    }
}
