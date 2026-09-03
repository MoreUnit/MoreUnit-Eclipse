package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IMethod;
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
 * Complements {@link MoreUnitWizardPageOneStubCreationTest} with the
 * configurations it does not cover: constructor stub for JUnit 4 tests and
 * method stubs for JUnit 3 tests (no annotations, protected visibility).
 * Finishing the wizard exercises
 * {@link MoreUnitWizardPageOne#createTypeMembers} end to end.
 */
@Project(
    mainSrcFolder = "main-src",
    testSrcFolder = "test-src",
    mainCls = "pack: Class",
    properties = @Properties(SimpleJUnit4Properties.class))
public class MoreUnitWizardPageOneStubVariantsTest extends NewClassyWizardTestCase
{
    private static final String PREFIX = "NewTestCaseCreationWizardPage.";

    @Override
    protected Class< ? extends NewClassyWizard> getWizardClass()
    {
        return NewTestCaseWizard.class;
    }

    @BeforeEach
    public void disableAllMethodStubs()
    {
        setStub("USE_SETUP", false);
        setStub("USE_TEARDOWN", false);
        setStub("USE_SETUPCLASS", false);
        setStub("USE_TEARDOWNCLASS", false);
        setStub("USE_CONSTRUCTOR", false);
    }

    @AfterEach
    public void resetAllMethodStubs()
    {
        disableAllMethodStubs();
    }

    private static void setStub(String key, boolean value)
    {
        final IDialogSettings settings = MoreUnitPlugin.getDefault().getDialogSettings();
        settings.put(PREFIX + key, value);
    }

    @Test
    public void should_create_constructor_stub_for_junit4_test() throws Exception
    {
        setStub("USE_CONSTRUCTOR", true);

        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class").get());

        willAutomaticallyValidateWhenOpen(wizard);

        final IType createdType = wizard.open();
        assertNotNull(createdType);

        boolean constructorFound = false;
        for (final IMethod method : createdType.getMethods())
        {
            constructorFound |= method.isConstructor();
        }
        assertTrue(constructorFound, "Constructor stub should exist");
    }

    @Test
    @Project(
        mainSrcFolder = "main-src",
        testSrcFolder = "test-src",
        mainCls = "pack: Class3",
        properties = @Properties(SimpleJUnit3Properties.class))
    public void should_create_setup_stubs_for_junit3_test() throws Exception
    {
        setStub("USE_SETUP", true);
        setStub("USE_TEARDOWN", true);
        setStub("USE_SETUPCLASS", true);
        setStub("USE_TEARDOWNCLASS", true);

        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class3").get());

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
}
