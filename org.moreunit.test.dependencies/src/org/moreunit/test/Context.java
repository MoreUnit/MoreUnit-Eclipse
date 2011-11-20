package org.moreunit.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation targeted at test methods that allows for specifying files
 * containing source code for an initial class under test, an initial test case
 * (before the test has run), and an expected resulting test case (after the
 * test has run).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Context
{
    /**
     * Name of the file containing the definition of the class under test. The
     * file itself must be in the same package as the test class using it.
     */
    String cutDefinition() default "";

    /**
     * Name of the file containing the definition of the expected resulting test
     * case. The file itself must be in the same package as the test class using
     * it.
     */
    String expectedTestCase() default "";

    /**
     * Name of the file containing the definition of the initial test case. The
     * file itself must be in the same package as the test class using it.
     */
    String testCaseDefinition() default "";
}
