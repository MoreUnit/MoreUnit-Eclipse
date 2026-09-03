package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

/**
 * Tests the remaining {@link MethodTreeContentProvider} contract methods.
 */
@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "Hello", testCls = "HelloTest")
public class MethodTreeContentProviderTest extends ContextTestCase
{
    private MethodTreeContentProvider contentProvider;

    @BeforeEach
    public void createContentProvider()
    {
        final IType cut = context.getPrimaryTypeHandler("Hello").get();
        contentProvider = new MethodTreeContentProvider(cut);
    }

    @Test
    public void getChildren_should_return_null()
    {
        assertNull(contentProvider.getChildren(new Object()));
    }

    @Test
    public void getParent_should_return_null()
    {
        assertNull(contentProvider.getParent(new Object()));
    }

    @Test
    public void hasChildren_should_return_false()
    {
        assertFalse(contentProvider.hasChildren(new Object()));
    }

    @Test
    public void dispose_should_not_fail()
    {
        contentProvider.dispose();
    }

    @Test
    public void getElements_should_return_nothing_when_input_is_not_a_method_page()
    {
        assertTrue(contentProvider.getElements(new Object()).length == 0);
    }
}
