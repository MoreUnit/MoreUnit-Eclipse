package org.moreunit.handler;

import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;

public class Jumper implements IJumper
{
    public JumpResult jump(IJumpContext context)
    {
        JumpActionExecutor.getInstance().executeJumpAction(context.getSelectedFile());
        return JumpResult.done();
    }
}
