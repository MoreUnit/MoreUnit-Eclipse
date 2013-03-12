package org.moreunit.test.context;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.Ignore;
import org.junit.Test;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.test.DummyPreferencesForTesting;


@Preferences(testPackagePrefix="Test")
public class ContextTest extends ContextTestCase
{
    @Project(mainCls="SomeClass", properties = @Properties(testPackagePrefix="tseT"))
    @Test
    public void should_use_properties_when_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertThat(prefs.hasProjectSpecificSettings(context.getProjectHandler().get())).isTrue();
        
        String prefix = prefs.getTestPackagePrefix(context.getProjectHandler().get());
        assertThat(prefix).isEqualTo("tseT");
    }
    
    @Project(mainCls="SomeClass")
    @Test
    public void should_use_preferences_when_not_annotated_with_properties()
    {
        org.moreunit.preferences.Preferences prefs = org.moreunit.preferences.Preferences.getInstance();
        assertThat(prefs.hasProjectSpecificSettings(context.getProjectHandler().get())).isFalse();
        
        String prefix = prefs.getTestPackagePrefix(context.getProjectHandler().get());
        assertThat(prefix).isEqualTo("Test");
    }
    
    @Project(mainCls="SomeClass")
    @Preferences(extendedMethodSearch=true,
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
        assertThat(prefs.shouldUseTestMethodExtendedSearch(javaProject)).isTrue();
        assertThat(prefs.getTestMethodType(javaProject)).isEqualTo("testMethodTypeJunit3");
        assertThat(prefs.getProjectView(javaProject).getTestClassNameTemplate()).isEqualTo("${srcFile}Mest");
        assertThat(prefs.getTestPackagePrefix(javaProject)).isEqualTo("pre");
        assertThat(prefs.getTestPackageSuffix(javaProject)).isEqualTo("post");
        assertThat(prefs.getJunitDirectoryFromPreferences(javaProject)).isEqualTo("testsrc");
        assertThat(prefs.getTestSuperClass(javaProject)).isEqualTo("my.SuperClass");
        assertThat(prefs.getTestType(javaProject)).isEqualToIgnoringCase(TestType.TESTNG.toString());
    }
    
    @Project(mainCls="SomeClass", 
             properties=@Properties(extendedMethodSearch=true,
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
        assertThat(prefs.shouldUseTestMethodExtendedSearch(javaProject)).isTrue();
        assertThat(prefs.getTestMethodType(javaProject)).isEqualTo("testMethodTypeJunit3");
        assertThat(prefs.getProjectView(javaProject).getTestClassNameTemplate()).isEqualTo("${srcFile}Dest");
        assertThat(prefs.getTestPackagePrefix(javaProject)).isEqualTo("pre");
        assertThat(prefs.getTestPackageSuffix(javaProject)).isEqualTo("post");
        assertThat(prefs.getTestSuperClass(javaProject)).isEqualTo("my.SuperClass");
        assertThat(prefs.getTestType(javaProject)).isEqualToIgnoringCase(TestType.TESTNG.toString());
    }
}
