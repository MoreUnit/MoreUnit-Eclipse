package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

public class NewTestCaseWizardBranchesCoverageTest extends NewClassyWizardTestCase
{
    @Override
    protected Class< ? extends NewClassyWizard> getWizardClass()
    {
        return NewTestCaseWizard.class;
    }

    @Properties(testType = TestType.JUNIT3, testClassNameTemplate = "${srcFile}Test", testSuperClass = "junit.framework.TestCase")
    protected static class JUnit3WithSuperClass
    {
    }

    @Test
    @Project(mainSrcFolder = "main-src", testSrcFolder = "test-src", mainCls = "pack:Class", properties = @Properties(JUnit3WithSuperClass.class))
    public void should_apply_configured_superclass_to_created_test_case() throws Exception
    {
        // given
        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class").get());

        willAutomaticallyValidateWhenOpen(wizard);

        // when
        final IType createdType = wizard.open();

        // then the configured superclass was applied (covers setSuperClass branch)
        assertNotNull(createdType);
        final String superClass = createdType.getSuperclassName();
        assertNotNull(superClass);
        assertTrue(superClass.endsWith("TestCase"), "unexpected superclass: " + superClass);

        // and the main source folder is exposed
        assertEquals(context.getProjectHandler().getSrcFolderHandler("main-src").get(), wizard.getMainSrcFolder());
    }

    @Test
    @Project(mainSrcFolder = "src", testSrcFolder = "test", mainCls = "pack:Class")
    public void should_create_spock_specification_with_spock_superclass() throws Exception
    {
        // given a project configured for Spock
        final org.eclipse.jdt.core.IJavaProject project = context.getProjectHandler().get();
        final Preferences preferences = Preferences.getInstance();
        final String oldTestType = preferences.getTestType(project);
        final boolean oldSpecificSettings = preferences.hasProjectSpecificSettings(project);
        preferences.setHasProjectSpecificSettings(project, true);
        preferences.setTestType(project, PreferenceConstants.TEST_TYPE_VALUE_SPOCK);
        try
        {
            final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class").get());
            wizard.addPages();

            // when the first page is configured (covers the Spock branches
            // without opening the wizard, which cannot finish without a
            // groovy test folder on the project)
            final Method configurePageOne = NewTestCaseWizard.class.getDeclaredMethod("configurePageOne");
            configurePageOne.setAccessible(true);
            configurePageOne.invoke(wizard);

            // then the spec name and the Spock superclass were applied
            final MoreUnitWizardPageOne pageOne = pageOneOf(wizard);
            assertTrue(pageOne.getTypeName().endsWith("Spec"), "unexpected test name: " + pageOne.getTypeName());
            assertEquals("spock.lang.Specification", pageOne.getSuperClass());
        }
        finally
        {
            preferences.setTestType(project, oldTestType);
            preferences.setHasProjectSpecificSettings(project, oldSpecificSettings);
        }
    }

    private static MoreUnitWizardPageOne pageOneOf(NewTestCaseWizard wizard) throws Exception
    {
        final Field pageOneField = NewTestCaseWizard.class.getDeclaredField("pageOne");
        pageOneField.setAccessible(true);
        return (MoreUnitWizardPageOne) pageOneField.get(wizard);
    }

    @Test
    @Project(mainCls = "pack:Class")
    public void should_throw_when_context_is_requested_before_pages_are_added() throws Exception
    {
        // given a wizard for which addPages() was never called
        final NewTestCaseWizard wizard = new NewTestCaseWizard(context.getPrimaryTypeHandler("pack.Class").get());

        // when requesting the context, then an IllegalStateException is reported
        final Method getContext = NewTestCaseWizard.class.getDeclaredMethod("getContext");
        getContext.setAccessible(true);
        try
        {
            getContext.invoke(wizard);
            throw new AssertionError("expected InvocationTargetException");
        }
        catch (final InvocationTargetException e)
        {
            assertTrue(e.getCause() instanceof IllegalStateException);
            assertTrue(e.getCause().getMessage().contains("Context is null"));
        }
    }

    @Test
    @Project(mainCls = "pack:Class")
    public void should_relay_creation_aborted_without_error() throws Exception
    {
        // given a wizard with an initialized context but without any opened page
        final IType cut = context.getPrimaryTypeHandler("pack.Class").get();
        final NewTestCaseWizard wizard = new NewTestCaseWizard(cut);
        final Field contextField = NewTestCaseWizard.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(wizard, new NewTestCaseWizardContext(cut, null));

        // when the creation is aborted, then no error occurs
        boolean aborted = false;
        try
        {
            final Method creationAborted = NewTestCaseWizard.class.getDeclaredMethod("creationAborted");
            creationAborted.setAccessible(true);
            creationAborted.invoke(wizard);
            aborted = true;
        }
        catch (final RuntimeException e)
        {
            throw new AssertionError("creationAborted should not fail", e);
        }
        assertTrue(aborted);
    }
}
