package org.moreunit.mock.preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.MoreUnitMockPlugin;

public class PreferenceStoreManager
{
    private static final Preference<Boolean> SPECIFIC_SETTINGS = new BooleanPreference("has_specific_settings", false);

    private final IPreferenceStore workspaceStore;
    private final Logger logger;
    private final Map<IJavaProject, IPreferenceStore> projectStores = new HashMap<IJavaProject, IPreferenceStore>();

    public PreferenceStoreManager(IPreferenceStore workspacePreferenceStore, Logger logger)
    {
        this.workspaceStore = workspacePreferenceStore;
        this.logger = logger;
    }

    public IPreferenceStore getWorkspaceStore()
    {
        return workspaceStore;
    }

    public IPreferenceStore getStore(IJavaProject project, boolean forWriting)
    {
        if(project == null)
        {
            return workspaceStore;
        }

        IPreferenceStore store = projectStores.get(project);

        if(store == null)
        {
            ProjectScope scope = new ProjectScope(project.getProject());
            ScopedPreferenceStore scopedStore = new ScopedPreferenceStore(scope, MoreUnitMockPlugin.PLUGIN_ID);
            scopedStore.setSearchContexts(new IScopeContext[] { scope });
            projectStores.put(project, scopedStore);

            store = scopedStore;
        }

        if(! forWriting && ! hasSpecificSettings(store))
        {
            return workspaceStore;
        }

        return store;
    }

    private boolean hasSpecificSettings(IPreferenceStore store)
    {
        return store.getBoolean(SPECIFIC_SETTINGS.name);
    }

    public void setSpecificSettings(IJavaProject project, boolean projectHasSpecificSettings)
    {
        IPreferenceStore store = getStore(project, true);
        store.setValue(SPECIFIC_SETTINGS.name, projectHasSpecificSettings);
        save(project, store);
    }

    void save(IJavaProject project, IPreferenceStore store)
    {
        if(store instanceof ScopedPreferenceStore)
        {
            try
            {
                ((ScopedPreferenceStore) store).save();
            }
            catch (IOException e)
            {
                logger.error("Could not store preferences for project " + project.getElementName(), e);
            }
        }
    }

    public boolean hasSpecificSettings(IJavaProject project)
    {
        return getStore(project, true).getBoolean(SPECIFIC_SETTINGS.name);
    }
}
