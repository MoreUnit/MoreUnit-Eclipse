package org.moreunit.mock.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.mock.MoreUnitMockPlugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PreferenceStoreManager
{
    private static final Preference<Boolean> SPECIFIC_SETTINGS = new BooleanPreference("has_specific_settings", false);

    private final IPreferenceStore workspaceStore;
    private final Map<IJavaProject, IPreferenceStore> projectStores = new HashMap<IJavaProject, IPreferenceStore>();

    @Inject
    public PreferenceStoreManager(IPreferenceStore workspacePreferenceStore)
    {
        this.workspaceStore = workspacePreferenceStore;
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
        if(! forWriting && (store == null || ! hasSpecificSettings(project)))
        {
            return workspaceStore;
        }

        if(store != null)
        {
            return store;
        }

        ProjectScope scope = new ProjectScope(project.getProject());
        ScopedPreferenceStore scopedStore = new ScopedPreferenceStore(scope, MoreUnitMockPlugin.PLUGIN_ID);
        scopedStore.setSearchContexts(new IScopeContext[] { scope });
        projectStores.put(project, scopedStore);
        return scopedStore;
    }

    public void setSpecificSettings(IJavaProject project, boolean projectHasSpecificSettings)
    {
        getStore(project, true).setValue(SPECIFIC_SETTINGS.name, projectHasSpecificSettings);
    }

    public boolean hasSpecificSettings(IJavaProject project)
    {
        IPreferenceStore projectStore = projectStores.get(project);
        return projectStore != null && projectStore.getBoolean(SPECIFIC_SETTINGS.name);
    }
}
