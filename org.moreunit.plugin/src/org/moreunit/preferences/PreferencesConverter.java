package org.moreunit.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.core.util.StringConstants;
import org.moreunit.core.util.Strings;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.util.PluginTools;

/**
 * @author vera 21.03.2008 17:15:43
 */
// TODO Nicolas: cache
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
        StringBuffer result = new StringBuffer();

        for (SourceFolderMapping mapping : mappingList)
        {
            result.append(PreferencesConverter.createStringFromSourceMapping(mapping));
            result.append(PreferencesConverter.DELIMITER_BETWEEN_MAPPING);
        }

        if(mappingList.size() > 0)
        {
            // remove the last delimiter char at the end of the string
            result.deleteCharAt(result.lastIndexOf(PreferencesConverter.DELIMITER_BETWEEN_MAPPING));
        }

        return result.toString();
    }

    public static String createStringFromSourceMapping(SourceFolderMapping mapping)
    {
        StringBuffer result = new StringBuffer();

        IPackageFragmentRoot sourceFolder = mapping.getSourceFolder();
        result.append(sourceFolder.getJavaProject().getElementName());
        result.append(PreferencesConverter.DELIMITER_INTERNAL);
        result.append(PluginTools.getPathStringWithoutProjectName(sourceFolder));

        result.append(PreferencesConverter.DELIMITER_INTERNAL);

        IPackageFragmentRoot testFolder = mapping.getTestFolder();
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

    public static String convertArrayToString(String[] array)
    {
        if(array == null || array.length == 0)
            return StringConstants.EMPTY_STRING;

        StringBuilder result = new StringBuilder();
        for (String token : array)
        {
            result.append(token);
            result.append(DELIMITER_LIST_VALUES);
        }

        // remove last delimiter
        return result.substring(0, result.length() - 1);
    }

}
