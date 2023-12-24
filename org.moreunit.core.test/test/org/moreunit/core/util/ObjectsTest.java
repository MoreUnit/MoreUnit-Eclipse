package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ObjectsTest
{
    @Test
    public void two_null_references_should_be_equal() throws Exception
    {
        assertThat(Objects.equal(null, null)).isTrue();
    }

    @Test
    public void null_reference_should_not_be_equal_to_another_object() throws Exception
    {
        assertThat(Objects.equal(null, new Object())).isFalse();
        assertThat(Objects.equal(new Object(), null)).isFalse();
    }

    @Test
    public void equal_objects_should_be_seen_as_such() throws Exception
    {
        assertThat(Objects.equal("abc", "abc")).isTrue();
        assertThat(Objects.equal(95, 95)).isTrue();
    }

    @Test
    public void unequal_objects_should_be_seen_as_such() throws Exception
    {
        assertThat(Objects.equal("abc", "aBc")).isFalse();
        assertThat(Objects.equal(95, 94)).isFalse();
    }
}
