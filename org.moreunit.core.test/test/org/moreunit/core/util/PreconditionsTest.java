package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PreconditionsTest
{
    @Test
    public void checkNotNull_should_return_reference_when_not_null()
    {
        String ref = "abc";
        assertSame(Preconditions.checkNotNull(ref), ref);
    }

    @Test
    public void checkNotNull_should_throw_exception_when_null()
    {
        assertThrows(NullPointerException.class, () -> Preconditions.checkNotNull(null));
    }

    @Test
    public void checkNotNull_with_message_should_return_reference_when_not_null()
    {
        String ref = "abc";
        assertSame(Preconditions.checkNotNull(ref, "error"), ref);
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
            assertEquals(e.getMessage(), "custom error");
        }
    }

    @Test
    public void checkArgument_should_do_nothing_when_true()
    {
        Preconditions.checkArgument(true);
    }

    @Test
    public void checkArgument_should_throw_exception_when_false()
    {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false));
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
            assertEquals(e.getMessage(), "custom error");
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
            assertEquals(e.getMessage(), "custom error");
        }
    }

    @Test
    public void checkNotNullOrEmpty_should_return_collection_when_not_null_nor_empty()
    {
        List<String> list = Arrays.asList("a");
        assertSame(Preconditions.checkNotNullOrEmpty(list), list);
    }

    @Test
    public void checkNotNullOrEmpty_should_throw_NPE_when_null()
    {
        assertThrows(NullPointerException.class, () -> Preconditions.checkNotNullOrEmpty(null));
    }

    @Test
    public void checkNotNullOrEmpty_should_throw_IAE_when_empty()
    {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkNotNullOrEmpty(new ArrayList<String>()));
    }

    @Test
    public void checkNotNullOrEmpty_with_message_should_return_collection_when_not_null_nor_empty()
    {
        List<String> list = Arrays.asList("a");
        assertSame(Preconditions.checkNotNullOrEmpty(list, "error"), list);
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
            assertEquals(e.getMessage(), "custom error");
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
            assertEquals(e.getMessage(), "custom error");
        }
    }
}
