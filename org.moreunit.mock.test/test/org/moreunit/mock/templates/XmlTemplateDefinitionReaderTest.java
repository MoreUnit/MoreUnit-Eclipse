package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.jupiter.api.Test;

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
}
