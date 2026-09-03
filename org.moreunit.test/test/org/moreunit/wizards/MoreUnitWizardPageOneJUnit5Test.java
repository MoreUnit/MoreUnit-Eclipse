package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.swt.widgets.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.LanguageType;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.preferences.Preferences;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit5Project;

/**
 * Tests the JUnit 5 flavour of {@link MoreUnitWizardPageOne} with real SWT
 * widgets (the JUnit 4 flavour is covered by {@link MoreUnitWizardPageOneTest}).
 */
@Context(SimpleJUnit5Project.class)
public class MoreUnitWizardPageOneJUnit5Test extends SwtPageTestCase
{
    private MoreUnitWizardPageOne page;
    private IType cutType;

    @BeforeEach
    public void createPage()
    {
        cutType = context.getPrimaryTypeHandler("org.SomeClass").get();
        page = new MoreUnitWizardPageOne(new NewTestCaseWizardPageTwo(), cutType.getJavaProject(), Preferences.forProject(cutType.getJavaProject()), LanguageType.JAVA);
    }

    @Test
    public void should_default_to_junit5_and_remove_public_modifier()
    {
        page.createControl(shell);

        assertTrue(page.isJUnit5());
        assertFalse(page.isJUnit4());
        assertEquals(TestType.JUNIT_5, page.getTestType());

        final Button unit5Toggle = (Button) getField(page, "unit5Toggle");
        assertTrue(unit5Toggle.getSelection());
        assertEquals(0, page.getModifiers() & Flags.AccPublic);
    }

    @Test
    public void should_resolve_class_under_test_from_selection()
    {
        page.createControl(shell);
        page.init(new org.eclipse.jface.viewers.StructuredSelection(cutType));

        assertEquals("org.SomeClass", page.getClassUnderTestText());
        assertEquals(cutType, page.getClassUnderTest());
        assertEquals(TestType.JUNIT_5, page.getTestType());
    }

    @Test
    public void should_keep_test_type_when_page_becomes_invisible()
    {
        page.createControl(shell);
        page.init(new org.eclipse.jface.viewers.StructuredSelection(cutType));

        page.setVisible(false);

        assertEquals(TestType.JUNIT_5, page.getTestType());
        assertNotNull(page.getTestCasePackage());
    }
}
