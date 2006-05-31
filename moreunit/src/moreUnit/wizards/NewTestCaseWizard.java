package moreUnit.wizards;

import moreUnit.elements.JavaProjectFacade;
import moreUnit.preferences.Preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
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
		//initialisePackageFragment();
		pageOne.setJUnit4(Preferences.instance().shouldUseJunit4Type(), true);
		pageOne.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);
		addPage(pageOne);
		addPage(pageTwo);
	}
	
	@Override
	protected IPackageFragmentRoot getPackageFragmentRootFromSettings() {
		return (new JavaProjectFacade(project)).getJUnitSourceFolder();
	}

//	private void initialisePackageFragment() {
//		if (!Preferences.instance().hasTestPackagePrefix()) {
//			return;
//		}
//		try {
//			IPackageFragment packageFragment = createPackageFragment(getPackageFragmentNameWithPrefix());
//			pageOne.setPackageFragment(packageFragment, true);
//		} catch (JavaModelException e) {
//			MoreUnitPlugin.getDefault().log("Unable to create package fragment root", e);
//		}
//	}
//
//	private String getPackageFragmentNameWithPrefix() {
//		return Preferences.instance().getTestPackagePrefix() + "." + pageOne.getPackageFragment().getElementName();
//	}

	public IType createClass() throws CoreException, InterruptedException {
		pageOne.createType(new NullProgressMonitor());
		return pageOne.getCreatedType();
	}

	protected IPackageFragmentRoot getPackageFragmentRoot() {
		return pageOne.getPackageFragmentRoot();
	}
}

// $Log$
// Revision 1.1  2006/05/12 22:33:41  channingwalton
// added class creation wizards if type to jump to does not exist
//