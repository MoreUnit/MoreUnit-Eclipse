package org.moreunit.wizards;

import java.util.Collection;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.util.ExtendedSafeRunner;
import org.moreunit.core.util.ExtendedSafeRunner.GenericRunnable;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardParticipator;
import org.moreunit.log.LogHandler;

public class NewTestCaseWizardParticipatorManager
{
    private static final String EXTENSION_ID = MoreUnitPlugin.PLUGIN_ID + ".newTestCaseWizardParticipator";

    private final LogHandler logger;
    private final ExtendedSafeRunner runner;

    public NewTestCaseWizardParticipatorManager()
    {
        this(LogHandler.getInstance(), new ExtendedSafeRunner());
    }

    public NewTestCaseWizardParticipatorManager(LogHandler logger, ExtendedSafeRunner extendedSafeRunner)
    {
        this.logger = logger;
        this.runner = extendedSafeRunner;
    }

    public NewTestCaseWizardComposer createWizardComposer(final INewTestCaseWizardContext context)
    {
        NewTestCaseWizardComposer composer = new NewTestCaseWizardComposer();
        addExtensionPagesToComposer(composer, context, getParticipators());
        return composer;
    }

    // public for testing
    public void addExtensionPagesToComposer(final NewTestCaseWizardComposer composer, final INewTestCaseWizardContext context, Collection<INewTestCaseWizardParticipator> participators)
    {
        runner.applyTo(participators, new GenericRunnable<INewTestCaseWizardParticipator, Void>()
        {
            public void handleException(Throwable throwable, INewTestCaseWizardParticipator participator)
            {
                logger.handleExceptionLog("Error calling extension: " + participator.getClass().getName() + ".getPages()", throwable);
            }

            public Void run(INewTestCaseWizardParticipator participator) throws Exception
            {
                logger.handleInfoLog("Calling extension: " + participator.getClass().getName() + ".getPages()");
                composer.registerExtensionPages(participator.getPages(context));
                return null;
            }
        });
    }

    private Collection<INewTestCaseWizardParticipator> getParticipators()
    {
        TreeMap<String, INewTestCaseWizardParticipator> participatorsOrderedById = new TreeMap<String, INewTestCaseWizardParticipator>();

        for (IConfigurationElement configuration : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID))
        {
            Object extension = null;
            try
            {
                extension = configuration.createExecutableExtension("class");
            }
            catch (CoreException e)
            {
                logger.handleWarnLog("Error in extension point " + EXTENSION_ID + ": " + e.getMessage());
                continue;
            }

            if(! (extension instanceof INewTestCaseWizardParticipator))
            {
                logger.handleWarnLog("Extension point " + EXTENSION_ID + " does not support class: " + extension.getClass());
                continue;
            }

            participatorsOrderedById.put(configuration.getNamespaceIdentifier(), (INewTestCaseWizardParticipator) extension);
        }

        return participatorsOrderedById.values();
    }

    public void testCaseCreationCanceled(INewTestCaseWizardContext context)
    {
        testCaseCreationCanceled(context, getParticipators());
    }

    // public for testing
    public void testCaseCreationCanceled(final INewTestCaseWizardContext context, Collection<INewTestCaseWizardParticipator> participators)
    {
        runner.applyTo(participators, new GenericRunnable<INewTestCaseWizardParticipator, Void>()
        {
            public void handleException(Throwable throwable, INewTestCaseWizardParticipator participator)
            {
                logger.handleExceptionLog("Error calling extension: " + participator.getClass().getName() + ".testCaseCreationCanceled()", throwable);
            }

            public Void run(INewTestCaseWizardParticipator participator) throws Exception
            {
                logger.handleInfoLog("Calling extension: " + participator.getClass().getName() + ".testCaseCreationCanceled()");
                participator.testCaseCreationCanceled(context);
                return null;
            }
        });
    }

    public void testCaseCreated(INewTestCaseWizardContext context)
    {
        testCaseCreated(context, getParticipators());
    }

    // public for testing
    public void testCaseCreated(final INewTestCaseWizardContext context, Collection<INewTestCaseWizardParticipator> participators)
    {
        runner.applyTo(participators, new GenericRunnable<INewTestCaseWizardParticipator, Void>()
        {
            public void handleException(Throwable throwable, INewTestCaseWizardParticipator participator)
            {
                logger.handleExceptionLog("Error calling extension: " + participator.getClass().getName() + ".testCaseCreated()", throwable);
            }

            public Void run(INewTestCaseWizardParticipator participator) throws Exception
            {
                logger.handleInfoLog("Calling extension: " + participator.getClass().getName() + ".testCaseCreated()");
                participator.testCaseCreated(context);
                return null;
            }
        });
    }

    public void testCaseCreationAborted(NewTestCaseWizardContext context)
    {
    }

    // public for testing
    public void testCaseCreationAborted(final INewTestCaseWizardContext context, Collection<INewTestCaseWizardParticipator> participators)
    {
        runner.applyTo(participators, new GenericRunnable<INewTestCaseWizardParticipator, Void>()
        {
            public void handleException(Throwable throwable, INewTestCaseWizardParticipator participator)
            {
                logger.handleExceptionLog("Error calling extension: " + participator.getClass().getName() + ".testCaseCreationAborted()", throwable);
            }

            public Void run(INewTestCaseWizardParticipator participator) throws Exception
            {
                logger.handleInfoLog("Calling extension: " + participator.getClass().getName() + ".testCaseCreationAborted()");
                participator.testCaseCreationAborted(context);
                return null;
            }
        });
    }
}
