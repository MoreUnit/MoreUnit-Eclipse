package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne.JUnitVersion;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.moreunit.elements.LanguageType;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link MoreUnitWizardPageOne} with real SWT widgets.
 */
@Context(SimpleJUnit4Project.class)
public class MoreUnitWizardPageOneTest extends SwtPageTestCase
{
    private MoreUnitWizardPageOne page;
    private IType cutType;

    @BeforeEach
    public void createPage()
    {
        cutType = context.getPrimaryTypeHandler("org.SomeClass").get();
        page = newPage(LanguageType.JAVA);
    }

    private MoreUnitWizardPageOne newPage(LanguageType langType)
    {
        return new MoreUnitWizardPageOne(new NewTestCaseWizardPageTwo(), cutType.getJavaProject(), Preferences.forProject(cutType.getJavaProject()), langType);
    }

    private void createPageControl()
    {
        page.createControl(shell);
    }

    @Test
    public void should_create_all_controls()
    {
        createPageControl();

        assertNotNull(page.getControl());
        assertTrue(page.getControl() instanceof Composite);
        assertTrue(allWidgets((Composite) page.getControl()).size() > 10);

        ((Control) page.getControl()).setFocus();
    }

    @Test
    public void should_default_to_the_test_type_of_the_project_preferences()
    {
        createPageControl();

        assertTrue(page.isJUnit4());
        assertFalse(page.isJUnit5());
        assertEquals(TestType.JUNIT_4, page.getTestType());
    }

    @Test
    public void should_keep_public_modifier_for_junit4()
    {
        createPageControl();

        assertEquals(org.eclipse.jdt.core.Flags.AccPublic, page.getModifiers() & org.eclipse.jdt.core.Flags.AccPublic);
    }

    @Test
    public void should_use_java_file_suffix_by_default_and_groovy_suffix_for_groovy()
    {
        assertEquals("SomeClass.java", invoke(page, "getCompilationUnitName", "SomeClass"));

        final MoreUnitWizardPageOne groovyPage = newPage(LanguageType.GROOVY);
        assertEquals("SomeClass.groovy", invoke(groovyPage, "getCompilationUnitName", "SomeClass"));
    }

    @Test
    public void should_resolve_class_under_test_from_selection()
    {
        page.init(new StructuredSelection(cutType));

        assertEquals("org.SomeClass", page.getClassUnderTestText());
        assertEquals(cutType, page.getClassUnderTest());

        // no wizard attached: cannot flip
        assertFalse(page.canFlipToNextPage());
    }

    @Test
    public void should_not_resolve_unknown_class_under_test()
    {
        page.init(new StructuredSelection(cutType));

        page.setClassUnderTest("does.not.Exist");

        assertNull(page.getClassUnderTest());
        assertEquals("does.not.Exist", page.getClassUnderTestText());
    }

    @Test
    public void should_not_resolve_interface_as_class_under_test_but_warn()
    {
        page.init(new StructuredSelection(cutType));

        page.setClassUnderTest("java.io.Serializable");

        // interfaces are accepted with a warning, so the type is kept
        assertNotNull(page.getClassUnderTest());
    }

    @Test
    public void should_enable_class_under_test_button_only_when_a_container_is_set()
    {
        createPageControl();

        invoke(page, "handleFieldChanged", getContainerFieldId());

        final Button classUnderTestButton = (Button) getField(page, "fClassUnderTestButton");
        assertNotNull(classUnderTestButton);
        final Object root = invoke(page, "getPackageFragmentRoot");
        assertEquals(root != null, classUnderTestButton.isEnabled(), "root=" + root + " enabled=" + classUnderTestButton.isEnabled());
    }

    private String getContainerFieldId()
    {
        // the id of the container field of NewContainerWizardPage (the
        // constant itself is protected and the type is not exported)
        return "NewContainerWizardPage.container";
    }

