package org.moreunit.mock.model;

import org.junit.Test;

public class SetterDependencyTest
{
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_methods_that_do_not_start_with_set() throws Exception
    {
        new SetterDependency("pack.age.Type", "notASetter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_methods_which_name_are_less_than_4_characters() throws Exception
    {
        new SetterDependency("pack.age.Type", "set");
    }
}
