package org.moreunit.launch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.support.DialogHelper;

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
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        final IType barTest = context.getPrimaryTypeHandler("com.BarTest").get();

        final List<Object[]> delegateInvocations = new CopyOnWriteArrayList<>();
        final AtomicReference<ILaunch> createdLaunch = new AtomicReference<>();

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
                final var constructor = shortcutClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                shortcut = constructor.newInstance();
            }
            catch (final ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }

            try
            {
                // the declaring class is not accessible from this bundle,
                // hence getDeclaredMethod + setAccessible
                final var launchMethod = shortcutClass.getDeclaredMethod("launch", org.eclipse.jface.viewers.ISelection.class, String.class);
                launchMethod.setAccessible(true);
                launchMethod.invoke(shortcut, new StructuredSelection(Arrays.asList(fooTest, barTest)), "run");
            }
            catch (final ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }

            await(() -> assertEquals(1, delegateInvocations.size()));

            final Object[] invocation = delegateInvocations.get(0);
            final ILaunchConfiguration configuration = (ILaunchConfiguration) invocation[0];
            final String mode = (String) invocation[1];
            final ILaunch launch = (ILaunch) invocation[2];

            assertNotNull(configuration);
            assertEquals("run", mode);
            assertNotNull(launch);
            createdLaunch.set(launch);

            // the launch must have been registered with the launch manager
            assertTrue(Arrays.asList(DebugPlugin.getDefault().getLaunchManager().getLaunches()).contains(launch));
            final IDebugTarget[] targets = launch.getDebugTargets();
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
        final Display display = Display.getDefault();
        final long deadline = System.currentTimeMillis() + 30_000;
        AssertionError lastFailure = null;
        while (System.currentTimeMillis() < deadline)
        {
            display.readAndDispatch();
            try
            {
                assertion.run();
                return;
            }
            catch (final AssertionError e)
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
        final Class< ? > shortcutClass = Class.forName("org.moreunit.launch.JUnitTestSelectionLaunchShortcut");
        final Method hasSameAttributes = shortcutClass
                .getDeclaredMethod("hasSameAttributes", ILaunchConfiguration.class, ILaunchConfiguration.class, String[].class);
        hasSameAttributes.setAccessible(true);

        final ILaunchConfiguration config1 = mock(ILaunchConfiguration.class);
        final ILaunchConfiguration config2 = mock(ILaunchConfiguration.class);
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
        final ILaunchConfiguration failingConfig = mock(ILaunchConfiguration.class);
        when(failingConfig.getAttribute("main", "")).thenThrow(new org.eclipse.core.runtime.CoreException(org.eclipse.core.runtime.Status.CANCEL_STATUS));
        assertFalse((boolean) hasSameAttributes.invoke(null, failingConfig, config2, new String[] { "main" }));
    }

    @Test
    public void launch_should_delegate_to_super_launch_for_non_structured_selection() throws Exception
    {
        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

        final Object shortcut = newShortcut();
        final Method launchMethod = shortcut.getClass().getDeclaredMethod("launch", ISelection.class, String.class);
        launchMethod.setAccessible(true);
        try
        {
            launchMethod.invoke(shortcut, mock(ISelection.class), "run");
            fail("launching a non-structured selection should fail after delegating to the super implementation");
        }
        catch (final InvocationTargetException e)
        {
            // the super implementation shows a "no tests found" dialog and
            // returns, then the shortcut fails to read the test list from the
            // non-structured selection
            assertInstanceOf(ClassCastException.class, e.getCause());
        }
    }

    @Test
    public void launch_should_log_and_swallow_core_exception_from_test_collection() throws Exception
    {
        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

        final Object shortcut = newShortcut();
        final Method launchMethod = shortcut.getClass().getDeclaredMethod("launch", ISelection.class, String.class);
        launchMethod.setAccessible(true);
        assertDoesNotThrow(() -> {
            try
            {
                launchMethod.invoke(shortcut, new FailingTestSelection(), "run");
            }
            catch (final ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void launch_should_report_error_status_when_delegate_launch_fails() throws Exception
    {
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        final IType barTest = context.getPrimaryTypeHandler("com.BarTest").get();
        final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        final List<ILaunch> launchesBefore = Arrays.asList(launchManager.getLaunches());

        final AtomicReference<IStatus> jobResult = new AtomicReference<>();
        final JobChangeAdapter jobListener = new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if("MoreUnit".equals(event.getJob().getName()))
                {
                    jobResult.set(event.getResult());
                }
            }
        };
        Job.getJobManager().addJobChangeListener(jobListener);
        try (MockedConstruction<JUnitTestSelectionLaunchConfigurationDelegate> delegate = mockConstruction(JUnitTestSelectionLaunchConfigurationDelegate.class, (mock, constructionContext) -> {
            doThrow(new CoreException(new Status(IStatus.ERROR, "org.moreunit.test", "delegate boom"))).when(mock).launch(any(ILaunchConfiguration.class), anyString(), any(ILaunch.class), any());
        }))
        {
            final Object shortcut = newShortcut();
            final Method launchMethod = shortcut.getClass().getDeclaredMethod("launch", ISelection.class, String.class);
            launchMethod.setAccessible(true);
            launchMethod.invoke(shortcut, new StructuredSelection(Arrays.asList(fooTest, barTest)), "run");

            await(() -> assertNotNull(jobResult.get()));
            assertEquals(IStatus.ERROR, jobResult.get().getSeverity());
        }
        finally
        {
            Job.getJobManager().removeJobChangeListener(jobListener);
            for (final ILaunch launch : launchManager.getLaunches())
            {
                if(! launchesBefore.contains(launch))
                {
                    launchManager.removeLaunch(launch);
                }
            }
        }
    }

    @Test
    public void createDefaultLaunchConfig_should_create_single_test_configuration_for_one_member() throws Exception
    {
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();

        final Object shortcut = newShortcut();
        final Method factory = shortcut.getClass().getDeclaredMethod("createDefaultLaunchConfig", Collection.class);
        factory.setAccessible(true);
        final ILaunchConfigurationWorkingCopy config = (ILaunchConfigurationWorkingCopy) factory.invoke(shortcut, List.of(fooTest));

        assertNotNull(config);
        assertEquals("com.FooTest", config.getAttribute("org.eclipse.jdt.launching.MAIN_TYPE", ""));
    }

    @Test
    public void getDelegate_should_distinguish_single_test_from_test_selection() throws Exception
    {
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        final IType barTest = context.getPrimaryTypeHandler("com.BarTest").get();

        final Object shortcut = newShortcut();
        final Method factory = shortcut.getClass().getDeclaredMethod("getDelegate", Collection.class);
        factory.setAccessible(true);

        assertInstanceOf(JUnitLaunchConfigurationDelegate.class, factory.invoke(shortcut, List.of(fooTest)));
        assertInstanceOf(JUnitTestSelectionLaunchConfigurationDelegate.class, factory.invoke(shortcut, Arrays.asList(fooTest, barTest)));
    }

    @Test
    public void getConfiguration_should_reuse_saved_configuration_with_same_attributes() throws Exception
    {
        final IType fooTest = context.getPrimaryTypeHandler("com.FooTest").get();
        final IType barTest = context.getPrimaryTypeHandler("com.BarTest").get();
        final String projectName = context.getProjectHandler().get().getElementName();

        final Object shortcut = newShortcut();
        final Method defaultConfig = shortcut.getClass().getDeclaredMethod("createDefaultLaunchConfig", Collection.class);
        defaultConfig.setAccessible(true);
        final Method findExisting = shortcut.getClass().getDeclaredMethod("findExistingLaunchConfiguration", ILaunchConfigurationWorkingCopy.class);
        findExisting.setAccessible(true);
        final Method getConfiguration = shortcut.getClass().getDeclaredMethod("getConfiguration", Collection.class);
        getConfiguration.setAccessible(true);

        assertNull(findExisting.invoke(shortcut, defaultConfig.invoke(shortcut, List.of(fooTest))));

        final ILaunchConfiguration savedFooTest = ((ILaunchConfigurationWorkingCopy) defaultConfig.invoke(shortcut, List.of(fooTest))).doSave();
        final ILaunchConfiguration savedBarTest = ((ILaunchConfigurationWorkingCopy) defaultConfig.invoke(shortcut, List.of(barTest))).doSave();
        try
        {
            final ILaunchConfiguration reused = (ILaunchConfiguration) getConfiguration.invoke(shortcut, List.of(fooTest));

            assertNotNull(reused);
            assertEquals(savedFooTest.getName(), reused.getName());
            assertEquals(projectName, reused.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", ""));
            assertEquals("com.FooTest", reused.getAttribute("org.eclipse.jdt.launching.MAIN_TYPE", ""));
        }
        finally
        {
            savedFooTest.delete();
            savedBarTest.delete();
        }
    }

    private static Object newShortcut() throws Exception
    {
        // the shortcut class is package-private and cannot even be
        // referenced from this bundle: only reflection can reach it
        final Class< ? > shortcutClass = Class.forName("org.moreunit.launch.JUnitTestSelectionLaunchShortcut");
        final var constructor = shortcutClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * A selection whose element list cannot be built: {@link #toList()}
     * sneaks a {@link CoreException} past the compiler to exercise the
     * exception handling of the launch shortcut.
     */
    private static final class FailingTestSelection extends StructuredSelection
    {
        FailingTestSelection()
        {
            super(java.util.Collections.emptyList());
        }

        @Override
        public java.util.List< ? > toList()
        {
            return sneakyThrow(new CoreException(Status.CANCEL_STATUS));
        }

        @SuppressWarnings("unchecked")
        private static <T extends Throwable, R> R sneakyThrow(Throwable throwable) throws T
        {
            throw (T) throwable;
        }
    }
}
