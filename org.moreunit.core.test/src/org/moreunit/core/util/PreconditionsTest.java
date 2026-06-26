package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class PreconditionsTest {

    @Test
    void testCheckNotNullSuccess() {
        Object obj = new Object();
        assertThat(Preconditions.checkNotNull(obj)).isSameAs(obj);
    }

    @Test
    void testCheckNotNullFailure() {
        assertThatThrownBy(() -> Preconditions.checkNotNull(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testCheckNotNullWithMessageSuccess() {
        Object obj = new Object();
        assertThat(Preconditions.checkNotNull(obj, "message")).isSameAs(obj);
    }

    @Test
    void testCheckNotNullWithMessageFailure() {
        assertThatThrownBy(() -> Preconditions.checkNotNull(null, "Expected error message"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expected error message");
    }

    @Test
    void testCheckArgumentSuccess() {
        Preconditions.checkArgument(true);
        // should not throw exception
    }

    @Test
    void testCheckArgumentFailure() {
        assertThatThrownBy(() -> Preconditions.checkArgument(false))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCheckArgumentWithMessageSuccess() {
        Preconditions.checkArgument(true, "message");
        // should not throw exception
    }

    @Test
    void testCheckArgumentWithMessageFailure() {
        assertThatThrownBy(() -> Preconditions.checkArgument(false, "Expected error message"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Expected error message");
    }

    @Test
    void testCheckStateSuccess() {
        Preconditions.checkState(true, "message");
        // should not throw exception
    }

    @Test
    void testCheckStateFailure() {
        assertThatThrownBy(() -> Preconditions.checkState(false, "Expected error message"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Expected error message");
    }

    @Test
    void testCheckNotNullOrEmptySuccess() {
        List<String> list = Arrays.asList("item");
        assertThat(Preconditions.checkNotNullOrEmpty(list)).isSameAs(list);
    }

    @Test
    void testCheckNotNullOrEmptyFailureNull() {
        assertThatThrownBy(() -> Preconditions.checkNotNullOrEmpty((List<String>) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testCheckNotNullOrEmptyFailureEmpty() {
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> Preconditions.checkNotNullOrEmpty(emptyList))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCheckNotNullOrEmptyWithMessageSuccess() {
        List<String> list = Arrays.asList("item");
        assertThat(Preconditions.checkNotNullOrEmpty(list, "message")).isSameAs(list);
    }

    @Test
    void testCheckNotNullOrEmptyWithMessageFailureNull() {
        assertThatThrownBy(() -> Preconditions.checkNotNullOrEmpty((List<String>) null, "Expected error message"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expected error message");
    }

    @Test
    void testCheckNotNullOrEmptyWithMessageFailureEmpty() {
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> Preconditions.checkNotNullOrEmpty(emptyList, "Expected error message"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Expected error message");
    }
}
