package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SeparatorElementTest
{
    @Test
    public void should_not_provide_element()
    {
        assertFalse(new SeparatorElement().provideElement());
    }

    @Test
    public void should_throw_on_execute()
    {
        assertThrows(UnsupportedOperationException.class, () -> new SeparatorElement().execute());
    }

    @Test
    public void should_have_no_image()
    {
        assertNull(new SeparatorElement().getImage());
    }

    @Test
    public void should_have_separator_text()
    {
        assertEquals("_________________", new SeparatorElement().getText());
    }
}
