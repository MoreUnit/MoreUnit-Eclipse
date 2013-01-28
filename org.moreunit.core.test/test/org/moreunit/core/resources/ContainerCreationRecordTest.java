package org.moreunit.core.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ContainerCreationRecordTest
{
    private ContainerCreationRecord containerCreationRecord = new ContainerCreationRecord();

    private InMemoryWorkspace workspace = new InMemoryWorkspace();

    private File childFile;
    private Folder parent;
    private Folder grandParent;
    private Folder grandGrandParent;

    @Before
    public void given_that_several_parent_folders_have_been_created() throws Exception
    {
        childFile = workspace.getFile("/segment1/segment2/segment3/segment4/file");

        parent = workspace.getFolder("/segment1/segment2/segment3/segment4");
        grandParent = workspace.getFolder("/segment1/segment2/segment3");
        grandGrandParent = workspace.getFolder("/segment1/segment2");

        childFile.create();

        containerCreationRecord.addCreatedContainer(parent);
        containerCreationRecord.addCreatedContainer(grandParent);
        containerCreationRecord.addCreatedContainer(grandGrandParent);
    }

    @Test
    public void should_delete_greatest_parent_folder_that_has_been_added_when_cancelling_whole_folder_creation() throws Exception
    {
        // when
        containerCreationRecord.cancelCreation();

        // then
        assertFalse(grandGrandParent.exists());
        assertTrue(grandGrandParent.getParent().exists());
    }

    @Test
    public void should_only_delete_folders_that_are_not_parent_of_given_resource() throws Exception
    {
        // when
        containerCreationRecord.cancelCreationOfFoldersThatAreNotAncestorsOf(workspace.getFile("/segment1/segment2/someFile"));

        // then
        assertFalse(grandParent.exists());
        assertTrue(grandGrandParent.exists());
    }
}
