package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.swt.layout.GridLayout;
import org.junit.jupiter.api.Test;

public class GridLayoutsTest
{
    @Test
    public void should_create_layout_without_margins()
    {
        GridLayout layout = GridLayouts.noMargin();

        assertEquals(0, layout.marginHeight);
        assertEquals(0, layout.marginWidth);
        assertFalse(layout.makeColumnsEqualWidth);
    }

    @Test
    public void should_create_layout_with_one_column_by_default()
    {
        GridLayout layout = GridLayouts.noMargin();

        assertEquals(1, layout.numColumns);
    }
}
