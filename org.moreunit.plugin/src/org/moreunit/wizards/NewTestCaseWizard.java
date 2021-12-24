package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.core.util.Strings;
import org.moreunit.elements.LanguageType;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.util.JavaType;

public class NewTestCaseWizard extends NewClassyWizard
{
    private static final String SPOCK_TEST_SUPERCLASS = "spock.lang.Specification"; //$NON-NLS-1$
    private final static String SPOCK_TEST_SUFFIX = "Spec"; //$NON-NLS-1$
    private final ProjectPreferences preferences;
    private final NewTestCaseWizardParticipatorManager participatorManager;
    private final IPackageFragmentRoot testSrcFolder;
    private final JavaType testCaseName;
    private final IPackageFragment testPackageFragment;
    private final IPackageFragmentRoot mainSrcFolder;

    private MoreUnitWizardPageOne pageOne;
    private NewTestCaseWizardPageTwo pageTwo;
    private NewTestCaseWizardContext context;
    private NewTestCaseWizardComposer wizardComposer;
    private IJavaProject javaProjectUnderTest;

    public NewTestCaseWizard(final IType element)
    {
        super(element);

        this.javaProjectUnderTest = element.getJavaProject();
        this.preferences = Preferences.forProject(javaProjectUnderTest);
        this.participatorManager = new NewTestCaseWizardParticipatorManager();

        mainSrcFolder = (IPackageFragmentRoot) element.getPackageFragment().getParent();
        testSrcFolder = preferences.getTestSourceFolder(mainSrcFolder);
        testCaseName = preferences.getTestClassNamePattern().nameTestCaseFor(getType());
        testPackageFragment = testSrcFolder.getPackageFragment(testCaseName.getQualifier());
    }

    @Override
    public void addPages()
    {
        this.pageTwo = new NewTestCaseWizardPageTwo();
        this.pageOne = new MoreUnitWizardPageOne(this.pageTwo, javaProjectUnderTest, this.preferences, LanguageType.forPath(getType().getPath()));
        this.pageOne.setWizard(this);
        this.pageTwo.setWizard(this);
        this.pageOne.init(new StructuredSelection(getType()));

        this.context = new NewTestCaseWizardContext(getType(), pageOne);
        this.wizardComposer = participatorManager.createWizardComposer(context);
        this.wizardComposer.registerBasePage(INewTestCaseWizardPage.TEST_CASE_PAGE, this.pageOne);
        this.wizardComposer.registerBasePage(INewTestCaseWizardPage.TEST_METHODS_PAGE, this.pageTwo);
        this.wizardComposer.compose(this);
    }

    @Override
    public void createPageControls(Composite pageContainer)
    {
        super.createPageControls(pageContainer);

        configurePageOne();
    }

    private void configurePageOne()
    {
        String testSuperClass = getTestSuperClass();
        if(testSuperClass != null && ! testSuperClass.isEmpty())
        {
            this.pageOne.setSuperClass(testSuperClass, true);
        }

        if(preferences.shouldUseSpockType())
        {
            this.pageOne.setPackageFragmentRoot(preferences.getSpockTestSourceFolder(mainSrcFolder), true);
            this.pageOne.setTypeName(pageOne.getClassUnderTestText() + SPOCK_TEST_SUFFIX, true);
        }
        else
        {
            this.pageOne.setPackageFragmentRoot(testSrcFolder, true);
            this.pageOne.setTypeName(testCaseName.getSimpleName(), true);
        }
        this.pageOne.setPackageFragment(testPackageFragment, true);
    }

    private String getTestSuperClass()
    {
        String result = this.preferences.getTestSuperClass();

        if(Strings.isBlank(result) && preferences.shouldUseJunit3Type())
        {
            return null;
        }
        else if(preferences.shouldUseSpockType())
        {
            return SPOCK_TEST_SUPERCLASS;
        }

        return result;
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

    public IPackageFragmentRoot getMainSrcFolder()
    {
        return mainSrcFolder;
    }
}
