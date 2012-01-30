package org.moreunit.test.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Properties
{
    /**
     * Another class annotated with @Properties.
     */
    Class< ? > value() default Undefined.class;

    boolean extendedMethodSearch() default false;

    boolean flexibleNaming() default false;

    boolean testMethodPrefix() default false;

    TestType testType() default TestType.UNDEFINED;

    String[] testClassPrefixes() default "";

    String[] testClassSuffixes() default "";

    String testPackagePrefix() default "";

    String testPackageSuffix() default "";

    String testSuperClass() default "";
}
