package org.moreunit.core.resources;

import java.util.List;

import org.moreunit.core.preferences.Preferences;

public interface Workspace extends ResourceContainer
{
    Preferences getPreferences();

    Project getProject(String projectName);

    List<Project> listProjects();

    Path path(String path);
}
