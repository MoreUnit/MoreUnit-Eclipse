package org.moreunit.test.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface Preferences
{
    /**
     * Another class annotated with @Preferences.
     */
    Class< ? > value() default Undefined.class;

    boolean extendedMethodSearch() default false;

    boolean methodSearchByName() default true;

    boolean testMethodPrefix() default false;

    String testSrcFolder() default "";

    TestType testType() default TestType.UNDEFINED;

    String testPackagePrefix() default "";

    String testPackageSuffix() default "";

    String testSuperClass() default "";

    String testClassNameTemplate() default "";
}
