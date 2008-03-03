package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author vera
 *
 * 02.03.2008 19:00:16
 */
public class AddUnitSourceFolderWizard extends Wizard {

	private AddUnitSourceFolderWizardPage page;
	
	private List<IPackageFragmentRoot> selectedSourceFolder = new ArrayList<IPackageFragmentRoot>();
	private UnitSourcesPropertiesPage unitSourcesPropertiesPage;

	public AddUnitSourceFolderWizard(UnitSourcesPropertiesPage unitSourcesPropertiesPage) {
		this.unitSourcesPropertiesPage = unitSourcesPropertiesPage;
	}
	
	@Override
	public boolean performFinish() {
		selectedSourceFolder = page.getSelectedSourceFolder();
		unitSourcesPropertiesPage.handlePerformFinishFromAddUnitSourceFolderWizard(selectedSourceFolder);
		return true;
	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		page = new AddUnitSourceFolderWizardPage();
		addPage(page);
		super.createPageControls(pageContainer);
	}
	
	public void open() {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this);
		dialog.open();
	}
	
	public List<IPackageFragmentRoot> getUnitSourceFolderFromPropertyPage() {
		return unitSourcesPropertiesPage.getListOfUnitSourceFolder();
	}
}