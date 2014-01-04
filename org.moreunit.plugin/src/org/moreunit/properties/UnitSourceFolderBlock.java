package org.moreunit.properties;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.SearchScopeSingelton;

/**
 * @author vera 11.03.2008 20:43:49
 */
public class UnitSourceFolderBlock implements ISelectionChangedListener
{

    private TreeViewer sourceFolderTree;
    private Button removeButton;
    private Button mappingButton;
    private UnitSourcesContentProvider unitSourcesContentProvider;

    private IJavaProject javaProject;
    private MoreUnitPropertyPage propertyPage;

    public UnitSourceFolderBlock(IJavaProject javaProject, MoreUnitPropertyPage propertyPage)
    {
        this.javaProject = javaProject;
        this.propertyPage = propertyPage;
    }

    public Composite getControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createLabel(composite);
        createTreeViewer(composite);
        createButtons(composite);

        return composite;
    }

    private void createTreeViewer(Composite composite)
    {
        sourceFolderTree = new TreeViewer(composite);
        unitSourcesContentProvider = new UnitSourcesContentProvider(javaProject);
        sourceFolderTree.setContentProvider(unitSourcesContentProvider);
        sourceFolderTree.setLabelProvider(new UnitSourceFolderLabelProvider());
        sourceFolderTree.addSelectionChangedListener(this);
        sourceFolderTree.setInput(javaProject);
        GridData layoutData = new GridData();
        layoutData.widthHint = 250;
        layoutData.heightHint = 200;
        sourceFolderTree.getControl().setLayoutData(layoutData);
    }

    private void createLabel(Composite composite)
    {
        Label label = new Label(composite, SWT.LEFT);
        label.setText("Projects with tests for " + javaProject.getElementName());
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;

        label.setLayoutData(layoutData);
    }

    private void createButtons(Composite composite)
    {
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        buttonComposite.setFont(composite.getFont());

        createAddButton(buttonComposite, composite.getFont());
        removeButton = createRemoveButton(buttonComposite, composite.getFont());
        mappingButton = createMappingButton(buttonComposite, composite.getFont());
        removeButton.setEnabled(false);
        mappingButton.setEnabled(false);

        FillLayout buttonBoxLayout = new FillLayout(SWT.VERTICAL);
        buttonComposite.setLayout(buttonBoxLayout);

        GridData layoutData = new GridData();
        layoutData.widthHint = 100;
        buttonComposite.setLayoutData(layoutData);
    }

    private Button createAddButton(Composite buttonComposite, Font font)
    {
        Button button = new Button(buttonComposite, SWT.PUSH);
        button.setFont(font);
        button.setText("Add");
        button.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent selectionEvent)
            {
                addButtonClicked();
            }
        });

        return button;
    }

    private Button createRemoveButton(Composite buttonComposite, Font font)
    {
        Button button = new Button(buttonComposite, SWT.PUSH);
        button.setFont(font);
        button.setText("Remove");
        button.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                removeButtonClicked();
            }
        });

        return button;
    }

    private Button createMappingButton(Composite buttonComposite, Font font)
    {
        Button button = new Button(buttonComposite, SWT.PUSH);
        button.setFont(font);
        button.setText("Remap");
        button.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                mappingButtonClicked();
            }
        });

        return button;
    }

    private void addButtonClicked()
    {
        new AddUnitSourceFolderWizard(javaProject, this).open();
    }

    private void removeButtonClicked()
    {
        if(unitSourcesContentProvider.remove((SourceFolderMapping) getSelectedObject()))
            sourceFolderTree.refresh();
    }

    private Object getSelectedObject()
    {
        TreeSelection selection = (TreeSelection) sourceFolderTree.getSelection();
        return selection.getFirstElement();
    }

    private void mappingButtonClicked()
    {
        TreeSelection selection = (TreeSelection) sourceFolderTree.getSelection();
        (new SourceFolderMappingDialog(this, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), (SourceFolderMapping) selection.getFirstElement())).open();
    }

    public void handlePerformFinishFromAddUnitSourceFolderWizard(List<SourceFolderMapping> mappingsToAdd)
    {
        if(mappingsToAdd.size() > 0)
        {
            unitSourcesContentProvider.add(mappingsToAdd);
            sourceFolderTree.refresh();
            propertyPage.updateValidState();
        }
    }

    public List<SourceFolderMapping> getListOfUnitSourceFolder()
    {
        return unitSourcesContentProvider.getListOfUnitSourceFolder();
    }

    public void handleSourceDialogMappingFinished(SourceFolderMapping mapping, List<IPackageFragmentRoot> newSourceFolder)
    {
        mapping.setSourceFolderList(newSourceFolder);
        sourceFolderTree.refresh();
    }

    public void saveProperties()
    {
        List<SourceFolderMapping> mappingList = getListOfUnitSourceFolder();
        Preferences.getInstance().setMappingList(javaProject, mappingList);

        SourceFolderContext.getInstance().initContextForWorkspace();
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        Object selectedObject = getSelectedObject();
        removeButton.setEnabled(selectedObject instanceof SourceFolderMapping);
        mappingButton.setEnabled(selectedObject instanceof SourceFolderMapping);

        propertyPage.updateValidState();
    }

    public String getError()
    {
        if(getListOfUnitSourceFolder().isEmpty())
        {
            return "Choose at least one test folder!";
        }
        return null;
    }
}
