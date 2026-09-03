package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class LoadingResultTest
{
    @Test
    public void should_have_no_invalid_templates_initially()
    {
        final LoadingResult result = new LoadingResult();
        assertFalse(result.invalidTemplatesFound());
        assertTrue(result.invalidTemplates().isEmpty());
    }

    @Test
    public void should_detect_invalid_templates_after_adding_one()
    {
        final LoadingResult result = new LoadingResult();
        result.addInvalidTemplate(url("http://example.com/bad.xml"), new RuntimeException("error"));

        assertTrue(result.invalidTemplatesFound());
        assertEquals(1, result.invalidTemplates().size());
    }

    @Test
    public void should_store_exception_message_for_generic_exception()
    {
        final LoadingResult result = new LoadingResult();
        final RuntimeException ex = new RuntimeException("something went wrong");
        result.addInvalidTemplate(url("http://example.com/a.xml"), ex);

        final String message = result.invalidTemplates().get(url("http://example.com/a.xml"));
        assertNotNull(message);
        assertTrue(message.contains("something went wrong"));
    }

    @Test
    public void should_store_fixed_message_for_template_already_defined_exception()
    {
        final LoadingResult result = new LoadingResult();
        final TemplateAlreadyDefinedException ex = new TemplateAlreadyDefinedException("myTemplate");
        result.addInvalidTemplate(url("http://example.com/b.xml"), ex);

        final String message = result.invalidTemplates().get(url("http://example.com/b.xml"));
        assertEquals("A template is already defined with this ID", message);
    }

    @Test
    public void should_store_multiple_invalid_templates()
    {
        final LoadingResult result = new LoadingResult();
        result.addInvalidTemplate(url("http://example.com/a.xml"), new RuntimeException("err1"));
        result.addInvalidTemplate(url("http://example.com/b.xml"), new RuntimeException("err2"));

        assertEquals(2, result.invalidTemplates().size());
    }

    @Test
    public void should_sort_templates_by_url()
    {
        final LoadingResult result = new LoadingResult();
        result.addInvalidTemplate(url("http://z.com/last.xml"), new RuntimeException("z"));
        result.addInvalidTemplate(url("http://a.com/first.xml"), new RuntimeException("a"));
        result.addInvalidTemplate(url("http://m.com/middle.xml"), new RuntimeException("m"));

        final String firstKey = result.invalidTemplates().keySet().iterator().next().toString();
        assertTrue(firstKey.contains("a.com"));
    }

    @Test
    public void should_return_same_map_instance()
    {
        final LoadingResult result = new LoadingResult();
        final Map<URL, String> map1 = result.invalidTemplates();
        final Map<URL, String> map2 = result.invalidTemplates();
        assertTrue(map1 == map2);
    }

    private static URL url(String urlString)
    {
        try
        {
            return URI.create(urlString).toURL();
        }
        catch (MalformedURLException | IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
    }
}
