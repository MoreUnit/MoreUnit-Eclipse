package moreUnit.wizards;

import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;

public class NewClassWizard extends NewClassyWizard {

	private NewClassWizardPage	newClassWizardPage;

	public NewClassWizard(IType element) {
		super(element);
	}

	public void addPages() {
		newClassWizardPage = new NewClassWizardPage();
		newClassWizardPage.setWizard(this);
		newClassWizardPage.init(new StructuredSelection(getType()));
		newClassWizardPage.setTypeName(getPotentialTypeName(), true);
		newClassWizardPage.setPackageFragment(getPackage(), true);
		newClassWizardPage.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);
		newClassWizardPage.setEnclosingType(null, false);
		newClassWizardPage.setSuperClass("", true);
		addPage(newClassWizardPage);
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
	
	private IPackageFragment getPackageFragmentStrippedOfPrefix(IPackageFragment packageFragment) {
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
		Preferences preferences = Preferences.instance();
		String name = getType().getElementName();
		String[] prefixes = preferences.getPrefixes();
		for (int i = 0; i < prefixes.length; i++) {
			name = name.replaceAll(prefixes[i], "");
		}
		String[] suffixes = preferences.getSuffixes();
		for (int i = 0; i < suffixes.length; i++) {
			name = name.replaceAll(suffixes[i], "");
		}
		return name;
	}

	public IType createClass() throws CoreException, InterruptedException {
		newClassWizardPage.createType(new NullProgressMonitor());
		return newClassWizardPage.getCreatedType();
	}

	protected IPackageFragmentRoot getPackageFragmentRoot() {
		return newClassWizardPage.getPackageFragmentRoot();
	}
}

// $Log$
// Revision 1.1  2006/05/12 22:33:41  channingwalton
// added class creation wizards if type to jump to does not exist
//