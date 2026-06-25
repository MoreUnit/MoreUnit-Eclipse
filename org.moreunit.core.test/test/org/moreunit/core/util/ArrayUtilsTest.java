package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ArrayUtilsTest
{
    @Test
    public void array_should_return_elements()
    {
        String[] strings = ArrayUtils.array("a", "b", "c");
        assertArrayEquals(new String[] { "a", "b", "c" }, strings);
    }

    @Test
    public void array_should_return_empty_array_when_no_arguments()
    {
        Object[] objects = ArrayUtils.array();
        assertTrue(objects.length == 0);
    }

    @Test
    public void array_should_handle_null_arguments()
    {
        Object[] objects = ArrayUtils.array((Object) null);
        assertArrayEquals(new Object[] { null }, objects);

        String[] strings = ArrayUtils.array("a", null, "c");
        assertArrayEquals(new String[] { "a", null, "c" }, strings);
    }
}
