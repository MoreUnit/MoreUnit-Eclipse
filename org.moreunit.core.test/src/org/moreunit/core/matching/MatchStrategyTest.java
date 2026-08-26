package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class MatchStrategyTest {

    @Test
    void testAllMatchesStrategy() {
        SourceFolderPath folderPath = mock(SourceFolderPath.class);
        FileMatchCollector collector = MatchStrategy.ALL_MATCHES.createMatchCollector(folderPath);

        assertThat(collector).isNotNull();
        assertThat(collector.searchIsOver()).isFalse();
    }

    @Test
    void testAnyMatchStrategy() {
        SourceFolderPath folderPath = mock(SourceFolderPath.class);
        FileMatchCollector collector = MatchStrategy.ANY_MATCH.createMatchCollector(folderPath);

        assertThat(collector).isNotNull();
        assertThat(collector.searchIsOver()).isFalse();

        collector.matchFound(null);
        assertThat(collector.searchIsOver()).isTrue();
    }
}
