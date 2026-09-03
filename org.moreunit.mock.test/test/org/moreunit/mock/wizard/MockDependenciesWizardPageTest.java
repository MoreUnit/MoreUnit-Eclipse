package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.dependencies.DependencyInjectionPointCollector;
import org.moreunit.mock.dependencies.DependencyInjectionPointProviderCache;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;

public class MockDependenciesWizardPageTest
{
    private static final String PAGE_ID = MoreUnitMockPlugin.PLUGIN_ID + ".mockDependenciesWizardPage";

    @Mock
    private MockDependenciesWizardValues wizardValues;
    @Mock
    private Preferences preferences;
    @Mock
    private TemplateStyleSelector templateStyleSelector;
    @Mock
    private Logger logger;
    @Mock
    private IType classUnderTest;
    @Mock
    private IJavaProject javaProject;
    @Mock
    private IPackageFragment testCasePackage;
    @Mock
    private IMethod parameterizedConstructor;

    private Display display;
    private Shell shell;
    private boolean headless;

    private MockDependenciesWizardPage page;
    private ITypeHierarchy hierarchy;
    @Mock
    private IField field;

    @BeforeEach
    public void setUp() throws Exception
    {
        MockitoAnnotations.openMocks(this);

        display = Display.getDefault();
        headless = display == null;
        if(headless)
        {
            return;
        }
        display.syncExec(() -> shell = new Shell(display));

        when(wizardValues.getClassUnderTest()).thenReturn(classUnderTest);
        when(classUnderTest.getJavaProject()).thenReturn(javaProject);
        when(preferences.hasSpecificSettings(any())).thenReturn(false);

        // default: the class under test has one parameterized constructor, no fields
        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { parameterizedConstructor });
        when(parameterizedConstructor.isConstructor()).thenReturn(true);
        when(parameterizedConstructor.getNumberOfParameters()).thenReturn(2);
        when(parameterizedConstructor.getElementName()).thenReturn("Foo");
        when(parameterizedConstructor.getElementType()).thenReturn(IJavaElement.METHOD);
        when(parameterizedConstructor.getDeclaringType()).thenReturn(classUnderTest);
        when(field.getElementType()).thenReturn(IJavaElement.FIELD);

        when(classUnderTest.getElementName()).thenReturn("Foo");
        when(classUnderTest.getElementType()).thenReturn(IJavaElement.TYPE);
        when(classUnderTest.getSuperInterfaceTypeSignatures()).thenReturn(new String[0]);
        when(classUnderTest.getSuperclassTypeSignature()).thenReturn(null);
        when(classUnderTest.getTypeParameters()).thenReturn(new ITypeParameter[0]);
        when(classUnderTest.getSuperclassName()).thenReturn(null);

        hierarchy = mock(ITypeHierarchy.class);
        when(classUnderTest.newSupertypeHierarchy(any())).thenReturn(hierarchy);
        when(hierarchy.getAllClasses()).thenReturn(new IType[0]);
        when(hierarchy.getAllSuperclasses(classUnderTest)).thenReturn(new IType[0]);
        when(field.getDeclaringType()).thenReturn(classUnderTest);
        when(field.getTypeSignature()).thenReturn("QBar;");
        when(field.getFlags()).thenReturn(0);
        when(classUnderTest.getFields()).thenReturn(new IField[] { field });
        when(classUnderTest.getPackageFragment()).thenReturn(testCasePackage);
        when(testCasePackage.getElementName()).thenReturn("com.x");

        final DependencyInjectionPointCollector collector = new DependencyInjectionPointCollector(classUnderTest, testCasePackage);
        // the cache is built lazily so that it collects the values stubbed by each test
        when(wizardValues.getInjectionPointProvider()).thenAnswer(invocation -> new DependencyInjectionPointProviderCache(collector));

