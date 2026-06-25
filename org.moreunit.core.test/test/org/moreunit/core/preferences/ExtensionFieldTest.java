package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ExtensionFieldTest
{
    private Text textField = mock(Text.class);

    private ExtensionField field = new ExtensionField(mock(Composite.class, Mockito.RETURNS_DEEP_STUBS), 0)
    {
        @Override
        public Text getField()
        {
            return textField;
        }
    };

    @Test
    public void should_reject_empty_extension() throws Exception
    {
        when(textField.getText()).thenReturn("");
        assertFalse(field.isValid());

        when(textField.getText()).thenReturn(".");
        assertFalse(field.isValid());

        when(textField.getText()).thenReturn("*.");
        assertFalse(field.isValid());
    }

    @Test
    public void should_reject_non_alphanum_extension() throws Exception
    {
        when(textField.getText()).thenReturn("a*bc");
        assertFalse(field.isValid());

        when(textField.getText()).thenReturn("a_bc");
        assertFalse(field.isValid());
    }

    @Test
    public void should_return_clean_extension() throws Exception
    {
        when(textField.getText()).thenReturn("rb");
        assertEquals(field.getExtension(), "rb");

        when(textField.getText()).thenReturn("RB");
        assertEquals(field.getExtension(), "rb");

        when(textField.getText()).thenReturn(".rb");
        assertEquals(field.getExtension(), "rb");

        when(textField.getText()).thenReturn("*.rb");
        assertEquals(field.getExtension(), "rb");
    }
}
