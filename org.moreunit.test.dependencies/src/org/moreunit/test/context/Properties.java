package org.moreunit.test.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Properties
{
    @Properties(
        testType = TestType.JUNIT3,
        testClassSuffixes = "Test")
    class JUNIT3 {}

    @Properties(
        testType = TestType.JUNIT4,
        testClassSuffixes = "Test")
    class JUNIT4 {}

    @Properties(
        testType = TestType.TESTNG,
        testClassSuffixes = "Test")
    class TESTNG {}
    
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
    
    boolean userSetProperties() default true;
}
