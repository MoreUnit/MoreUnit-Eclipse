package org.moreunit.core.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class FileTester extends PropertyTester
{
    private static final String JUMP_COMMAND_ID = "org.moreunit.core.commands.jumpCommand";

    public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
    {
        if(! (receiver instanceof IFile))
        {
            return false;
        }

        IFile res = (IFile) receiver;

        if("supportsMoreUnitCommand".equals(method))
        {
            return JUMP_COMMAND_ID.equals(expectedValue) && "js".equals(res.getFileExtension());
        }

        return false;
    }
}
