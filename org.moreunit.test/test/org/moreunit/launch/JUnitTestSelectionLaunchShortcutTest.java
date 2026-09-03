package org.moreunit.launch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo, Bar", testCls = "com:FooTest, BarTest")
public class JUnitTestSelectionLaunchShortcutTest extends ContextTestCase
{
    /**
     * The shortcuts delegate the real launch to a
     * {@link JUnitTestSelectionLaunchConfigurationDelegate} running in a
     * background job. The delegate is mocked so that no launch is actually
     * performed, and the job is awaited by dispatching the UI event queue (the
     * tests themselves run on the UI thread).
     *
     * @throws Exception if the package-private shortcut cannot be reached via
     *             reflection or if awaiting the delegate launch times out
     */
    @Test
    public void launch_should_build_configuration_and_delegate_launch_for_several_selected_tests() throws Exception
    {
        IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        IType barTest = context.getPrimaryTypeHandler("com.BarTest").get();

        List<Object[]> delegateInvocations = new CopyOnWriteArrayList<>();
        AtomicReference<ILaunch> createdLaunch = new AtomicReference<>();

        try (MockedConstruction<JUnitTestSelectionLaunchConfigurationDelegate> delegate = mockConstruction(JUnitTestSelectionLaunchConfigurationDelegate.class, (mock, constructionContext) -> {
            doAnswer(invocation -> {
                delegateInvocations.add(new Object[] { invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2) });
                return null;
            }).when(mock).launch(any(ILaunchConfiguration.class), anyString(), any(ILaunch.class), any());
        }))
        {
            // the shortcut class is package-private and cannot even be
            // referenced from this bundle: only reflection can reach it
            Object shortcut;
            Class< ? > shortcutClass;
            try
            {
                shortcutClass = Class.forName("org.moreunit.launch.JUnitTestSelectionLaunchShortcut");
                var constructor = shortcutClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                shortcut = constructor.newInstance();
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }

            try
            {
                // the declaring class is not accessible from this bundle,
                // hence getDeclaredMethod + setAccessible
                var launchMethod = shortcutClass.getDeclaredMethod("launch", org.eclipse.jface.viewers.ISelection.class, String.class);
                launchMethod.setAccessible(true);
                launchMethod.invoke(shortcut, new StructuredSelection(Arrays.asList(fooTest, barTest)), "run");
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }

            await(() -> assertEquals(1, delegateInvocations.size()));

            Object[] invocation = delegateInvocations.get(0);
            ILaunchConfiguration configuration = (ILaunchConfiguration) invocation[0];
            String mode = (String) invocation[1];
            ILaunch launch = (ILaunch) invocation[2];

            assertNotNull(configuration);
            assertEquals("run", mode);
            assertNotNull(launch);
            createdLaunch.set(launch);

            // the launch must have been registered with the launch manager
            assertTrue(Arrays.asList(DebugPlugin.getDefault().getLaunchManager().getLaunches()).contains(launch));
            IDebugTarget[] targets = launch.getDebugTargets();
            assertNotNull(targets);
        }
        finally
        {
            if(createdLaunch.get() != null)
            {
                DebugPlugin.getDefault().getLaunchManager().removeLaunch(createdLaunch.get());
            }
        }
    }

    private void await(Runnable assertion) throws Exception
    {
        Display display = Display.getDefault();
        long deadline = System.currentTimeMillis() + 30_000;
        AssertionError lastFailure = null;
        while (System.currentTimeMillis() < deadline)
        {
            display.readAndDispatch();
            try
            {
                assertion.run();
                return;
            }
            catch (AssertionError e)
            {
                lastFailure = e;
            }
            Thread.sleep(10);
        }
        throw lastFailure != null ? lastFailure : new AssertionError("condition not met in time");
    }

    @Test
    public void hasSameAttributes_should_compare_the_given_attributes() throws Exception
    {
        Class< ? > shortcutClass = Class.forName("org.moreunit.launch.JUnitTestSelectionLaunchShortcut");
        Method hasSameAttributes = shortcutClass
                .getDeclaredMethod("hasSameAttributes", ILaunchConfiguration.class, ILaunchConfiguration.class, String[].class);
        hasSameAttributes.setAccessible(true);

        ILaunchConfiguration config1 = mock(ILaunchConfiguration.class);
        ILaunchConfiguration config2 = mock(ILaunchConfiguration.class);
        when(config1.getAttribute("main", "")).thenReturn("com.FooTest");
        when(config2.getAttribute("main", "")).thenReturn("com.FooTest");

        assertTrue((boolean) hasSameAttributes.invoke(null, config1, config2, new String[] { "main" }));

        when(config2.getAttribute("main", "")).thenReturn("com.BarTest");
        assertFalse((boolean) hasSameAttributes.invoke(null, config1, config2, new String[] { "main" }));

        // attributes absent from both configurations fall back to the empty string
        when(config2.getAttribute("main", "")).thenReturn("com.FooTest");
        when(config1.getAttribute("proj", "")).thenReturn("");
        when(config2.getAttribute("proj", "")).thenReturn("");
        assertTrue((boolean) hasSameAttributes.invoke(null, config1, config2, new String[] { "main", "proj" }));

        // a CoreException while reading an attribute results in false
        ILaunchConfiguration failingConfig = mock(ILaunchConfiguration.class);
        when(failingConfig.getAttribute("main", "")).thenThrow(new org.eclipse.core.runtime.CoreException(org.eclipse.core.runtime.Status.CANCEL_STATUS));
        assertFalse((boolean) hasSameAttributes.invoke(null, failingConfig, config2, new String[] { "main" }));
    }
}
