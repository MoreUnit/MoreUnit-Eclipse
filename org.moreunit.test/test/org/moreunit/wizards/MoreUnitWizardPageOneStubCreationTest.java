package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.configs.SimpleJUnit3Properties;
import org.moreunit.test.context.configs.SimpleJUnit4Properties;

/**
 * Exercises {@link MoreUnitWizardPageOne#createTypeMembers} by finishing the
 * {@link NewTestCaseWizard} with all method stubs enabled (through the dialog
 * settings written by the page itself between two wizard invocations).
 */
@Project(
    mainSrcFolder = "main-src",
    testSrcFolder = "test-src",
    mainCls = "pack: Class",
    properties = @Properties(SimpleJUnit4Properties.class))
public class MoreUnitWizardPageOneStubCreationTest extends NewClassyWizardTestCase
{
    private static final String PREFIX = "NewTestCaseCreationWizardPage.";

    @Override
    protected Class< ? extends NewClassyWizard> getWizardClass()
    {
        return NewTestCaseWizard.class;
    }

    @BeforeEach
    public void enableMethodStubs()
    {
        final IDialogSettings settings = MoreUnitPlugin.getDefault().getDialogSettings();
        settings.put(PREFIX + "USE_SETUP", true);
        settings.put(PREFIX + "USE_TEARDOWN", true);
        settings.put(PREFIX + "USE_SETUPCLASS", true);
        settings.put(PREFIX + "USE_TEARDOWNCLASS", true);
        settings.put(PREFIX + "USE_CONSTRUCTOR", false);
    }

    @AfterEach
    public void disableMethodStubs()
    {
        final IDialogSettings settings = MoreUnitPlugin.getDefault().getDialogSettings();
        settings.put(PREFIX + "USE_SETUP", false);
        settings.put(PREFIX + "USE_TEARDOWN", false);
        settings.put(PREFIX + "USE_SETUPCLASS", false);
        settings.put(PREFIX + "USE_TEARDOWNCLASS", false);
        settings.put(PREFIX + "USE_CONSTRUCTOR", false);
    }

    @Test
    public void should_create_selected_method_stubs_when_finishing_the_wizard() throws Exception
    {
        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class").get());

        willAutomaticallyValidateWhenOpen(wizard);

        final IType createdType = wizard.open();
        assertNotNull(createdType);

        assertMethodExists(createdType, "setUp");
        assertMethodExists(createdType, "tearDown");
        assertMethodExists(createdType, "setUpBeforeClass");
        assertMethodExists(createdType, "tearDownAfterClass");
    }

    private void assertMethodExists(IType type, String methodName) throws Exception
    {
        assertTrue(type.getMethod(methodName, new String[0]).exists(), "Method " + methodName + " should exist");
    }

    @Test
    @Project(
        mainSrcFolder = "main-src",
        testSrcFolder = "test-src",
        mainCls = "pack: Class3",
        properties = @Properties(SimpleJUnit3Properties.class))
    public void should_create_constructor_stub_when_finishing_the_wizard() throws Exception
    {
        final IDialogSettings settings = MoreUnitPlugin.getDefault().getDialogSettings();
        settings.put(PREFIX + "USE_CONSTRUCTOR", true);

        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class3").get());

        willAutomaticallyValidateWhenOpen(wizard);

        final IType createdType = wizard.open();
        assertNotNull(createdType);
        // for JUnit 3 the constructor stub takes the class under test name as parameter
        boolean constructorFound = false;
        for (final org.eclipse.jdt.core.IMethod method : createdType.getMethods())
        {
            constructorFound |= method.isConstructor();
        }
        assertTrue(constructorFound, "Constructor stub should exist");
    }
}
