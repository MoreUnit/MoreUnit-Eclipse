package org.moreunit.test.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface Preferences
{
    @Preferences(
        testSourcefolder = "test",
        testType = TestType.JUNIT3,
        testClassSuffixes = "Test")
    public static class JUNIT3 {}

    @Preferences(
        testSourcefolder = "test",
        testType = TestType.JUNIT4,
        testClassSuffixes = "Test")
    public static class JUNIT4 {}

    @Preferences(
        testSourcefolder = "test",
        testType = TestType.TESTNG,
        testClassSuffixes = "Test")
    public static class TESTNG{}

    /**
     * Another class annotated with @Preferences.
     */
    Class< ? > value() default Undefined.class;

    boolean extendedMethodSearch() default false;

    boolean flexibleNaming() default false;

    boolean testMethodPrefix() default false;

    String testSourcefolder() default "";

    TestType testType() default TestType.UNDEFINED;

    String[] testClassPrefixes() default "";

    String[] testClassSuffixes() default "";

    String testPackagePrefix() default "";

    String testPackageSuffix() default "";

    String testSuperClass() default "";
}
