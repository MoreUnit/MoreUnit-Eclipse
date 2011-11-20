package org.moreunit.test;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ManagedClassTest
{
    private ManagedClass managedClass = new ManagedClass(null, null, null);

    @Test
    public void should_extract_package_from_source() throws Exception
    {
        String packageName = managedClass.extract("\n  package pack.aGe.2extract  ;\r\n  public class A { }", ManagedClass.PACKAGE_PATTERN);
        assertThat(packageName).isEqualTo("pack.aGe.2extract");
    }

    @Test
    public void should_extract_classname_from_source() throws Exception
    {
        String className = managedClass.extract("package pack.age  ;\r\n  public  class Class2Extract  { }", ManagedClass.CLASSNAME_PATTERN);
        assertThat(className).isEqualTo("Class2Extract");
    }
}
