package org.moreunit.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.core.ui.ExpandableCompositeContainer;

class GenericConfigurationPage
{
    private final PreferencePage page;
    private final LanguagePreferencesWriter prefWriter;
    private ExpandableCompositeContainer container;
    private List<GenericPreferencesGroup> groups;
    
    public GenericConfigurationPage(PreferencePage page, LanguagePreferencesWriter prefWriter)
    {
        this.page = page;
        this.prefWriter = prefWriter;
        this.groups = new ArrayList<GenericPreferencesGroup>();
    }

    public void createContainer(final Composite parent)
    {
        container = new ExpandableCompositeContainer(parent);
    }

    public ExpandableCompositeContainer getContainer()
    {
        return container;
    }

    public void createContents()
    {
        Composite parent = container;

        groups.clear();
        groups.add(new FolderPatternGroup(parent, container, prefWriter));
        groups.add(new FileExtGroup(parent, container, prefWriter));
        groups.add(TestFileNamePatternGroup.forPatternUsingSeparator(parent, container, prefWriter));
        
        for( GenericPreferencesGroup group: groups )
        {
            group.createContents();
            group.addModifyListener(new PageValidator());
        }
    }
    
    private void setValid()
    {
        page.setMessage(null);
        page.setValid(true);
    }
    
    public boolean performOk()
    {
        for( GenericPreferencesGroup group: groups )
        {
            group.saveProperties();
        }
        prefWriter.save();
        return true;
    }

    public void performDefaults()
    {
        for( GenericPreferencesGroup group: groups )
        {
            group.restoreDefaults();
        }
    }

    public void validateNamePattern()
    {       
        setValid();
        for( GenericPreferencesGroup group: groups )
        {
            String errorMsg = group.getError();
            
            if(errorMsg != null)
            {
                group.forceFocus();
                page.setMessage(errorMsg, IMessageProvider.ERROR);
                page.setValid(false);
                // one error is enough
                break;
            }
            else
            {
                String warningMsg = group.getWarning();
                if(warningMsg != null)
                {
                    page.setMessage(warningMsg, IMessageProvider.WARNING);
                }
            }
        }
    }

    public void setEnabled(boolean enabled)
    {
        container.setExpandable(enabled);
        for( GenericPreferencesGroup group: groups )
        {
            group.setEnabled(enabled);
        }

        if(!enabled)
        {
            setValid();
        }
    }

    public Composite getBody()
    {
        return container;
    }

    private class PageValidator implements ModifyListener
    {
        public void modifyText(ModifyEvent event)
        {
            // Note: not sufficient. Since you check all errorMsgs by every change.
            validateNamePattern();
        }
    }
}
