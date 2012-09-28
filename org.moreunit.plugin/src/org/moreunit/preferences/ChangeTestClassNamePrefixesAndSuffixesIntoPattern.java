package org.moreunit.preferences;

import static java.util.Arrays.asList;
import static org.moreunit.preferences.PreferencesConverter.convertStringToArray;

import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.util.Strings;

public class ChangeTestClassNamePrefixesAndSuffixesIntoPattern implements MigrationStep
{
    private final TestClassNameTemplateBuilder templateBuilder;
    private final Logger logger;

    public ChangeTestClassNamePrefixesAndSuffixesIntoPattern()
    {
        this(new TestClassNameTemplateBuilder(), MoreUnitPlugin.getDefault().getLogger());
    }

    public ChangeTestClassNamePrefixesAndSuffixesIntoPattern(TestClassNameTemplateBuilder templateBuilder, Logger logger)
    {
        this.templateBuilder = templateBuilder;
        this.logger = logger;
    }

    public int targetVersion()
    {
        return 2;
    }

    public void apply(IPreferenceStore store)
    {
        String currentTemplate = store.getString(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE);
        if(Strings.isBlank(currentTemplate) || ! TestFileNamePattern.isValid(currentTemplate, ""))
        {
            convertPrefs(store);
        }
        removeOldPrefs(store);
    }

    private void convertPrefs(IPreferenceStore store)
    {
        String[] prefixes = convertStringToArray(store.getString(PreferenceConstants.Deprecated.PREFIXES));
        String[] suffixes = convertStringToArray(store.getString(PreferenceConstants.Deprecated.SUFFIXES));
        boolean flexibleNaming = store.getBoolean(PreferenceConstants.Deprecated.FLEXIBEL_TESTCASE_NAMING);

        final String template;

        // nothing to convert
        if(prefixes.length == 0 && suffixes.length == 0)
        {
            template = PreferenceConstants.DEFAULT_TEST_CLASS_NAME_TEMPLATE;
        }
        else
        {
            template = templateBuilder.buildFromSettings(prefixes, suffixes, flexibleNaming);
        }

        // define new pref
        store.setValue(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE, template);

        if(logger.debugEnabled())
        {
            logger.debug(String.format("Test class naming: (%s, %s, %s) converted to (%s) for store %s", asList(prefixes), asList(suffixes), flexibleNaming, template, store));
        }
    }

    private void removeOldPrefs(IPreferenceStore store)
    {
        store.setToDefault(PreferenceConstants.Deprecated.PREFIXES);
        store.setToDefault(PreferenceConstants.Deprecated.SUFFIXES);
        store.setToDefault(PreferenceConstants.Deprecated.FLEXIBEL_TESTCASE_NAMING);
    }
}
