package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.moreunit.core.config.CoreModule;
import org.moreunit.core.languages.Language;

/**
 * Tests the "Other languages" preference page. Warning and preference dialogs
 * opened by the page are non-modally closed from a queued {@code asyncExec},
 * which gets dispatched by the dialog's own event loop.
 */
public class OtherLanguagesPreferencePageTest
{
    private static final String LANGUAGE_EXT = "olpx";
    private static final String LANGUAGE_NAME = "OLPX";

    private Display display;
    private Shell shell;

    @BeforeEach
    public void createShell()
    {
        try
        {
            display = Display.getDefault();
        }
        catch (Throwable t)
        {
            display = null;
        }
        assumeTrue(display != null, "No SWT display available");
        shell = new Shell(display);
    }

    @AfterEach
    public void disposeShell()
    {
        removeTestLanguage();
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    private static void removeTestLanguage()
    {
        // removed directly from the user-defined repository so that no
        // listener is notified (listeners would open a preference dialog)
        CoreModule.$().getPreferences().remove(new Language(LANGUAGE_EXT));
    }

    private OtherLanguagesPreferencePage createPage()
    {
        return new OtherLanguagesPreferencePage();
    }

    private Control createContents(OtherLanguagesPreferencePage page)
    {
        try
        {
            Method method = OtherLanguagesPreferencePage.class.getSuperclass().getDeclaredMethod("createContents", Composite.class);
            method.setAccessible(true);
            Control control = (Control) method.invoke(page, shell);

            // needed so that the page handlers can resolve their shell
            Method setControl = findMethod(page.getClass(), "setControl", Control.class);
            setControl.setAccessible(true);
            setControl.invoke(page, control);

            return control;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Method findMethod(Class<?> c, String name, Class<?>... parameterTypes)
    {
        while(c != null)
        {
            try
            {
                return c.getDeclaredMethod(name, parameterTypes);
            }
            catch (NoSuchMethodException e)
            {
                c = c.getSuperclass();
            }
        }
        throw new IllegalArgumentException("Method not found: " + name);
    }

    private void createLanguageConfiguration(String name, String extension)
    {
        Group group = findGroup(shell, "Per-language configurations may also be created:");
        assertNotNull(group, "Should find the per-language configuration group");

        Text[] fields = findTextFields(group);
        assertEquals(2, fields.length, "Group should contain the language name and extension fields");

        fields[0].setText(name);
        fields[1].setText(extension);

        Button button = findButton(group, "Create Configuration");
        assertNotNull(button);

        button.notifyListeners(SWT.Selection, new Event());
    }

    /**
     * Queues the closing of any modal warning dialog opened by the page: the
     * queued runnable is dispatched by the dialog's own event loop.
     */
    private void scheduleWarningDialogCloser()
    {
        scheduleShellCloser(s -> shell.equals(s.getParent()));
    }

    /**
     * Queues the closing of the modal preference dialog opened as a
     * consequence of a language configuration change: the queued runnable is
     * dispatched by the dialog's own event loop. The dialog is parented to the
     * workbench shell and titled "Preferences".
     */
    private void schedulePreferenceDialogCloser()
    {
        scheduleShellCloser(s -> ! s.equals(shell) && "Preferences".equals(s.getText()));
    }

    private void scheduleShellCloser(java.util.function.Predicate<Shell> matches)
    {
        display.asyncExec(new Runnable()
        {
            int attemptsLeft = 50;

            public void run()
            {
                boolean closedOne = false;
                for (Shell s : display.getShells())
                {
                    if(matches.test(s))
                    {
                        s.close();
                        closedOne = true;
                    }
                }
                if(! closedOne && --attemptsLeft > 0)
                {
                    display.timerExec(100, this);
                }
            }
        });
    }

    private void drainEvents()
    {
        while (display.readAndDispatch())
        {
            // process all pending events
        }
    }

    @Test
    public void should_create_contents_with_default_and_per_language_sections()
    {
        OtherLanguagesPreferencePage page = createPage();

        Control control = createContents(page);

        assertTrue(control != null);
        assertNotNull(findButton(shell, "Create Configuration"));
        assertTrue(findTextFields(shell).length >= 2);
    }

    @Test
    public void should_apply_default_configuration_on_perform_ok()
    {
        OtherLanguagesPreferencePage page = createPage();
        createContents(page);

        assertTrue(page.performOk());
    }

    @Test
    public void should_warn_and_not_create_configuration_when_extension_is_invalid()
    {
        OtherLanguagesPreferencePage page = createPage();
        createContents(page);

        scheduleWarningDialogCloser();
        createLanguageConfiguration("Invalid", "1!");

        drainEvents();

        assertFalse(CoreModule.$().getLanguageRepository().contains("1!"));
    }

    @Test
    public void should_warn_and_not_create_configuration_when_it_already_exists()
    {
        CoreModule.$().getPreferences().add(new Language(LANGUAGE_EXT, LANGUAGE_NAME));

        OtherLanguagesPreferencePage page = createPage();
        createContents(page);

        scheduleWarningDialogCloser();
        createLanguageConfiguration(LANGUAGE_NAME, LANGUAGE_EXT);

        drainEvents();

        assertTrue(CoreModule.$().getLanguageRepository().contains(LANGUAGE_EXT));
    }

    @Test
    public void should_create_new_language_configuration_when_button_is_pressed()
    {
        assumeTrue(org.moreunit.core.MoreUnitCore.get() != null, "MoreUnit core plugin not started");

        OtherLanguagesPreferencePage page = createPage();
        createContents(page);

        // the creation triggers a language configuration listener that opens
        // a modal preference dialog: close it from the queued runnable
        schedulePreferenceDialogCloser();
        createLanguageConfiguration(LANGUAGE_NAME, LANGUAGE_EXT);

        drainEvents();

        assertTrue(CoreModule.$().getLanguageRepository().contains(LANGUAGE_EXT));
    }

    private Button findButton(Composite composite, String text)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Button && text.equals(((Button) control).getText()))
            {
                return (Button) control;
            }
            if(control instanceof Composite)
            {
                Button button = findButton((Composite) control, text);
                if(button != null)
                {
                    return button;
                }
            }
        }
        return null;
    }

    private Group findGroup(Composite composite, String text)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Group && text.equals(((Group) control).getText()))
            {
                return (Group) control;
            }
            if(control instanceof Composite)
            {
                Group group = findGroup((Composite) control, text);
                if(group != null)
                {
                    return group;
                }
            }
        }
        return null;
    }

    private Text[] findTextFields(Composite composite)
    {
        java.util.List<Text> texts = new java.util.ArrayList<>();
        collectTexts(composite, texts);
        return texts.toArray(new Text[texts.size()]);
    }

    private void collectTexts(Composite composite, java.util.List<Text> texts)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Text)
            {
                texts.add((Text) control);
            }
            else if(control instanceof Composite)
            {
                collectTexts((Composite) control, texts);
            }
        }
    }
}
