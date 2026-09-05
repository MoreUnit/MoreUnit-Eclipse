package org.moreunit.core.log;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class LevelTest {

    @Test
    void testIsLowerThan() {
        assertThat(Level.TRACE.isLowerThan(Level.DEBUG)).isTrue();
        assertThat(Level.DEBUG.isLowerThan(Level.INFO)).isTrue();
        assertThat(Level.INFO.isLowerThan(Level.WARNING)).isTrue();
        assertThat(Level.WARNING.isLowerThan(Level.ERROR)).isTrue();

        assertThat(Level.TRACE.isLowerThan(Level.ERROR)).isTrue();

        assertThat(Level.ERROR.isLowerThan(Level.WARNING)).isFalse();
        assertThat(Level.WARNING.isLowerThan(Level.INFO)).isFalse();
        assertThat(Level.INFO.isLowerThan(Level.DEBUG)).isFalse();
        assertThat(Level.DEBUG.isLowerThan(Level.TRACE)).isFalse();

        // Not lower than itself
        for (Level level : Level.values()) {
            assertThat(level.isLowerThan(level)).isFalse();
        }
    }
}
