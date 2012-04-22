package org.moreunit.core.commands;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.log.Logger;
import org.moreunit.core.utils.ExtendedSafeRunner;
import org.moreunit.core.utils.ExtendedSafeRunner.GenericRunnable;

public class JumperExtensionManager
{
    private static final String EXTENSION_ID = MoreUnitCore.PLUGIN_ID + ".languages";
    private static final String JUMPER_EL = "jumper";
    private static final String CLASS_ATTR = "class";
    private static final String FILE_EXTENSION_ATTR = "fileExtension";

    private final ExtendedSafeRunner safeRunner = new ExtendedSafeRunner();
    private final Logger logger;

    public JumperExtensionManager(Logger logger)
    {
        this.logger = logger;
    }

    public JumpResult jump(final IJumpContext context)
    {
        for (IConfigurationElement cfg : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID))
        {
            if(! context.getSelectedFile().getFileExtension().equalsIgnoreCase(cfg.getAttribute(FILE_EXTENSION_ATTR)))
            {
                continue;
            }

            try
            {
                for (IConfigurationElement el : cfg.getChildren(JUMPER_EL))
                {
                    final Object jumper = el.createExecutableExtension(CLASS_ATTR);
                    if(! (jumper instanceof IJumper))
                    {
                        logger.warn("Element " + JUMPER_EL + " of point " + EXTENSION_ID + " does not support class: " + el.getClass());
                        continue;
                    }

                    JumpResult result = safeRunner.applyTo((IJumper) jumper, new GenericRunnable<IJumper, JumpResult>()
                    {
                        @Override
                        public void handleException(Throwable t, IJumper j)
                        {
                            logger.warn("Error calling extension: " + j.getClass().getName() + ".jump()", t);
                        }

                        @Override
                        public JumpResult run(IJumper j) throws Exception
                        {
                            logger.debug("Calling extension: " + j.getClass().getName() + ".jump()");
                            return j.jump(context);
                        }
                    });

                    if(result != null && result.isDone())
                    {
                        return result;
                    }
                }
            }
            catch (Exception e)
            {
                logger.warn("Could not load extension from plug-in \"" + cfg.getContributor().getName() + "\" for point \"" + EXTENSION_ID + "\": " + e.getMessage());
                continue;
            }
        }

        return JumpResult.notDone();
    }
}
