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
import org.moreunit.log.LogHandler;
import org.moreunit.ui.MissingTestsViewPart;
import org.moreunit.util.PluginTools;

public class MissingClassTreeContentProvider implements ITreeContentProvider
{

    @Override
    public Object[] getChildren(Object parent)
    {
        if(parent instanceof final IPackageFragment packageFragment)
        {
            try
            {
            final Set<ICompilationUnit> compilationUnits = new HashSet<>();
            for (final ICompilationUnit compilationUnit : packageFragment.getCompilationUnits())
            {
                if(compilationUnit.findPrimaryType() != null)
                {
                    final ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                    if(! TypeFacade.isTestCase(compilationUnit) && ! classTypeFacade.hasTestCase())
                    {
                        compilationUnits.add(compilationUnit);
                    }
                }
            }
            return compilationUnits.stream().sorted(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER)).toArray(ICompilationUnit[]::new);
        }
        catch (final JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        }
        return null;
    }

    @Override
    public Object getParent(Object child)
    {
        if(child instanceof final ICompilationUnit unit)
        {
            return unit.getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object parent)
    {
        return ! (parent instanceof ICompilationUnit);
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        final Set<IPackageFragment> packages = new HashSet<>();
        if(inputElement instanceof final MissingTestsViewPart missingTestsViewPart)
        {
            final IJavaProject javaProject = missingTestsViewPart.getSelectedJavaProject();
            if(javaProject != null)
            {
                final List<IPackageFragmentRoot> allSourceFolderFromProject = PluginTools.getAllSourceFolderFromProject(javaProject);
                for (final IPackageFragmentRoot sourceFolder : allSourceFolderFromProject)
                {
                    try
                    {
                        final IJavaElement[] children = sourceFolder.getChildren();
                        for (final IJavaElement javaPackage : children)
                        {
                            final ICompilationUnit[] compilationUnits = ((IPackageFragment) javaPackage).getCompilationUnits();
                            for (final ICompilationUnit compilationUnit : compilationUnits)
                            {
                                if(compilationUnit.findPrimaryType() != null)
                                {
                                    final ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                                    if(! TypeFacade.isTestCase(compilationUnit) && ! classTypeFacade.hasTestCase())
                                    {
                                        packages.add((IPackageFragment) javaPackage);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (final JavaModelException e)
                    {
                        LogHandler.getInstance().handleExceptionLog(e);
                    }
                }
            }
        }
        return packages.stream().sorted(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER)).toArray(IJavaElement[]::new);
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    @Override
    public void dispose()
    {
    }

}
