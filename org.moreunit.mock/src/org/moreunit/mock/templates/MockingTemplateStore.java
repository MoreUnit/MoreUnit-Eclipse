package org.moreunit.mock.templates;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.moreunit.core.config.Service;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateStore implements Service
{
    private final Map<String, Category> categories = new HashMap<>();
    private final Map<String, MockingTemplate> templates = new HashMap<>();
    private final Map<String, Set<MockingTemplate>> templatesByCategory = new HashMap<>();

    public void store(MockingTemplates mockingTemplates) throws TemplateAlreadyDefinedException
    {
        for (Category category : mockingTemplates.categories())
        {
            if(! categories.containsKey(category.id()))
            {
                categories.put(category.id(), category);
            }
        }

        for (MockingTemplate template : mockingTemplates)
        {
            store(template);
        }
    }

    private void store(MockingTemplate template) throws TemplateAlreadyDefinedException
    {
        if(templates.containsKey(template.id()))
        {
            throw new TemplateAlreadyDefinedException(template.id());
        }

        Set<MockingTemplate> categoryTemplates = templatesByCategory.get(template.categoryId());
        if(categoryTemplates == null)
        {
            categoryTemplates = new HashSet<>();
            templatesByCategory.put(template.categoryId(), categoryTemplates);
        }
        categoryTemplates.add(template);
        templates.put(template.id(), template);
    }

    public MockingTemplate get(String templateId)
    {
        return templates.get(templateId);
    }

    public void clear()
    {
        categories.clear();
        templates.clear();
        templatesByCategory.clear();
    }

    public Collection<Category> getCategories()
    {
        return categories.values();
    }

    public Collection<MockingTemplate> getTemplates(Category category)
    {
        return templatesByCategory.get(category.id());
    }

    public Category getCategory(String categoryId)
    {
        return categories.get(categoryId);
    }

    @Override
    public void start()
    {
        // nothing to do
    }

    @Override
    public void stop()
    {
        clear();
    }
}
