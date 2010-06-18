package org.moreunit.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;

public interface ITreeContentAndDefaultSelectionProvider extends ITreeContentProvider
{
    ISelection getDefaultSelection();
}
