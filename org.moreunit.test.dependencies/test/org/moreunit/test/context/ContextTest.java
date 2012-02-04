package org.moreunit.test.context;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


@Preferences(testClassPrefixes="Test")
public class ContextTest extends ContextTestCase
{
    @Project(mainCls="SomeClass", properties = @Properties(testClassPrefixes="tseT"))
    @Test
    public void should_use_properties_when_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertThat(prefs.hasProjectSpecificSettings(context.getProjectHandler().get())).isTrue();
        
        String[] prefixes = prefs.getPrefixes(context.getProjectHandler().get());
        assertThat(prefixes[0]).isEqualTo("tseT");
    }
    
    @Project(mainCls="SomeClass")
    @Test
    public void should_use_preferences_when_not_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertThat(prefs.hasProjectSpecificSettings(context.getProjectHandler().get())).isFalse();
        
        String[] prefixes = prefs.getPrefixes(context.getProjectHandler().get());
        assertThat(prefixes[0]).isEqualTo("Test");
    }
    
    @Project(mainCls="SomeClass")
    @Preferences(extendedMethodSearch=true,
                 testClassPrefixes="Pre",
                 testMethodPrefix=true,
                 flexibleNaming=true,
                 testClassSuffixes="Post",
                 testPackagePrefix="pre",
                 testPackageSuffix="post",
                 testSrcFolder="testsrc",
                 testSuperClass="my.SuperClass",
                 testType=TestType.TESTNG)
    @Test
    public void should_read_preferences_from_annotation()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        IJavaProject javaProject = context.getProjectHandler().get();
        assertThat(prefs.shouldUseTestMethodExtendedSearch(javaProject)).isTrue();
        assertThat(prefs.getPrefixes(javaProject)).containsOnly("Pre");
        assertThat(prefs.getTestMethodType(javaProject)).isEqualTo("testMethodTypeJunit3");
        assertThat(prefs.shouldUseFlexibleTestCaseNaming(javaProject)).isTrue();
        assertThat(prefs.getSuffixes(javaProject)).containsOnly("Post");
        assertThat(prefs.getTestPackagePrefix(javaProject)).isEqualTo("pre");
        assertThat(prefs.getTestPackageSuffix(javaProject)).isEqualTo("post");
        assertThat(prefs.getJunitDirectoryFromPreferences(javaProject)).isEqualTo("testsrc");
        assertThat(prefs.getTestSuperClass(javaProject)).isEqualTo("my.SuperClass");
        assertThat(prefs.getTestType(javaProject)).isEqualToIgnoringCase(TestType.TESTNG.toString());
    }
    
    @Project(mainCls="SomeClass", 
             properties=@Properties(extendedMethodSearch=true,
                                    testClassPrefixes="Pre",
                                    testMethodPrefix=true,
                                    flexibleNaming=true,
                                    testClassSuffixes="Post",
                                    testPackagePrefix="pre",
                                    testPackageSuffix="post",
                                    testSuperClass="my.SuperClass",
                                    testType=TestType.TESTNG))
    @Test
    public void should_read_properties_from_annotation()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        IJavaProject javaProject = context.getProjectHandler().get();
        assertThat(prefs.shouldUseTestMethodExtendedSearch(javaProject)).isTrue();
        assertThat(prefs.getPrefixes(javaProject)).containsOnly("Pre");
        assertThat(prefs.getTestMethodType(javaProject)).isEqualTo("testMethodTypeJunit3");
        assertThat(prefs.shouldUseFlexibleTestCaseNaming(javaProject)).isTrue();
        assertThat(prefs.getSuffixes(javaProject)).containsOnly("Post");
        assertThat(prefs.getTestPackagePrefix(javaProject)).isEqualTo("pre");
        assertThat(prefs.getTestPackageSuffix(javaProject)).isEqualTo("post");
        assertThat(prefs.getTestSuperClass(javaProject)).isEqualTo("my.SuperClass");
        assertThat(prefs.getTestType(javaProject)).isEqualToIgnoringCase(TestType.TESTNG.toString());
    }
}
