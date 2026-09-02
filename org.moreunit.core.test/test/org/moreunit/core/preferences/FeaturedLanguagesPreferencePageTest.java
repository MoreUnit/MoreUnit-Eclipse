package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the featured languages welcome pages. The {@code createContents}
 * methods are protected and the pages live in another OSGi bundle, so they
 * can only be invoked through reflection.
 */
public class FeaturedLanguagesPreferencePageTest
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

    private Control createContents(Object page)
    {
        try
        {
            Method method = findMethod(page.getClass(), "createContents", Composite.class);
            method.setAccessible(true);
            return (Control) method.invoke(page, shell);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_create_welcome_contents_for_preference_page()
    {
        FeaturedLanguagesPreferencePage page = new FeaturedLanguagesPreferencePage();
        page.init(null);

        Control control = createContents(page);

        assertTrue(control instanceof Composite);
        assertTrue(((Composite) control).getChildren().length > 0, "Page should contain labels");
    }

    @Test
    public void should_create_property_page_with_link_to_workspace_preferences()
    {
        FeaturedLanguagesPropertyPage page = new FeaturedLanguagesPropertyPage();

        Control control = createContents(page);

        Composite composite = (Composite) control;
        Link link = findLink(composite);

        assertNotNull(link, "Property page should contain a link");
        assertEquals("<A>Open workspace preferences</A>", link.getText());
    }

    private Link findLink(Composite composite)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Link)
            {
                return (Link) control;
            }
            if(control instanceof Composite)
            {
                Link link = findLink((Composite) control);
                if(link != null)
                {
                    return link;
                }
            }
        }
        return null;
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
