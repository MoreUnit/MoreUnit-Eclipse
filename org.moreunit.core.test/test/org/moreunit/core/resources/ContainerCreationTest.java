package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public class ContainerCreationTest
{
    @Test
    public void should_not_create_anything_when_container_already_exists()
    {
        // given
        final ResourceContainer container = mock(ResourceContainer.class);
        when(container.exists()).thenReturn(true);

        // when
        final ContainerCreationRecord record = new ContainerCreation(container).execute();

        // then
        verify(container, never()).create();
        // record is empty: cancelling it has no effect
        record.cancelCreation();
        verify(container, never()).delete();
    }

    @Test
    public void should_create_container_when_it_does_not_exist_but_parent_does()
    {
        // given
        final ResourceContainer parent = mock(ResourceContainer.class);
        when(parent.exists()).thenReturn(true);

        final ResourceContainer container = mock(ResourceContainer.class);
        when(container.exists()).thenReturn(false);
        when(container.getParent()).thenReturn(parent);

        // when
        final ContainerCreationRecord record = new ContainerCreation(container).execute();

        // then
        verify(container).create();
        verify(parent, never()).create();
        // record only contains the container: cancelling deletes it
        record.cancelCreation();
        verify(container).delete();
    }

    @Test
    public void should_create_parents_recursively_before_container()
    {
        // given
        final ResourceContainer grandParent = mock(ResourceContainer.class);
        when(grandParent.exists()).thenReturn(true);

        final ResourceContainer parent = mock(ResourceContainer.class);
        when(parent.exists()).thenReturn(false);
        when(parent.getParent()).thenReturn(grandParent);

        final ResourceContainer container = mock(ResourceContainer.class);
        when(container.exists()).thenReturn(false);
        when(container.getParent()).thenReturn(parent);

        // when
        final ContainerCreationRecord record = new ContainerCreation(container).execute();

        // then
        final InOrder order = inOrder(parent, container);
        order.verify(parent).create();
        order.verify(container).create();
        // both parent and container are recorded; last added is the parent
        record.cancelCreation();
        verify(parent).delete();
    }

    @Test
    public void should_return_record_containing_created_containers()
    {
        // given
        final ResourceContainer parent = mock(ResourceContainer.class);
        when(parent.exists()).thenReturn(true);

        final ResourceContainer container = mock(ResourceContainer.class);
        when(container.exists()).thenReturn(false);
        when(container.getParent()).thenReturn(parent);

        // when
        final ContainerCreationRecord record = new ContainerCreation(container).execute();

        // then: cancelling folders that are not ancestors of a resource in the
        // parent should keep the parent and delete the container
        record.cancelCreationOfFoldersThatAreNotAncestorsOf(parent);
        verify(container).delete();
        verify(parent, never()).delete();
    }

    @Test
    public void should_use_provided_container_record()
    {
        // given
        final ResourceContainer container = mock(ResourceContainer.class);
        when(container.exists()).thenReturn(true);

        // when
        final ContainerCreationRecord record = new ContainerCreation(container).execute();

        // then the same record instance is returned
        assertSame(record, record);
    }
}
