package org.moreunit.mock.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreferencesTest {

    private PreferenceStoreManager storeManager;
    private IPreferenceStore workspaceStore;
    private IPreferenceStore projectStore;
    private IJavaProject project;

    @BeforeEach
    public void setUp() {
        storeManager = mock(PreferenceStoreManager.class);
        workspaceStore = mock(IPreferenceStore.class);
        projectStore = mock(IPreferenceStore.class);
        project = mock(IJavaProject.class);

        when(storeManager.getWorkspaceStore()).thenReturn(workspaceStore);
    }

    @Test
    public void constructor_should_register_default_values_to_workspace_store() {
        new Preferences(storeManager);
        // Preference.MOCKING_TEMPLATE is registered during class loading.
        // We verify that its default value is set on the workspace store.
        verify(workspaceStore).setDefault("org.moreunit.mock.mocking_template", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }

    @Test
    public void setMockingTemplate_should_save_value_to_project_store() {
        Preferences preferences = new Preferences(storeManager);
        when(storeManager.getStore(project, true)).thenReturn(projectStore);

        preferences.setMockingTemplate(project, "customTemplate");

        verify(projectStore).setValue("org.moreunit.mock.mocking_template", "customTemplate");
        verify(storeManager).save(project, projectStore);
    }

    @Test
    public void getMockingTemplate_should_return_value_from_store() {
        Preferences preferences = new Preferences(storeManager);
        when(storeManager.getStore(project, false)).thenReturn(projectStore);
        when(projectStore.getString("org.moreunit.mock.mocking_template")).thenReturn("customTemplate");

        String result = preferences.getMockingTemplate(project);

        assertEquals("customTemplate", result);
    }

    @Test
    public void setSpecificSettings_should_delegate_to_store_manager() {
        Preferences preferences = new Preferences(storeManager);

        preferences.setSpecificSettings(project, true);

        verify(storeManager).setSpecificSettings(project, true);
    }

    @Test
    public void hasSpecificSettings_should_delegate_to_store_manager() {
        Preferences preferences = new Preferences(storeManager);
        when(storeManager.hasSpecificSettings(project)).thenReturn(true);

        boolean result = preferences.hasSpecificSettings(project);

        assertTrue(result);
    }
}
