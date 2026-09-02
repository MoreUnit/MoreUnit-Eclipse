package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.moreunit.core.config.CoreModule;
import org.moreunit.core.languages.Language;

public class GenericPreferencePageTest
{
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
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    private GenericPreferencePage createPage(String languageId)
    {
        Language lang = new Language(languageId, languageId.toUpperCase());
        return new GenericPreferencePage(lang, CoreModule.$().getPreferences().writerForLanguage(languageId), CoreModule.$().getPreferences());
    }

    @Test
    public void should_use_language_label_as_title()
    {
        GenericPreferencePage page = createPage("zzy");

        assertEquals("ZZY", page.getTitle());
    }

    @Test
    public void should_init_without_error()
    {
        GenericPreferencePage page = createPage("zzy");

        page.init(null);
    }

    @Test
    public void should_create_contents_with_fields()
    {
        GenericPreferencePage page = createPage("zzy");

        Control control = createContents(page, shell);

        assertTrue(control != null);
        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_save_properties_on_perform_ok()
    {
        GenericPreferencePage page = createPage("zzy");
        createContents(page, shell);

        assertTrue(page.performOk());

        LanguagePreferencesWriter writer = CoreModule.$().getPreferences().writerForLanguage("zzy");
        assertEquals(Preferences.DEFAULTS.getSrcFolderPathTemplate(), writer.getSrcFolderPathTemplate());
        assertEquals(Preferences.DEFAULTS.getTestFolderPathTemplate(), writer.getTestFolderPathTemplate());
    }

    @Test
    public void should_restore_default_fields_on_perform_defaults()
    {
        GenericPreferencePage page = createPage("zzy");
        createContents(page, shell);

        performDefaults(page);

        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_validate_page_when_shown()
    {
        GenericPreferencePage page = createPage("zzy");
        Control control = createContents(page, shell);
        setControl(page, control);

        page.setVisible(true);

        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_offer_delete_configuration_button()
    {
        GenericPreferencePage page = createPage("zzy");
        createContents(page, shell);

        Button deleteButton = findButton(shell, "Delete Configuration");

        assertNotNull(deleteButton);
        assertFalse(deleteButton.getSelection());
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

    // The createContents methods are protected and the pages live in another
    // OSGi bundle, so they can only be invoked through reflection.
    private static Control createContents(Object page, Composite parent)
    {
        try
        {
            Method method = findMethod(page.getClass(), "createContents", Composite.class);
            method.setAccessible(true);
            return (Control) method.invoke(page, parent);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void performDefaults(Object page)
    {
        try
        {
            Method method = findMethod(page.getClass(), "performDefaults");
            method.setAccessible(true);
            method.invoke(page);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void setControl(Object page, Control control)
    {
        try
        {
            Method method = findMethod(page.getClass(), "setControl", Control.class);
            method.setAccessible(true);
            method.invoke(page, control);
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
}
