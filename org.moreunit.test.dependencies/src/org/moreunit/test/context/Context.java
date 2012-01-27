package org.moreunit.test.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation targeted at test methods that allows for specifying an initial
 * configuration for the Eclipse project(s) used during the test.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Context
{
    /**
     * Another class annotated with @Context or @Project/@Preferences.
     */
    Class< ? > value() default Undefined.class;

    /**
     * (Optional) The preferences to be applied to MoreUnit during the test.
     */
    Preferences preferences() default @Preferences(None.class);

    /**
     * Names of the classes to generate as the initial production sources.
     */
    String mainCls() default "";

    /**
     * Names of the files constituting the initial production sources. The files
     * themselves must be in the same package as the test class using this
     * configuration.
     */
    String mainSrc() default "";

    /**
     * (Optional) Names of the classes to generate as the initial test sources.
     */
    String testCls() default "";

    /**
     * (Optional) Names of the files constituting the initial test sources. The
     * files themselves must be in the same package as the test class using this
     * configuration.
     */
    String testSrc() default "";
}
