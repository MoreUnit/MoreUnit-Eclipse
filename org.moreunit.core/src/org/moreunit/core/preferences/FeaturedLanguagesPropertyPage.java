package org.moreunit.core.preferences;

import static java.util.Collections.emptyMap;
import static org.moreunit.core.ui.Labels.wrappingLabel;
import static org.moreunit.core.util.ArrayUtils.array;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.moreunit.core.ui.Composites;

public class FeaturedLanguagesPropertyPage extends FeaturedLanguagesPreferencePage
{
    @Override
    protected void createLastPart(Composite composite)
    {
        wrappingLabel("The main page of the section allows you to target all possible languages at once. You may also create \"per-language\" settings, using the form at the bottom of the corresponding workspace preference page:", WIDTH_HINT, composite);
        createLink(composite, "Open workspace preferences");
    }

    private Link createLink(Composite parent, String text)
    {
        return Composites.link(parent, text, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                final String id = PreferencePages.OTHER_LANGUAGES;
                PreferencesUtil.createPreferenceDialogOn(getShell(), id, array(id), emptyMap()).open();
            }
        });
    }
}
