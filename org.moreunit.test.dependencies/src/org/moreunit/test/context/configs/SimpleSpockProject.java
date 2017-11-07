package org.moreunit.test.context.configs;

import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;

@Project(
        mainCls = "org:SomeClass",
        testCls = "org:SomeClassSpec",
        properties = @Properties(SimpleSpockProperties.class))
public class SimpleSpockProject
{
}
