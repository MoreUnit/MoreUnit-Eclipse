package org.moreunit.core.resources;

import org.moreunit.core.preferences.ProjectPreferences;

public interface ProjectResource extends Resource
{
    Project getProject();

    ProjectPreferences getProjectPreferences();
}
