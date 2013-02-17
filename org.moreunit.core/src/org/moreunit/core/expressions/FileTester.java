package org.moreunit.core.expressions;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;

public class FileTester extends PropertyTester
{
    private static final String HAS_DEFAULT_SUPPORT = "hasDefaultSupport";

    private Workspace workspace;

    public FileTester()
    {
        this($().getWorkspace());
    }

    public FileTester(Workspace workspace)
    {
        this.workspace = workspace;
    }

    public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
    {
        if(! (receiver instanceof IFile))
        {
            return false;
        }

        if(HAS_DEFAULT_SUPPORT.equals(method))
        {
            SrcFile file = workspace.toSrcFile((IFile) receiver);
            return "false".equals(expectedValue) || file.hasDefaultSupport();
        }

        return false;
    }
}
