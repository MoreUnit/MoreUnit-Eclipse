package org.moreunit.preferences;

import static org.moreunit.preferences.PreferencesConverter.convertStringToArray;

import org.eclipse.jface.preference.IPreferenceStore;

public class ChangeTestClassNamePrefixesAndSuffixesIntoPattern implements MigrationStep
{
    private final TestClassNameTemplateBuilder templateBuilder;

    public ChangeTestClassNamePrefixesAndSuffixesIntoPattern()
    {
        this(new TestClassNameTemplateBuilder());
    }

    public ChangeTestClassNamePrefixesAndSuffixesIntoPattern(TestClassNameTemplateBuilder templateBuilder)
    {
        this.templateBuilder = templateBuilder;
    }

    public int targetVersion()
    {
        return 2;
    }

    public void apply(IPreferenceStore store)
    {
        String[] prefixes = convertStringToArray(store.getString(PreferenceConstants.PREFIXES));
        String[] suffixes = convertStringToArray(store.getString(PreferenceConstants.SUFFIXES));

        // nothing to convert
        if(prefixes.length == 0 && suffixes.length == 0)
        {
            return;
        }

        boolean flexibleNaming = store.getBoolean(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING);

        String template = templateBuilder.buildFromSettings(prefixes, suffixes, flexibleNaming);

        store.setValue(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE, template);
    }
}
