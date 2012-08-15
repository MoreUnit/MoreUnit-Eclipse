package org.moreunit.test.model;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

public final class Types
{
    private Types()
    {
        // hidden
    }

    public static IType type(String typeName)
    {
        return typeWithPackage(typeName, null);
    }

    public static IType typeWithPackage(String typeName, String packageName)
    {
        IType t = mock(IType.class);

        when(t.getElementName()).thenReturn(typeName);

        IPackageFragment p = mock(IPackageFragment.class);
        when(p.getElementName()).thenReturn(packageName);
        when(t.getPackageFragment()).thenReturn(p);
        when(t.getFullyQualifiedName()).thenReturn((isNullOrEmpty(packageName) ? "" : packageName + ".") + typeName);

        return t;
    }
}
