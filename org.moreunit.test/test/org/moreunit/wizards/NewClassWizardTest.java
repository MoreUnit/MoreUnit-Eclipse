package org.moreunit.wizards;

import static org.fest.assertions.Assertions.assertThat;
import static org.moreunit.wizards.NewClassWizard.removePrefix;
import static org.moreunit.wizards.NewClassWizard.removeSuffix;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestProject;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.SourceFolderHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.wizards.NewClassyWizardTestCase.JUnit3WithVariousPrefixesAndSuffixesPreferences;

public class NewClassWizardTest extends NewClassyWizardTestCase
{
    @Override
    protected Class< ? extends NewClassyWizard> getWizardClass()
    {
        return NewClassWizard.class;
    }

    @Test
    public void should_not_remove_possible_suffixes_from_middle_of_name() throws Exception
    {
        String[] possibleSuffixes = { "Test", "Spec" };

        for (String startName : new String[] { "NotATestReally", "SpecTestFoo", "FooSpecBar" })
        {
            String nameWithoutSuffix = removeSuffix(startName, possibleSuffixes);
            assertThat(nameWithoutSuffix).isEqualTo(startName);
        }
    }

    @Test
    public void should_remove_first_possible_suffix_only() throws Exception
    {
        String[] possibleSuffixes = { "Test", "Spec" };

        assertThat(removeSuffix("Class1Test", possibleSuffixes)).isEqualTo("Class1");
        assertThat(removeSuffix("Class2Spec", possibleSuffixes)).isEqualTo("Class2");
        assertThat(removeSuffix("Class3TestSpec", possibleSuffixes)).isEqualTo("Class3Test");
        assertThat(removeSuffix("Class4SpecTest", possibleSuffixes)).isEqualTo("Class4Spec");
    }

    @Test
    public void should_not_remove_possible_prefixes_from_middle_of_name() throws Exception
    {
        String[] possiblePrefixes = { "Test", "Spec" };

        for (String startName : new String[] { "NotATestReally", "FooSpecTest", "FooSpecBar" })
        {
            String nameWithoutPrefix = removePrefix(startName, possiblePrefixes);
            assertThat(nameWithoutPrefix).isEqualTo(startName);
        }
    }

    @Test
    public void should_remove_first_possible_prefix_only() throws Exception
    {
        String[] possiblePrefixes = { "Test", "Spec" };

        assertThat(removePrefix("TestClass1", possiblePrefixes)).isEqualTo("Class1");
        assertThat(removePrefix("SpecClass2", possiblePrefixes)).isEqualTo("Class2");
        assertThat(removePrefix("SpecTestClass3", possiblePrefixes)).isEqualTo("TestClass3");
        assertThat(removePrefix("TestSpecClass4", possiblePrefixes)).isEqualTo("SpecClass4");
    }
    
    @Test
    @Project(
        mainSrcFolder = "main-src",
        testSrcFolder = "test-src",
        testCls = "pre.pack.suf: SomeClassTest",
        properties = @Properties(JUnit3WithVariousPrefixesAndSuffixes.class))
    public void should_create_cut_in_main_source_folder_of_same_project() throws Exception
    {
        // given
        NewClassWizard wizard = new NewClassWizard(context.getPrimaryTypeHandler("pre.pack.suf.SomeClassTest").get());

        willAutomaticallyValidateWhenOpen(wizard);

        // when
        IType createdType = wizard.open();

        // then
        ProjectHandler mainProject = context.getProjectHandler();
        mainProject.assertThat().hasSourceFolder("main-src");
        context.assertCompilationUnit("pack.Class").isInSourceFolder("main-src").hasPrimaryType(createdType);
    }

    @Test
    @Project(mainSrcFolder = "main-src", testCls = "pre.pack.suf: SomeClassTest")
    @org.moreunit.test.context.Preferences(JUnit3WithVariousPrefixesAndSuffixesPreferences.class)
    public void should_create_cut_in_first_non_test_folder_of_same_project() throws Exception
    {
        // given
        NewClassWizard wizard = new NewClassWizard(context.getPrimaryTypeHandler("pre.pack.suf.SomeClassTest").get());

        willAutomaticallyValidateWhenOpen(wizard);

        // when
        IType createdType = wizard.open();

        // then
        ProjectHandler mainProject = context.getProjectHandler();
        mainProject.assertThat().hasSourceFolder("main-src");
        context.assertCompilationUnit("pack.Class").isInSourceFolder("main-src").hasPrimaryType(createdType);
    }
    
    @Test
    @Project(
        mainSrcFolder = "main-src",
        properties = @Properties(JUnit3WithVariousPrefixesAndSuffixes.class),
        testProject = @TestProject(
            srcFolder = "test-src",
            cls = "pre.pack.suf: SomeClassTest"))
    public void should_create_cut_in_source_folder_of_main_project() throws Exception
    {
        // given
        NewClassWizard wizard = new NewClassWizard(context.getPrimaryTypeHandler("pre.pack.suf.SomeClassTest").get());

        willAutomaticallyValidateWhenOpen(wizard);

        // when
        IType createdType = wizard.open();

        // then
        ProjectHandler mainProject = context.getMainProjectHandler();
        mainProject.assertThat().hasSourceFolder("main-src");
        context.assertCompilationUnit("pack.Class").isInProject(mainProject).isInSourceFolder("main-src").hasPrimaryType(createdType);
    }

    @Test
    @Project(
        mainSrcFolder = "main-src",
        testSrcFolder = "test-src",
        properties = @Properties(JUnit3WithVariousPrefixesAndSuffixes.class))
    public void should_create_cut_in_main_source_folder_associated_to_current_test_folder() throws Exception
    {
        // given
        ProjectHandler project = context.getProjectHandler();
        
        addMapping(project, project.getSrcFolderHandler("main-src2"), project.getSrcFolderHandler("test-src2"));

        NewClassWizard wizard = new NewClassWizard(project.getSrcFolderHandler("test-src2").createClass("pre.pack.suf.SomeClassTest").get());

        willAutomaticallyValidateWhenOpen(wizard);

        // when
        IType createdType = wizard.open();

        // then
        project.assertThat().hasSourceFolder("main-src2");
        context.assertCompilationUnit("pack.Class").isInSourceFolder("main-src2").hasPrimaryType(createdType);
    }
}
