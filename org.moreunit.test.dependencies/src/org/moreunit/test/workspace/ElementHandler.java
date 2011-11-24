package org.moreunit.test.workspace;

import org.eclipse.jdt.core.IJavaElement;

public interface ElementHandler<ElementType extends IJavaElement, AssertionsType>
{
    ElementType get();

    WorkspaceHandler getWorkspaceHandler();

    AssertionsType assertThat();
}
