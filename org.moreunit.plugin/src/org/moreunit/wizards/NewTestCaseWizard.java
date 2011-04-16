package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.BaseTools;
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

    @Override
    protected IPackageFragmentRoot getSourceFolderForUnitTest()
    {
        return preferences.getJUnitSourceFolder(this.project);
    }

    private IPackageFragment initialisePackageFragment()
    {
        String testPackagePrefix = this.preferences.getTestPackagePrefix(project);
        String testPackageSuffix = this.preferences.getTestPackageSuffix(project);

        boolean hasPrefix = (testPackagePrefix != null) && (testPackagePrefix.length() > 0);
        boolean hasSuffix = (testPackageSuffix != null) && (testPackageSuffix.length() > 0);

        IPackageFragment fragment = this.pageOne.getPackageFragment();
        if(hasPrefix || hasSuffix)
        {
            String fragmentName = fragment.getElementName();
            if(hasPrefix)
            {
                fragmentName = testPackagePrefix + "." + fragmentName;
            }

            if(hasSuffix)
            {
                fragmentName = fragmentName + "." + testPackageSuffix;
            }

            try
            {
                IPackageFragment packageFragment = createPackageFragment(fragmentName);
                this.pageOne.setPackageFragment(packageFragment, true);
                fragment = packageFragment;
            }
            catch (JavaModelException e)
            {
                LogHandler.getInstance().handleWarnLog("Unable to create package fragment root");
            }
        }

        return fragment;
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

// $Log: not supported by cvs2svn $
// Revision 1.16 2008/11/23 14:31:02 gianasista
// Improved TestNG support
//
// Revision 1.15 2008/05/26 18:26:07 gianasista
// Bugfix superclass using TestNG
//
// Revision 1.14 2008/05/13 18:54:28 gianasista
// Bugfix for sourcefolder in subfolder
//
// Revision 1.13 2008/02/29 21:34:32 gianasista
// Minor refactorings
//
// Revision 1.12 2008/02/20 19:24:32 gianasista
// Rename of classes for constants
//
// Revision 1.11 2008/02/12 20:22:19 gianasista
// Bugfix (wrong testcasename)
//
// Revision 1.10 2008/02/04 20:12:35 gianasista
// Bugfix: project specific settings
//
// Revision 1.9 2008/01/23 19:37:01 gianasista
// Bugfix for default super class
//
// Revision 1.8 2007/11/19 21:15:17 gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.7 2007/09/02 19:25:42 gianasista
// TestNG support
//
// Revision 1.6 2007/08/24 18:31:59 gianasista
// TestNG support
//
// Revision 1.5 2007/08/24 18:30:46 gianasista
// TestNG support
//
// Revision 1.4 2007/08/11 17:09:05 gianasista
// Applied patch for super class
//
// Revision 1.3 2006/11/25 15:01:21 gianasista
// organize import
//
// Revision 1.2 2006/10/08 17:28:29 gianasista
// Suffix preference
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
// Revision 1.3 2006/06/11 20:09:04 gianasista
// added package prefix preference and support for eclipse 3.1.x
//
// Revision 1.2 2006/05/31 19:40:23 gianasista
// Preferences are used for initialization of the wizard
//
// Revision 1.1 2006/05/12 22:33:41 channingwalton
// added class creation wizards if type to jump to does not exist
//
