package org.moreunit.mock;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.moreunit.mock.MoreUnitMockPlugin.TEMPLATE_DIRECTORY;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;
import org.moreunit.mock.templates.MockingTemplateException;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.XmlTemplateDefinitionReader;

@RunWith(MockitoJUnitRunner.class)
public class MoreUnitMockPluginTest
{
    @Mock
    private Logger logger;
    @Mock
    private PluginResourceLoader resourceLoader;
    @Mock
    private XmlTemplateDefinitionReader templateDefinitionReader;
    @Mock
    private MockingTemplateStore templateStore;
    private MoreUnitMockPlugin plugin;

    @Before
    public void setUp() throws Exception
    {
        plugin = new MoreUnitMockPlugin();
        plugin.initDependencies(null, logger, resourceLoader, templateDefinitionReader, templateStore);
    }

    @Test
    public void should_log_error_when_template_definition_resource_is_not_found() throws Exception
    {
        // given
        when(resourceLoader.getResourceAsStream(anyString())).thenReturn(null);

        // when
        plugin.loadDefaultMockingTemplates();

        // then
        verify(logger, atLeastOnce()).error(anyString());
    }

    @Test
    public void should_log_error_when_template_definition_is_invalid() throws Exception
    {
        // given
        InputStream stream = new MockInputStream("<invalidDefinition />");
        when(resourceLoader.getResourceAsStream(TEMPLATE_DIRECTORY + "mockito.xml")).thenReturn(stream);

        MockingTemplateException testException = new MockingTemplateException("test excepstion");
        when(templateDefinitionReader.read(stream)).thenThrow(testException);

        // when
        plugin.loadDefaultMockingTemplates();

        // then
        verify(logger).error(anyString(), eq(testException));
    }

    @Test
    public void should_store_templates() throws Exception
    {
        // given
        InputStream stream = new MockInputStream("<validDefinition />");
        when(resourceLoader.getResourceAsStream(TEMPLATE_DIRECTORY + "mockito.xml")).thenReturn(stream);

        MockingTemplates expectedTemplates = new MockingTemplates(new ArrayList<Category>(), asList(new MockingTemplate("template")));
        when(templateDefinitionReader.read(stream)).thenReturn(expectedTemplates);

        // when
        plugin.loadDefaultMockingTemplates();

        // then
        verify(templateStore).store(expectedTemplates);
    }

    private static class MockInputStream extends InputStream
    {
        String string;
        int index = 0;

        MockInputStream(String string)
        {
            this.string = string;
        }

        @Override
        public int read() throws IOException
        {
            return index < string.length() ? (int) string.charAt(index++) : - 1;
        }
    }
}