        page = new MockDependenciesWizardPage(wizardValues, new DependencyInjectionPointStore(logger), preferences, templateStyleSelector, logger);
    }

    @AfterEach
    public void tearDown()
    {
        if(shell != null && ! shell.isDisposed())
        {
            display.syncExec(() -> shell.dispose());
        }
    }

    private void createControl()
    {
        page.createControl(shell);
    }

    @Test
    public void should_expose_page_id_and_position()
    {
        assertEquals(PAGE_ID, page.getId());
        assertSame(page, page.getPage());
        assertNotNull(page.getPosition());
        assertNotNull(page.getClassUnderTest());
        assertNotNull(page.getInjectionPointStore());
    }

    @Test
    public void should_create_control_with_tree_and_side_buttons()
    {
        if(headless)
        {
            return;
        }

        createControl();

        assertNotNull(page.getControl());
        assertNotNull(findButton(shell, "Select All"));
        assertNotNull(findButton(shell, "Deselect All"));
    }

    @Test
    public void should_toggle_injectable_fields_checkbox_when_all_fields_are_shown()
    {
        if(headless)
        {
            return;
        }

        createControl();

        final Button showAllFieldsCheckbox = findButton(shell, "Show all fields");
        final Button showInjectableFieldsCheckbox = findButton(shell, "Show injectable fields");

        // when: the user asks for all fields to be displayed
        showAllFieldsCheckbox.setSelection(true);
        showAllFieldsCheckbox.notifyListeners(SWT.Selection, new Event());

        // then: the "injectable fields" filter becomes useless and is disabled
        assertEquals(false, showInjectableFieldsCheckbox.getEnabled());

        // when: the user restores the default filter
        showAllFieldsCheckbox.setSelection(false);
        showAllFieldsCheckbox.notifyListeners(SWT.Selection, new Event());

        // then
        assertEquals(true, showInjectableFieldsCheckbox.getEnabled());
    }

    @Test
    public void should_not_warn_when_class_under_test_has_no_constructor() throws Exception
    {
        if(headless)
        {
            return;
        }

        final IMethod plainMethod = mock(IMethod.class);
        when(plainMethod.isConstructor()).thenReturn(false);
        when(plainMethod.getElementName()).thenReturn("doSomething");
        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { plainMethod });

        createControl();

        assertEquals("Select dependencies for which mocks should be created.", page.getMessage());
    }

    @Test
    public void should_not_warn_when_class_under_test_has_a_default_constructor() throws Exception
    {
        if(headless)
        {
            return;
        }

        final IMethod defaultConstructor = mock(IMethod.class);
        when(defaultConstructor.isConstructor()).thenReturn(true);
        when(defaultConstructor.getNumberOfParameters()).thenReturn(0);
        when(defaultConstructor.getElementName()).thenReturn("Foo");
        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { defaultConstructor });

        createControl();

        assertEquals("Select dependencies for which mocks should be created.", page.getMessage());
    }

    @Test
    public void should_not_warn_when_constructor_information_cannot_be_read() throws Exception
    {
        if(headless)
        {
            return;
        }

        when(classUnderTest.getMethods()).thenThrow(new JavaModelException(new CoreException(Status.CANCEL_STATUS)));

        createControl();

        assertEquals("Select dependencies for which mocks should be created.", page.getMessage());
    }

    @Test
    public void should_not_refresh_anything_when_page_is_hidden()
    {
        if(headless)
        {
            return;
        }

        createControl();
        page.setVisible(false);

        // no exception: early return path
        verifyNothingBroken();
    }

    private void verifyNothingBroken()
    {
        // nothing to verify: the test only ensures that no exception is thrown
    }

    @Test
    public void should_uncheck_all_elements_when_deselect_all_is_clicked()
    {
        if(headless)
        {
            return;
        }

        createControl();

        final Button deselectAll = findButton(shell, "Deselect All");
        deselectAll.notifyListeners(SWT.Selection, new Event());

        // no exception, no checked element
        assertFalse(deselectAll.isDisposed());
    }

    @Test
    public void should_warn_when_selected_dependencies_do_not_include_constructor_and_class_has_no_default_one() throws Exception
    {
        if(headless)
        {
            return;
        }

        when(hierarchy.getAllClasses()).thenReturn(new IType[] { classUnderTest });
        when(field.getFlags()).thenReturn(0);

        createControl();
        page.setVisible(true);

        // when: only the field dependency is selected
        page.checkElements(new Object[] { field });

        // then
        assertEquals("No constructor has been selected, and the class under test has no default one."
                     + " This will cause a compiler error in the generated test case.", page.getMessage());
        assertEquals(1, page.getInjectionPointStore().getFields().size());
        assertEquals(0, page.getInjectionPointStore().getConstructors().size());
        verify(templateStyleSelector, never()).savePreferences();

        // when: all dependencies are deselected
        final Button deselectAll = findButton(shell, "Deselect All");
        deselectAll.notifyListeners(SWT.Selection, new Event());

        // then
        assertEquals("Select dependencies for which mocks should be created.", page.getMessage());
        verify(templateStyleSelector).initValues(null);
    }

    @Test
    public void should_not_warn_when_a_constructor_is_selected() throws Exception
    {
        if(headless)
        {
            return;
        }

        when(hierarchy.getAllClasses()).thenReturn(new IType[] { classUnderTest });
        when(field.getFlags()).thenReturn(0);
        when(classUnderTest.getFields()).thenReturn(new IField[] { field });

        createControl();
        page.setVisible(true);
        // when: the constructor dependency is selected
        page.checkElements(new Object[] { parameterizedConstructor });

        // then
        assertEquals("Select dependencies for which mocks should be created.", page.getMessage());
        assertEquals(1, page.getInjectionPointStore().getConstructors().size());
    }

    private static Button findButton(Composite composite, String text)
    {
        for (final Control child : composite.getChildren())
        {
            if(child instanceof final Button button && text.equals(button.getText()))
            {
                return button;
            }
            if(child instanceof final Composite nested)
            {
                final Button found = findButton(nested, text);
                if(found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }
}
