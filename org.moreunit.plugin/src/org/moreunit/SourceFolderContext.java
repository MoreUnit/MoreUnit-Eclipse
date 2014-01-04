package org.moreunit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.PluginTools;

/**
 * @author vera 15.03.2008 16:26:16
 */
public class SourceFolderContext
{

    private static SourceFolderContext instance;
    private Map<IPackageFragmentRoot, List<IPackageFragmentRoot>> folderToLookupMap;

    public static SourceFolderContext getInstance()
    {
        if(instance == null)
            instance = new SourceFolderContext();

        return instance;
    }

    private SourceFolderContext()
    {
        initContextForWorkspace();
    }

    public void initContextForWorkspace()
    {
        folderToLookupMap = new HashMap<IPackageFragmentRoot, List<IPackageFragmentRoot>>();

        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
        for (IJavaProject project : javaProjectsFromWorkspace)
        {
            List<SourceFolderMapping> testSourceFolder = Preferences.getInstance().getSourceMappingList(project);
            for (SourceFolderMapping mapping : testSourceFolder)
            {
                updateMap(mapping);
            }
        }
    }

    private void updateMap(SourceFolderMapping mapping)
    {
        for (IPackageFragmentRoot sourceFolder : mapping.getSourceFolderList())
        {
            List<IPackageFragmentRoot> list = new ArrayList<IPackageFragmentRoot>();
            list.add(mapping.getTestFolder());
            updateMap(sourceFolder, list);
        }

        updateMap(mapping.getTestFolder(), mapping.getSourceFolderList());
    }

    private void updateMap(IPackageFragmentRoot key, List<IPackageFragmentRoot> value)
    {
        if(folderToLookupMap.containsKey(key))
        {
            List<IPackageFragmentRoot> list = folderToLookupMap.get(key);
            list.addAll(value);
        }
        else
        {
            List<IPackageFragmentRoot> list = new ArrayList<IPackageFragmentRoot>();
            list.addAll(value);
            folderToLookupMap.put(key, list);
        }
    }

    public List<IPackageFragmentRoot> getSourceFolderToSearch(IPackageFragmentRoot baseFolder)
    {
        if(folderToLookupMap.containsKey(baseFolder))
            return folderToLookupMap.get(baseFolder);

        // if there are no settings for the project then take all source folder
        // as context
        // you can not take the source folder for test because this method is
        // used
        // to jump from the test to the CUT as well
        List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();
        try
        {
            for (IPackageFragmentRoot fragmentRoot : baseFolder.getJavaProject().getPackageFragmentRoots())
            {
                if(! fragmentRoot.isArchive())
                    resultList.add(fragmentRoot);
            }
        }
        catch (CoreException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return resultList;
    }
}
