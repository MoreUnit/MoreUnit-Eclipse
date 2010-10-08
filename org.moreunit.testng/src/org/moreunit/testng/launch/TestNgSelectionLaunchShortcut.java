package org.moreunit.testng.launch;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.testng.eclipse.launch.TestNGLaunchShortcut;

/**
 * A modified version of {@link TestNGLaunchShortcut} that can launch a
 * collection of test case classes.
 */
public class TestNgSelectionLaunchShortcut implements ILaunchShortcut
{

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers
     * .ISelection, java.lang.String)
     */
    public void launch(ISelection selection, String mode)
    {
        StructuredSelection s = (StructuredSelection) selection;
        if(selection instanceof StructuredSelection && s.size() > 1)
        {
            IJavaElement element = (IJavaElement) s.getFirstElement();
            if(null != element)
            {
                IJavaProject ijp = element.getJavaProject();
                TestNgSelectionLaunchUtil.launchTypesConfiguration(ijp, asTypes(s), mode);
            }
        }
    }

    private IType[] asTypes(StructuredSelection selection)
    {
        Object[] elements = selection.toArray();
        IType[] types = new IType[elements.length];
        for (int i = 0; i < elements.length; i++)
        {
            types[i] = (IType) elements[i];
        }
        return types;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart,
     * java.lang.String)
     */
    public void launch(IEditorPart arg0, String arg1)
    {
        throw new UnsupportedOperationException();
    }

}
