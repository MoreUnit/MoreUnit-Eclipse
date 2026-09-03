package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;

public class CodeMiningPreferencesTest extends ContextTestCase
{
    @Project(mainCls = "Foo")
    @Test
    public void codeMiningPreferences_should_return_defaults_then_written_values() throws Exception
    {
        final Preferences preferences = Preferences.getInstance();
        final IJavaProject javaProject = context.getProjectHandler().get();

        // defaults: the workbench store does not contain the keys yet
        assertTrue(preferences.shouldEnableMoreUnitCodeMining(javaProject));
        assertTrue(preferences.shouldEnableJumpToMethodCodeMining(javaProject));
        assertTrue(preferences.shouldEnableJumpToClassCodeMining(javaProject));
        assertNotNull(preferences.getTestType(javaProject));

        // from now on, the project store is read and written
        preferences.setHasProjectSpecificSettings(javaProject, true);

        // defaults: the project store does not contain the keys yet
        assertTrue(preferences.shouldEnableMoreUnitCodeMining(javaProject));
        assertTrue(preferences.shouldEnableJumpToMethodCodeMining(javaProject));
        assertTrue(preferences.shouldEnableJumpToClassCodeMining(javaProject));

        // written values
        preferences.setEnableMoreUnitCodeMining(javaProject, false);
        preferences.setEnableJumpToMethodCodeMining(javaProject, false);
        preferences.setEnableJumpToClassCodeMining(javaProject, false);

        assertFalse(preferences.shouldEnableMoreUnitCodeMining(javaProject));
        assertFalse(preferences.shouldEnableJumpToMethodCodeMining(javaProject));
        assertFalse(preferences.shouldEnableJumpToClassCodeMining(javaProject));

        // test type: written value
        preferences.setTestType(javaProject, PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, preferences.getTestType(javaProject));
    }
}
