package org.moreunit.properties;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera 11.03.2008 20:33:10
 */
public class MoreUnitPropertyPage extends PropertyPage
{
    private Button projectSpecificSettingsCheckbox;
    private TabFolder tabFolder;

    private UnitSourceFolderBlock firstTabUnitSourceFolder;
    private OtherMoreunitPropertiesBlock secondTabOtherProperties;

    @Override
    protected Control createContents(Composite parent)
    {
        Composite contentComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
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
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                handleCheckboxSelectionChanged();
            }
        });

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

        projectSpecificSettingsCheckbox.setLayoutData(gridData);

        projectSpecificSettingsCheckbox.setSelection(Preferences.getInstance().hasProjectSpecificSettings(getJavaProject()));
    }

    private void createTabContent(Composite parent)
    {
        tabFolder = new TabFolder(parent, SWT.BORDER);
        TabItem sourceFolderItem = new TabItem(tabFolder, SWT.NONE);
        sourceFolderItem.setText("Test source folder");
        firstTabUnitSourceFolder = new UnitSourceFolderBlock(getJavaProject(), this);
        sourceFolderItem.setControl(fixesFirstTabStyle(firstTabUnitSourceFolder.getControl(tabFolder)));

        TabItem otherFolderItem = new TabItem(tabFolder, SWT.NONE);
        otherFolderItem.setText("Other");
        secondTabOtherProperties = new OtherMoreunitPropertiesBlock(getJavaProject());
        otherFolderItem.setControl(fixesSecondTabStyle(secondTabOtherProperties.getControl(tabFolder, true)));

        secondTabOtherProperties.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                updateValidState();
            }
        });

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        tabFolder.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = otherFolderItem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        tabFolder.setLayoutData(gridData);

        setEnabled(shouldUseProjectspecificSettings());
    }

    private Composite fixesFirstTabStyle(Composite tab)
    {
        GridLayout l = ((GridLayout) tab.getLayout());
        l.marginRight = 10;
        l.marginLeft = 10;
        return tab;
    }

    private Composite fixesSecondTabStyle(Composite tab)
    {
        GridLayout l = ((GridLayout) tab.getLayout());
        l.marginLeft = 10;
        return tab;
    }

    private IJavaProject getJavaProject()
    {
        if(getElement() instanceof IJavaProject)
        {
            return (IJavaProject) getElement();
        }
        return JavaCore.create((IProject) getElement());
    }

    private void handleCheckboxSelectionChanged()
    {
        setEnabled(shouldUseProjectspecificSettings());
        updateValidState();
    }

    private void setEnabled(boolean enabled)
    {
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
            IPreferenceStore store = Preferences.getInstance().getProjectStore(getJavaProject());
            if(store instanceof ScopedPreferenceStore)
                ((ScopedPreferenceStore) store).save();
        }
        catch (IOException e)
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
