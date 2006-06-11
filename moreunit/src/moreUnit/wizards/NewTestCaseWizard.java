package moreUnit.wizards;

import moreUnit.elements.JavaProjectFacade;
import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;

public class NewTestCaseWizard extends NewClassyWizard {

	private NewTestCaseWizardPageOne	pageOne;
	private NewTestCaseWizardPageTwo	pageTwo;
	
	private IJavaProject project;

	public NewTestCaseWizard(IType element) {
		super(element);
		
		this.project = element.getJavaProject();
	}

	public void addPages() {
		pageTwo = new NewTestCaseWizardPageTwo();
		pageOne = new NewTestCaseWizardPageOne(pageTwo);
		pageOne.setWizard(this);
		pageTwo.setWizard(this);
		pageOne.init(new StructuredSelection(getType()));
		initialisePackageFragment();
		
		// Eclipse 3.1.x  does not support junit 4
		try {
		pageOne.setJUnit4(Preferences.instance().shouldUseJunit4Type(), true);
		} catch (NoSuchMethodError error) {
		}
		pageOne.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);
		addPage(pageOne);
		addPage(pageTwo);
	}
	
	@Override
	protected IPackageFragmentRoot getPackageFragmentRootFromSettings() {
		return (new JavaProjectFacade(project)).getJUnitSourceFolder();
	}

	private void initialisePackageFragment() {
		String testPackagePrefix = Preferences.instance().getTestPackagePrefix();
		if (testPackagePrefix == null || testPackagePrefix.length() == 0) {
			return;
		}
		try {
			IPackageFragment packageFragment = createPackageFragment(getPackageFragmentNameWithPrefix());
			pageOne.setPackageFragment(packageFragment, true);
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleWarnLog("Unable to create package fragment root");
		}
	}

	private String getPackageFragmentNameWithPrefix() {
		return Preferences.instance().getTestPackagePrefix() + "." + pageOne.getPackageFragment().getElementName();
	}

	public IType createClass() throws CoreException, InterruptedException {
		pageOne.createType(new NullProgressMonitor());
		return pageOne.getCreatedType();
	}

	protected IPackageFragmentRoot getPackageFragmentRoot() {
		return pageOne.getPackageFragmentRoot();
	}
}

// $Log$
// Revision 1.2  2006/05/31 19:40:23  gianasista
// Preferences are used for initialization of the wizard
//
// Revision 1.1  2006/05/12 22:33:41  channingwalton
// added class creation wizards if type to jump to does not exist
//