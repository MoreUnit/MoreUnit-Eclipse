package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.moreunit.log.LogHandler;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.ui.MissingTestsViewPart;
import org.moreunit.util.PluginTools;

@Context(SimpleJUnit3Project.class)
public class MissingClassTreeContentProviderTest extends ContextTestCase
{
    @Test
    public void should_not_throw_exception_but_return_null_when_java_model_exception_occurs() throws JavaModelException
    {
        final MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        final IPackageFragment mockFragment = mock(IPackageFragment.class);
        when(mockFragment.getCompilationUnits()).thenThrow(new JavaModelException(new RuntimeException("Test exception"), 1));

        Object[] result = null;
        try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class)) {
            final LogHandler mockHandler = mock(LogHandler.class);
            logHandlerMock.when(LogHandler::getInstance).thenReturn(mockHandler);
            result = provider.getChildren(mockFragment);
        }

        assertNull(result);
    }

    @Test
    public void getChildren_should_return_null_when_parent_is_not_a_package_fragment()
    {
        assertNull(new MissingClassTreeContentProvider().getChildren(new Object()));
    }

    @Test
    public void getChildren_should_return_compilation_units_of_classes_without_test_case() throws Exception
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Isolated");

        final IPackageFragment orgPackage = getPackageFragment("org");

        final Object[] children = new MissingClassTreeContentProvider().getChildren(orgPackage);

        final List<String> names = Arrays.stream(children).map(Object::toString).collect(Collectors.toList());
        assertEquals(1, children.length);
        assertTrue(names.get(0).startsWith("Isolated.java"));
    }

    @Test
    public void getElements_should_return_packages_containing_classes_without_test_case() throws Exception
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Isolated");

        final MissingTestsViewPart viewPart = mock(MissingTestsViewPart.class);
        when(viewPart.getSelectedJavaProject()).thenReturn(context.getProjectHandler().get());

        final Object[] elements = new MissingClassTreeContentProvider().getElements(viewPart);

        assertEquals(1, elements.length);
        assertEquals("org", ((IPackageFragment) elements[0]).getElementName());
    }

    @Test
    public void getElements_should_return_no_element_when_no_project_is_selected() throws Exception
    {
        final MissingTestsViewPart viewPart = mock(MissingTestsViewPart.class);
        when(viewPart.getSelectedJavaProject()).thenReturn(null);

        assertEquals(0, new MissingClassTreeContentProvider().getElements(viewPart).length);
    }

    @Test
    public void getElements_should_return_no_element_when_input_is_not_the_missing_tests_view()
    {
        assertEquals(0, new MissingClassTreeContentProvider().getElements(new Object()).length);
    }

    @Test
    public void getParent_should_return_package_of_compilation_unit_and_null_otherwise() throws Exception
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Isolated");

        final IPackageFragment orgPackage = getPackageFragment("org");
        final ICompilationUnit unit = orgPackage.getCompilationUnit("Isolated.java");

        final MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertEquals(orgPackage, provider.getParent(unit));
        assertNull(provider.getParent(orgPackage));
        assertNull(provider.getParent(new Object()));
    }

    @Test
    public void hasChildren_should_return_true_for_anything_but_compilation_units()
    {
        final MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        assertTrue(provider.hasChildren(new Object()));
        assertFalse(provider.hasChildren(mock(ICompilationUnit.class)));
    }

    @Test
    public void dispose_and_inputChanged_should_do_nothing()
    {
        final MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        provider.dispose();
        provider.inputChanged(null, null, null);
    }

    private IPackageFragment getPackageFragment(String packageName) throws JavaModelException
    {
        final IJavaProject project = context.getProjectHandler().get();
        for (final IPackageFragmentRoot root : PluginTools.getAllSourceFolderFromProject(project))
        {
            final IPackageFragment packageFragment = root.getPackageFragment(packageName);
            if(packageFragment.exists())
                return packageFragment;
        }
        throw new AssertionError("package not found: " + packageName);
    }
}
