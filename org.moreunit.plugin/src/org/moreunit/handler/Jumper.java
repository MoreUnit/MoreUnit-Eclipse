package org.moreunit.handler;

import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;

public class Jumper implements IJumper
{
    private final JumpActionExecutor actionExecutor = JumpActionExecutor.getInstance();

    public JumpResult jump(IJumpContext context)
    {
        if(context.isFileOpenInEditor())
            actionExecutor.executeJumpAction(context.getOpenEditorPart());
        else
            actionExecutor.executeJumpAction(context.getSelectedFile());

        return JumpResult.done();
    }
}
