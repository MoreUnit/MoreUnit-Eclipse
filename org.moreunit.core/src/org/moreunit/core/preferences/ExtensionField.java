package org.moreunit.core.preferences;

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A form field for file extensions.
 */
public class ExtensionField
{
    private static final Pattern ALPHANUM = Pattern.compile("[a-zA-Z0-9]+");

    private final Composite parent;
    private final int style;
    private Text field;

    public ExtensionField(Composite parent, int style)
    {
        this.parent = parent;
        this.style = style;
    }

    public Text getField()
    {
        if(field == null)
        {
            field = new Text(parent, style);
        }
        return field;
    }

    public void setLayoutData(Object layoutData)
    {
        getField().setLayoutData(layoutData);
    }

    public void setText(String text)
    {
        getField().setText(text);
    }

    public String getExtension()
    {
        return getField().getText().trim().replaceFirst("\\*?\\.", "").toLowerCase();
    }

    public boolean isValid()
    {
        return ALPHANUM.matcher(getExtension()).matches();
    }
}
