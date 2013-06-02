package org.moreunit.test.mockito;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsOneOfTest
{
    Matcher<String> stringMatcher = new IsOneOf<String>("a", "b", "c");
    Matcher<Integer> intMatcher = new IsOneOf<Integer>(1, 2);

    @Test
    public void should_match_either_one_of_given_args() throws Exception
    {
        assertTrue(stringMatcher.matches("a"));
        assertTrue(stringMatcher.matches("b"));
        assertTrue(stringMatcher.matches("c"));

        assertTrue(intMatcher.matches(1));
        assertTrue(intMatcher.matches(2));
    }

    @Test
    public void should_not_match_any_other_arg() throws Exception
    {
        assertFalse(stringMatcher.matches("e"));
        assertFalse(stringMatcher.matches("Z"));

        assertFalse(intMatcher.matches(7));
        assertFalse(intMatcher.matches(- 9));
    }

    @Test
    public void should_have_a_human_readable_description() throws Exception
    {
        StringDescription description = new StringDescription();
        stringMatcher.describeTo(description);
        assertThat(description.toString()).isEqualTo("is one of (\"a\", \"b\", \"c\")");

        description = new StringDescription();
        intMatcher.describeTo(description);
        assertThat(description.toString()).isEqualTo("is one of (<1>, <2>)");
    }
}
