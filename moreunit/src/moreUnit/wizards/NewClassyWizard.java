package moreUnit.wizards;

import moreUnit.MoreUnitPlugin;
import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public abstract class NewClassyWizard extends Wizard implements INewWizard {

	private final IType	element;
	private IType		createdType;

	public NewClassyWizard(IType element) {
		this.element = element;
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/switchunit_wizard.png"));
	}

	protected IType getType() {
		return element;
	}

	public IType open() {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this);
		if (dialog.open() == Window.OK) {
			return createdType;
		}
		return null;
	}

	public boolean performFinish() {
		try {
			createdType = createClass();
			getDialogSettings().put(getPackageFragmentRootKey(), getPackageFragmentRoot().getHandleIdentifier());
		} catch (Exception e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		return true;
	}

	public IDialogSettings getDialogSettings() {
		return MoreUnitPlugin.getDefault().getDialogSettings();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public abstract IType createClass() throws CoreException, InterruptedException;

	protected abstract IPackageFragmentRoot getPackageFragmentRoot();

	protected IPackageFragmentRoot getPackageFragmentRootFromSettings() {
		String key = getPackageFragmentRootKey();
		String root = getDialogSettings().get(key);
		IPackageFragmentRoot fragment = (IPackageFragmentRoot) JavaCore.create(root);
		if (fragment != null && fragment.exists()) {
			return fragment;
		}
		return getPackageFragmentRootFromType();
	}

	private IPackageFragmentRoot getPackageFragmentRootFromType() {
		return (IPackageFragmentRoot) getType().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
	}

	/**
	 * we don't care how the fragment was found, or if it exists because the class creation wizard just wants the string
	 * name from it
	 */
	protected IPackageFragment createPackageFragment(String targetPackage) throws JavaModelException {
		return getType().getJavaProject().getPackageFragmentRoots()[0].getPackageFragment(targetPackage);
	}

	private String getPackageFragmentRootKey() {
		return getClass().getName() + ".packageFragmentRoot";
	}
}

// $Log$