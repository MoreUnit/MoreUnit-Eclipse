package org.moreunit.core.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * <p>
 * Creates {@link GenericPropertyPage}s, using data passed to this factory to
 * configure the pages. Authorized data are: the language ID (i.e. the language
 * file extension) and - optionally - the page description (unfortunately, title
 * of property pages cannot be different of the name of their node in the tree).
 * </p>
 * <p>
 * For instance, here is the code to create a property page for the file
 * extension "someFileExtension", with the description
 * "Description of the page":
 * </p>
 * 
 * <pre>
 * &lt;extension point="org.eclipse.ui.propertyPages"&gt;
 *       &lt;page id="org.moreunit.core.properties.otherLanguagesPage"
 *          name="Name of the node in the tree and title of the page"
 *          class="org.moreunit.core.preferences.PropertyPageFactory:someFileExtension:Description of the page"/&gt;
 *    &lt;/extension&gt;
 * </pre>
 */
public class PropertyPageFactory implements IExecutableExtension, IExecutableExtensionFactory
{
    private String languageId;
    private String description;

    public Object create() throws CoreException
    {
        return languageId == null ? null : createPage(languageId, description);
    }

    protected GenericPropertyPage createPage(String langId, String desc)
    {
        return new GenericPropertyPage(langId, desc);
    }

    public void setInitializationData(IConfigurationElement cfg, String name, Object data) throws CoreException
    {
        if(data instanceof String)
        {
            String[] parts = ((String) data).split(":");
            languageId = parts[0];
            if(parts.length > 1)
            {
                description = parts[1];
            }
        }
    }
}
