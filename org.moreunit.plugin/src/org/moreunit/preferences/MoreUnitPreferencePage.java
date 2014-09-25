package org.moreunit.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.SourceFolderContext;
import org.moreunit.core.ui.LayoutData;
import org.moreunit.properties.OtherMoreunitPropertiesBlock;
import org.moreunit.util.SearchScopeSingelton;

/**
 * @author vera 08.01.2006 19:24:23
 */
public class MoreUnitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private Text testSourceFolderField;
    private OtherMoreunitPropertiesBlock otherMoreunitPropertiesBlock;

    public MoreUnitPreferencePage()
    {
        setDescription(PreferenceConstants.TEXT_GENERAL_SETTINGS);
    }

    @Override
    protected Control createContents(Composite parent)
    {
        initializeDialogUnits(parent);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);

        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        otherMoreunitPropertiesBlock = new OtherMoreunitPropertiesBlock(null)
        {
            @Override
            protected void beforeContent(Composite parentWith2Cols)
            {
                createTestSourceFolderField(parentWith2Cols);
            }
        };
        otherMoreunitPropertiesBlock.getControl(parent, false);

        otherMoreunitPropertiesBlock.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });

        Dialog.applyDialogFont(parent);

        return parent;
    }

    private void validate()
    {
        String errorMsg = otherMoreunitPropertiesBlock.getError();
        if(errorMsg == null)
        {
            setValid();

            String warningMsg = otherMoreunitPropertiesBlock.getWarning();
            if(warningMsg != null)
            {
                setMessage(warningMsg, IMessageProvider.WARNING);
            }
        }
        else
        {
            otherMoreunitPropertiesBlock.forceFocus();
            setMessage(errorMsg, IMessageProvider.ERROR);
            setValid(false);
        }
    }

    private void setValid()
    {
        setMessage(null);
        setValid(true);
    }

    private void createTestSourceFolderField(Composite parentWith2Cols)
    {
        Label label = new Label(parentWith2Cols, SWT.NONE);
        label.setText(PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);
        label.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SOURCE_FOLDER);

        testSourceFolderField = new Text(parentWith2Cols, SWT.SINGLE | SWT.BORDER);
        testSourceFolderField.setLayoutData(LayoutData.labelledField());
        testSourceFolderField.setText(Preferences.getInstance().getJunitDirectoryFromPreferences(null));
        testSourceFolderField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SOURCE_FOLDER);
        testSourceFolderField.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                if(testSourceFolderField.getText().endsWith("/"))
                    setErrorMessage("Test source folder should not end with a slash");
                else
                    setErrorMessage(null);
            }
        });
    }

    public void init(IWorkbench workbench)
    {
        setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore()
    {
        return MoreUnitPlugin.getDefault().getPreferenceStore();
    }

    public boolean performOk()
    {
        Preferences.getInstance().setJunitDirectory(testSourceFolderField.getText());
        otherMoreunitPropertiesBlock.saveProperties();
        Preferences.getInstance().clearProjectCache();

        SourceFolderContext.getInstance().initContextForWorkspace();
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();

        return super.performOk();
    }
}
