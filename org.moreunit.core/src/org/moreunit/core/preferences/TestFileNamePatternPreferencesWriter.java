package org.moreunit.core.preferences;

public interface TestFileNamePatternPreferencesWriter
{
    String getFileWordSeparator();

    String getTestFileNameTemplate();

    void setTestFileNameTemplate(String template, String separator);
}
