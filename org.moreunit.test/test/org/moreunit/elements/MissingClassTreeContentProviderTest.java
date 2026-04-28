package org.moreunit.elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

public class MissingClassTreeContentProviderTest
{
    @Test
    public void should_not_throw_exception_but_return_null_when_java_model_exception_occurs() throws JavaModelException
    {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        IPackageFragment mockFragment = mock(IPackageFragment.class);
        when(mockFragment.getCompilationUnits()).thenThrow(new JavaModelException(new RuntimeException("Test exception"), 1));

        Object[] result = provider.getChildren(mockFragment);

        assertThat(result).isNull();
    }

    @Test
    public void testGetParent() {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertThat(provider.getParent(new Object())).isNull();
    }

    @Test
    public void testHasChildren() {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertThat(provider.hasChildren(new Object())).isTrue();
    }

    @Test
    public void testGetChildrenReturnsNullForNonPackageFragment() {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertThat(provider.getChildren(new Object())).isNull();
    }

    @Test
    public void testGetElementsReturnsEmptyArrayForNonMissingTestsViewPart() {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertThat(provider.getElements(new Object())).isEmpty();
    }
}
