package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringConstantsTest
{
    @Test
    public void constants_should_have_expected_values()
    {
        assertEquals(StringConstants.DOT, ".");
        assertEquals(StringConstants.EMPTY_STRING, "");
        assertEquals(StringConstants.NEWLINE, System.getProperty("line.separator"));
        assertEquals(StringConstants.SLASH, "/");
        assertEquals(StringConstants.WILDCARD, "*");
    }
}
