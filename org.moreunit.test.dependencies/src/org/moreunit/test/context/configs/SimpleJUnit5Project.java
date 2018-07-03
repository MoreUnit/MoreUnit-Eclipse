package org.moreunit.test.context.configs;

import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;

@Project(
        mainCls = "org:SomeClass",
        testCls = "org:SomeClassTest",
        properties = @Properties(SimpleJUnit5Properties.class))
public class SimpleJUnit5Project
{
}
