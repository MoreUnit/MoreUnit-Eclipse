package org.moreunit.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
}
