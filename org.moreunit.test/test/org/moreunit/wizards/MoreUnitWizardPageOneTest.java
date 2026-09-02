package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.LanguageType;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.preferences.Preferences;
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

        MoreUnitWizardPageOne groovyPage = newPage(LanguageType.GROOVY);
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

        Button classUnderTestButton = (Button) getField(page, "fClassUnderTestButton");
        assertNotNull(classUnderTestButton);
        Object root = invoke(page, "getPackageFragmentRoot");
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

        Button classUnderTestButton = (Button) getField(page, "fClassUnderTestButton");
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

        Object[] statuses = (Object[]) invoke(page, "getStatusList");

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

        Button unit4Toggle = (Button) getField(page, "unit4Toggle");
        assertTrue(unit4Toggle.getSelection());
    }

    @Test
    public void should_return_package_fragment_root_of_selection()
    {
        page.init(new StructuredSelection(cutType));

        Object root = invoke(page, "getPackageFragmentRoot");

        assertNotNull(root);
        assertEquals(cutType.getPackageFragment().getParent(), root);
    }
}
