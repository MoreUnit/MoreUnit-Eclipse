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
import org.moreunit.util.BaseTools;

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
        if(! BaseTools.isStringTrimmedEmpty(prefix) && packageName.startsWith(prefix))
        {
            packageName = removePrefix(packageName, prefix + ".");
        }

        String suffix = Preferences.getInstance().getTestPackageSuffix(projectUnderTest);
        if(! BaseTools.isStringTrimmedEmpty(suffix) && packageName.endsWith(suffix))
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
            if(! BaseTools.isStringTrimmedEmpty(suffix) && name.endsWith(suffix))
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
            if(! BaseTools.isStringTrimmedEmpty(prefix) && name.startsWith(prefix))
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