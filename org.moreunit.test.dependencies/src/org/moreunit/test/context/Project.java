package org.moreunit.test.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Project
{
    /**
     * Another class annotated with @Project.
     */
    Class< ? > value() default Undefined.class;

    /**
     * (Optional) This project's name.
     */
    String name() default "";

    /**
     * Names of the classes to generate as the initial production sources.
     */
    String mainCls() default "";

    /**
     * Names of the files constituting the initial production sources. The files
     * themself must be in the same package as the test class using this
     * configuration.
     */
    String mainSrc() default "";

    /**
     * (Optional) Name of the main source folder (defaults to "src").
     */
    String mainSrcFolder() default "";

    /**
     * (Optional) Names of the classes to generate as the initial test sources.
     */
    String testCls() default "";

    /**
     * (Optional) Names of the files constituting the initial test sources. The
     * files themself must be in the same package as the test class using this
     * configuration.
     */
    String testSrc() default "";

    /**
     * (Optional) Name of the test source folder (defaults to MoreUnit's
     * default: "junit").
     */
    String testSrcFolder() default "";

    /**
     * (Optional) The properties to be applied to MoreUnit during the test, for
     * this project.
     */
    Properties properties() default @Properties(userSetProperties=false);

    /**
     * (Optional) A project containing the tests for this project.
     */
    TestProject testProject() default @TestProject;
}
