package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;

@Preferences(testClassPrefixes="Test")
public class PreferencesPropertiesTest extends ContextTestCase
{
    @Project(mainCls="SomeClass", properties = @Properties(testClassPrefixes="tseT"))
    @Test
    public void testWithProperties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertTrue(prefs.hasProjectSpecificSettings(context.getProjectHandler().get()));
        String[] prefixes = prefs.getPrefixes(context.getProjectHandler().get());
        assertEquals(1, prefixes.length);
        assertEquals("tseT", prefixes[0]);
    }
    
    @Project(mainCls="SomeClass")
    @Test
    public void testWithoutProperties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertFalse(prefs.hasProjectSpecificSettings(context.getProjectHandler().get()));
    }
}
