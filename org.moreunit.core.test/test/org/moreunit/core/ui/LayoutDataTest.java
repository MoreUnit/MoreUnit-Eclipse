package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.layout.GridData;
import org.junit.jupiter.api.Test;

public class LayoutDataTest
{
    @Test
    public void should_create_labelled_field_data_with_indent()
    {
        final GridData gd = LayoutData.labelledField();

        assertEquals(GridData.FILL, gd.horizontalAlignment);
        assertTrue(gd.grabExcessHorizontalSpace);
        assertEquals(15, gd.horizontalIndent);
    }

    @Test
    public void should_create_col_span_data()
    {
        final GridData gd = LayoutData.colSpan(3);

        assertEquals(GridData.FILL, gd.horizontalAlignment);
        assertTrue(gd.grabExcessHorizontalSpace);
        assertEquals(3, gd.horizontalSpan);
    }

    @Test
    public void should_create_fill_grid_data()
    {
        final GridData gd = LayoutData.fillGrid();

        assertEquals(GridData.FILL, gd.horizontalAlignment);
        assertEquals(GridData.FILL, gd.verticalAlignment);
        assertTrue(gd.grabExcessHorizontalSpace);
        assertTrue(gd.grabExcessVerticalSpace);
    }

    @Test
    public void should_create_fill_row_data_spanning_all_columns()
    {
        final GridData gd = LayoutData.fillRow();

        assertEquals(GridData.FILL, gd.horizontalAlignment);
        assertTrue(gd.grabExcessHorizontalSpace);
        assertEquals(Integer.MAX_VALUE, gd.horizontalSpan);
    }
}
