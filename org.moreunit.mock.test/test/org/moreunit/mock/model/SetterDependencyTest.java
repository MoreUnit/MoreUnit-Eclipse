package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SetterDependencyTest
{
    @Test
    public void should_reject_methods_that_do_not_start_with_set() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new SetterDependency("pack.age.Type", "notASetter"));
    }

    @Test
    public void should_reject_methods_which_name_are_less_than_4_characters() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new SetterDependency("pack.age.Type", "set"));
    }
}
