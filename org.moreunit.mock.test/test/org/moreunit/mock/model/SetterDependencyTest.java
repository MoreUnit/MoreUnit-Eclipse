package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void should_extract_name_from_setter_method()
    {
        SetterDependency dep = new SetterDependency("com.example.MyService", "setMyService");
        assertEquals("myService", dep.name);
        assertEquals("setMyService", dep.setterMethodName);
    }

    @Test
    public void should_handle_uppercase_after_set_prefix()
    {
        SetterDependency dep = new SetterDependency("com.example.Foo", "setURLHandler");
        assertEquals("uRLHandler", dep.name);
    }

    @Test
    public void should_reject_null_setter_method_name()
    {
        assertThrows(NullPointerException.class, () -> new SetterDependency("com.example.Foo", null));
    }

    @Test
    public void should_reject_empty_setter_method_name()
    {
        assertThrows(IllegalArgumentException.class, () -> new SetterDependency("com.example.Foo", ""));
    }
}
