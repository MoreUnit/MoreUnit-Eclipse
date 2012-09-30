package org.moreunit.core.ui;

import org.eclipse.swt.layout.GridData;

public class LayoutData
{
    private static final int SPAN_ALL_COLS = Integer.MAX_VALUE;

    public static GridData labelledField()
    {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = 15;
        return gd;
    }

    public static GridData colSpan(int numColumns)
    {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        return gd;
    }

    public static GridData fillGrid()
    {
        return new GridData(GridData.FILL, GridData.FILL, true, true);
    }

    public static GridData fillRow()
    {
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = SPAN_ALL_COLS;
        return gd;
    }
}
