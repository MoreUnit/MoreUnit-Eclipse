package org.moreunit.test.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestProject
{
    /**
     * Another class annotated with @TestProject.
     */
    Class< ? > value() default Undefined.class;
    
    /**
     * (Optional) This project's name.
     */
    String name() default "";

    /**
     * Names of the classes to generate as the initial sources.
     */
    String cls() default "";

    /**
     * Names of the files constituting the initial sources. The files themselves
     * must be in the same package as the test class using this configuration.
     */
    String src() default "";

    /**
     * (Optional) Name of the source folder.
     */
    String srcFolder() default "";
}
