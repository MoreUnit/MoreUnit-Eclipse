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

        String ext = ((IFile) receiver).getFileExtension();
        if(ext == null)
        {
            return false;
        }

        if(HAS_DEFAULT_SUPPORT.equals(method))
        {
            return ! MoreUnitCore.get().getLanguageExtensionManager().extensionExistsForLanguage(ext.toLowerCase()) || "false".equals(expectedValue);
        }

        return false;
    }
}
