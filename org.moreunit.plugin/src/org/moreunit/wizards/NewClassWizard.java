package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.util.FeatureDetector;
import org.moreunit.util.JavaType;

public class NewClassWizard extends NewClassyWizard
{
    private final IPackageFragmentRoot mainSrcFolder;

    private NewClassWizardPage newClassWizardPage;
    private ProjectPreferences preferences;

    public NewClassWizard(IType testCase)
    {
        super(testCase);
        IJavaProject projectUnderTest = Preferences.getInstance().getMainProject(testCase.getJavaProject());
        preferences = Preferences.forProject(projectUnderTest);
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
        return preferences.getMainSourceFolder(testSrcFolder);
    }

    @Override
    public void addPages()
    {
        if("groovy".equals(getType().getPath().getFileExtension()))
        {
            this.newClassWizardPage = new FeatureDetector().createNewGroovyClassWizardPageIfPossible();
        }

        if(this.newClassWizardPage == null)
        {
            this.newClassWizardPage = new NewClassWizardPage();
        }
        this.newClassWizardPage.setWizard(this);
        this.newClassWizardPage.init(new StructuredSelection(getType()));

        JavaType cutName = preferences.getTestClassNamePattern().nameClassTestedBy(getType());
        this.newClassWizardPage.setTypeName(cutName.getSimpleName(), true);
        this.newClassWizardPage.setPackageFragment(mainSrcFolder.getPackageFragment(cutName.getQualifier()), true);

        this.newClassWizardPage.setPackageFragmentRoot(mainSrcFolder, true);
        this.newClassWizardPage.setEnclosingType(null, false);
        this.newClassWizardPage.setSuperClass("", true);
        addPage(this.newClassWizardPage);
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
