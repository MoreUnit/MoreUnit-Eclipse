package org.moreunit.mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.moreunit.mock.MoreUnitMockPlugin.TEMPLATE_DIRECTORY;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.MockingTemplates;
import org.moreunit.mock.templates.TemplateException;
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
        plugin.initDependencies(logger, resourceLoader, templateDefinitionReader, templateStore);
    }

    @Test
    public void shouldThrowExceptionWhenTemplateDefinitionResourceIsNotFound() throws Exception
    {
        when(resourceLoader.getResourceAsStream(anyString())).thenReturn(null);

        plugin.loadDefaultMockingTemplates();

        verify(logger).error(anyString());
    }

    @Test
    public void shouldThrowExceptionWhenTemplateDefinitionIsInvalid() throws Exception
    {
        InputStream stream = new MockInputStream("<invalidDefinition />");
        when(resourceLoader.getResourceAsStream(TEMPLATE_DIRECTORY + "mockitoWithAnnotationsAndJUnitRunner.xml")).thenReturn(stream);
        TemplateException testException = new TemplateException("test excepstion");
        when(templateDefinitionReader.read(stream)).thenThrow(testException);

        plugin.loadDefaultMockingTemplates();

        verify(logger).error(anyString(), eq(testException));
    }

    @Test
    public void shouldStoreTemplates() throws Exception
    {
        InputStream stream = new MockInputStream("<validDefinition />");
        when(resourceLoader.getResourceAsStream(TEMPLATE_DIRECTORY + "mockitoWithAnnotationsAndJUnitRunner.xml")).thenReturn(stream);
        MockingTemplates expectedTemplates = new MockingTemplates(new MockingTemplate("template"));
        when(templateDefinitionReader.read(stream)).thenReturn(expectedTemplates);

        plugin.loadDefaultMockingTemplates();

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