    @Test
    public void should_enable_class_under_test_button_when_a_container_is_set()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        final Button classUnderTestButton = (Button) getField(page, "fClassUnderTestButton");
        assertNotNull(classUnderTestButton);
        assertTrue(classUnderTestButton.isEnabled());
    }

    @Test
    public void should_keep_test_type_and_package_when_page_becomes_invisible()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        page.setVisible(false);

        assertEquals(TestType.JUNIT_4, page.getTestType());
        assertNotNull(page.getTestCasePackage());
    }

    @Test
    public void should_expose_all_statuses_for_validation()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        final Object[] statuses = (Object[]) invoke(page, "getStatusList");

        assertEquals(7, statuses.length);
    }

    @Test
    public void should_select_junit4_explicitly()
    {
        createPageControl();

        page.setJUnit4Selection();

        assertTrue(page.isJUnit4());
        assertFalse(page.isJUnit5());
        assertEquals(TestType.JUNIT_4, page.getTestType());

        final Button unit4Toggle = (Button) getField(page, "unit4Toggle");
        assertTrue(unit4Toggle.getSelection());
    }

    @Test
    public void should_return_package_fragment_root_of_selection()
    {
        page.init(new StructuredSelection(cutType));

        final Object root = invoke(page, "getPackageFragmentRoot");

        assertNotNull(root);
        assertEquals(cutType.getPackageFragment().getParent(), root);
    }

    private void selectOnly(String toggleFieldName)
    {
        for (final String name : new String[] { "junit3Toggle", "unit4Toggle", "unit5Toggle", "spockToggle", "testNgToggle" })
        {
            ((Button) getField(page, name)).setSelection(name.equals(toggleFieldName));
        }
    }

    private void attachRealWizard()
    {
        page.setWizard(new NewTestCaseWizard(cutType));
    }

    private IType mockNewType()
    {
        final IType type = mock(IType.class);
        when(type.exists()).thenReturn(false);
        when(type.getJavaProject()).thenReturn(cutType.getJavaProject());
        when(type.getCompilationUnit()).thenReturn(cutType.getCompilationUnit());
        when(type.getElementName()).thenReturn("SomeClassTest");
        return type;
    }

    private Object mockImportsManager() throws Exception
    {
        // NewTypeWizardPage.ImportsManager is internal JDT API whose package
        // this test bundle does not wire, so it cannot be named statically
        // (access restriction) nor loaded by name. Load it through the
        // production bundle instead, then mock and stub reflectively; Mockito
        // still records every invocation for assertImportsMethodInvoked.
        final Class<?> importsManagerClass = MoreUnitWizardPageOne.class.getClassLoader()
                .loadClass("org.eclipse.jdt.ui.wizards.NewTypeWizardPage$ImportsManager");
        final Object imports = mock(importsManagerClass);
        final java.lang.reflect.Method addImport = importsManagerClass.getMethod("addImport", String.class);
        when(addImport.invoke(imports, anyString())).thenAnswer(invocation -> {
            final String qualifiedName = invocation.getArgument(0);
            return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        });
        return imports;
    }

    private static void assertImportsMethodInvoked(Object importsMock, String methodName, Object... args)
    {
        final boolean invoked = mockingDetails(importsMock).getInvocations().stream()
                .anyMatch(invocation -> invocation.getMethod().getName().equals(methodName)
                        && java.util.Arrays.asList(invocation.getArguments()).containsAll(java.util.Arrays.asList(args)));
        assertTrue(invoked, "Expected import manager call: " + methodName + java.util.Arrays.toString(args));
    }

    @Test
    public void should_resolve_junit_version_from_preferences()
    {
        final ProjectPreferences junit5Prefs = mock(ProjectPreferences.class);
        when(junit5Prefs.shouldUseJunit5Type()).thenReturn(true);
        assertEquals(JUnitVersion.VERSION_5, invoke(page, "retrieveJUnitVersion", junit5Prefs));

        final ProjectPreferences junit4Prefs = mock(ProjectPreferences.class);
        when(junit4Prefs.shouldUseJunit4Type()).thenReturn(true);
        assertEquals(JUnitVersion.VERSION_4, invoke(page, "retrieveJUnitVersion", junit4Prefs));

        final ProjectPreferences junit3Prefs = mock(ProjectPreferences.class);
        when(junit3Prefs.shouldUseJunit3Type()).thenReturn(true);
        assertEquals(JUnitVersion.VERSION_3, invoke(page, "retrieveJUnitVersion", junit3Prefs));

        assertEquals(JUnitVersion.VERSION_5, invoke(page, "retrieveJUnitVersion", mock(ProjectPreferences.class)));
    }

    @Test
    public void should_init_with_empty_selection()
    {
        page.init(new StructuredSelection());

        assertNull(page.getClassUnderTest());
        assertEquals("", page.getClassUnderTestText());
    }

    @Test
    public void should_init_class_under_test_from_compilation_unit_selection()
    {
        page.init(new StructuredSelection(cutType.getCompilationUnit()));

        assertEquals("org.SomeClass", page.getClassUnderTestText());
        assertEquals(cutType, page.getClassUnderTest());
    }

    @Test
    public void should_init_without_class_under_test_for_package_selection()
    {
        page.init(new StructuredSelection(cutType.getPackageFragment()));

        assertNull(page.getClassUnderTest());
        assertEquals("", page.getClassUnderTestText());
    }

    @Test
    public void should_init_class_under_test_from_class_file_selection() throws Exception
    {
        final IType objectType = cutType.getJavaProject().findType("java.lang.Object");
        assertNotNull(objectType);

        page.init(new StructuredSelection(objectType.getClassFile()));

        assertEquals("java.lang.Object", page.getClassUnderTestText());
    }

    @Test
    public void should_set_spock_superclass_when_spock_selected()
    {
        createPageControl();
        selectOnly("spockToggle");

        page.handleSelectionChanged();

        assertEquals("spock.lang.Specification", page.getSuperClass());
        assertFalse(page.isJUnit4());
        assertFalse(page.isJUnit5());
    }

    @Test
    public void should_set_groovy_test_case_superclass_for_groovy_pages()
    {
        final MoreUnitWizardPageOne groovyPage = newPage(LanguageType.GROOVY);
        groovyPage.createControl(shell);

        groovyPage.handleSelectionChanged();

        assertEquals("groovy.util.GroovyTestCase", groovyPage.getSuperClass());
    }

    @Test
    public void should_set_junit3_superclass_when_junit3_selected()
    {
        createPageControl();
        selectOnly("junit3Toggle");

        page.handleSelectionChanged();

        assertEquals("junit.framework.TestCase", page.getSuperClass());
        assertFalse(page.isJUnit4());
        assertFalse(page.isJUnit5());
    }

    @Test
    public void should_track_testng_and_spock_toggle_state()
    {
        createPageControl();
        selectOnly("testNgToggle");

        page.handleSelectionChanged();

        assertEquals(TestType.TESTNG, page.getTestType());
        assertTrue((Boolean) invoke(page, "isTestNgSelected"));
        assertFalse((Boolean) invoke(page, "isSpockSelected"));
    }

    @Test
    public void should_report_no_testng_or_spock_selection_before_controls_exist()
    {
        assertFalse((Boolean) invoke(page, "isTestNgSelected"));
        assertFalse((Boolean) invoke(page, "isSpockSelected"));
    }

    @Test
    public void should_ignore_class_under_test_browse_without_container()
    {
        createPageControl();
        page.setPackageFragmentRoot(null, true);

        final Button button = (Button) getField(page, "fClassUnderTestButton");
        button.notifyListeners(SWT.Selection, new Event());
        button.notifyListeners(SWT.DefaultSelection, new Event());

        assertEquals("", page.getClassUnderTestText());
        assertNull(page.getClassUnderTest());
    }

    @Test
    public void should_return_null_class_to_test_type_without_container()
    {
        createPageControl();
        page.setPackageFragmentRoot(null, true);

        assertNull(invoke(page, "chooseClassToTestType"));
    }

    @Test
    public void should_ignore_buildpath_link_without_container()
    {
        createPageControl();
        page.setPackageFragmentRoot(null, true);

        final Link link = (Link) getField(page, "fLink");
        final Event event = new Event();
        event.text = "a3";
        link.notifyListeners(SWT.Selection, event);

        assertNull(invoke(page, "getPackageFragmentRoot"));
    }

    @Test
    public void should_skip_buildpath_configuration_without_container()
    {
        invoke(page, "performBuildpathConfiguration", "a4");

        assertNull(invoke(page, "getPackageFragmentRoot"));
    }

    @Test
    public void should_switch_test_type_to_junit3()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        attachRealWizard();
        selectOnly("junit3Toggle");

        // junit3Toggle carries no listener of its own: natively, selecting a
        // radio deselects the others, and their Selection event drives
        // testTypeSelectionChanged(), so notify the deselected toggle
        ((Button) getField(page, "unit4Toggle")).notifyListeners(SWT.Selection, new Event());

        assertFalse(page.isJUnit4());
        assertFalse(page.isJUnit5());
        assertEquals(TestType.JUNIT_3, page.getTestType());
    }

    @Test
    public void should_switch_test_type_to_junit4()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        attachRealWizard();
        selectOnly("unit4Toggle");

        ((Button) getField(page, "unit4Toggle")).notifyListeners(SWT.Selection, new Event());

        assertTrue(page.isJUnit4());
        assertEquals(TestType.JUNIT_4, page.getTestType());
    }

    @Test
    public void should_switch_test_type_to_junit5()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        attachRealWizard();
        selectOnly("unit5Toggle");

        ((Button) getField(page, "unit5Toggle")).notifyListeners(SWT.Selection, new Event());

        assertTrue(page.isJUnit5());
        assertEquals(TestType.JUNIT_5, page.getTestType());
    }

    @Test
    public void should_switch_test_type_to_spock()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        attachRealWizard();
        selectOnly("spockToggle");

        ((Button) getField(page, "spockToggle")).notifyListeners(SWT.Selection, new Event());

        assertEquals(TestType.SPOCK, page.getTestType());
        assertEquals("spock.lang.Specification", page.getSuperClass());
    }

    @Test
    public void should_switch_test_type_to_testng()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        attachRealWizard();
        selectOnly("testNgToggle");

        ((Button) getField(page, "testNgToggle")).notifyListeners(SWT.Selection, new Event());

        assertEquals(TestType.TESTNG, page.getTestType());
        assertFalse(page.isJUnit4());
        assertFalse(page.isJUnit5());
    }

    @Test
    public void should_update_completion_processor_on_package_change()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        assertNotNull(invoke(page, "packageChanged"));
    }

    @Test
    public void should_validate_empty_class_under_test_as_ok()
    {
        page.init(new StructuredSelection(cutType));
        page.setClassUnderTest("");

        assertNull(page.getClassUnderTest());
        assertTrue(((IStatus) invoke(page, "classUnderTestChanged")).isOK());
    }

    @Test
    public void should_reject_invalid_class_under_test_name()
    {
        page.init(new StructuredSelection(cutType));
        page.setClassUnderTest("not a valid name!!");

        assertNull(page.getClassUnderTest());
        assertEquals(IStatus.ERROR, ((IStatus) invoke(page, "classUnderTestChanged")).getSeverity());
    }

    @Test
    public void should_reject_unknown_class_under_test_with_error()
    {
        page.init(new StructuredSelection(cutType));
        page.setClassUnderTest("does.not.Exist");

        assertEquals(IStatus.ERROR, ((IStatus) invoke(page, "classUnderTestChanged")).getSeverity());
    }

    @Test
    public void should_warn_for_interface_class_under_test()
    {
        page.init(new StructuredSelection(cutType));
        page.setClassUnderTest("java.io.Serializable");

        assertEquals(IStatus.WARNING, ((IStatus) invoke(page, "classUnderTestChanged")).getSeverity());
    }

    @Test
    public void should_accept_valid_class_under_test_without_warnings()
    {
        page.init(new StructuredSelection(cutType));

        assertEquals(cutType, page.getClassUnderTest());
        assertTrue(((IStatus) invoke(page, "classUnderTestChanged")).isOK());
    }

    @Test
    public void should_validate_class_under_test_without_container_as_ok()
    {
        assertTrue(((IStatus) invoke(page, "classUnderTestChanged")).isOK());
    }

    @Test
    public void should_accept_any_superclass_for_junit4()
    {
        assertTrue(((IStatus) invoke(page, "superClassChanged")).isOK());
    }

    @Test
    public void should_accept_any_superclass_for_junit5()
    {
        createPageControl();
        selectOnly("unit5Toggle");
        page.handleSelectionChanged();

        assertTrue(((IStatus) invoke(page, "superClassChanged")).isOK());
    }

    @Test
    public void should_accept_any_superclass_for_testng()
    {
        createPageControl();
        selectOnly("testNgToggle");
        page.handleSelectionChanged();

        assertTrue(((IStatus) invoke(page, "superClassChanged")).isOK());
    }

    @Test
    public void should_reject_empty_superclass_for_junit3()
    {
        createPageControl();
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setSuperClass("", true);

        assertEquals(IStatus.ERROR, ((IStatus) invoke(page, "superClassChanged")).getSeverity());
    }

    @Test
    public void should_warn_for_unknown_superclass_for_junit3()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setSuperClass("does.not.Exist", true);

        assertEquals(IStatus.WARNING, ((IStatus) invoke(page, "superClassChanged")).getSeverity());
    }

    @Test
    public void should_reject_interface_superclass_for_junit3()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setSuperClass("java.io.Serializable", true);

        assertEquals(IStatus.ERROR, ((IStatus) invoke(page, "superClassChanged")).getSeverity());
    }

    @Test
    public void should_reject_superclass_not_implementing_test_interface()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setSuperClass("java.lang.Object", true);

        assertEquals(IStatus.ERROR, ((IStatus) invoke(page, "superClassChanged")).getSeverity());
    }

    @Test
    public void should_accept_test_case_superclass_for_junit3()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setSuperClass("junit.framework.TestCase", true);

        assertTrue(((IStatus) invoke(page, "superClassChanged")).isOK());
    }

    @Test
    public void should_validate_spock_superclass()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("spockToggle");
        page.handleSelectionChanged();

        assertNotNull(invoke(page, "superClassChanged"));
    }

    @Test
    public void should_validate_junit_project_without_container_as_ok()
    {
        assertTrue(((IStatus) invoke(page, "validateIfJUnitProject")).isOK());
    }

    @Test
    public void should_validate_junit4_project_as_ok()
    {
        page.init(new StructuredSelection(cutType));

        assertTrue(((IStatus) invoke(page, "validateIfJUnitProject")).isOK());
    }

    @Test
    public void should_validate_junit3_project_as_ok()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();

        assertTrue(((IStatus) invoke(page, "validateIfJUnitProject")).isOK());
    }

    @Test
    public void should_flip_to_next_page_with_wizard_and_class_under_test()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        page.setTypeName("SomeClassTest", true);

        final IWizard wizard = mock(IWizard.class);
        final IWizardPage nextPage = mock(IWizardPage.class);
        when(wizard.getNextPage(page)).thenReturn(nextPage);
        page.setWizard(wizard);

        assertTrue(page.canFlipToNextPage());
    }

    @Test
    public void should_not_flip_to_next_page_without_class_under_test()
    {
        final IWizard wizard = mock(IWizard.class);
        final IWizardPage nextPage = mock(IWizardPage.class);
        when(wizard.getNextPage(page)).thenReturn(nextPage);
        page.setWizard(wizard);

        assertFalse(page.canFlipToNextPage());
    }

    @Test
    public void should_strip_modifiers_for_groovy()
    {
        final MoreUnitWizardPageOne groovyPage = newPage(LanguageType.GROOVY);
        groovyPage.createControl(shell);

        assertEquals(0, groovyPage.getModifiers() & (Flags.AccPublic | Flags.AccPrivate | Flags.AccProtected));
    }

    @Test
    public void should_strip_public_modifier_for_junit5()
    {
        createPageControl();
        selectOnly("unit5Toggle");

        assertEquals(0, page.getModifiers() & Flags.AccPublic);
    }

    @Test
    public void should_keep_public_modifier_for_junit3()
    {
        createPageControl();
        selectOnly("junit3Toggle");

        assertEquals(Flags.AccPublic, page.getModifiers() & Flags.AccPublic);
    }

    @Test
    public void should_return_current_package_before_page_hides()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        assertEquals("org", page.getTestCasePackage().getElementName());
    }

    @Test
    public void should_determine_test_type_from_toggles()
    {
        createPageControl();

        selectOnly("junit3Toggle");
        assertEquals(TestType.JUNIT_3, page.getTestType());

        selectOnly("unit4Toggle");
        assertEquals(TestType.JUNIT_4, page.getTestType());

        selectOnly("unit5Toggle");
        assertEquals(TestType.JUNIT_5, page.getTestType());

        selectOnly("spockToggle");
        assertEquals(TestType.SPOCK, page.getTestType());

        selectOnly("testNgToggle");
        assertEquals(TestType.TESTNG, page.getTestType());
    }

    @Test
    public void should_return_test_type_pref_value_from_toggles()
    {
        createPageControl();

        selectOnly("junit3Toggle");
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, invoke(page, "getTestTypePrefValue"));

        selectOnly("unit4Toggle");
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, invoke(page, "getTestTypePrefValue"));

        selectOnly("unit5Toggle");
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, invoke(page, "getTestTypePrefValue"));

        selectOnly("spockToggle");
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_SPOCK, invoke(page, "getTestTypePrefValue"));

        selectOnly("testNgToggle");
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, invoke(page, "getTestTypePrefValue"));
    }

    @Test
    public void should_use_groovy_suffix_for_spock_tests()
    {
        createPageControl();
        selectOnly("spockToggle");

        assertEquals("Foo.groovy", invoke(page, "getCompilationUnitName", "Foo"));
    }

    @Test
    public void should_update_type_name_with_spock_suffix()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("spockToggle");

        invoke(page, "updateTypeName");

        assertEquals("SomeClassSpec", page.getTypeName());
    }

    @Test
    public void should_update_type_name_with_test_suffix()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("unit4Toggle");

        invoke(page, "updateTypeName");

        assertEquals("SomeClassTest", page.getTypeName());
    }

    @Test
    public void should_keep_test_type_when_page_stays_visible()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        page.setVisible(true);

        assertEquals(TestType.JUNIT_4, page.getTestType());
    }

    @Test
    public void should_save_widget_values_to_dialog_settings()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        final IDialogSettings settings = mock(IDialogSettings.class);
        final IWizard wizard = mock(IWizard.class);
        when(wizard.getDialogSettings()).thenReturn(settings);
        page.setWizard(wizard);

        page.init(new StructuredSelection(cutType));
        page.setVisible(false);

        verify(settings, times(5)).put(anyString(), anyBoolean());
        assertEquals(TestType.JUNIT_4, page.getTestType());
        assertNotNull(page.getTestCasePackage());
    }

    @Test
    public void should_save_widget_values_on_dispose()
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));

        page.dispose();

        assertNotNull(page.getTestCasePackage());
    }

    @Test
    public void should_use_class_under_test_line_delimiter()
    {
        page.init(new StructuredSelection(cutType));

        assertNotNull(invoke(page, "getLineDelimiter"));
    }

    @Test
    public void should_use_package_line_delimiter_without_class_under_test()
    {
        createPageControl();
        page.setPackageFragment(cutType.getPackageFragment(), true);

        assertNotNull(invoke(page, "getLineDelimiter"));
    }

    @Test
    public void should_return_null_for_unknown_project()
    {
        final IJavaProject unknownProject = mock(IJavaProject.class);

        assertNull(invoke(page, "resolveClassNameToType", unknownProject, null, "foo.Bar"));
    }

    @Test
    public void should_resolve_type_directly_in_package_and_in_java_lang()
    {
        final IJavaProject project = cutType.getJavaProject();

        assertNotNull(invoke(page, "resolveClassNameToType", project, null, "org.SomeClass"));
        assertNotNull(invoke(page, "resolveClassNameToType", project, cutType.getPackageFragment(), "SomeClass"));
        assertNotNull(invoke(page, "resolveClassNameToType", project, null, "String"));
    }

    @Test
    public void should_create_constructor_stub_for_junit4() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        page.setTypeName("SomeClassTest", true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        invoke(page, "createConstructor", newType, imports);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertTrue(content.getValue().contains("SomeClassTest("));
        assertFalse(content.getValue().contains("super(name)"));
    }

    @Test
    public void should_create_constructor_stub_with_name_parameter_for_junit3() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setTypeName("SomeClassTest", true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        invoke(page, "createConstructor", newType, imports);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertTrue(content.getValue().contains("super(name)"));
    }

    @Test
    public void should_create_setup_stub_with_annotation_for_junit4() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        page.setTypeName("SomeClassTest", true);
        page.setAddComments(true, true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        invoke(page, "createSetupStubs", newType, "setUp", false, "org.junit.Before", imports);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertTrue(content.getValue().contains("@Before"));
        assertTrue(content.getValue().contains("public void setUp"));
    }

    @Test
    public void should_create_static_setup_stub_without_annotation_for_junit3() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setTypeName("SomeClassTest", true);
        page.setAddComments(false, true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        invoke(page, "createSetupStubs", newType, "setUpBeforeClass", true, null, imports);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertTrue(content.getValue().contains("protected static void setUpBeforeClass"));
    }

    @Test
    public void should_create_all_type_members_for_junit4() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        page.setTypeName("SomeClassTest", true);

        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 0, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 1, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 2, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 3, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 4, true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        // page two was never opened: create its control so that checked
        // methods can be queried (empty selection by default)
        ((NewTestCaseWizardPageTwo) getField(page, "fPage2")).createControl(shell);

        invoke(page, "createTypeMembers", newType, imports, null);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType, times(4)).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertEquals(4, content.getAllValues().size());
        assertImportsMethodInvoked(imports, "addImport", "org.junit.Test");
        assertImportsMethodInvoked(imports, "addStaticImport", "org.junit.Assert", "*", false);
    }

    @Test
    public void should_create_constructor_member_for_junit3() throws Exception
    {
        createPageControl();
        page.init(new StructuredSelection(cutType));
        selectOnly("junit3Toggle");
        page.handleSelectionChanged();
        page.setTypeName("SomeClassTest", true);

        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 0, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 1, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 2, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 3, true);
        invoke(getField(page, "fMethodStubsButtons"), "setSelection", 4, true);

        final IType newType = mockNewType();
        final Object imports = mockImportsManager();

        // page two was never opened: create its control so that checked
        // methods can be queried (empty selection by default)
        ((NewTestCaseWizardPageTwo) getField(page, "fPage2")).createControl(shell);

        invoke(page, "createTypeMembers", newType, imports, null);

        final ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(newType, times(3)).createMethod(content.capture(), isNull(), anyBoolean(), isNull());
        assertEquals(3, content.getAllValues().size());
    }
}
