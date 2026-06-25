package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.Test;

@Preferences(testPackagePrefix="Test")
public class ContextTest extends ContextTestCase
{
    @Project(mainCls="SomeClass", properties = @Properties(testPackagePrefix="tseT"))
    @Test
    public void should_use_properties_when_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertTrue(prefs.hasProjectSpecificSettings(context.getProjectHandler().get()));

        String prefix = prefs.getTestPackagePrefix(context.getProjectHandler().get());
        assertEquals(prefix, "tseT");
    }

    @Project(mainCls="SomeClass")
    @Test
    public void should_use_preferences_when_not_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertFalse(prefs.hasProjectSpecificSettings(context.getProjectHandler().get()));

        String prefix = prefs.getTestPackagePrefix(context.getProjectHandler().get());
        assertEquals(prefix, "Test");
    }

    @Project(mainCls="SomeClass")
    @Preferences(extendedMethodSearch=true,
                 methodSearchByName = false,
                 testMethodPrefix=true,
                 testPackagePrefix="pre",
                 testPackageSuffix="post",
                 testSrcFolder="testsrc",
                 testSuperClass="my.SuperClass",
                 testType=TestType.TESTNG,
                 testClassNameTemplate="${srcFile}Mest")
    @Test
    public void should_read_preferences_from_annotation()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        IJavaProject javaProject = context.getProjectHandler().get();
        assertTrue(prefs.getMethodSearchMode(javaProject).searchByCall);
        assertFalse(prefs.getMethodSearchMode(javaProject).searchByName);
        assertEquals(prefs.getTestMethodType(javaProject), "testMethodTypeJunit3");
        assertEquals(prefs.getProjectView(javaProject).getTestClassNameTemplate(), "${srcFile}Mest");
        assertEquals(prefs.getTestPackagePrefix(javaProject), "pre");
        assertEquals(prefs.getTestPackageSuffix(javaProject), "post");
        assertEquals(prefs.getJunitDirectoryFromPreferences(javaProject), "testsrc");
        assertEquals(prefs.getTestSuperClass(javaProject), "my.SuperClass");
        assertEquals(TestType.TESTNG.toString().toLowerCase(), prefs.getTestType(javaProject).toLowerCase());
    }

    @Project(mainCls="SomeClass",
             properties=@Properties(extendedMethodSearch=true,
                                    methodSearchByName = false,
                                    testMethodPrefix=true,
                                    testPackagePrefix="pre",
                                    testPackageSuffix="post",
                                    testSuperClass="my.SuperClass",
                                    testType=TestType.TESTNG,
                                    testClassNameTemplate="${srcFile}Dest"))
    @Test
    public void should_read_properties_from_annotation()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        IJavaProject javaProject = context.getProjectHandler().get();
        assertTrue(prefs.getMethodSearchMode(javaProject).searchByCall);
        assertFalse(prefs.getMethodSearchMode(javaProject).searchByName);
        assertEquals(prefs.getTestMethodType(javaProject), "testMethodTypeJunit3");
        assertEquals(prefs.getProjectView(javaProject).getTestClassNameTemplate(), "${srcFile}Dest");
        assertEquals(prefs.getTestPackagePrefix(javaProject), "pre");
        assertEquals(prefs.getTestPackageSuffix(javaProject), "post");
        assertEquals(prefs.getTestSuperClass(javaProject), "my.SuperClass");
        assertEquals(TestType.TESTNG.toString().toLowerCase(), prefs.getTestType(javaProject).toLowerCase());
    }
}
