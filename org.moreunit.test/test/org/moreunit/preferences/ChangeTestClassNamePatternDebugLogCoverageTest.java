package org.moreunit.preferences;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.moreunit.core.util.ArrayUtils.array;
import static org.moreunit.preferences.PreferenceConstants.TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.FLEXIBEL_TESTCASE_NAMING;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.PREFIXES;
import static org.moreunit.preferences.PreferenceConstants.Deprecated.SUFFIXES;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;

public class ChangeTestClassNamePatternDebugLogCoverageTest
{
    @BeforeEach
    public void initMocks()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    IPreferenceStore store;
    @Mock
    TestClassNameTemplateBuilder templateBuilder;
    @Mock
    Logger logger;

    @Test
    public void should_log_debug_message_when_debug_is_enabled()
    {
        // given old-style preferences to convert and debug logging enabled
        given(store.getString(TEST_CLASS_NAME_TEMPLATE)).willReturn("");
        given(store.getString(PREFIXES)).willReturn("Pre");
        given(store.getString(SUFFIXES)).willReturn("Suf");
        given(store.getBoolean(FLEXIBEL_TESTCASE_NAMING)).willReturn(false);
        given(templateBuilder.buildFromSettings(array("Pre"), array("Suf"), false)).willReturn("generated template");
        given(logger.debugEnabled()).willReturn(true);

        final ChangeTestClassNamePrefixesAndSuffixesIntoPattern migrationStep = new ChangeTestClassNamePrefixesAndSuffixesIntoPattern(templateBuilder, logger);

        // when
        migrationStep.apply(store);

        // then the conversion happened and was logged
        verify(store).setValue(TEST_CLASS_NAME_TEMPLATE, "generated template");
        verify(logger).debug(anyString());
    }
}
