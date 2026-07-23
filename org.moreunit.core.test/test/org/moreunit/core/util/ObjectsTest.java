package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ObjectsTest
{
    @Test
    public void two_null_references_should_be_equal() throws Exception
    {
        assertTrue(Objects.equal(null, null));
    }

    @Test
    public void null_reference_should_not_be_equal_to_another_object() throws Exception
    {
        assertFalse(Objects.equal(null, new Object()));
        assertFalse(Objects.equal(new Object(), null));
    }

    @Test
    public void equal_objects_should_be_seen_as_such() throws Exception
    {
        assertTrue(Objects.equal("abc", "abc"));
        assertTrue(Objects.equal(95, 95));
    }

    @Test
    public void unequal_objects_should_be_seen_as_such() throws Exception
    {
        assertFalse(Objects.equal("abc", "aBc"));
        assertFalse(Objects.equal(95, 94));
    }

    @Test
    public void testHash() {
        org.junit.jupiter.api.Assertions.assertEquals(
            java.util.Arrays.hashCode(new Object[]{"a", "b"}),
            Objects.hash("a", "b")
        );
        org.junit.jupiter.api.Assertions.assertEquals(
            java.util.Arrays.hashCode(new Object[]{null, "b"}),
            Objects.hash(null, "b")
        );
        org.junit.jupiter.api.Assertions.assertEquals(
            java.util.Arrays.hashCode(new Object[]{}),
            Objects.hash()
        );
    }
}
