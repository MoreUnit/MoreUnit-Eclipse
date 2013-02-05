package org.moreunit.core.resources;

import org.eclipse.core.resources.IFile;
import org.moreunit.core.preferences.ProjectPreferences;

public interface File extends ProjectResource
{
    String getBaseNameWithoutExtension();

    String getExtension();

    Project getProject();

    ProjectPreferences getProjectPreferences();

    IFile getUnderlyingPlatformFile();

    boolean hasExtension();
}
