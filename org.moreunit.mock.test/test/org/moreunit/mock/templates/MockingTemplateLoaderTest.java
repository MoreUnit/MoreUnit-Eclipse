package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.moreunit.mock.templates.MockingTemplateLoader.TEMPLATE_DIRECTORY;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.PluginResourceLoader;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

@RunWith(MockitoJUnitRunner.class)
public class MockingTemplateLoaderTest
{
    @Mock
    Logger logger;
    @Mock
    PluginResourceLoader resourceLoader;
    @Mock
    XmlTemplateDefinitionReader templateDefinitionReader;
    @Mock
    MockingTemplateStore templateStore;
    @InjectMocks
    MockingTemplateLoader loader;

    List<URL> noResources = Collections.<URL> emptyList();
    MockingTemplateException someException = new MockingTemplateException("test exception");
    MockingTemplates someTemplates = someTemplates();

    @Test
    public void should_log_error_when_template_definition_resource_is_not_found() throws Exception
    {
        // given
        when(resourceLoader.findBundleResources(anyString(), anyString())).thenReturn(noResources);
        when(resourceLoader.findWorkspaceStateResources(anyString(), anyString())).thenReturn(noResources);

        // when
        loader.loadTemplates();

        // then
        verify(logger, atLeastOnce()).error(anyString());
    }

    @Test
    public void should_log_error_and_add_to_invalid_templates_when_generic_exception_occurs() throws Exception
    {
        // given
        URL definitionUrl = URI.create("file:/generic.exception.url").toURL();
        RuntimeException genericException = new RuntimeException("generic exception");

        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(singleton(definitionUrl));
        when(resourceLoader.findWorkspaceStateResources(anyString(), anyString())).thenReturn(noResources);

        when(templateDefinitionReader.read(definitionUrl)).thenThrow(genericException);

        // when
        LoadingResult result = loader.loadTemplates();

        // then
        verify(logger).error(anyString(), eq(genericException));
        assertThat(result.invalidTemplatesFound()).isTrue();
        assertThat(result.invalidTemplates()).contains(entry(definitionUrl, genericException.toString()));
    }

    @Test
    public void should_log_error_when_template_definition_is_invalid() throws Exception
    {
        // given
        URL invalidDefinitionUrl = URI.create("file:/invalid.definition.url").toURL();

        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(singleton(invalidDefinitionUrl));
        when(resourceLoader.findWorkspaceStateResources(anyString(), anyString())).thenReturn(noResources);

        when(templateDefinitionReader.read(invalidDefinitionUrl)).thenThrow(someException);

        // when
        loader.loadTemplates();

        // then
        verify(logger).error(anyString(), eq(someException));
    }

    @Test
    public void should_store_templates() throws Exception
    {
        // given
        URL validDefinitionUrl = URI.create("file:/valid.definition.url").toURL();

        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(singleton(validDefinitionUrl));
        when(resourceLoader.findWorkspaceStateResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(noResources);

        when(templateDefinitionReader.read(validDefinitionUrl)).thenReturn(someTemplates);

        // when
        LoadingResult result = loader.loadTemplates();

        // then
        verify(templateStore).store(someTemplates);

        assertThat(result.invalidTemplatesFound()).isFalse();
        assertThat(result.invalidTemplates()).isEmpty();
    }

    @Test
    public void should_return_urls_of_templates_that_could_not_be_loaded_with_a_reason() throws Exception
    {
        // given
        URL validDefinition1Url = URI.create("file:/valid.definition.1.url").toURL();
        URL validDefinition2Url = URI.create("file:/valid.definition.2.url").toURL();
        URL invalidDefinition1Url = URI.create("file:/invalid.definition.1.url").toURL();
        URL invalidDefinition2Url = URI.create("file:/invalid.definition.2.url").toURL();
        URL invalidDefinition3Url = URI.create("file:/invalid.definition.3.url").toURL();

        // some template definitions will be found in the plugin resources and
        // in the workspace resources
        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString()))
                .thenReturn(asList(validDefinition1Url, invalidDefinition2Url));
        when(resourceLoader.findWorkspaceStateResources(eq(TEMPLATE_DIRECTORY), anyString()))
                .thenReturn(asList(invalidDefinition3Url, validDefinition2Url, invalidDefinition1Url));

        when(templateDefinitionReader.read(oneOf(validDefinition1Url, validDefinition2Url)))
                .thenReturn(someTemplates);
        when(templateDefinitionReader.read(oneOf(invalidDefinition1Url, invalidDefinition2Url, invalidDefinition3Url)))
                .thenThrow(someException);

        // when
        LoadingResult result = loader.loadTemplates();

        // then
        assertThat(result.invalidTemplatesFound()).isTrue();
        assertThat(result.invalidTemplates()).hasSize(3)
                .contains(entry(invalidDefinition1Url, someException.toString()),
                          entry(invalidDefinition2Url, someException.toString()),
                          entry(invalidDefinition3Url, someException.toString()));
    }

    @Test
    public void should_return_urls_of_templates_that_are_already_defined_with_a_reason() throws Exception
    {
        // given
        URL existingDefinition1Url = URI.create("file:/existing.definition.1.url").toURL();
        URL existingDefinition2Url = URI.create("file:/existing.definition.2.url").toURL();
        workspaceResourcesContainTemplateDefinitions(existingDefinition1Url, existingDefinition2Url);

        doThrow(new TemplateAlreadyDefinedException("templateId1"))
                /* then */.doThrow(new TemplateAlreadyDefinedException("templateId2"))
                .when(templateStore).store(any(MockingTemplates.class));

        // when
        LoadingResult result = loader.loadTemplates();

        // then
        assertThat(result.invalidTemplatesFound()).isTrue();
        assertThat(result.invalidTemplates()).hasSize(2)
                .contains(entry(existingDefinition1Url, "A template is already defined with this ID"),
                          entry(existingDefinition2Url, "A template is already defined with this ID"));
    }

    private MockingTemplates someTemplates()
    {
        return new MockingTemplates(new ArrayList<Category>(), asList(new MockingTemplate("template")));
    }

    private void workspaceResourcesContainTemplateDefinitions(URL... definitionUrls) throws MockingTemplateException
    {
        // irrelevant
        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(noResources);

        when(resourceLoader.findWorkspaceStateResources(eq(TEMPLATE_DIRECTORY), anyString()))
                .thenReturn(asList(definitionUrls));

        for (URL url : definitionUrls)
        {
            when(templateDefinitionReader.read(url)).thenReturn(someTemplates());
        }
    }

    @SafeVarargs
    private <T> T oneOf(T... args)
    {
        return argThat(new IsOneOf<T>(args));
    }

    private class IsOneOf<T> implements ArgumentMatcher<T>
    {
        private final List<T> wanted;

        @SafeVarargs
        public IsOneOf(T... wanted)
        {
            this.wanted = asList(wanted);
        }

        @Override
        public boolean matches(Object arg)
        {
            return wanted.contains(arg);
        }

    }

}
