package org.moreunit.mock.preferences;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PreferencesTest
{
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }
    @Mock
    private PreferenceStoreManager storeManager;
    @Mock
    private IPreferenceStore workspacePreferenceStore;

    @Test
    public void should_register_preference_default_values() throws Exception
    {
        when(storeManager.getWorkspaceStore()).thenReturn(workspacePreferenceStore);

        new Preferences(storeManager);

        verify(workspacePreferenceStore).setDefault(eq(Preferences.MOCKING_TEMPLATE.name), anyString());
    }
}
