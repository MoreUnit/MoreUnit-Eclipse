package org.moreunit.test.mockito;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(stringMatcher.matches("a")).isTrue();
        assertThat(stringMatcher.matches("b")).isTrue();
        assertThat(stringMatcher.matches("c")).isTrue();

        assertThat(intMatcher.matches(1)).isTrue();
        assertThat(intMatcher.matches(2)).isTrue();
    }

    @Test
    public void should_not_match_any_other_arg() throws Exception
    {
        assertThat(stringMatcher.matches("e")).isFalse();
        assertThat(stringMatcher.matches("Z")).isFalse();

        assertThat(intMatcher.matches(7)).isFalse();
        assertThat(intMatcher.matches(- 9)).isFalse();
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
