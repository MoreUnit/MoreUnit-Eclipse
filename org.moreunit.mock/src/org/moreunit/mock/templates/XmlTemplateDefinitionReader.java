package org.moreunit.mock.templates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.moreunit.core.util.IOUtils;
import org.moreunit.mock.model.MockingTemplates;
import org.xml.sax.SAXException;

public class XmlTemplateDefinitionReader
{
    private Unmarshaller unmarshaller;

    public XmlTemplateDefinitionReader(URL xsd)
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance(MockingTemplates.class);
            unmarshaller = jc.createUnmarshaller();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Could not create unmarshaller", e);
        }

        try
        {
            SchemaFactory schemaFactoy = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            unmarshaller.setSchema(schemaFactoy.newSchema(xsd));
        }
        catch (SAXException e)
        {
            // ignored, XML won't be validated
        }
    }

    public MockingTemplates read(URL url) throws MockingTemplateException
    {
        InputStream is = null;
        try
        {
            is = url.openStream();
            return read(is);
        }
        catch (IOException e)
        {
            throw new MockingTemplateException("Could not open XML definition URL", e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    public MockingTemplates read(InputStream is) throws MockingTemplateException
    {
        try
        {
            return (MockingTemplates) unmarshaller.unmarshal(is);
        }
        catch (JAXBException e)
        {
            throw new MockingTemplateException("Could not read XML definition", e);
        }
    }
}
