package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class XmlTemplateDefinitionReaderTest
{
    @Test
    public void should_read_valid_xml() throws Exception
    {
        URL xsd = getClass().getResource("/templates/mocking-templates.xsd");
        assertNotNull(xsd, "XSD resource not found");

        XmlTemplateDefinitionReader reader = new XmlTemplateDefinitionReader(xsd);

        String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<mocking-templates version=\"1.0\" xmlns=\"http://moreunit.org/mock/mocking-templates\">"
            + "<mocking-template id=\"test.id\" category=\"test.cat\" name=\"Test\">"
            + "<code-template id=\"body\" part=\"test-class-fields\">"
            + "<pattern><![CDATA[test]]></pattern>"
            + "</code-template>"
            + "</mocking-template>"
            + "</mocking-templates>";
        InputStream is = new ByteArrayInputStream(validXml.getBytes());

        assertNotNull(reader.read(is));
    }

    @Test
    public void should_throw_on_invalid_xml()
    {
        URL xsd = getClass().getResource("/templates/mocking-templates.xsd");
        assertNotNull(xsd);

        XmlTemplateDefinitionReader reader = new XmlTemplateDefinitionReader(xsd);

        String invalidXml = "not xml";
        InputStream is = new ByteArrayInputStream(invalidXml.getBytes());

        assertThrows(MockingTemplateException.class, () -> reader.read(is));
    }

    @Test
    public void should_read_xml_from_url() throws Exception
    {
        URL xsd = getClass().getResource("/templates/mocking-templates.xsd");
        assertNotNull(xsd);

        XmlTemplateDefinitionReader reader = new XmlTemplateDefinitionReader(xsd);

        String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<mocking-templates version=\"1.0\" xmlns=\"http://moreunit.org/mock/mocking-templates\">"
            + "<category id=\"cat.id\" name=\"Category\"/>"
            + "<mocking-template id=\"test.id\" category=\"cat.id\" name=\"Test\">"
            + "<code-template id=\"body\" part=\"test-class-fields\">"
            + "<pattern><![CDATA[test]]></pattern>"
            + "</code-template>"
            + "</mocking-template>"
            + "</mocking-templates>";
        URL url = writeTempFile(validXml).toURI().toURL();

        MockingTemplates templates = reader.read(url);

        assertNotNull(templates);
        assertEquals(1, templates.categories().size());
        MockingTemplate template = templates.iterator().next();
        assertEquals("test.id", template.id());
        assertEquals("cat.id", template.categoryId());
    }

    @Test
    public void should_throw_when_url_cannot_be_opened() throws Exception
    {
        URL xsd = getClass().getResource("/templates/mocking-templates.xsd");
        assertNotNull(xsd);

        XmlTemplateDefinitionReader reader = new XmlTemplateDefinitionReader(xsd);

        URL missingUrl = new File("/nonexistent/moreunit/does-not-exist.xml").toURI().toURL();

        MockingTemplateException exception = assertThrows(MockingTemplateException.class, () -> reader.read(missingUrl));
        assertEquals("Could not open XML definition URL", exception.getMessage());
    }

    @Test
    public void should_still_work_without_validation_when_xsd_is_not_a_schema() throws Exception
    {
        // a URL that does not point to a schema: the reader must ignore the error
        URL notASchema = writeTempFile("this file is not a schema").toURI().toURL();

        XmlTemplateDefinitionReader reader = new XmlTemplateDefinitionReader(notASchema);

        String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<mocking-templates version=\"1.0\" xmlns=\"http://moreunit.org/mock/mocking-templates\">"
            + "<mocking-template id=\"test.id\" category=\"cat.id\" name=\"Test\">"
            + "<code-template id=\"body\" part=\"test-class-fields\">"
            + "<pattern><![CDATA[test]]></pattern>"
            + "</code-template>"
            + "</mocking-template>"
            + "</mocking-templates>";

        MockingTemplates templates = reader.read(new ByteArrayInputStream(validXml.getBytes()));
        assertNotNull(templates);
        MockingTemplate template = templates.iterator().next();
        assertEquals("test.id", template.id());
    }

    private File writeTempFile(String content) throws IOException
    {
        File file = File.createTempFile("moreunit-test", ".xml");
        file.deleteOnExit();
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
        return file;
    }
}
