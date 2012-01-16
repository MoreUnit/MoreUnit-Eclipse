package org.moreunit.util;

import static org.junit.Assert.assertEquals;

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
    public void testProperties()
    {
        String[] prefixes = org.moreunit.preferences.Preferences.getInstance().getPrefixes(context.getProjectHandler().get());
        assertEquals(1, prefixes.length);
        assertEquals("tseT", prefixes[0]);
    }
}
