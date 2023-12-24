package org.moreunit.mock.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;

@RunWith(MockitoJUnitRunner.class)
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
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(false);
        // when
        propertyPage.performOk();
        // then
        verify(preferences, never()).setSpecificSettings(any(IJavaProject.class), anyBoolean());

        // given
        specificSettingsChecked = true;
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(true);
        // when
        propertyPage.performOk();
        // then
        verify(preferences, never()).setSpecificSettings(any(IJavaProject.class), anyBoolean());
    }

    @Test
    public void should_save_specific_settings_flag_when_it_changes() throws Exception
    {
        // given
        specificSettingsChecked = true;
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(false);
        // when
        propertyPage.performOk();
        // then
        verify(preferences).setSpecificSettings(any(IJavaProject.class), eq(specificSettingsChecked));

        // given
        specificSettingsChecked = false;
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(true);
        // when
        propertyPage.performOk();
        // then
        verify(preferences).setSpecificSettings(any(IJavaProject.class), eq(specificSettingsChecked));
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
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(true);

        // when
        propertyPage.initValues();

        // then
        assertThat(specificSettingsChecked).isTrue();
    }

    @Test
    public void should_disable_specific_settings_on_creation_when_project_has_no_specific_settings() throws Exception
    {
        // given
        when(preferences.hasSpecificSettings(any(IJavaProject.class))).thenReturn(false);

        // when
        propertyPage.initValues();

        // then
        assertThat(specificSettingsChecked).isFalse();
    }
}
