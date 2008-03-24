package org.moreunit.properties;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.SearchScopeSingelton;

/**
 * @author vera
 *
 * 11.03.2008 20:33:10
 */
public class MoreUnitPropertyPage extends PropertyPage {
	
	private Button projectSpecificSettingsCheckbox;
	private TabFolder tabFolder;
	
	private UnitSourceFolderBlock firstTabUnitSourceFolder;
	private OtherMoreunitPropertiesBlock secondTabOtherProperties;
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contentComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		contentComposite.setLayout(gridLayout);
		
		createCheckboxContent(contentComposite);
		createTabContent(contentComposite);
		
		return parent;
	}
	
	private void createCheckboxContent(Composite parent) {
		projectSpecificSettingsCheckbox = new Button(parent, SWT.CHECK);
		projectSpecificSettingsCheckbox.setText("Use project specific settings");
		
		projectSpecificSettingsCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				handleCheckboxSelectionChanged();
			}
		});
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		
		projectSpecificSettingsCheckbox.setLayoutData(gridData);
		
		projectSpecificSettingsCheckbox.setSelection(Preferences.getInstance().hasProjectSpecificSettings(getJavaProject()));
	}
	
	private void createTabContent(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BORDER);
		TabItem sourceFolderItem = new TabItem(tabFolder, SWT.NONE);
		sourceFolderItem.setText("Test source folder");
		firstTabUnitSourceFolder = new UnitSourceFolderBlock(getJavaProject());
		sourceFolderItem.setControl(firstTabUnitSourceFolder.getControl(tabFolder));
		
		TabItem otherFolderItem = new TabItem(tabFolder, SWT.NONE);
		otherFolderItem.setText("Other");
		secondTabOtherProperties = new OtherMoreunitPropertiesBlock(getJavaProject());
		otherFolderItem.setControl(secondTabOtherProperties.getControl(tabFolder));
		
		GridData gridData = new GridData();
		gridData.widthHint = 550;
		tabFolder.setLayoutData(gridData);
		
		tabFolder.pack();

		tabFolder.setEnabled(shouldUseProjectspecificSettings());
	}
	
	private IJavaProject getJavaProject() {
		if (getElement() instanceof IJavaProject) {
			return (IJavaProject) getElement();
		}
		return JavaCore.create((IProject) getElement());
	}
	
	private void handleCheckboxSelectionChanged() {
		if(shouldUseProjectspecificSettings())
			tabFolder.setEnabled(true);
		else
			tabFolder.setEnabled(false);
	}

	@Override
	public boolean performOk() {
		saveProperties();
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		saveProperties();
		super.performApply();
	}

	private boolean shouldUseProjectspecificSettings() {
		return projectSpecificSettingsCheckbox.getSelection();
	}
	
	private void saveProperties() {
		try {
			Preferences.getInstance().setHasProjectSpecificSettings(getJavaProject(), shouldUseProjectspecificSettings());
			if(shouldUseProjectspecificSettings()) {
				firstTabUnitSourceFolder.saveProperties();
				secondTabOtherProperties.saveProperties();
				IPreferenceStore store = Preferences.getInstance().store(getJavaProject());
				if(store instanceof ScopedPreferenceStore)
					((ScopedPreferenceStore)store).save();
			}
		} catch (IOException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}
}
