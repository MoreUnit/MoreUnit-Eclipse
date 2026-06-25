package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.moreunit.test.workspace.JavaType;

public class AnnotationConfigExtractorTest
{
    private AnnotationConfigExtractor configExtractor = new AnnotationConfigExtractor();

    @Test
    public void should_reject_null_annotated_element() throws Exception
    {
        assertThrows(NullPointerException.class, () -> configExtractor.extractFrom(null, new ElementWithoutAnnotation()));
    }

    @Test
    public void should_accept_null_default_annotated_element() throws Exception
    {
        // given
        @Context(mainSrc = "SampleProductionType")
        class AnnotationHolder
        {
        }

        // when
        configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then no exception
    }

    @Test
    public void should_return_null_when_no_annotations() throws Exception
    {
        assertNull(configExtractor.extractFrom(new ElementWithoutAnnotation(), null));
    }

    @Test
    public void should_load_config_from_context_annotation() throws Exception
    {
        // given
        @Context(mainSrc = "SomeProductionType")
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList("SomeProductionType")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
    }

    @Test
    public void should_load_config_from_default_context() throws Exception
    {
        // given
        @Context(mainSrc = "DefaultProductionType")
        class DefaultAnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(new ElementWithoutAnnotation(), annotatedElement(DefaultAnnotationHolder.class));

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList("DefaultProductionType")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
    }

