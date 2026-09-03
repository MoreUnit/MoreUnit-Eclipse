package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;
import java.util.List;

public class EclipseWorkspaceTest
{
    @Test
    public void getProject_should_throw_on_invalid_name()
    {
        final Workspace workspace = EclipseWorkspace.get();
        assertThrows(IllegalArgumentException.class, () -> workspace.getProject("invalid/name"));
    }
}
