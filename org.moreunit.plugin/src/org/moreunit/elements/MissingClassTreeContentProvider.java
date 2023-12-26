package org.moreunit.elements;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Object[] getChildren(Object parent)
    {
        if(parent instanceof IPackageFragment packageFragment)
        {
            try
            {
            Set<ICompilationUnit> compilationUnits = new HashSet<>();
            for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits())
            {
                if(compilationUnit.findPrimaryType() != null)
                {
                    ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                    if(! TypeFacade.isTestCase(compilationUnit) && ! classTypeFacade.hasTestCase())
                    {
                        compilationUnits.add(compilationUnit);
                    }
                }
            }
            return compilationUnits.stream().sorted(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER)).toArray(ICompilationUnit[]::new);
        }
        catch (JavaModelException e)
        {
            e.printStackTrace();
        }
        }
        return null;
    }

    public Object getParent(Object child)
    {
        if(child instanceof ICompilationUnit unit)
        {
            return unit.getParent();
        }
        return null;
    }

    public boolean hasChildren(Object parent)
    {
        return ! (parent instanceof ICompilationUnit);
    }

    public Object[] getElements(Object inputElement)
    {
        Set<IPackageFragment> packages = new HashSet<>();
        if(inputElement instanceof MissingTestsViewPart missingTestsViewPart)
        {
            IJavaProject javaProject = missingTestsViewPart.getSelectedJavaProject();
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
                                if(compilationUnit.findPrimaryType() != null)
                                {
                                    ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                                    if(! TypeFacade.isTestCase(compilationUnit) && ! classTypeFacade.hasTestCase())
                                    {
                                        packages.add((IPackageFragment) javaPackage);
                                        break;
                                    }
                                }
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
        return packages.stream().sorted(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER)).toArray(IJavaElement[]::new);
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    public void dispose()
    {
    }

}
