package org.moreunit.mock.templates;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

@Singleton
public class MockingTemplateStore
{
    private final Map<String, MockingTemplate> templates = new HashMap<String, MockingTemplate>();

    public void store(MockingTemplates mockingTemplates)
    {
        for (MockingTemplate template : mockingTemplates)
        {
            store(template.id(), template);
        }
    }

    public void store(String defaultTemplateId, MockingTemplate defaultTemplate)
    {
        templates.put(defaultTemplateId, defaultTemplate);
    }

    public MockingTemplate get(String templateId)
    {
        return templates.get(templateId);
    }

    public void clear()
    {
        templates.clear();
    }

}
