package org.moreunit.core.expressions;

import static org.moreunit.core.config.Module.$;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.extension.LanguageExtensionManager;

public class FileTester extends PropertyTester
{
    private static final String HAS_DEFAULT_SUPPORT = "hasDefaultSupport";

    private LanguageExtensionManager languageExtensionManager;

    public FileTester()
    {
        this($().getLanguageExtensionManager());
    }

    public FileTester(LanguageExtensionManager languageExtensionManager)
    {
        this.languageExtensionManager = languageExtensionManager;
    }

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
            return ! languageExtensionManager.extensionExistsForLanguage(ext.toLowerCase()) || "false".equals(expectedValue);
        }

        return false;
    }
}
