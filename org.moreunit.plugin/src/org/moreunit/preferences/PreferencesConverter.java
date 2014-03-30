package org.moreunit.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.core.util.Strings;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.util.PluginTools;

/**
 * @author vera 21.03.2008 17:15:43
 */
public class PreferencesConverter
{

    public static final String DELIMITER_BETWEEN_MAPPING = "#";
    public static final String DELIMITER_INTERNAL = ":";

    public static final String DELIMITER_LIST_VALUES = ",";

    public static final int INDEX_SOURCE_PROJECT = 0;
    public static final int INDEX_SOURCE_FOLDER = 1;
    public static final int INDEX_TEST_PROJECT = 2;
    public static final int INDEX_TEST_FOLDER = 3;

    /**
     * Creates a String of the following format project1:src1;project1:test1
     */
    public static String convertSourceMappingsToString(List<SourceFolderMapping> mappingList)
    {
        List<String> mappingStrings = new ArrayList<String>();
        for (SourceFolderMapping mapping : mappingList)
        {
            mappingStrings.add(PreferencesConverter.createStringFromSourceMapping(mapping));
        }

        return Strings.join(PreferencesConverter.DELIMITER_BETWEEN_MAPPING, mappingStrings);
    }

    public static String createStringFromSourceMapping(SourceFolderMapping mapping)
    {
        List<String> resultList = new ArrayList<String>();

        new StringBuffer();

        for (IPackageFragmentRoot sourceFolder : mapping.getSourceFolderList())
        {
            StringBuilder stringPart = new StringBuilder();
            stringPart.append(getSourceFolderTokenPart(sourceFolder));
            stringPart.append(PreferencesConverter.DELIMITER_INTERNAL);
            stringPart.append(getTestFolderTokenPart(mapping.getTestFolder()));
            resultList.add(stringPart.toString());
        }

        return Strings.join(DELIMITER_BETWEEN_MAPPING, resultList);
    }

    private static final String getSourceFolderTokenPart(IPackageFragmentRoot sourceFolder)
    {
        StringBuilder result = new StringBuilder();
        result.append(sourceFolder.getJavaProject().getElementName());
        result.append(PreferencesConverter.DELIMITER_INTERNAL);
        result.append(PluginTools.getPathStringWithoutProjectName(sourceFolder));
        return result.toString();
    }

    private static final String getTestFolderTokenPart(IPackageFragmentRoot testFolder)
    {
        StringBuilder result = new StringBuilder();
        result.append(testFolder.getJavaProject().getElementName());
        result.append(PreferencesConverter.DELIMITER_INTERNAL);
        result.append(PluginTools.getPathStringWithoutProjectName(testFolder));
        return result.toString();
    }

    public static List<SourceFolderMapping> convertStringToSourceMappingList(String sourceMappingString)
    {
        List<SourceFolderMapping> resultList = new ArrayList<SourceFolderMapping>();

        if(Strings.isBlank(sourceMappingString))
            return resultList;

        String[] mappingSplits = sourceMappingString.split(PreferencesConverter.DELIMITER_BETWEEN_MAPPING);

        for (String mappingToken : mappingSplits)
        {
            String[] folderSplit = mappingToken.split(PreferencesConverter.DELIMITER_INTERNAL);

            IPackageFragmentRoot sourceFolder = PluginTools.createPackageFragmentRoot(folderSplit[PreferencesConverter.INDEX_SOURCE_PROJECT], folderSplit[PreferencesConverter.INDEX_SOURCE_FOLDER]);
            IPackageFragmentRoot testFolder = PluginTools.createPackageFragmentRoot(folderSplit[PreferencesConverter.INDEX_TEST_PROJECT], folderSplit[PreferencesConverter.INDEX_TEST_FOLDER]);

            if(sourceFolder != null && testFolder != null)
            {
                SourceFolderMapping mapping = new SourceFolderMapping(sourceFolder.getJavaProject(), sourceFolder, testFolder);
                resultList.add(mapping);
            }
        }

        return resultList;
    }

    public static String[] convertStringToArray(String listString)
    {
        if(Strings.isBlank(listString))
            return new String[] {};

        return listString.split(DELIMITER_LIST_VALUES);
    }

    public static String convertArrayToStringWithListValueDelimiter(String[] array)
    {
        return Strings.join(DELIMITER_LIST_VALUES, array);
    }

}
