package org.moreunit.core.log;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LevelTest
{
    @Test
    public void isLowerThan_should_return_true_when_other_is_higher()
    {
        assertTrue(Level.TRACE.isLowerThan(Level.DEBUG));
        assertTrue(Level.DEBUG.isLowerThan(Level.INFO));
        assertTrue(Level.INFO.isLowerThan(Level.WARNING));
        assertTrue(Level.WARNING.isLowerThan(Level.ERROR));
    }

    @Test
    public void isLowerThan_should_return_false_when_other_is_lower()
    {
        assertFalse(Level.ERROR.isLowerThan(Level.WARNING));
        assertFalse(Level.WARNING.isLowerThan(Level.INFO));
        assertFalse(Level.INFO.isLowerThan(Level.DEBUG));
        assertFalse(Level.DEBUG.isLowerThan(Level.TRACE));
    }

    @Test
    public void isLowerThan_should_return_false_when_same()
    {
        assertFalse(Level.INFO.isLowerThan(Level.INFO));
    }
}
