package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ArrayUtilsTest
{
    @Test
    public void array_should_return_elements()
    {
        String[] strings = ArrayUtils.array("a", "b", "c");
        assertThat(strings).containsExactly("a", "b", "c");
    }

    @Test
    public void array_should_return_empty_array_when_no_arguments()
    {
        Object[] objects = ArrayUtils.array();
        assertThat(objects).isEmpty();
    }
}
