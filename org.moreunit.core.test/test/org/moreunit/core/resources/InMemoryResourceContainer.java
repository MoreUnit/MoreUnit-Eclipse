package org.moreunit.core.resources;

import static org.moreunit.core.util.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class InMemoryResourceContainer extends InMemoryResource implements ResourceContainer
{
    private final Map<String, InMemoryFile> files = new TreeMap<String, InMemoryFile>();
    private final Map<String, InMemoryFolder> folders = new TreeMap<String, InMemoryFolder>();

    protected InMemoryResourceContainer(InMemoryPath path, InMemoryResourceContainer parent)
    {
        super(path, parent);
    }

    final void add(InMemoryFile file)
    {
        files.put(file.getName(), file);
    }

    final void add(InMemoryFolder folder)
    {
        folders.put(folder.getName(), folder);
    }

    @Override
    public ContainerCreationRecord createWithRecord()
    {
        return new ContainerCreation(this).execute();
    }

    @Override
    public void delete()
    {
        for (Resource child : children())
        {
            child.delete();
        }
        super.delete();
    }

    private Iterable<Resource> children()
    {
        Collection<Resource> children = new HashSet<Resource>();
        children.addAll(files.values());
        children.addAll(folders.values());
        return children;
    }

    @Override
    public InMemoryFile getFile(String fileRelativePath)
    {
        return getFile(new InMemoryPath(fileRelativePath));
    }

    @Override
    public InMemoryFile getFile(Path fileRelativePath)
    {
        checkArgument(! fileRelativePath.isEmpty(), "path must not be empty");
        checkArgument(fileRelativePath.isRelative(), "path must be relative to this resource");

        if(isRelativeToThisContainer(fileRelativePath))
        {
            return getRelativeFile(fileRelativePath);
        }

        InMemoryResourceContainer container = findResourceContainer(fileRelativePath.withoutLastSegment());
        return container.getFile(fileRelativePath.getBaseName());
    }

    private InMemoryResourceContainer findResourceContainer(Path relativePath)
    {
        InMemoryResourceContainer container = this;
        for (String segment : relativePath)
        {
            container = container.getFolder(segment);
        }
        return container;
    }

    private boolean isRelativeToThisContainer(Path path)
    {
        return path.getSegmentCount() == 1;
    }

    private InMemoryFile getRelativeFile(Path fileRelativePath)
    {
        String fileName = fileRelativePath.getBaseName();
        InMemoryFile file = files.get(fileName);
        if(file == null)
        {
            checkThatNoFolderExistsWithName(fileName);

            file = new InMemoryFile(getPath().withRelativePath(fileRelativePath), this);
            files.put(fileName, file);
        }
        return file;
    }

    private void checkThatNoFolderExistsWithName(String name)
    {
        Folder maybeExistingFolder = folders.get(name);
        if(maybeExistingFolder != null && maybeExistingFolder.exists())
        {
            throw new ResourceException("a folder already exists at this path");
        }
    }

    @Override
    public InMemoryFolder getFolder(String folderRelativePath)
    {
        return getFolder(new InMemoryPath(folderRelativePath));
    }

    @Override
    public InMemoryFolder getFolder(Path folderRelativePath)
    {
        checkArgument(! folderRelativePath.isEmpty(), "path must not be empty");
        checkArgument(folderRelativePath.isRelative(), "path must be relative to this resource");

        if(isRelativeToThisContainer(folderRelativePath))
        {
            return getRelativeFolder(folderRelativePath);
        }

        InMemoryResourceContainer container = findResourceContainer(folderRelativePath.withoutLastSegment());
        return container.getFolder(folderRelativePath.getBaseName());
    }

    private InMemoryFolder getRelativeFolder(Path folderRelativePath)
    {
        String folderName = folderRelativePath.getBaseName();
        InMemoryFolder folder = folders.get(folderName);
        if(folder == null)
        {
            checkThatNoFileExistsWithName(folderName);

            folder = new InMemoryFolder(getPath().withRelativePath(folderRelativePath), this);
            folders.put(folderName, folder);
        }
        return folder;
    }

    private void checkThatNoFileExistsWithName(String name)
    {
        File maybeExistingFile = files.get(name);
        if(maybeExistingFile != null && maybeExistingFile.exists())
        {
            throw new ResourceException("a file already exists at this path");
        }
    }

    @Override
    public boolean isParentOf(Resource resource)
    {
        return getPath().isPrefixOf(resource.getPath());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<File> listFiles()
    {
        return (List<File>) keepIfExists(files.values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Folder> listFolders()
    {
        return (List<Folder>) keepIfExists(folders.values());
    }

    protected final <T extends Resource> List< ? extends T> keepIfExists(Collection<T> resources)
    {
        List<T> result = new ArrayList<T>(resources.size());
        for (T resource : resources)
        {
            if(resource.exists())
                result.add(resource);
        }
        return result;
    }
}
