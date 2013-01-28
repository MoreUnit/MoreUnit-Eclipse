package org.moreunit.core.resources;

public class ContainerCreation
{
    private final ResourceContainer containerToCreate;

    public ContainerCreation(ResourceContainer containerToCreate)
    {
        this.containerToCreate = containerToCreate;
    }

    public ContainerCreationRecord execute()
    {
        final ContainerCreationRecord record = new ContainerCreationRecord();
        createContainerAndRecord(containerToCreate, record);
        return record;
    }

    private void createContainerAndRecord(ResourceContainer container, ContainerCreationRecord record)
    {
        if(container.exists())
            return;

        record.addCreatedContainer(container);

        ResourceContainer parentContainer = container.getParent();
        if(! parentContainer.exists())
            createContainerAndRecord(parentContainer, record);

        container.create();
    }
}
