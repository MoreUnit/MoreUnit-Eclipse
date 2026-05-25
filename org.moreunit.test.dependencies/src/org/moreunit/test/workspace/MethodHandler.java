package org.moreunit.test.workspace;

import org.eclipse.jdt.core.IMethod;

public class MethodHandler implements ElementHandler<IMethod, MethodAssertions>
{
    private final TypeHandler typeHandler;
    private final IMethod method;

    public MethodHandler(TypeHandler typeHandler, IMethod method)
    {
        this.typeHandler = typeHandler;
        this.method = method;
    }

    @Override
    public IMethod get()
    {
        return method;
    }

    @Override
    public WorkspaceHandler getWorkspaceHandler()
    {
        return typeHandler.getWorkspaceHandler();
    }

    @Override
    public MethodAssertions assertThat()
    {
        return new MethodAssertions(this);
    }
}