    @Test
    public void should_load_context_from_target_element_even_when_default_element_is_provided() throws Exception
    {
        // given
        @Context(mainSrc = "SomeProductionType")
        class AnnotationHolder
        {
        }

        @Context(mainSrc = "DefaultProductionType")
        class DefaultAnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), annotatedElement(DefaultAnnotationHolder.class));

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList("SomeProductionType")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
    }

    @Test
    public void should_complain_when_both_context_and_project_are_defined_for_annotated_element() throws Exception
    {
        // given
        @Context
        @Project
        class AnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
    }

    @Test
    public void should_complain_when_both_context_and_project_are_defined_for_default_annotated_element() throws Exception
    {
        // given
        @Context
        @Project
        class DefaultAnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(new ElementWithoutAnnotation(), annotatedElement(DefaultAnnotationHolder.class)));
    }

    @Test
    public void should_complain_when_both_context_and_preferences_are_defined_for_annotated_element() throws Exception
    {
        // given
        @Context
        @Preferences
        class AnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
    }

    @Test
    public void should_complain_when_both_context_and_preferences_are_defined_for_default_annotated_element() throws Exception
    {
        // given
        @Context
        @Preferences
        class DefaultAnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(new ElementWithoutAnnotation(), annotatedElement(DefaultAnnotationHolder.class)));
    }

    @Test
    public void should_load_config_from_preferences_and_project() throws Exception
    {
        // given
        @Preferences(testClassNameTemplate = "${srcFile}Suf")
        @Project(mainSrc = "SampleType")
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList("SampleType")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Suf");
    }

    @Test
    public void should_load_config_from_default_preferences_and_project() throws Exception
    {
        // given
        @Project(mainSrc = "SampleType")
        class AnnotationHolder
        {
        }

        @Preferences(testClassNameTemplate = "${srcFile}Suffix")
        class DefaultAnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), annotatedElement(DefaultAnnotationHolder.class));

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList("SampleType")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Suffix");
    }

    @Test
    public void should_load_config_from_preferences_and_default_project() throws Exception
    {
        // given
        @Preferences(testClassNameTemplate = "${srcFile}Suf")
        class AnnotationHolder
        {
        }

        @Project(mainSrcFolder = "sources")
        class DefaultAnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), annotatedElement(DefaultAnnotationHolder.class));

        // then
        assertEquals(config.getProject(DEFAULT_PROJECT_NAME).getMainSourceFolder(), "sources");
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Suf");
    }

    @Test
    public void should_load_preferences_from_context_annotation() throws Exception
    {
        // given
        @Context(preferences = @Preferences(testClassNameTemplate = "${srcFile}Pre"))
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Pre");
    }

    @Test
    public void should_not_load_default_preferences_from_context_annotation() throws Exception
    {
        // given
        @Context
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertNull(config.getPreferencesConfig());
    }

    @Test
    public void should_create_project_config_with_default_name_for_context_annotation() throws Exception
    {
        // given
        @Context
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertNotNull(config.getProject(DEFAULT_PROJECT_NAME));
    }

    @Test
    public void should_load_preferences_from_annotation_value() throws Exception
    {
        // given
        @Preferences(testClassNameTemplate = "${srcFile}Suffix")
        class PreferencesDefinition
        {
        }

        @Preferences(PreferencesDefinition.class)
        @Project
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Suffix");
    }

    @Test
    public void should_load_properties_from_project_annotation() throws Exception
    {
        // given
        @Project(properties = @Properties(testClassNameTemplate = "${srcFile}Prefix"))
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getProject(DEFAULT_PROJECT_NAME).getPropertiesConfig().getTestClassNameTemplate(), "${srcFile}Prefix");
    }

    @Test
    public void should_load_properties_from_annotation_value() throws Exception
    {
        // given
        @Properties(testSuperClass = "SuperClass")
        class PropertiesDefinition
        {
        }

        @Project(properties = @Properties(PropertiesDefinition.class))
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getProject(DEFAULT_PROJECT_NAME).getPropertiesConfig().getTestSuperClass(), "SuperClass");
    }

    @Test
    public void should_ignore_properties_when_not_provided() throws Exception
    {
        // given
        @Project
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertNull(config.getProject(DEFAULT_PROJECT_NAME).getPropertiesConfig());
    }

    @Test
    public void should_load_test_project_from_project_annotation() throws Exception
    {
        // given
        @Project(testProject = @TestProject(name = "test-project-name", cls = "net: Foo, enum Baz", src = "Bar.txt, qux.txt", srcFolder = "src-folder"))
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        TestProjectConfiguration testProjectConfig = config.getProject(DEFAULT_PROJECT_NAME).getTestProjectConfig();
        assertEquals(testProjectConfig.getProjectName(), "test-project-name");
        assertEquals(new java.util.HashSet<>(Arrays.asList(JavaType.newClass("net", "Foo"), JavaType.newEnum("net", "Baz"))), new java.util.HashSet<>((testProjectConfig.getTypes())));
        assertEquals(new java.util.HashSet<>(Arrays.asList("Bar.txt", "qux.txt")), new java.util.HashSet<>((testProjectConfig.getSources())));
        assertEquals(testProjectConfig.getSourceFolder(), "src-folder");
    }

    @Test
    public void should_complain_when_both_testProject_and_testSrc_are_defined() throws Exception
    {
        // given
        @Project(testSrc = "SomeFile", testProject = @TestProject(name = "test-project-name"))
        class AnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
    }

    @Test
    public void should_complain_when_both_testProject_and_testCls_are_defined() throws Exception
    {
        // given
        @Project(testCls = "SomeFile", testProject = @TestProject(name = "test-project-name"))
        class AnnotationHolder
        {
        }

        // when/then
        assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
    }

    @Test
    public void should_recursively_load_preferences_from_annotation_value() throws Exception
    {
        // given
        @Preferences(testClassNameTemplate = "Pfx${srcFile}")
        class PreferencesDefinition1
        {
        }

        @Preferences(PreferencesDefinition1.class)
        class PreferencesDefinition2
        {
        }

        @Preferences(PreferencesDefinition2.class)
        @Project
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "Pfx${srcFile}");
    }

    @Test
    public void should_prevent_too_much_recursion_when_loading_preferences_from_annotation_value() throws Exception
    {
        // given
        configExtractor.setMaximimDefinitionDepth(2);

        @Preferences(testClassNameTemplate = "Pfx${srcFile}")
        class PreferencesDefinition1
        {
        }

        @Preferences(PreferencesDefinition1.class)
        class PreferencesDefinition2
        {
        }

        @Preferences(PreferencesDefinition2.class)
        @Project
        class PreferencesDefinition3
        {
        }

        @Preferences(PreferencesDefinition3.class)
        @Project
        class AnnotationHolder
        {
        }

        {
            IllegalConfigurationException e = assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
            assertEquals("Too much recursion in @Preferences definitions", e.getMessage());
        }
    }

    @Test
    public void should_recursively_load_properties_from_annotation_value() throws Exception
    {
        // given
        @Properties(testType = TestType.TESTNG)
        class PropertiesDefinition1
        {
        }

        @Properties(PropertiesDefinition1.class)
        class PropertiesDefinition2
        {
        }

        @Project(properties = @Properties(PropertiesDefinition2.class))
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getProject(DEFAULT_PROJECT_NAME).getPropertiesConfig().getTestType(), TestType.TESTNG);
    }

    @Test
    public void should_prevent_too_much_recursion_when_loading_properties_from_annotation_value() throws Exception
    {
        // given
        configExtractor.setMaximimDefinitionDepth(1);

        @Properties(testType = TestType.TESTNG)
        class PropertiesDefinition1
        {
        }

        @Properties(PropertiesDefinition1.class)
        class PropertiesDefinition2
        {
        }

        @Project(properties = @Properties(PropertiesDefinition2.class))
        class AnnotationHolder
        {
        }

        {
            IllegalConfigurationException e = assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
            assertEquals("Too much recursion in @Properties definitions", e.getMessage());
        }
    }

    @Test
    public void should_recursively_load_project_from_annotation_value() throws Exception
    {
        // given
        @Project(testSrcFolder = "tests")
        class ProjectDefinition
        {
        }

        @Project(ProjectDefinition.class)
        class ProjectDefinition1
        {
        }

        @Project(ProjectDefinition1.class)
        class ProjectDefinition2
        {
        }

        @Project(ProjectDefinition2.class)
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getProject(DEFAULT_PROJECT_NAME).getTestSourceFolder(), "tests");
    }

    @Test
    public void should_prevent_too_much_recursion_when_loading_project_from_annotation_value() throws Exception
    {
        // given
        configExtractor.setMaximimDefinitionDepth(2);

        @Project(mainSrc = "SomeConcept")
        class ProjectDefinition
        {
        }

        @Project(ProjectDefinition.class)
        class ProjectDefinition1
        {
        }

        @Project(ProjectDefinition1.class)
        class ProjectDefinition2
        {
        }

        @Project(ProjectDefinition2.class)
        class AnnotationHolder
        {
        }

        {
            IllegalConfigurationException e = assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
            assertEquals("Too much recursion in @Project definitions", e.getMessage());
        }
    }

    @Test
    public void should_recursively_load_project_and_preferences_from_context_value() throws Exception
    {
        // given
        @Preferences(testClassNameTemplate = "${srcFile}Suffix")
        class PreferencesDefinition
        {
        }
        @Project(mainSrc = "SomeConcept")
        class ProjectDefinition
        {
        }

        @Preferences(PreferencesDefinition.class)
        @Project(ProjectDefinition.class)
        class ContextDefinition1
        {
        }

        @Context(ContextDefinition1.class)
        class ContextDefinition2
        {
        }

        @Context(ContextDefinition2.class)
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "${srcFile}Suffix");
        assertEquals(new java.util.HashSet<>(Arrays.asList("SomeConcept")), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainSources())));
    }

    @Test
    public void should_prevent_too_much_recursion_when_loading_project_and_properties_from_context_value() throws Exception
    {
        // given
        configExtractor.setMaximimDefinitionDepth(1);

        @Preferences(testClassNameTemplate = "${srcFile}Suffix")
        @Project(mainSrc = "SomeConcept")
        class ContextDefinition1
        {
        }

        @Context(ContextDefinition1.class)
        class ContextDefinition2
        {
        }

        @Context(ContextDefinition2.class)
        class AnnotationHolder
        {
        }

        {
            IllegalConfigurationException e = assertThrows(IllegalConfigurationException.class, () -> configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null));
            assertEquals("Too much recursion in @Context definitions", e.getMessage());
        }
    }

    @Test
    public void should_extract_type_defs_from_project_annotation() throws Exception
    {
        @Project(mainCls = "org.example:SomeClass,enum SomeEnum; AnotherClass", testCls = "class ClassTest; net: AType")
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList(JavaType.newClass("org.example", "SomeClass"), JavaType.newEnum("org.example", "SomeEnum"), JavaType.newClass("", "AnotherClass"))), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainTypes())));
        assertEquals(new java.util.HashSet<>(Arrays.asList(JavaType.newClass("", "ClassTest"), JavaType.newClass("net", "AType"))), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getTestTypes())));
    }

    @Test
    public void should_extract_type_defs_from_context_annotation() throws Exception
    {
        @Context(mainCls = "org.example:SomeClass,enum SomeEnum; AnotherClass", testCls = "class ClassTest; net: AType")
        class AnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), null);

        // then
        assertEquals(new java.util.HashSet<>(Arrays.asList(JavaType.newClass("org.example", "SomeClass"), JavaType.newEnum("org.example", "SomeEnum"), JavaType.newClass("", "AnotherClass"))), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getMainTypes())));
        assertEquals(new java.util.HashSet<>(Arrays.asList(JavaType.newClass("", "ClassTest"), JavaType.newClass("net", "AType"))), new java.util.HashSet<>((config.getProject(DEFAULT_PROJECT_NAME).getTestTypes())));
    }

    private static final String DEFAULT_PROJECT_NAME = Defaults.PROJECT_NAME;

    private static AnnotatedElement annotatedElement(final Class< ? > clazz)
    {
        return new AnnotatedElement()
        {
            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return clazz.getAnnotation(annotationClass);
            }
        };
    }

    private static class ElementWithoutAnnotation implements AnnotatedElement
    {
        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
        {
            return null;
        }

        @Override
        public String toString()
        {
            return getClass().getSimpleName();
        }
    }

    @Test
    public void should_load_test_class_template_from_preferences()
    {
        // given
        @Preferences(testClassNameTemplate = "{srcFile}Mest")
        class AnnotationHolder
        {
        }

        @Project(mainSrcFolder = "sources")
        class DefaultAnnotationHolder
        {
        }

        // when
        WorkspaceConfiguration config = configExtractor.extractFrom(annotatedElement(AnnotationHolder.class), annotatedElement(DefaultAnnotationHolder.class));

        // then
        assertEquals(config.getPreferencesConfig().getTestClassNameTemplate(), "{srcFile}Mest");
    }
}
