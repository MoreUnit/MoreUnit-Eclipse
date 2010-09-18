/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.extensionpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.handler.AddTestMethodContext;
import org.moreunit.log.LogHandler;

/**
 * Runs client extensions to the extension point
 * <code>addTestmethodParticipator</code> allowing to modify the created test
 * method.
 * <p>
 * This class is a singleton. We guarantee, that clients to this extension point
 * will run never in parallel.
 * 
 * @author andreas 10.08.2010
 */
public final class AddTestMethodParticipatorHandler
{

    /*
     * Singleton-Instance.
     */
    private static volatile AddTestMethodParticipatorHandler instance = null;

    /*
     * IDs.
     */
    private static final String extensionName = "addTestmethodParticipator";
    private static final String extensionID = MoreUnitPlugin.PLUGIN_ID + "." + extensionName;

    /**
     * Get singleton instance.
     * 
     * @return {@link AddTestMethodParticipatorHandler}.
     */
    public static AddTestMethodParticipatorHandler getInstance()
    {

        // Double checked lock
        if(instance == null)
        {
            synchronized (AddTestMethodParticipatorHandler.class)
            {
                if(instance == null)
                {
                    instance = new AddTestMethodParticipatorHandler();
                }
            }
        }

        // Get instance
        return instance;
    }

    /**
     * Constructor for AddTestMethodParticipatorHandler.
     */
    private AddTestMethodParticipatorHandler()
    {
        // Avoid instancing from external classes
    }

    /**
     * Try to find extensions to the extension point and, if any, run them.
     * 
     * @param testMethod Test method.
     * @param methodUnderTest Method under test.
     */
    public IAddTestMethodContext callExtension(final IMethod testMethod, IMethod methodUnderTest)
    {

        return callExtension(new AddTestMethodContext(testMethod, methodUnderTest));
    }

    /**
     * Try to find extensions to the extension point and, if any, run them.
     * 
     * @param testClass Test class.
     * @param testMethod Test method.
     * @param classUnderTest Class under test.
     * @param methodUnderTest Method under test.
     * @param newTestClassCreated Is a new test class created?
     */
    public IAddTestMethodContext callExtension(final ICompilationUnit testClass, final IMethod testMethod, ICompilationUnit classUnderTest, IMethod methodUnderTest, boolean newTestClassCreated)
    {

        return callExtension(new AddTestMethodContext(testClass, testMethod, classUnderTest, methodUnderTest, newTestClassCreated));
    }

    /**
     * Try to find extensions to the extension point and, if any, run them.
     * 
     * @param context Context for extension runner.
     */
    public IAddTestMethodContext callExtension(final IAddTestMethodContext context)
    {

        // Ignore all exceptions
        try
        {
            doCallExtension(context);
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        // In any case, get context back
        return context;
    }

    /**
     * Call extension point for modification of created testmethod.
     * 
     * @param context Testmethodcontext.
     * @throws CoreException Error.
     */
    private synchronized void doCallExtension(final IAddTestMethodContext context) throws CoreException
    {

        // Get extensions
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);

        // Run all extensions found
        for (IConfigurationElement e : config)
        {

            // Create Object from class definition
            final Object extension = e.createExecutableExtension("class");
            LogHandler.getInstance().handleInfoLog("Found extension to " + extensionName + ": " + extension.getClass());

            // Castable to interface?
            if(! (extension instanceof IAddTestMethodParticipator))
            {
                LogHandler.getInstance().handleWarnLog("Bad class for extension point");
                continue;
            }

            // Create safe runner
            ISafeRunnable runnable = new ISafeRunnable()
            {
                public void handleException(Throwable throwable)
                {
                    LogHandler.getInstance().handleExceptionLog("Error running extension", throwable);
                }

                public void run() throws Exception
                {
                    LogHandler.getInstance().handleInfoLog("Run extension");
                    ((IAddTestMethodParticipator) extension).addTestMethod(context);
                }
            };

            // Run ...
            SafeRunner.run(runnable);
        }
    }
}
