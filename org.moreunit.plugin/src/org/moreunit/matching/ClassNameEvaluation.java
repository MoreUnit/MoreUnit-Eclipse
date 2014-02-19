package org.moreunit.matching;

import java.util.ArrayList;
import java.util.List;

import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.util.JavaType;

public class ClassNameEvaluation
{
    private final FileNameEvaluation fileNameEvaluation;
    private final String packagePrefix;
    private final String packageSuffix;
    private final String actualPackageName;

    public ClassNameEvaluation(FileNameEvaluation fileNameEvaluation, String packagePrefix, String packageSuffix, String actualPackageName)
    {
        this.fileNameEvaluation = fileNameEvaluation;
        this.packagePrefix = packagePrefix;
        this.packageSuffix = packageSuffix;
        this.actualPackageName = actualPackageName;
    }

    public List<String> getAllCorrespondingClassPatterns(boolean withPackage)
    {
        List<String> patterns = fileNameEvaluation.getAllCorrespondingFileEclipsePatterns();
        if(withPackage)
        {
            patterns = addPackageToPatterns(patterns);
        }
        return patterns;
    }

    private List<String> addPackageToPatterns(List<String> patterns)
    {
        String packageName = getPackageName();

        List<String> result = new ArrayList<String>();
        for (String p : patterns)
        {
            result.add(packageName + "." + p);
        }
        return result;
    }

    private String getPackageName()
    {
        return isTestCase() ? getCutPackageName() : getTestPackageName();
    }

    private String getCutPackageName()
    {
        String packageName = actualPackageName;

        if(packagePrefix != null)
        {
            packageName = packageName.replaceFirst("^" + packagePrefix + "\\.", "");
        }

        if(packageSuffix != null)
        {
            packageName = packageName.replaceFirst("\\." + packageSuffix + "$", "");
        }

        return packageName;
    }

    private String getTestPackageName()
    {
        String packageName = actualPackageName;

        if(packagePrefix != null)
        {
            packageName = String.format("%s.%s", packagePrefix, packageName);
        }

        if(packageSuffix != null)
        {
            packageName = String.format("%s.%s", packageName, packageSuffix);
        }

        return packageName;
    }

    public JavaType getPreferredCorrespondingClass()
    {
        return new JavaType(fileNameEvaluation.getPreferredCorrespondingFileName(), getPackageName());
    }

    public boolean isTestCase()
    {
        return fileNameEvaluation.isTestFile();
    }
}
