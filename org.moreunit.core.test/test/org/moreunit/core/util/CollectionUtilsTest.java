package org.moreunit.core.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.moreunit.core.util.CollectionUtils.asSet;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class CollectionUtilsTest
{
    @Test
    public void asSet_should_return_empty_set() throws Exception
    {
        assertThat(asSet()).isEmpty();
    }

    @Test
    public void asSet_should_return_set_made_of_given_elements() throws Exception
    {
        Set<String> someSetWithSameStrings = new TreeSet<String>();
        someSetWithSameStrings.add("b");
        someSetWithSameStrings.add("c");
        someSetWithSameStrings.add("a");

        assertThat(asSet("a", "b", "c", "a")).isEqualTo(someSetWithSameStrings);

        Set<Integer> someSetWithSameIntegers = new HashSet<Integer>();
        someSetWithSameIntegers.add(4);
        someSetWithSameIntegers.add(9);
        someSetWithSameIntegers.add(- 1);

        assertThat(asSet(9, - 1, 4, - 1)).isEqualTo(someSetWithSameIntegers);
    }
}
