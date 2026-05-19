package org.moreunit.core.log;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LevelTest
{
    @Test
    public void isLowerThan_should_return_true_when_other_is_higher()
    {
        assertThat(Level.TRACE.isLowerThan(Level.DEBUG)).isTrue();
        assertThat(Level.DEBUG.isLowerThan(Level.INFO)).isTrue();
        assertThat(Level.INFO.isLowerThan(Level.WARNING)).isTrue();
        assertThat(Level.WARNING.isLowerThan(Level.ERROR)).isTrue();
    }

    @Test
    public void isLowerThan_should_return_false_when_other_is_lower()
    {
        assertThat(Level.ERROR.isLowerThan(Level.WARNING)).isFalse();
        assertThat(Level.WARNING.isLowerThan(Level.INFO)).isFalse();
        assertThat(Level.INFO.isLowerThan(Level.DEBUG)).isFalse();
        assertThat(Level.DEBUG.isLowerThan(Level.TRACE)).isFalse();
    }

    @Test
    public void isLowerThan_should_return_false_when_same()
    {
        assertThat(Level.INFO.isLowerThan(Level.INFO)).isFalse();
    }
}
