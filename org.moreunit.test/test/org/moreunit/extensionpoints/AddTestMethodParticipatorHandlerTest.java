package org.moreunit.extensionpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.handler.AddTestMethodContext;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class AddTestMethodParticipatorHandlerTest extends ContextTestCase
{
    private IMethod testMethod;
    private IMethod methodUnderTest;

    @BeforeEach
    public void setUp()
    {
        final MethodHandler foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;");
        final MethodHandler testFoo = context.getPrimaryTypeHandler("com.FooTest").addMethod("public void foo()", "");
        testMethod = testFoo.get();
        methodUnderTest = foo.get();
    }

    @Test
    public void getInstance_should_return_singleton()
    {
        assertSame(AddTestMethodParticipatorHandler.getInstance(), AddTestMethodParticipatorHandler.getInstance());
    }

    @Test
    public void callExtension_should_attach_preferences_and_return_context_even_without_extension()
    {
        final AddTestMethodContext context = new AddTestMethodContext(testMethod, methodUnderTest);

        final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(context);

        assertSame(context, result);
        assertNotNull(result.getPreferences());
        assertSame(org.moreunit.preferences.Preferences.getInstance(), result.getPreferences());
    }

    @Test
    public void callExtension_should_run_registered_participator() throws Exception
    {
        final IAddTestMethodParticipator participator = mock(IAddTestMethodParticipator.class);
        final IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.createExecutableExtension("class")).thenReturn(participator);

        final LogHandler logHandlerInstance = mock(LogHandler.class);
        try (var platform = mockStatic(Platform.class);
             var logHandler = mockStatic(LogHandler.class))
        {
            final IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(anyString())).thenReturn(new IConfigurationElement[] { configElement });
            platform.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandler.when(LogHandler::getInstance).thenReturn(logHandlerInstance);

            final AddTestMethodContext context = new AddTestMethodContext(testMethod, methodUnderTest);
            final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(context);

            assertSame(context, result);
            verify(participator).addTestMethod(context);
        }
    }

    @Test
    public void callExtension_should_ignore_extensions_that_do_not_implement_the_participator_interface() throws Exception
    {
        final IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.createExecutableExtension("class")).thenReturn(new Object());

        final LogHandler logHandlerInstance = mock(LogHandler.class);
        try (var platform = mockStatic(Platform.class);
             var logHandler = mockStatic(LogHandler.class))
        {
            final IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(anyString())).thenReturn(new IConfigurationElement[] { configElement });
            platform.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandler.when(LogHandler::getInstance).thenReturn(logHandlerInstance);

            final AddTestMethodContext context = new AddTestMethodContext(testMethod, methodUnderTest);
            final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(context);

            assertSame(context, result);
            verify(logHandlerInstance).handleWarnLog("Bad class for extension point");
        }
    }

    @Test
    public void callExtension_should_survive_failing_participator() throws Exception
    {
        final IAddTestMethodParticipator participator = mock(IAddTestMethodParticipator.class);
        doThrow(new RuntimeException("boom")).when(participator).addTestMethod(any());
        final IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.createExecutableExtension("class")).thenReturn(participator);

        try (var platform = mockStatic(Platform.class);
             var logHandler = mockStatic(LogHandler.class))
        {
            final IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(anyString())).thenReturn(new IConfigurationElement[] { configElement });
            platform.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandler.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

            final AddTestMethodContext context = new AddTestMethodContext(testMethod, methodUnderTest, true);

            final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(context);

            assertSame(context, result);
            assertTrue(result.isNewTestClassCreated());
        }
    }

    @Test
    public void callExtension_should_build_context_from_two_methods()
    {
        final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(testMethod, methodUnderTest);

        assertNotNull(result);
        assertEquals(testMethod, result.getTestMethod());
        assertEquals(methodUnderTest, result.getMethodUnderTest());
    }

    @Test
    public void callExtension_should_keep_new_test_class_created_flag()
    {
        final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().callExtension(testMethod, methodUnderTest, true);

        assertNotNull(result);
        assertTrue(result.isNewTestClassCreated());
    }

    @Test
    public void callExtension_should_build_context_from_all_parameters()
    {
        final ICompilationUnit testClass = testMethod.getCompilationUnit();
        final ICompilationUnit classUnderTest = methodUnderTest.getCompilationUnit();

        final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance() //
        .callExtension(testClass, testMethod, classUnderTest, methodUnderTest, false);

        assertNotNull(result);
        assertEquals(testClass, result.getTestClass());
        assertEquals(testMethod, result.getTestMethod());
        assertEquals(classUnderTest, result.getClassUnderTest());
        assertEquals(methodUnderTest, result.getMethodUnderTest());
        assertEquals(false, result.isNewTestClassCreated());
    }

    @Test
    public void maybeCallExtension_should_return_context_when_test_method_matches_exactly_one_method()
    {
        final IAddTestMethodContext result = AddTestMethodParticipatorHandler.getInstance().maybeCallExtension(testMethod);

        assertNotNull(result);
        assertEquals(testMethod, result.getTestMethod());
        assertEquals(methodUnderTest, result.getMethodUnderTest());
    }

    @Test
    public void maybeCallExtension_should_return_null_when_no_method_of_the_class_under_test_matches() throws Exception
    {
        final IMethod orphanTestMethod = context.getPrimaryTypeHandler("com.FooTest").addMethod("public void orphan()", "").get();

        assertNull(AddTestMethodParticipatorHandler.getInstance().maybeCallExtension(orphanTestMethod));
    }

    @Test
    public void maybeCallExtension_should_return_null_when_test_class_has_no_corresponding_class() throws Exception
    {
        // a test class living in the test folder, without matching class under
        // test
        context.getProjectHandler().getTestSrcFolderHandler().createClass("com.BazTest");
        final IMethod bazTestMethod = context.getPrimaryTypeHandler("com.BazTest").addMethod("public void testSomething()", "").get();

        assertNull(AddTestMethodParticipatorHandler.getInstance().maybeCallExtension(bazTestMethod));
    }
}
