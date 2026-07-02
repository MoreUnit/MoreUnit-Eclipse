package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.jupiter.api.Test;

public class SourceFolderMappingTest
{
    @Test
    public void three_arg_constructor_should_init_project_source_and_test_folders()
    {
        IJavaProject project = mock(IJavaProject.class);
        IPackageFragmentRoot sourceFolder = mock(IPackageFragmentRoot.class);
        IPackageFragmentRoot testFolder = mock(IPackageFragmentRoot.class);

        SourceFolderMapping mapping = new SourceFolderMapping(project, sourceFolder, testFolder);

        assertSame(project, mapping.getJavaProject());
        assertSame(testFolder, mapping.getTestFolder());
        assertEquals(1, mapping.getSourceFolderList().size());
        assertSame(sourceFolder, mapping.getSourceFolderList().get(0));
    }

    @Test
    public void set_source_folder_list_should_replace_the_list()
    {
        IJavaProject project = mock(IJavaProject.class);
        IPackageFragmentRoot sourceFolder = mock(IPackageFragmentRoot.class);
        IPackageFragmentRoot testFolder = mock(IPackageFragmentRoot.class);
        IPackageFragmentRoot otherSource = mock(IPackageFragmentRoot.class);

        SourceFolderMapping mapping = new SourceFolderMapping(project, sourceFolder, testFolder);
        mapping.setSourceFolderList(List.of(otherSource));

        assertEquals(1, mapping.getSourceFolderList().size());
        assertSame(otherSource, mapping.getSourceFolderList().get(0));
    }

    @Test
    public void to_string_should_describe_source_to_test_mapping()
    {
        IJavaProject project = mock(IJavaProject.class);
        IPackageFragmentRoot sourceFolder = mock(IPackageFragmentRoot.class);
        IPackageFragmentRoot testFolder = mock(IPackageFragmentRoot.class);

        when(project.getElementName()).thenReturn("Proj");
        when(sourceFolder.getJavaProject()).thenReturn(project);
        when(sourceFolder.getElementName()).thenReturn("src");
        when(testFolder.getJavaProject()).thenReturn(project);
        when(testFolder.getElementName()).thenReturn("test");

        SourceFolderMapping mapping = new SourceFolderMapping(project, sourceFolder, testFolder);

        String str = mapping.toString();
        assertTrue(str.contains("SourceFolderMapping"));
        assertTrue(str.contains("Proj:src => Proj:test"));
    }
}
