package org.moreunit.mock.templates;

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class LoadingResult
{
    private Map<URL, String> invalidTemplates = new TreeMap<URL, String>(new Comparator<URL>()
    {
        @Override
        public int compare(URL url1, URL url2)
        {
            return url1.toString().compareTo(url2.toString());
        }
    });

    public boolean invalidTemplatesFound()
    {
        return ! invalidTemplates.isEmpty();
    }

    public Map<URL, String> invalidTemplates()
    {
        return invalidTemplates;
    }

    public void addInvalidTemplate(URL template, Exception reason)
    {
        invalidTemplates.put(template, reason.toString());
    }

    public void addInvalidTemplate(URL template, TemplateAlreadyDefinedException reason)
    {
        invalidTemplates.put(template, "A template is already defined with this ID");
    }
}
