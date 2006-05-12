package moreUnit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;

public class NewTestCaseWizard extends NewClassyWizard {

	private NewTestCaseWizardPageOne	pageOne;
	private NewTestCaseWizardPageTwo	pageTwo;

	public NewTestCaseWizard(IType element) {
		super(element);
	}

	public void addPages() {
		pageTwo = new NewTestCaseWizardPageTwo();
		pageOne = new NewTestCaseWizardPageOne(pageTwo);
		pageOne.setWizard(this);
		pageTwo.setWizard(this);
		pageOne.init(new StructuredSelection(getType()));
		//initialisePackageFragment();
		pageOne.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);
		addPage(pageOne);
		addPage(pageTwo);
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