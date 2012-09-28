package org.moreunit.preferences;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.moreunit.core.util.ArrayUtils.array;
import static org.moreunit.core.util.Strings.emptyArray;
import static org.moreunit.preferences.PreferenceConstants.DEFAULT_TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.FLEXIBEL_TESTCASE_NAMING;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.PREFIXES;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.SUFFIXES;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ChangeTestClassNamePrefixesAndSuffixesIntoPatternTest
{
    @Mock
    IPreferenceStore store;
    @Mock
    TestClassNameTemplateBuilder templateBuilder;
    @Mock
    Logger logger;
    @InjectMocks
    ChangeTestClassNamePrefixesAndSuffixesIntoPattern migrationStep;

    @Test
    public void should_build_pattern_from_old_prefs() throws Exception
    {
        given(store.getString(PREFIXES)).willReturn("Pre1,Pre2");
        given(store.getString(SUFFIXES)).willReturn("Suf1,Suf2");
        given(store.getBoolean(FLEXIBEL_TESTCASE_NAMING)).willReturn(true);

        given(templateBuilder.buildFromSettings(array("Pre1", "Pre2"), array("Suf1", "Suf2"), true)).willReturn("generated template");

        // when
        migrationStep.apply(store);

        // then
        verify(store).setValue(TEST_CLASS_NAME_TEMPLATE, "generated template");
    }

    @Test
    public void should_ignore_undefined_prefs() throws Exception
    {
        given(store.getString(PREFIXES)).willReturn(null);
        given(store.getString(SUFFIXES)).willReturn("Suf1,Suf2");
        given(store.getBoolean(FLEXIBEL_TESTCASE_NAMING)).willReturn(false);

        given(templateBuilder.buildFromSettings(emptyArray(), array("Suf1", "Suf2"), false)).willReturn("generated template");

        // when
        migrationStep.apply(store);

        // then
        verify(store).setValue(TEST_CLASS_NAME_TEMPLATE, "generated template");
    }

    @Test
    public void should_set_default_template_when_old_prefs_are_undefined() throws Exception
    {
        given(store.getString(PREFIXES)).willReturn("");
        given(store.getString(SUFFIXES)).willReturn("");

        // when
        migrationStep.apply(store);

        // then
        verify(store).setValue(TEST_CLASS_NAME_TEMPLATE, DEFAULT_TEST_CLASS_NAME_TEMPLATE);
    }

    @Test
    public void should_not_override_new_pref_when_already_defined() throws Exception
    {
        given(store.getString(TEST_CLASS_NAME_TEMPLATE)).willReturn(DEFAULT_TEST_CLASS_NAME_TEMPLATE);

        // when
        migrationStep.apply(store);

        // then
        verify(store, never()).setValue(eq(TEST_CLASS_NAME_TEMPLATE), anyString());
    }

    @Test
    public void should_remove_old_prefs_when_new_pref_already_defined() throws Exception
    {
        given(store.getString(TEST_CLASS_NAME_TEMPLATE)).willReturn(DEFAULT_TEST_CLASS_NAME_TEMPLATE);

        // when
        migrationStep.apply(store);

        // then
        verify(store).setToDefault(PREFIXES);
        verify(store).setToDefault(SUFFIXES);
        verify(store).setToDefault(FLEXIBEL_TESTCASE_NAMING);
    }

    @Test
    public void should_remove_old_prefs_when_new_pref_not_already_defined() throws Exception
    {
        given(store.getString(TEST_CLASS_NAME_TEMPLATE)).willReturn("");

        // when
        migrationStep.apply(store);

        // then
        verify(store).setToDefault(PREFIXES);
        verify(store).setToDefault(SUFFIXES);
        verify(store).setToDefault(FLEXIBEL_TESTCASE_NAMING);
    }
}
