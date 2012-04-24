package org.moreunit.core.util;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.moreunit.core.util.StringLengthComparator;

public class StringLengthComparatorTest
{
    @Test
    public void should_return_positive_integer_when_first_has_greater_length_than_second_parameter() throws Exception
    {
        assertThat(new StringLengthComparator().compare("Long", "")).isGreaterThan(0);
    }

    @Test
    public void should_return_negative_integer_when_second_has_greater_length_than_first_parameter() throws Exception
    {
        assertThat(new StringLengthComparator().compare("", "Long")).isLessThanOrEqualTo(0);
    }

    @Test
    public void should_return_zero_when_called_with_equal_strings() throws Exception
    {
        assertThat(new StringLengthComparator().compare("Long", "Long")).isEqualTo(0);
    }
}
