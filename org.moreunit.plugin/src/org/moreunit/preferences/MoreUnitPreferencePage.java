package org.moreunit.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
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

        Composite contentComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginRight = 10;
        layout.numColumns = 2;
        contentComposite.setLayout(layout);
        contentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        otherMoreunitPropertiesBlock = new OtherMoreunitPropertiesBlock(null);

        createTestSourceFolderField(contentComposite);
        otherMoreunitPropertiesBlock.createCompositeWith2ColsParent(contentComposite);

        Dialog.applyDialogFont(contentComposite);

        return parent;
    }

    private void createTestSourceFolderField(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText(PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);
        label.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SOURCE_FOLDER);

        testSourceFolderField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testSourceFolderField.setLayoutData(otherMoreunitPropertiesBlock.getLayoutForTextFields());
        testSourceFolderField.setText(Preferences.getInstance().getJunitDirectoryFromPreferences(null));
        testSourceFolderField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SOURCE_FOLDER);
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
        Preferences.clearProjectCach();

        SourceFolderContext.getInstance().initContextForWorkspace();
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();

        return super.performOk();
    }
}