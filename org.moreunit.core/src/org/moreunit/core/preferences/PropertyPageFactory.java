package org.moreunit.core.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class PropertyPageFactory implements IExecutableExtension, IExecutableExtensionFactory
{
    private String languageId;

    public Object create() throws CoreException
    {
        return languageId == null ? null : new GenericPropertyPage(languageId);
    }

    public void setInitializationData(IConfigurationElement cfg, String name, Object data) throws CoreException
    {
        if(data instanceof String)
        {
            languageId = (String) data;
        }
    }
}
