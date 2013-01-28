package org.moreunit.core.resources;

import java.util.List;

public interface ResourceContainer extends Resource
{
    ContainerCreationRecord createWithRecord();

    File getFile(String fileRelativePath);

    File getFile(Path fileRelativePath);

    Folder getFolder(String folderRelativePath);

    Folder getFolder(Path folderRelativePath);

    boolean isParentOf(Resource resource);

    List<File> listFiles();

    List<Folder> listFolders();
}
