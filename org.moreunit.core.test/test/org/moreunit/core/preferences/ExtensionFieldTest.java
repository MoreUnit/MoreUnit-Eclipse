package org.moreunit.core.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.mockito.Mockito;

public class ExtensionFieldTest
{
    private Text textField = mock(Text.class);

    private ExtensionField field = new ExtensionField(mock(Composite.class, Mockito.RETURNS_DEEP_STUBS), 0)
    {
        public Text getField()
        {
            return textField;
        }
    };

    @Test
    public void should_reject_empty_extension() throws Exception
    {
        when(textField.getText()).thenReturn("");
        assertThat(field.isValid()).isFalse();

        when(textField.getText()).thenReturn(".");
        assertThat(field.isValid()).isFalse();

        when(textField.getText()).thenReturn("*.");
        assertThat(field.isValid()).isFalse();
    }

    @Test
    public void should_reject_non_alphanum_extension() throws Exception
    {
        when(textField.getText()).thenReturn("a*bc");
        assertThat(field.isValid()).isFalse();

        when(textField.getText()).thenReturn("a_bc");
        assertThat(field.isValid()).isFalse();
    }

    @Test
    public void should_return_clean_extension() throws Exception
    {
        when(textField.getText()).thenReturn("rb");
        assertThat(field.getExtension()).isEqualTo("rb");

        when(textField.getText()).thenReturn("RB");
        assertThat(field.getExtension()).isEqualTo("rb");

        when(textField.getText()).thenReturn(".rb");
        assertThat(field.getExtension()).isEqualTo("rb");

        when(textField.getText()).thenReturn("*.rb");
        assertThat(field.getExtension()).isEqualTo("rb");
    }
}
