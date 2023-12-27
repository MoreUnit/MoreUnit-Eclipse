package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.elements.SourceFolderMapping;

/**
 * @author vera 02.03.2008 19:00:16
 */
public class AddUnitSourceFolderWizard extends Wizard
{

    private AddUnitSourceFolderWizardPage page;
    private IJavaProject javaProject;

    private List<IPackageFragmentRoot> selectedSourceFolder = new ArrayList<IPackageFragmentRoot>();

    private UnitSourceFolderBlock unitSourceFolderBlock;

    public AddUnitSourceFolderWizard(IJavaProject javaProject, UnitSourceFolderBlock unitSourceFolderBlock)
    {
        this.javaProject = javaProject;
        this.unitSourceFolderBlock = unitSourceFolderBlock;
        setWindowTitle("MoreUnit test source folders");
        setHelpAvailable(false);
    }

    @Override
    public boolean performFinish()
    {
        selectedSourceFolder = page.getSelectedSourceFolder();
        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        for (IPackageFragmentRoot sourceFolder : selectedSourceFolder)
        {
            SourceFolderMapping mapping = new SourceFolderMapping(javaProject, sourceFolder);
            mappingList.add(mapping);
        }
        unitSourceFolderBlock.handlePerformFinishFromAddUnitSourceFolderWizard(mappingList);
        return true;
    }

    @Override
    public void addPages()
    {
        page = new AddUnitSourceFolderWizardPage();
        addPage(page);
    }

    public void open(Shell parentShell)
    {
        WizardDialog dialog = new WizardDialog(parentShell, this);
        dialog.open();
    }

    public List<SourceFolderMapping> getUnitSourceFolderFromPropertyPage()
    {
        return unitSourceFolderBlock.getListOfUnitSourceFolder();
    }
}
