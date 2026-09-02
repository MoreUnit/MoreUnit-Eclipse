package org.moreunit.mock.preferences;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;

public class PreferenceStoreManagerTest
{
    @Mock
    private IPreferenceStore workspaceStore;
    @Mock
    private Logger logger;

    private PreferenceStoreManager storeManager;

    private static final String SPECIFIC_SETTINGS_KEY = "org.moreunit.mock.has_specific_settings";

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
        storeManager = new PreferenceStoreManager(workspaceStore, logger);
    }

    @Test
    public void should_return_workspace_store_when_no_project_is_given()
    {
        assertSame(workspaceStore, storeManager.getStore(null, false));
        assertSame(workspaceStore, storeManager.getWorkspaceStore());
    }

    @Test
    public void should_read_specific_settings_from_workspace_store_when_no_project_is_given()
    {
        // given
        when(workspaceStore.getBoolean(SPECIFIC_SETTINGS_KEY)).thenReturn(true);

        // then
        assertTrue(storeManager.hasSpecificSettings(null));
    }

    @Test
    public void should_write_specific_settings_to_workspace_store_when_no_project_is_given()
    {
        // when
        storeManager.setSpecificSettings(null, true);

        // then
        verify(workspaceStore).setValue(SPECIFIC_SETTINGS_KEY, true);
    }
}
