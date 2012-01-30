package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.preferences.Preferences;

public class NewClassWizard extends NewClassyWizard
{
    private final IJavaProject projectUnderTest;
    private final IPackageFragmentRoot mainSrcFolder;
    
    private NewClassWizardPage newClassWizardPage;
    
    public NewClassWizard(IType testCase)
    {
        super(testCase);
        projectUnderTest = Preferences.getInstance().getMainProject(testCase.getJavaProject());
        mainSrcFolder = getSourceFolderForCut(testCase);
    }
    
    private IPackageFragmentRoot getSourceFolderForCut(IType testCase)
    {
        String key = getPackageFragmentRootKey();
        String root = getDialogSettings().get(key);
        IPackageFragmentRoot fragment = (IPackageFragmentRoot) JavaCore.create(root);
        if(fragment != null && fragment.exists())
        {
            return fragment;
        }
        
        IPackageFragmentRoot testSrcFolder = (IPackageFragmentRoot) testCase.getCompilationUnit().getParent().getParent();
        return Preferences.getInstance().getMainSourceFolder(projectUnderTest, testSrcFolder);
    }

    @Override
    public void addPages()
    {
        this.newClassWizardPage = new NewClassWizardPage();
        this.newClassWizardPage.setWizard(this);
        this.newClassWizardPage.init(new StructuredSelection(getType()));
        this.newClassWizardPage.setTypeName(getPotentialTypeName(), true);
        this.newClassWizardPage.setPackageFragment(getPackage(), true);
        this.newClassWizardPage.setPackageFragmentRoot(mainSrcFolder, true);
        this.newClassWizardPage.setEnclosingType(null, false);
        this.newClassWizardPage.setSuperClass("", true);
        addPage(this.newClassWizardPage);
    }

    private IPackageFragment getPackage()
    {
        IPackageFragment packageFragment = getType().getPackageFragment();
        String packageName = packageFragment.getElementName();

        String prefix = Preferences.getInstance().getTestPackagePrefix(projectUnderTest);
        if(packageName.startsWith(prefix))
        {
            packageName = removePrefix(packageName, prefix + ".");
        }

        String suffix = Preferences.getInstance().getTestPackageSuffix(projectUnderTest);
        if(packageName.endsWith(suffix))
        {
            packageName = removeSuffix(packageName, "." + suffix);
        }

        return mainSrcFolder.getPackageFragment(packageName);
    }

    private String getPotentialTypeName()
    {
        Preferences preferences = Preferences.getInstance();

        String[] prefixes = preferences.getPrefixesOrderedByDescLength(projectUnderTest);
        String[] suffixes = preferences.getSuffixesOrderedByDescLength(projectUnderTest);

        String name = getType().getElementName();
        name = removePrefix(name, prefixes);
        name = removeSuffix(name, suffixes);
        return name;
    }

    public static String removeSuffix(String name, String[] possibleSuffixes)
    {
        for (String suffix : possibleSuffixes)
        {
            if(name.endsWith(suffix))
            {
                return removeSuffix(name, suffix);
            }
        }
        return name;
    }

    private static String removeSuffix(String name, String suffix)
    {
        return name.substring(0, name.length() - suffix.length());
    }

    public static String removePrefix(String name, String[] possiblePrefixes)
    {
        for (String prefix : possiblePrefixes)
        {
            if(name.startsWith(prefix))
            {
                return removePrefix(name, prefix);
            }
        }
        return name;
    }

    private static String removePrefix(String name, String prefix)
    {
        return name.substring(prefix.length());
    }

    @Override
    public IType createClass() throws CoreException, InterruptedException
    {
        this.newClassWizardPage.createType(new NullProgressMonitor());
        return this.newClassWizardPage.getCreatedType();
    }

    @Override
    protected IPackageFragmentRoot getPackageFragmentRoot()
    {
        return this.newClassWizardPage.getPackageFragmentRoot();
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.5 2009/01/23 21:20:21 gianasista
// Organize Imports
//
// Revision 1.4 2008/02/29 21:34:00 gianasista
// Minor refactorings
//
// Revision 1.3 2008/02/04 20:12:24 gianasista
// Bugfix: project specific settings
//
// Revision 1.2 2007/11/19 21:15:01 gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:29 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.2 2006/05/13 18:30:49 gianasista
// Preferences as singelton (protected constructor for testing purposes)
//
// Revision 1.1 2006/05/12 22:33:41 channingwalton
// added class creation wizards if type to jump to does not exist
//
