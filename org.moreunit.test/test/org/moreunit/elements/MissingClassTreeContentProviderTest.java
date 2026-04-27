package org.moreunit.elements;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.junit.Test;

public class MissingClassTreeContentProviderTest {

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
