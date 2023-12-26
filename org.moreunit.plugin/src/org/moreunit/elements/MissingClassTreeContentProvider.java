package org.moreunit.elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
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
    private List<ICompilationUnit> units = new ArrayList<>();
    private Set<IJavaElement> packages = new LinkedHashSet<>();

    public MissingClassTreeContentProvider()
    {

    }

    public Object[] getChildren(Object parent)
    {
        if(parent instanceof IJavaElement pack)
        {
            return units.stream().filter(unit -> unit.getParent().equals(parent)).toArray();
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
        units = new ArrayList<>();
        packages.clear();

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
                                if(compilationUnit.findPrimaryType() != null)
                                {
                                    ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                                    if(! TypeFacade.isTestCase(compilationUnit) && ! classTypeFacade.hasTestCase())
                                    {
                                        units.add(compilationUnit);
                                        packages.add(javaPackage);
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

        // sort compilation units. use toString() because getName() is internal
        units = units.stream().sorted(Comparator.comparing(unit -> unit.toString(), String.CASE_INSENSITIVE_ORDER)).toList();

        return packages.stream().toArray(IJavaElement[]::new);
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    public void dispose()
    {
    }

}
