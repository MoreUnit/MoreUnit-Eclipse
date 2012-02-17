package org.moreunit.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.PluginTools;
import org.moreunit.util.SearchScopeSingelton;
import org.moreunit.util.SearchTools;
import org.moreunit.util.TestCaseDiviner;

public class MissingClassPage extends Page implements IElementChangedListener
{
    private ICompilationUnit compilationUnit;

    public MissingClassPage(ICompilationUnit compilationUnit)
    {
        super();
        JavaCore.addElementChangedListener(this);
        this.compilationUnit = compilationUnit;
    }

    @Override
    public void createControl(Composite parent)
    {
        Combo combo = new Combo(parent, SWT.NONE);
        combo.setItems(new String[] { "A", "B", "C" });
    }

    @Override
    public Control getControl()
    {
        return null;
    }

    @Override
    public void setFocus()
    {
    }

    public IType getInputType()
    {
        return compilationUnit.findPrimaryType();
    }

    public Set<IType> getClassesNotUnderTest()
    {
        try
        {
            Set<IType> results = SearchTools.searchFor("*", compilationUnit, getSearchScope());
            HashSet<IType> classesWithoutTestCases = new HashSet<IType>();
            for (IType type : results)
            {
                if(! TestCaseTypeFacade.isTestCase(type))
                {
                    TestCaseDiviner testCaseDiviner = new TestCaseDiviner(compilationUnit, Preferences.getInstance(), type);
                    if(testCaseDiviner.getMatches(false).isEmpty())
                    {
                        classesWithoutTestCases.add(type);
                    }
                }
            }
            return classesWithoutTestCases;
        }
        catch (JavaModelException e)
        {
        }
        catch (CoreException e)
        {
        }
        return null;
    }

    public void elementChanged(ElementChangedEvent arg0)
    {
    }

    private IJavaSearchScope getSearchScope()
    {
        IPackageFragmentRoot sourceFolder = PluginTools.getSourceFolder(compilationUnit);
        return SearchScopeSingelton.getInstance().getSearchScope(sourceFolder);
    }

}
