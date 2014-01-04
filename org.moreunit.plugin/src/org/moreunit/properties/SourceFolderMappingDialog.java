/**
 *
 */
package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.util.PluginTools;

/**
 * @author vera 15.03.2008 19:38:50
 */
public class SourceFolderMappingDialog extends Dialog implements ICheckStateListener, ITreeContentProvider
{

    private SourceFolderMapping sourceFolderMapping;
    private CheckboxTreeViewer checkboxTreeViewer;
    private UnitSourceFolderBlock unitSourceFolderBlock;

    protected SourceFolderMappingDialog(UnitSourceFolderBlock unitSourceFolderBlock, Shell parentShell, SourceFolderMapping sourceFolderMapping)
    {
        super(parentShell);

        this.unitSourceFolderBlock = unitSourceFolderBlock;
        this.sourceFolderMapping = sourceFolderMapping;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        checkboxTreeViewer = new CheckboxTreeViewer(parent);
        checkboxTreeViewer.addCheckStateListener(this);
        checkboxTreeViewer.setLabelProvider(new JavaElementLabelProvider());
        checkboxTreeViewer.setContentProvider(this);
        checkboxTreeViewer.setInput(this);

        return parent;
    }

    public void checkStateChanged(CheckStateChangedEvent event)
    {

    }

    public Object[] getChildren(Object parentElement)
    {
        return null;
    }

    public Object getParent(Object element)
    {
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return false;
    }

    public Object[] getElements(Object inputElement)
    {
        return PluginTools.getAllSourceFolderFromProject(sourceFolderMapping.getJavaProject()).toArray();
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    protected void okPressed()
    {
        Object[] checkedFolder = checkboxTreeViewer.getCheckedElements();
        List<IPackageFragmentRoot> asList = new ArrayList<IPackageFragmentRoot>();
        for(Object elem : checkedFolder) asList.add((IPackageFragmentRoot) elem);
        if(checkedFolder != null && checkedFolder.length > 0)
            unitSourceFolderBlock.handleSourceDialogMappingFinished(sourceFolderMapping, asList);

        super.okPressed();
    }
}
