package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.ui.MissingTestsViewPart;
import org.moreunit.util.PluginTools;

public class MissingClassTreeContentProvider implements ITreeContentProvider
{
    
    public MissingClassTreeContentProvider()
    {
        
    }

    public Object[] getChildren(Object arg0)
    {
        return null;
    }

    public Object getParent(Object arg0)
    {
        return null;
    }

    public boolean hasChildren(Object arg0)
    {
        return false;
    }

    public Object[] getElements(Object inputElement)
    {
        List<Object> elements = new ArrayList<Object>();

        if(inputElement instanceof MissingTestsViewPart)
        {
            IJavaProject javaProject = ((MissingTestsViewPart) inputElement).getSelectedJavaProject();
            if(javaProject != null)
            {
                List<IPackageFragmentRoot> allSourceFolderFromProject = PluginTools.getAllSourceFolderFromProject(javaProject);
                for (IPackageFragmentRoot sourceFolder : allSourceFolderFromProject)
                {
                    try
                    {
                        IJavaElement[] children = sourceFolder.getChildren();
                        for (IJavaElement javaPackage : children)
                        {
                            ICompilationUnit[] compilationUnits = ((IPackageFragment) javaPackage).getCompilationUnits();
                            for (ICompilationUnit compilationUnit : compilationUnits)
                            {
                                ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                                if(!TypeFacade.isTestCase(compilationUnit) && !classTypeFacade.hasTestCase())
                                    elements.add(compilationUnit);
                            }
                        }
                    }
                    catch (JavaModelException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return elements.toArray();
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    public void dispose()
    {
    }

}
