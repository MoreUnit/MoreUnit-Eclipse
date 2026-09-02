package org.moreunit.mock.preferences;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;

public class MainPropertyPageTest
{
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void disposeShells()
    {
        for (Shell shell : shellsToDispose)
        {
            if(! shell.isDisposed())
            {
                shell.dispose();
            }
        }
        shellsToDispose.clear();
    }
    @Mock
    private Preferences preferences;
    @Mock
    private TemplateStyleSelector templateStyleSelector;
    @Mock
    private Logger logger;

    private MainPropertyPage propertyPage;

    private boolean specificSettingsChecked;

    private static final List<Shell> shellsToDispose = new ArrayList<>();

    @BeforeEach
    public void createPropertyPage() throws Exception
    {
        propertyPage = new MainPropertyPage(preferences, templateStyleSelector, logger)
        {
            @Override
            protected void checkSpecificSettingsCheckbox(boolean checked)
            {
                specificSettingsChecked = checked;
            }

            @Override
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
        assertTrue(specificSettingsChecked);
    }

    @Test
    public void should_disable_specific_settings_on_creation_when_project_has_no_specific_settings() throws Exception
    {
        // given
        when(preferences.hasSpecificSettings(null)).thenReturn(false);

        // when
        propertyPage.initValues();

        // then
        assertFalse(specificSettingsChecked);
    }

    // --- tests using real SWT widgets ---

    @Test
    public void should_initialize_checkbox_from_project_preferences_when_content_is_created()
    {
        if(headless())
        {
            return;
        }

        IJavaProject project = mock(IJavaProject.class);
        when(preferences.hasSpecificSettings(project)).thenReturn(true);

        Shell shell = shell();
        MainPropertyPage page = new MainPropertyPage(preferences, templateStyleSelector, logger);
        page.setElement(project);
        page.createControl(shell);

        Button checkbox = findCheckbox(shell);
        assertTrue(checkbox.getSelection());
        verify(templateStyleSelector).setEnabled(true);
        verify(templateStyleSelector).createContents(any(Composite.class), eq(project));
    }

    @Test
    public void should_disable_template_selector_when_project_has_no_specific_settings_on_creation()
    {
        if(headless())
        {
            return;
        }

        IJavaProject project = mock(IJavaProject.class);
        when(preferences.hasSpecificSettings(project)).thenReturn(false);

        Shell shell = shell();
        MainPropertyPage page = new MainPropertyPage(preferences, templateStyleSelector, logger);
        page.setElement(project);
        page.createControl(shell);

        Button checkbox = findCheckbox(shell);
        assertFalse(checkbox.getSelection());
        verify(templateStyleSelector).setEnabled(false);
    }

    @Test
    public void should_enable_and_save_specific_settings_when_checkbox_is_selected_and_page_is_ok()
    {
        if(headless())
        {
            return;
        }

        IJavaProject project = mock(IJavaProject.class);
        when(preferences.hasSpecificSettings(project)).thenReturn(false);
        when(logger.debugEnabled()).thenReturn(true);

        Shell shell = shell();
        MainPropertyPage page = new MainPropertyPage(preferences, templateStyleSelector, logger);
        page.setElement(project);
        page.createControl(shell);

        // when the user checks the "specific settings" checkbox
        Button checkbox = findCheckbox(shell);
        checkbox.setSelection(true);
        checkbox.notifyListeners(SWT.Selection, new Event());

        verify(templateStyleSelector).setEnabled(true);

        // when
        page.performOk();

        // then
        verify(preferences).setSpecificSettings(project, true);
        verify(templateStyleSelector).savePreferences();
        verify(logger).debug(org.mockito.ArgumentMatchers.contains("Defined specific settings"));
    }

    @Test
    public void should_disable_and_save_specific_settings_when_checkbox_is_unselected_and_page_is_ok()
    {
        if(headless())
        {
            return;
        }

        IJavaProject project = mock(IJavaProject.class);
        when(preferences.hasSpecificSettings(project)).thenReturn(true);
        when(logger.debugEnabled()).thenReturn(true);

        Shell shell = shell();
        MainPropertyPage page = new MainPropertyPage(preferences, templateStyleSelector, logger);
        page.setElement(project);
        page.createControl(shell);

        // when the user unchecks the "specific settings" checkbox
        Button checkbox = findCheckbox(shell);
        assertTrue(checkbox.getSelection());
        checkbox.setSelection(false);
        checkbox.notifyListeners(SWT.Selection, new Event());

        verify(templateStyleSelector).setEnabled(false);

        // when
        page.performOk();

        // then
        verify(preferences).setSpecificSettings(project, false);
        verify(templateStyleSelector, never()).savePreferences();
        verify(logger).debug(org.mockito.ArgumentMatchers.contains("Disabled specific settings"));
    }

    private static boolean headless()
    {
        return Display.getDefault() == null;
    }

    private static Shell shell()
    {
        Shell shell = new Shell(Display.getDefault());
        shellsToDispose.add(shell);
        return shell;
    }

    private static Button findCheckbox(Composite composite)
    {
        for (Control child : composite.getChildren())
        {
            if(child instanceof Button button && "Use project specific settings".equals(button.getText()))
            {
                return button;
            }
            if(child instanceof Composite nested)
            {
                Button found = findCheckbox(nested);
                if(found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }
}
