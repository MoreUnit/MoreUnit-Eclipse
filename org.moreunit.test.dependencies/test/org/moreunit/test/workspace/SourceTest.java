package org.moreunit.test.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class SourceTest
{
    private Source source = new Source(null, null, null);

    @Test
    public void should_complain_when_source_does_not_exist() throws Exception
    {
        assertNull(new Source(null, "doesNotExist.txt", this.getClass()).getSource());
    }

    @Test
    public void should_extract_package_from_source() throws Exception
    {
        String packageName = source.extract("\n  package pack.aGe.2extract  ;\r\n  public class A { }", CompilationUnitHandler.PACKAGE_PATTERN);
        assertEquals(packageName, "pack.aGe.2extract");
    }

    @Test
    public void should_extract_classname_from_source() throws Exception
    {
        String className = source.extract("package pack.age  ;\r\n  public  class Class2Extract  { }", CompilationUnitHandler.CLASSNAME_PATTERN);
        assertEquals(className, "Class2Extract");
    }
}
