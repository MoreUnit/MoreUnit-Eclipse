package org.moreunit.core.extension;

import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.log.Logger;
import org.moreunit.core.util.ExtendedSafeRunner;
import org.moreunit.core.util.ExtendedSafeRunner.GenericRunnable;

public class JumperExtensionManager
{
    private final ExtendedSafeRunner safeRunner = new ExtendedSafeRunner();
    private final LanguageExtensionManager languageExtensionMgr;
    private final Logger logger;

    public JumperExtensionManager(LanguageExtensionManager languageExtensionMgr, Logger logger)
    {
        this.languageExtensionMgr = languageExtensionMgr;
        this.logger = logger;
    }

    public JumpResult jump(final IJumpContext context)
    {
        for (IJumper jumper : languageExtensionMgr.getJumpersFor(context.getSelectedFile().getFileExtension()))
        {
            JumpResult result = safeRunner.applyTo(jumper, new GenericRunnable<IJumper, JumpResult>()
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

            // Maybe an exception occurred, or the extension failed to return a
            // result. In both cases, we do not want to handle the jumping twice
            if(result == null)
            {
                return JumpResult.done();
            }

            if(result.isDone())
            {
                return result;
            }
        }

        return JumpResult.notDone();
    }
}
