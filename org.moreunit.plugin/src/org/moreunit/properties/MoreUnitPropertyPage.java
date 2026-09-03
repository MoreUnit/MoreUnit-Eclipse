package org.moreunit.properties;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.decorator.UnitDecorator;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera 11.03.2008 20:33:10
 */
public class MoreUnitPropertyPage extends PropertyPage
{
    private Button projectSpecificSettingsCheckbox;
    private CTabFolder tabFolder;

    private UnitSourceFolderBlock firstTabUnitSourceFolder;
    private OtherMoreunitPropertiesBlock secondTabOtherProperties;

    public MoreUnitPropertyPage()
    {
        noDefaultButton();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        final Composite contentComposite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        contentComposite.setLayout(layout);
        contentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createCheckboxContent(contentComposite);
        createTabContent(contentComposite);

        updateValidState();

        return parent;
    }

    private void createCheckboxContent(Composite parent)
    {
        projectSpecificSettingsCheckbox = new Button(parent, SWT.CHECK);
        projectSpecificSettingsCheckbox.setText("Use project specific settings");

        projectSpecificSettingsCheckbox.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                handleCheckboxSelectionChanged();
            }
        });

        final GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

        projectSpecificSettingsCheckbox.setLayoutData(gridData);

        projectSpecificSettingsCheckbox.setSelection(Preferences.getInstance().hasProjectSpecificSettings(getJavaProject()));
    }

    private void createTabContent(Composite parent)
    {
        tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.TOP);
        final CTabItem sourceFolderItem = new CTabItem(tabFolder, SWT.NONE);
        sourceFolderItem.setText("Test source folder");
        firstTabUnitSourceFolder = new UnitSourceFolderBlock(getJavaProject(), this);
        sourceFolderItem.setControl(fixesFirstTabStyle(firstTabUnitSourceFolder.getControl(tabFolder)));

        final CTabItem otherFolderItem = new CTabItem(tabFolder, SWT.NONE);
        otherFolderItem.setText("Other");
        secondTabOtherProperties = new OtherMoreunitPropertiesBlock(getJavaProject());
        otherFolderItem.setControl(fixesSecondTabStyle(secondTabOtherProperties.getControl(tabFolder, true)));

        secondTabOtherProperties.addModifyListener(e -> updateValidState());

        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        tabFolder.setLayout(layout);

        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = otherFolderItem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        tabFolder.setLayoutData(gridData);
        tabFolder.setSelection(0);

        setEnabled(shouldUseProjectspecificSettings());
    }

    private Composite fixesFirstTabStyle(Composite tab)
    {
        final GridLayout l = ((GridLayout) tab.getLayout());
        l.marginRight = 10;
        l.marginLeft = 10;
        return tab;
    }

    private Composite fixesSecondTabStyle(Composite tab)
    {
        final GridLayout l = ((GridLayout) tab.getLayout());
        l.marginLeft = 10;
        return tab;
    }

    private IJavaProject getJavaProject()
    {
        final IAdaptable selection = getElement();
        if(selection instanceof final IJavaProject project)
        {
            return project;
        }
        // when files are selected, they need to be adapted to their project
        // first (see enabledWhen clause of plugin.xml)
        final IProject project = Adapters.adapt(selection, IProject.class);
        return JavaCore.create(project);
    }

    private void handleCheckboxSelectionChanged()
    {
        setEnabled(shouldUseProjectspecificSettings());
        updateValidState();
    }

    private void setEnabled(boolean enabled)
    {
        firstTabUnitSourceFolder.setEnabled(enabled);
        secondTabOtherProperties.setEnabled(enabled);
        tabFolder.setEnabled(enabled);
    }

    @Override
    public boolean performOk()
    {
        saveProperties();
        return super.performOk();
    }

    @Override
    protected void performApply()
    {
        saveProperties();
        super.performApply();
    }

    private boolean shouldUseProjectspecificSettings()
    {
        return projectSpecificSettingsCheckbox.getSelection();
    }

    private void saveProperties()
    {
        try
        {
            Preferences.getInstance().setHasProjectSpecificSettings(getJavaProject(), shouldUseProjectspecificSettings());
            if(shouldUseProjectspecificSettings())
            {
                firstTabUnitSourceFolder.saveProperties();
                secondTabOtherProperties.saveProperties();
            }
            final IPreferenceStore store = Preferences.getInstance().getProjectStore(getJavaProject());
            if(store instanceof final ScopedPreferenceStore preferenceStore)
                preferenceStore.save();

            UnitDecorator.refreshAll();
        }
        catch (final IOException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
    }

    protected void updateValidState()
    {
        String errorMsg = null;
        String warningMsg = null;
        if(shouldUseProjectspecificSettings())
        {
            errorMsg = firstTabUnitSourceFolder.getError();

            if(errorMsg == null)
            {
                errorMsg = secondTabOtherProperties.getError();
            }

            if(errorMsg == null)
            {
                warningMsg = secondTabOtherProperties.getWarning();
            }
            else
            {
                secondTabOtherProperties.forceFocus();
            }

        }
        setValid(errorMsg == null);
        setErrorMessage(errorMsg);
        setMessage(warningMsg, WARNING);
    }
}
