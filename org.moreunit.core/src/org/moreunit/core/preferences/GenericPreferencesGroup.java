package org.moreunit.core.preferences;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.core.ui.ExpandableCompositeContainer;

public interface GenericPreferencesGroup
{
    public void createContents();
    public void forceFocus();
    public String getError();
    public String getWarning();
    public void saveProperties();
    public void setEnabled(boolean enabled);
    public void restoreDefaults();
    public void addModifyListener(ModifyListener listener);
}
