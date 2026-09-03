package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IAdaptable;
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

import org.moreunit.core.commands.TmpProjectTestCase;
import org.moreunit.core.config.CoreModule;

public class GenericPropertyPageTest extends TmpProjectTestCase
{
    private static final String LANGUAGE_ID = "gpp";

    private Display display;
    private Shell shell;

    @BeforeEach
    public void createShell()
    {
        try
        {
            display = Display.getDefault();
        }
        catch (final Throwable t)
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

    private GenericPropertyPage createPage(String description)
    {
        final GenericPropertyPage page = new GenericPropertyPage(LANGUAGE_ID, description);
        page.setElement(new IAdaptable()
        {
            @SuppressWarnings("unchecked")
            public <T> T getAdapter(Class<T> adapter)
            {
                return adapter == org.eclipse.core.resources.IProject.class ? (T) project : null;
            }
        });
        return page;
    }

    @Test
    public void should_use_given_description()
    {
        final GenericPropertyPage page = createPage("Page for GPP");

        assertEquals("Page for GPP", page.getDescription());
    }

    @Test
    public void should_return_parent_unchanged_when_element_is_not_a_project()
    {
        final GenericPropertyPage page = new GenericPropertyPage(LANGUAGE_ID, null);
        page.setElement(new IAdaptable()
        {
            public <T> T getAdapter(Class<T> adapter)
            {
                return null;
            }
        });

        final Shell parent = new Shell(display);

        assertSame(parent, createContents(page, parent));
        assertEquals(0, parent.getChildren().length);

        parent.dispose();
    }

    @Test
    public void should_build_contents_when_element_is_a_project()
    {
        final GenericPropertyPage page = createPage(null);

        final Control control = createContents(page, shell);

        assertTrue(control != null);
        assertTrue(shell.getChildren().length > 0);
    }

    @Test
    public void should_show_project_specific_settings_unchecked_by_default()
    {
        final GenericPropertyPage page = createPage(null);
        createContents(page, shell);

        assertFalse(findCheckbox().getSelection());
        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_validate_page_when_project_specific_settings_get_activated()
    {
        final GenericPropertyPage page = createPage(null);
        createContents(page, shell);

        final Button checkbox = findCheckbox();
        checkbox.setSelection(true);
        checkbox.notifyListeners(SWT.Selection, new Event());

        assertTrue(checkbox.getSelection());
        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());

        checkbox.setSelection(false);
        checkbox.notifyListeners(SWT.Selection, new Event());

        assertFalse(checkbox.getSelection());
        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_save_properties_on_perform_ok()
    {
        final GenericPropertyPage page = createPage(null);
        createContents(page, shell);

        assertTrue(page.performOk());

        final LanguagePreferencesWriter writer = CoreModule.$().getPreferences().get(project).writerForLanguage(LANGUAGE_ID);
        assertEquals(Preferences.DEFAULTS.getSrcFolderPathTemplate(), writer.getSrcFolderPathTemplate());
        assertEquals(Preferences.DEFAULTS.getTestFolderPathTemplate(), writer.getTestFolderPathTemplate());
    }

    @Test
    public void should_restore_default_fields_on_perform_defaults()
    {
        final GenericPropertyPage page = createPage(null);
        createContents(page, shell);

        performDefaults(page);

        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    @Test
    public void should_validate_page_when_shown()
    {
        final GenericPropertyPage page = createPage(null);
        final Control control = createContents(page, shell);
        setControl(page, control);

        page.setVisible(true);

        assertTrue(page.isValid(), () -> "unexpected message: " + page.getMessage());
    }

    private Button findCheckbox()
    {
        final Button checkbox = findButton(shell, "Use project specific settings");
        assertNotNull(checkbox);
        return checkbox;
    }

    private Button findButton(Composite composite, String text)
    {
        for (final Control control : composite.getChildren())
        {
            if(control instanceof Button && text.equals(((Button) control).getText()))
            {
                return (Button) control;
            }
            if(control instanceof Composite)
            {
                final Button button = findButton((Composite) control, text);
                if(button != null)
                {
                    return button;
                }
            }
        }
        return null;
    }

    // The createContents method is protected and the page lives in another
    // OSGi bundle, so it can only be invoked through reflection.
    private static Control createContents(Object page, Composite parent)
    {
        try
        {
            final Method method = findMethod(page.getClass(), "createContents", Composite.class);
            method.setAccessible(true);
            return (Control) method.invoke(page, parent);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void performDefaults(Object page)
    {
        try
        {
            final Method method = findMethod(page.getClass(), "performDefaults");
            method.setAccessible(true);
            method.invoke(page);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void setControl(Object page, Control control)
    {
        try
        {
            final Method method = findMethod(page.getClass(), "setControl", Control.class);
            method.setAccessible(true);
            method.invoke(page, control);
        }
        catch (final Exception e)
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
            catch (final NoSuchMethodException e)
            {
                c = c.getSuperclass();
            }
        }
        throw new IllegalArgumentException("Method not found: " + name);
    }
}
