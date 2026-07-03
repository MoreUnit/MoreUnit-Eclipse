package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EclipseProjectTest
{
    private Constructor<EclipseProject> constructor;

    @BeforeEach
    public void setUp() throws Exception
    {
        constructor = EclipseProject.class.getDeclaredConstructor(IProject.class);
        constructor.setAccessible(true);
    }

    @Test
    public void exists_should_check_project_and_open() throws Exception
    {
        IProject iproject = mock(IProject.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(iproject.getFullPath()).thenReturn(path);
        when(iproject.exists()).thenReturn(true);
        when(iproject.isOpen()).thenReturn(true);

        assertTrue(constructor.newInstance(iproject).exists());

        when(iproject.isOpen()).thenReturn(false);
        assertFalse(constructor.newInstance(iproject).exists());
    }
}
