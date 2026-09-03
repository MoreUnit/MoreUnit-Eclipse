package org.moreunit.core.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.moreunit.core.MoreUnitCore;

public class ExtensionPointsTest
{
    @Test
    public void LANGUAGES_should_be_correct_extension_point_id()
    {
        final String expected = MoreUnitCore.PLUGIN_ID + ".languages";

        assertEquals(expected, ExtensionPoints.LANGUAGES);
    }
}