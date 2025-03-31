package org.moreunit.mock.preferences;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreferencesTest
{
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
