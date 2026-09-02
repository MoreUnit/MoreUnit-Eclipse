package org.moreunit.properties;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.moreunit.test.context.ContextTestCase;

/**
 * Base class for tests that need to instantiate real SWT widgets (the test
 * harness runs on the UI thread with a workbench and a display).
 */
public abstract class SwtPageTestCase extends ContextTestCase
{
    protected Display display;
    protected Shell shell;

    @BeforeEach
    public void createSwtShell()
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
    public void disposeSwtShell()
    {
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    // Some pages and blocks have protected or private members that cannot be
    // accessed from this test bundle, so they are reached through reflection.

    protected static Object getField(Object target, String fieldName)
    {
        try
        {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static void setField(Object target, String fieldName, Object value)
    {
        try
        {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Field findField(Class<?> c, String name)
    {
        for (Class<?> current = c; current != null; current = current.getSuperclass())
        {
            try
            {
                return current.getDeclaredField(name);
            }
            catch (NoSuchFieldException e)
            {
                // continue with superclass
            }
        }
        throw new IllegalArgumentException("Field not found: " + name);
    }

    protected static Object invoke(Object target, String methodName, Object... args)
    {
        try
        {
            Method method = findMethod(target.getClass(), methodName, args);
            method.setAccessible(true);
            return method.invoke(target, args);
        }
        catch (ReflectiveOperationException e)
        {
            if(e.getCause() instanceof RuntimeException re)
                throw re;
            throw new RuntimeException(e);
        }
    }

    private static Method findMethod(Class<?> c, String name, Object[] args)
    {
        for (Class<?> current = c; current != null; current = current.getSuperclass())
        {
            for (Method method : current.getDeclaredMethods())
            {
                if(method.getName().equals(name) && method.getParameterCount() == args.length)
                {
                    return method;
                }
            }
        }
        throw new IllegalArgumentException("Method not found: " + name + " with " + args.length + " arg(s)");
    }

    protected static Control createContents(Object page, Composite parent)
    {
        return (Control) invoke(page, "createContents", parent);
    }

    protected static void performApply(Object page)
    {
        invoke(page, "performApply");
    }

    protected static Button findButton(Composite composite, String text)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Button button && text.equals(button.getText()))
            {
                return button;
            }
            if(control instanceof Composite child)
            {
                Button button = findButton(child, text);
                if(button != null)
                {
                    return button;
                }
            }
        }
        return null;
    }

    /**
     * Returns the text field placed next to the label having the given text
     * (fields and their labels are consecutive children of their parent).
     */
    protected static Text findTextByLabel(Composite composite, String labelText)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Label && labelText.equals(((Label) control).getText()))
            {
                Label label = (Label) control;
                Control[] siblings = label.getParent().getChildren();
                for (int i = indexOf(siblings, label); i < siblings.length - 1; i++)
                {
                    if(siblings[i + 1] instanceof Text)
                    {
                        return (Text) siblings[i + 1];
                    }
                }
            }
            if(control instanceof Composite child)
            {
                Text text = findTextByLabel(child, labelText);
                if(text != null)
                {
                    return text;
                }
            }
        }
        return null;
    }

    private static int indexOf(Control[] controls, Control control)
    {
        for (int i = 0; i < controls.length; i++)
        {
            if(controls[i] == control)
            {
                return i;
            }
        }
        return 0;
    }

    protected static Text findTextWithTooltip(Composite composite, String tooltip)
    {
        for (Control control : composite.getChildren())
        {
            if(control instanceof Text text && tooltip.equals(text.getToolTipText()))
            {
                return text;
            }
            if(control instanceof Composite child)
            {
                Text text = findTextWithTooltip(child, tooltip);
                if(text != null)
                {
                    return text;
                }
            }
        }
        return null;
    }

    protected static List<Widget> allWidgets(Composite composite)
    {
        List<Widget> widgets = new ArrayList<>();
        collectWidgets(composite, widgets);
        return widgets;
    }

    private static void collectWidgets(Composite composite, List<Widget> widgets)
    {
        widgets.add(composite);
        for (Control control : composite.getChildren())
        {
            widgets.add(control);
            if(control instanceof Composite child)
            {
                collectWidgets(child, widgets);
            }
        }
    }
}
