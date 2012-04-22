package org.moreunit.core.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.MoreUnitCore;

public class FileTester extends PropertyTester
{
    private static final String HAS_DEFAULT_SUPPORT = "hasDefaultSupport";

    public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
    {
        if(! (receiver instanceof IFile))
        {
            return false;
        }

        IFile file = (IFile) receiver;

        if(HAS_DEFAULT_SUPPORT.equals(method))
        {
            return !MoreUnitCore.get().getLanguageExtensionManager().extensionExistsForLanguage(file.getFileExtension().toLowerCase()) || "false".equals(expectedValue);
        }

        return false;
    }
}
