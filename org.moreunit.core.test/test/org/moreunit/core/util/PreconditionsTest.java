package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class PreconditionsTest
{
    @Test
    public void checkNotNull_should_return_reference_when_not_null()
    {
        String ref = "abc";
        assertThat(Preconditions.checkNotNull(ref)).isSameAs(ref);
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNull_should_throw_exception_when_null()
    {
        Preconditions.checkNotNull(null);
    }

    @Test
    public void checkNotNull_with_message_should_return_reference_when_not_null()
    {
        String ref = "abc";
        assertThat(Preconditions.checkNotNull(ref, "error")).isSameAs(ref);
    }

    @Test
    public void checkNotNull_with_message_should_throw_exception_with_message_when_null()
    {
        try
        {
            Preconditions.checkNotNull(null, "custom error");
            fail("Should have thrown NullPointerException");
        }
        catch (NullPointerException e)
        {
            assertThat(e.getMessage()).isEqualTo("custom error");
        }
    }

    @Test
    public void checkArgument_should_do_nothing_when_true()
    {
        Preconditions.checkArgument(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkArgument_should_throw_exception_when_false()
    {
        Preconditions.checkArgument(false);
    }

    @Test
    public void checkArgument_with_message_should_do_nothing_when_true()
    {
        Preconditions.checkArgument(true, "error");
    }

    @Test
    public void checkArgument_with_message_should_throw_exception_with_message_when_false()
    {
        try
        {
            Preconditions.checkArgument(false, "custom error");
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertThat(e.getMessage()).isEqualTo("custom error");
        }
    }

    @Test
    public void checkState_should_do_nothing_when_true()
    {
        Preconditions.checkState(true, "error");
    }

    @Test
    public void checkState_should_throw_exception_with_message_when_false()
    {
        try
        {
            Preconditions.checkState(false, "custom error");
            fail("Should have thrown IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertThat(e.getMessage()).isEqualTo("custom error");
        }
    }

    @Test
    public void checkNotNullOrEmpty_should_return_collection_when_not_null_nor_empty()
    {
        List<String> list = Arrays.asList("a");
        assertThat(Preconditions.checkNotNullOrEmpty(list)).isSameAs(list);
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNullOrEmpty_should_throw_NPE_when_null()
    {
        Preconditions.checkNotNullOrEmpty(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNotNullOrEmpty_should_throw_IAE_when_empty()
    {
        Preconditions.checkNotNullOrEmpty(new ArrayList<String>());
    }

    @Test
    public void checkNotNullOrEmpty_with_message_should_return_collection_when_not_null_nor_empty()
    {
        List<String> list = Arrays.asList("a");
        assertThat(Preconditions.checkNotNullOrEmpty(list, "error")).isSameAs(list);
    }

    @Test
    public void checkNotNullOrEmpty_with_message_should_throw_NPE_with_message_when_null()
    {
        try
        {
            Preconditions.checkNotNullOrEmpty(null, "custom error");
            fail("Should have thrown NullPointerException");
        }
        catch (NullPointerException e)
        {
            assertThat(e.getMessage()).isEqualTo("custom error");
        }
    }

    @Test
    public void checkNotNullOrEmpty_with_message_should_throw_IAE_with_message_when_empty()
    {
        try
        {
            Preconditions.checkNotNullOrEmpty(new ArrayList<String>(), "custom error");
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertThat(e.getMessage()).isEqualTo("custom error");
        }
    }
}
