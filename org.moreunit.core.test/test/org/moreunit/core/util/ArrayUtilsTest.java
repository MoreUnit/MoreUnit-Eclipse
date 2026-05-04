package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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

    @Test
    public void array_should_handle_null_arguments()
    {
        Object[] objects = ArrayUtils.array((Object) null);
        assertThat(objects).containsExactly((Object) null);

        String[] strings = ArrayUtils.array("a", null, "c");
        assertThat(strings).containsExactly("a", null, "c");
    }
}
