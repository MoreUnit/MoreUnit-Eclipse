package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.moreunit.mock.templates.MockingTemplateLoader.TEMPLATE_DIRECTORY;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.PluginResourceLoader;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

@RunWith(MockitoJUnitRunner.class)
public class MockingTemplateLoaderTest
{
    @Mock
    private Logger logger;
    @Mock
    private PluginResourceLoader resourceLoader;
    @Mock
    private XmlTemplateDefinitionReader templateDefinitionReader;
    @Mock
    private MockingTemplateStore templateStore;
    @InjectMocks
    private MockingTemplateLoader loader;

    @Test
    public void should_log_error_when_template_definition_resource_is_not_found() throws Exception
    {
        // given
        when(resourceLoader.findBundleResources(anyString(), anyString())).thenReturn(Collections.<URL> emptyList());
        when(resourceLoader.findWorkspaceStateResources(anyString(), anyString())).thenReturn(Collections.<URL> emptyList());

        // when
        loader.loadTemplates();

        // then
        verify(logger, atLeastOnce()).error(anyString());
    }

    @Test
    public void should_log_error_when_template_definition_is_invalid() throws Exception
    {
        // given
        URL invalidUrl = new URL("file:///invalid.url");
        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(singleton(invalidUrl));
        when(resourceLoader.findWorkspaceStateResources(anyString(), anyString())).thenReturn(Collections.<URL> emptyList());

        MockingTemplateException testException = new MockingTemplateException("test exception");
        when(templateDefinitionReader.read(invalidUrl)).thenThrow(testException);

        // when
        loader.loadTemplates();

        // then
        verify(logger).error(anyString(), eq(testException));
    }

    @Test
    public void should_store_templates() throws Exception
    {
        // given
        URL validUrl = new URL("file:///valid.url");

        when(resourceLoader.findBundleResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(singleton(validUrl));
        when(resourceLoader.findWorkspaceStateResources(eq(TEMPLATE_DIRECTORY), anyString())).thenReturn(Collections.<URL> emptyList());

        MockingTemplates expectedTemplates = new MockingTemplates(new ArrayList<Category>(), asList(new MockingTemplate("template")));
        when(templateDefinitionReader.read(validUrl)).thenReturn(expectedTemplates);

        // when
        loader.loadTemplates();

        // then
        verify(templateStore).store(expectedTemplates);
    }
}
