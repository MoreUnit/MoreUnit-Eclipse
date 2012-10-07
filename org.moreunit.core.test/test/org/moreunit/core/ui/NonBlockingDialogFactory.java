package org.moreunit.core.ui;

public class NonBlockingDialogFactory extends DialogFactory
{
    public NonBlockingDialogFactory()
    {
        super(null);
    }

    @Override
    public Dialog createErrorDialog(String message)
    {
        return new NullDialog();
    }

    @Override
    public Dialog createInfoDialog(String message)
    {
        return new NullDialog();
    }
}
