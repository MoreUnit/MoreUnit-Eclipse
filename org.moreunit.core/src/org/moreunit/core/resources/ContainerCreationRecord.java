package org.moreunit.core.resources;

import java.util.Deque;
import java.util.LinkedList;

public class ContainerCreationRecord
{
    private final Deque<ResourceContainer> createdContainers = new LinkedList<ResourceContainer>();

    public void addCreatedContainer(ResourceContainer container)
    {
        createdContainers.add(container);
    }

    public void cancelCreation()
    {
        if(! createdContainers.isEmpty())
            createdContainers.getLast().delete();
    }

    public void cancelCreationOfFoldersThatAreNotAncestorsOf(Resource resource)
    {
        ResourceContainer ancestor = findGreatestNonAncestorOf(resource);

        if(ancestor != null)
            ancestor.delete();
    }

    private ResourceContainer findGreatestNonAncestorOf(Resource resource)
    {
        ResourceContainer ancestor = null;
        for (ResourceContainer container : createdContainers)
        {
            if(container.isParentOf(resource))
            {
                break;
            }
            ancestor = container;
        }

        return ancestor;
    }
}
