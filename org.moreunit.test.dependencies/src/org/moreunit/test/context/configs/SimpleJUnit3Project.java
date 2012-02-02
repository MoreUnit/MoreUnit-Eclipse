package org.moreunit.test.context.configs;

import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Project(
        mainCls = "org:SomeClass",
        testCls = "org:SomeClassTest",
        properties = @Properties(SimpleJUnit3Properties.class))
public class SimpleJUnit3Project
{
}
