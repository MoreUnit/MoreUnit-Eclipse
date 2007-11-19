package org.moreunit.wizards;



import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

public class NewClassWizard extends NewClassyWizard {

	private NewClassWizardPage	newClassWizardPage;

	public NewClassWizard(IType element) {
		super(element);
	}

	@Override
	public void addPages() {
		this.newClassWizardPage = new NewClassWizardPage();
		this.newClassWizardPage.setWizard(this);
		this.newClassWizardPage.init(new StructuredSelection(getType()));
		this.newClassWizardPage.setTypeName(getPotentialTypeName(), true);
		this.newClassWizardPage.setPackageFragment(getPackage(), true);
		this.newClassWizardPage.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);
		this.newClassWizardPage.setEnclosingType(null, false);
		this.newClassWizardPage.setSuperClass("", true);
		addPage(this.newClassWizardPage);
	}

	private IPackageFragment getPackage() {
		IPackageFragment packageFragment = getType().getPackageFragment();
		if (packageStartsWithPrefix(packageFragment)) {
			packageFragment = getPackageFragmentStrippedOfPrefix(packageFragment);
		}
		return packageFragment;
	}

	private boolean packageStartsWithPrefix(IPackageFragment packageFragment) {
		return false;
//		return Preferences.instance().hasTestPackagePrefix() && packageFragment.getElementName().startsWith(Preferences.instance().getTestPackagePrefix());
	}

	private IPackageFragment getPackageFragmentStrippedOfPrefix(final IPackageFragment packageFragment) {
//		String targetPackage = packageFragment.getElementName().replaceFirst(Preferences.instance().getTestPackagePrefix() + "\\.", "");
		String targetPackage = packageFragment.getElementName();
		try {
			return createPackageFragment(targetPackage);
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
			return null;
		}
	}

	private String getPotentialTypeName() {
		Preferences preferences = Preferences.newInstance(getType().getJavaProject());
		String name = getType().getElementName();
		String[] prefixes = preferences.getPrefixes();
		for (String element2 : prefixes) {
			name = name.replaceAll(element2, "");
		}
		String[] suffixes = preferences.getSuffixes();
		for (String element2 : suffixes) {
			name = name.replaceAll(element2, "");
		}
		return name;
	}

	@Override
	public IType createClass() throws CoreException, InterruptedException {
		this.newClassWizardPage.createType(new NullProgressMonitor());
		return this.newClassWizardPage.getCreatedType();
	}

	@Override
	protected IPackageFragmentRoot getPackageFragmentRoot() {
		return this.newClassWizardPage.getPackageFragmentRoot();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.2  2006/05/13 18:30:49  gianasista
// Preferences as singelton (protected constructor for testing purposes)
//
// Revision 1.1  2006/05/12 22:33:41  channingwalton
// added class creation wizards if type to jump to does not exist
//