package org.moreunit.mock.templates;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XmlTemplateDefinitionReader
{

    public MockingTemplates read(InputStream is) throws TemplateException
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance(MockingTemplates.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (MockingTemplates) unmarshaller.unmarshal(is);
        }
        catch (JAXBException e)
        {
            throw new TemplateException("Could not read XML definition", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                // TODO loghandler
                e.printStackTrace();
            }
        }
    }
}
