package org.moreunit.properties;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.util.PluginTools;

/**
 * @author vera 03.03.2008 19:54:23
 */
public class UnitSourceFolderLabelProvider extends LabelProvider
{

    private JavaElementLabelProvider baseLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);

    private static final String SUFFIX_SOURCE = " (mapped source folder)";

    @Override
    public Image getImage(Object element)
    {
        if(element instanceof SourceFolderMapping)
        {
            IPackageFragmentRoot testFolder = ((SourceFolderMapping) element).getTestFolder();
            return baseLabelProvider.getImage(testFolder);
        }

        return baseLabelProvider.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
        if(element instanceof SourceFolderMapping)
        {
            IPackageFragmentRoot sourceFolder = ((SourceFolderMapping) element).getTestFolder();
            return getLabelForPackageFragmentRoot(sourceFolder);
        }

        if(element instanceof IPackageFragmentRoot)
        {
            return getLabelForPackageFragmentRoot((IPackageFragmentRoot) element) + SUFFIX_SOURCE;
        }

        return baseLabelProvider.getText(element);
    }

    private String getLabelForPackageFragmentRoot(IPackageFragmentRoot folder)
    {
        StringBuffer result = new StringBuffer();
        result.append(folder.getJavaProject().getElementName());
        result.append(StringConstants.SLASH);
        result.append(PluginTools.getPathStringWithoutProjectName(folder));

        return result.toString();
    }

}
