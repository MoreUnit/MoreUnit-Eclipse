package org.moreunit.test.workspace;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class TypeHandler implements ElementHandler<IType, TypeAssertions>
{
    private final CompilationUnitHandler cuHandler;
    private final IType type;

    public TypeHandler(CompilationUnitHandler cuHandler, IType type)
    {
        this.cuHandler = cuHandler;
        this.type = type;
    }

    public IType get()
    {
        return type;
    }

    public MethodHandler addMethod(String signature)
    {
        return addMethod(signature, "");
    }

    public MethodHandler addMethod(String signature, String content)
    {
        try
        {
            return new MethodHandler(this, WorkspaceHelper.createMethodInJavaType(type, signature, content));
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ICompilationUnit getCompilationUnit()
    {
        return cuHandler.get();
    }

    public CompilationUnitHandler getCompilationUnitHandler()
    {
        return cuHandler;
    }

    public WorkspaceHandler getWorkspaceHandler()
    {
        return cuHandler.getWorkspaceHandler();
    }

    public TypeAssertions assertThat()
    {
        return new TypeAssertions(this);
    }

    public TypeHandler createSubclass(String fullyQualifiedSubclassName)
    {
        return cuHandler.getSourceFolderHandler().extendClass(this, fullyQualifiedSubclassName);
    }

    public String getName()
    {
        return type.getFullyQualifiedName();
    }
}
