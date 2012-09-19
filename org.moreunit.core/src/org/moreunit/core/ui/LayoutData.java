package org.moreunit.core.ui;

import org.eclipse.swt.layout.GridData;

public class LayoutData
{
    public static GridData labelledField()
    {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = 15;
        return gd;
    }

    public static GridData row(int numColumns)
    {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        return gd;
    }

    public static GridData fillGrid()
    {
        return new GridData(GridData.FILL, GridData.FILL, true, true);
    }
}
