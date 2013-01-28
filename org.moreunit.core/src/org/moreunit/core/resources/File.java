package org.moreunit.core.resources;

import org.moreunit.core.preferences.ProjectPreferences;

public interface File extends ProjectResource
{
    String getBaseNameWithoutExtension();

    String getExtension();

    Project getProject();

    ProjectPreferences getProjectPreferences();

    boolean hasExtension();
}
