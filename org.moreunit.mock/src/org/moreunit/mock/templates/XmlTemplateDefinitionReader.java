package org.moreunit.mock.templates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.moreunit.core.util.IOUtils;
import org.moreunit.mock.model.MockingTemplates;

import com.google.inject.Singleton;

@Singleton
public class XmlTemplateDefinitionReader
{
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
            JAXBContext jc = JAXBContext.newInstance(MockingTemplates.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (MockingTemplates) unmarshaller.unmarshal(is);
        }
        catch (JAXBException e)
        {
            throw new MockingTemplateException("Could not read XML definition", e);
        }
    }
}
