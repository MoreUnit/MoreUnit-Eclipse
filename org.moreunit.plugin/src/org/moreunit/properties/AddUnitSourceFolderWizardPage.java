package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.elements.SourceFolderMapping;

/**
 * @author vera 02.03.2008 19:01:24
 */
public class AddUnitSourceFolderWizardPage extends WizardPage implements ICheckStateListener
{

    private CheckboxTreeViewer checkboxTreeViewer;
    private WorkspaceSourceFolderContentProvider workspaceSourceFolderContentProvider;

    protected AddUnitSourceFolderWizardPage()
    {
        super("Add Unit Source Folder");
    }

    public void createControl(Composite parent)
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        checkboxTreeViewer = new CheckboxTreeViewer(parent);
        checkboxTreeViewer.addCheckStateListener(this);
        checkboxTreeViewer.setLabelProvider(new JavaElementLabelProvider());
        workspaceSourceFolderContentProvider = new WorkspaceSourceFolderContentProvider(getSelectedUnitSourceFolderFromPropertyPage());
        checkboxTreeViewer.setContentProvider(workspaceSourceFolderContentProvider);
        checkboxTreeViewer.setInput(this);

        GridData gridData = new GridData();
        gridData.heightHint = 200;
        gridData.widthHint = 400;

        checkboxTreeViewer.getControl().setLayoutData(gridData);
        setControl(checkboxTreeViewer.getControl());
    }

    private List<SourceFolderMapping> getSelectedUnitSourceFolderFromPropertyPage()
    {
        return ((AddUnitSourceFolderWizard) getWizard()).getUnitSourceFolderFromPropertyPage();
    }

    public void checkStateChanged(CheckStateChangedEvent event)
    {
        Object element = event.getElement();
        boolean isChecked = event.getChecked();

        if(element instanceof IJavaProject)
        {
            checkboxTreeViewer.setGrayed(element, false);
            Object[] children = workspaceSourceFolderContentProvider.getChildren(element);

            for (Object sourceFolder : children)
            {
                checkboxTreeViewer.setChecked(sourceFolder, isChecked);
            }
        }
        else if(element instanceof IPackageFragmentRoot)
        {
            Object javaProject = workspaceSourceFolderContentProvider.getParent(element);
            if(isChecked && areAllSourceFolderInProjectSelected(javaProject))
            {
                checkboxTreeViewer.setGrayed(javaProject, false);
                checkboxTreeViewer.setChecked(javaProject, true);
            }
            else if(isChecked && ! areAllSourceFolderInProjectSelected(javaProject))
                checkboxTreeViewer.setGrayChecked(javaProject, true);
            else if(! isChecked && isNoSourceFolderInProjectSelected(javaProject))
                checkboxTreeViewer.setChecked(javaProject, false);
            else if(! isChecked && ! isNoSourceFolderInProjectSelected(javaProject))
                checkboxTreeViewer.setGrayChecked(javaProject, true);
        }
    }

    private boolean areAllSourceFolderInProjectSelected(Object javaProjectElement)
    {
        Object[] children = workspaceSourceFolderContentProvider.getChildren(javaProjectElement);
        for (Object child : children)
        {
            if(! checkboxTreeViewer.getChecked(child))
                return false;
        }

        return true;
    }

    private boolean isNoSourceFolderInProjectSelected(Object javaProjectElement)
    {
        Object[] children = workspaceSourceFolderContentProvider.getChildren(javaProjectElement);
        for (Object child : children)
        {
            if(checkboxTreeViewer.getChecked(child))
                return false;
        }

        return true;
    }

    protected List<IPackageFragmentRoot> getSelectedSourceFolder()
    {
        List<IPackageFragmentRoot> selectedFolders = new ArrayList<IPackageFragmentRoot>();
        Object[] checkedElements = checkboxTreeViewer.getCheckedElements();

        for (Object checkedElement : checkedElements)
        {
            if(checkedElement instanceof IPackageFragmentRoot)
                selectedFolders.add((IPackageFragmentRoot) checkedElement);
        }

        return selectedFolders;
    }
}
