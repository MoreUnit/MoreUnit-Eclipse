package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.BaseTools;
import org.moreunit.util.PluginTools;
import org.moreunit.util.StringConstants;

public class NewTestCaseWizard extends NewClassyWizard
{
    private final IJavaProject project;
    private final Preferences preferences;
    private final NewTestCaseWizardParticipatorManager participatorManager;

    private MoreUnitWizardPageOne pageOne;
    private NewTestCaseWizardPageTwo pageTwo;
    private NewTestCaseWizardContext context;
    private NewTestCaseWizardComposer wizardComposer;

    public NewTestCaseWizard(final IType element)
    {
        super(element);

        this.project = element.getJavaProject();
        this.preferences = Preferences.getInstance();
        this.participatorManager = new NewTestCaseWizardParticipatorManager();
    }

    @Override
    public void addPages()
    {
        this.pageTwo = new NewTestCaseWizardPageTwo();
        this.pageOne = new MoreUnitWizardPageOne(this.pageTwo, this.preferences, project);
        this.pageOne.setWizard(this);
        this.pageTwo.setWizard(this);
        this.pageOne.init(new StructuredSelection(getType()));

        IPackageFragment fragment = initialisePackageFragment();

        this.context = new NewTestCaseWizardContext(getType(), fragment);
        this.wizardComposer = participatorManager.createWizardComposer(context);
        this.wizardComposer.registerBasePage(INewTestCaseWizardPage.TEST_CASE_PAGE, this.pageOne);
        this.wizardComposer.registerBasePage(INewTestCaseWizardPage.TEST_METHODS_PAGE, this.pageTwo);
        this.wizardComposer.compose(this);
    }

    @Override
    public void createPageControls(Composite pageContainer)
    {
        super.createPageControls(pageContainer);
        // Eclipse 3.1.x does not support junit 4
        try
        {
            this.pageOne.setJUnit4(this.preferences.shouldUseJunit4Type(project), true);
        }
        catch (NoSuchMethodError error)
        {
        }

        String testSuperClass = getTestSuperClass();
        if(testSuperClass != null)
            this.pageOne.setSuperClass(testSuperClass, true);

        this.pageOne.setTypeName(getTestCaseClassName(), true);
        this.pageOne.setPackageFragmentRoot(getSourceFolderForUnitTest(), true);
    }

    private String getTestCaseClassName()
    {
        String classUnderTest = pageOne.getClassUnderTestText();

        if(classUnderTest.length() == 0)
            return StringConstants.EMPTY_STRING;

        return getFirstPrefix() + Signature.getSimpleName(classUnderTest) + getFirstSuffix();
    }

    private String getFirstSuffix()
    {
        String[] suffixes = preferences.getSuffixes(project);
        String suffix = StringConstants.EMPTY_STRING;
        if(suffixes.length > 0)
        {
            suffix = suffixes[0];
        }
        return suffix;
    }

    private String getFirstPrefix()
    {
        String[] prefixes = preferences.getPrefixes(project);
        String prefix = StringConstants.EMPTY_STRING;
        if(prefixes.length > 0)
        {
            prefix = prefixes[0];
        }
        return prefix;
    }

    private String getTestSuperClass()
    {
        String result = this.preferences.getTestSuperClass(project);

        if(BaseTools.isStringTrimmedEmpty(result) && preferences.shouldUseJunit3Type(project))
            return null;

        return result;
    }

    private IPackageFragmentRoot getSourceFolderForUnitTest()
    {
        return preferences.getTestSourceFolder(project, getPackageFragmentRoot());
    }

    private IPackageFragment initialisePackageFragment()
    {
        IPackageFragment mainPackageFragment = this.pageOne.getPackageFragment();
        String fragmentName = PluginTools.getTestPackageName(mainPackageFragment.getElementName(), preferences, project);
        IPackageFragmentRoot root = Preferences.getInstance().getTestSourceFolder(project, (IPackageFragmentRoot) mainPackageFragment.getParent());
        
        IPackageFragment testPackageFragment = root.getPackageFragment(fragmentName);
        this.pageOne.setPackageFragment(testPackageFragment, true);
        return testPackageFragment;
    }

    @Override
    public IType createClass() throws CoreException, InterruptedException
    {
        this.pageOne.createType(new NullProgressMonitor());
        return this.pageOne.getCreatedType();
    }

    @Override
    protected IPackageFragmentRoot getPackageFragmentRoot()
    {
        return this.pageOne.getPackageFragmentRoot();
    }

    @Override
    protected void typeCreated(IType createdType)
    {
        super.typeCreated(createdType);

        NewTestCaseWizardContext ctxt = getContext();
        ctxt.setCreatedTestCase(createdType);
        participatorManager.testCaseCreated(ctxt);
    }

    private NewTestCaseWizardContext getContext()
    {
        if(context == null)
        {
            throw new IllegalStateException("Context is null. It should not be retrieved before addPages() has been called.");
        }
        return context;
    }

    @Override
    public boolean performCancel()
    {
        boolean cancelationAccepted = super.performCancel();
        if(cancelationAccepted)
        {
            participatorManager.testCaseCreationCanceled(getContext());
        }
        return cancelationAccepted;
    }

    @Override
    protected void creationAborted()
    {
        super.creationAborted();
        participatorManager.testCaseCreationAborted(getContext());
    }
}