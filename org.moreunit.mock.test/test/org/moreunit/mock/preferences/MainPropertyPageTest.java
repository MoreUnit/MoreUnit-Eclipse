package org.moreunit.mock.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MainPropertyPageTest
{
    @Mock
    private Preferences preferences;
    @Mock
    private TemplateStyleSelector templateStyleSelector;
    @Mock
    private Logger logger;

    private MainPropertyPage propertyPage;

    private boolean specificSettingsChecked;

    @Before
    public void createPropertyPage() throws Exception
    {
        propertyPage = new MainPropertyPage(preferences, templateStyleSelector, logger)
        {
            protected void checkSpecificSettingsCheckbox(boolean checked)
            {
                specificSettingsChecked = checked;
            }

            protected boolean specificSettingsChecked()
            {
                return specificSettingsChecked;
            }
        };
    }

    @Test
    public void should_not_save_specific_settings_flag_when_it_does_not_change() throws Exception
    {
        // given
        specificSettingsChecked = false;
        when(preferences.hasSpecificSettings(null)).thenReturn(false);
        // when
        propertyPage.performOk();
        // then
        verify(preferences, never()).setSpecificSettings(null, false);

        // given
        specificSettingsChecked = true;
        when(preferences.hasSpecificSettings(null)).thenReturn(true);
        // when
        propertyPage.performOk();
        // then
        verify(preferences, never()).setSpecificSettings(null, true);
    }

    @Test
    public void should_save_specific_settings_flag_when_it_changes() throws Exception
    {
        // given
        specificSettingsChecked = true;
        when(preferences.hasSpecificSettings(null)).thenReturn(false);
        // when
        propertyPage.performOk();
        // then
        verify(preferences).setSpecificSettings(null, specificSettingsChecked);

        // given
        specificSettingsChecked = false;
        when(preferences.hasSpecificSettings(null)).thenReturn(true);
        // when
        propertyPage.performOk();
        // then
        verify(preferences).setSpecificSettings(null, specificSettingsChecked);
    }

    @Test
    public void should_not_save_settings_when_specific_settings_are_not_used() throws Exception
    {
        // given
        specificSettingsChecked = false;

        // when
        propertyPage.performOk();

        // then
        verify(templateStyleSelector, never()).savePreferences();
    }

    @Test
    public void should_save_settings_when_specific_settings_are_used() throws Exception
    {
        // given
        specificSettingsChecked = true;

        // when
        propertyPage.performOk();

        // then
        verify(templateStyleSelector).savePreferences();
    }

    @Test
    public void should_enable_specific_settings_on_creation_when_project_has_specific_settings() throws Exception
    {
        // given
        when(preferences.hasSpecificSettings(null)).thenReturn(true);

        // when
        propertyPage.initValues();

        // then
        assertThat(specificSettingsChecked).isTrue();
    }

    @Test
    public void should_disable_specific_settings_on_creation_when_project_has_no_specific_settings() throws Exception
    {
        // given
        when(preferences.hasSpecificSettings(null)).thenReturn(false);

        // when
        propertyPage.initValues();

        // then
        assertThat(specificSettingsChecked).isFalse();
    }
}
