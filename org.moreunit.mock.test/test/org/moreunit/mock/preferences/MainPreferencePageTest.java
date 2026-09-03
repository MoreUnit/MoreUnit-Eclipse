package org.moreunit.mock.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
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
import org.moreunit.mock.templates.LoadingResult;
import org.moreunit.mock.templates.MockingTemplateLoader;

public class MainPreferencePageTest
{
    @Mock
    private TemplateStyleSelector templateStyleSelector;
    @Mock
    private MockingTemplateLoader templateLoader;

    private Display display;
    private Shell shell;
    private boolean headless;

    private MainPreferencePage preferencePage;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);

        display = Display.getDefault();
        headless = display == null;
        if(headless)
        {
            return;
        }
        display.syncExec(() -> shell = new Shell(display));

        preferencePage = new MainPreferencePage(templateStyleSelector, templateLoader);
        when(templateLoader.getWorkspaceTemplatesLocation()).thenReturn("/some/workspace/location");
    }

    @AfterEach
    public void tearDown()
    {
        if(shell != null && ! shell.isDisposed())
        {
            display.syncExec(() -> shell.dispose());
        }
    }

    @Test
    public void should_create_contents_with_template_location_and_reload_button()
    {
        if(headless)
        {
            return;
        }

        preferencePage.createControl(shell);

        final Control content = preferencePage.getControl();
        assertEquals(shell, content.getShell());

        final Button reloadButton = findButton(shell, "Reload templates");
        assertEquals("Reload templates", reloadButton.getText());
    }

    @Test
    public void should_reload_templates_when_reload_button_is_selected()
    {
        if(headless)
        {
            return;
        }

        preferencePage.createControl(shell);

        final LoadingResult loadingResult = new LoadingResult();
        when(templateLoader.loadTemplates()).thenReturn(loadingResult);

        final Button reloadButton = findButton(shell, "Reload templates");
        reloadButton.notifyListeners(SWT.Selection, new Event());

        verify(templateLoader).loadTemplates();
        verify(templateStyleSelector).reloadTemplates();
    }

    @Test
    public void should_save_preferences_when_perform_ok()
    {
        if(headless)
        {
            return;
        }

        preferencePage.createControl(shell);

        assertEquals(true, preferencePage.performOk());

        verify(templateStyleSelector).savePreferences();
    }

    @Test
    public void should_inform_user_about_invalid_templates_when_reload_button_is_selected() throws Exception
    {
        if(headless)
        {
            return;
        }

        preferencePage.createControl(shell);

        final LoadingResult loadingResult = new LoadingResult();
        loadingResult.addInvalidTemplate(java.net.URI.create("file:/templates/bad.xml").toURL(), new RuntimeException("boom"));
        when(templateLoader.loadTemplates()).thenReturn(loadingResult);

        final Button reloadButton = findButton(shell, "Reload templates");
        final Display display = shell.getDisplay();

        final java.util.Set<Shell> knownShells = new java.util.HashSet<>(java.util.Arrays.asList(display.getShells()));
        final java.util.concurrent.atomic.AtomicReference<String> dialogMessage = new java.util.concurrent.atomic.AtomicReference<>();

        // the reload action opens a modal error dialog; it is dismissed by a
        // self-re-scheduling timer so that the event loop is never blocked
        final int[] attempts = { 0 };
        final Runnable closer = new Runnable()
        {
            public void run()
            {
                for (final Shell s : display.getShells())
                {
                    if(! knownShells.contains(s) && ! s.isDisposed())
                    {
                        for (final Control c : s.getChildren())
                        {
                            if(c instanceof org.eclipse.swt.widgets.Label && c.isVisible() && ((org.eclipse.swt.widgets.Label) c).getText().contains("could not be loaded"))
                            {
                                dialogMessage.set(((org.eclipse.swt.widgets.Label) c).getText());
                            }
                        }
                        s.close();
                        return;
                    }
                }
                if(++attempts[0] < 30)
                {
                    display.timerExec(100, this);
                }
            }
        };
        display.timerExec(100, closer);

        display.asyncExec(() -> reloadButton.notifyListeners(SWT.Selection, new Event()));

        // process events (including the nested dialog loop) until quiet
        final long deadline = System.currentTimeMillis() + 10_000;
        while(System.currentTimeMillis() < deadline)
        {
            if(! display.readAndDispatch())
            {
                display.sleep();
                if(dialogMessage.get() != null)
                {
                    break;
                }
            }
        }

        assertNotNull(dialogMessage.get());
        assertTrue(dialogMessage.get().contains("The following templates could not be loaded"));
        assertTrue(dialogMessage.get().contains("bad.xml"));
        verify(templateLoader).loadTemplates();
        verify(templateStyleSelector).reloadTemplates();
    }

    private static Button findButton(Composite composite, String text)
    {
        for (final Control child : composite.getChildren())
        {
            if(child instanceof final Button button && text.equals(button.getText()))
            {
                return button;
            }
            if(child instanceof final Composite nested)
            {
                final Button found = findButton(nested, text);
                if(found != null)
                {
                    return found;
                }
            }
        }
        throw new AssertionError("No button found with text: " + text);
    }
}
