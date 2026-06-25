package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class MockingTemplatesTest
{
    @Test
    public void should_return_empty_categories_when_none_provided()
    {
        MockingTemplates templates = new MockingTemplates(null, new ArrayList<>());
        Collection<Category> categories = templates.categories();
        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    @Test
    public void should_return_provided_categories()
    {
        List<Category> cats = Arrays.asList(new Category("mock", "Mocking"), new Category("stub", "Stubbing"));
        MockingTemplates templates = new MockingTemplates(cats, new ArrayList<>());
        assertEquals(2, templates.categories().size());
    }

    @Test
    public void should_return_empty_iterator_when_no_templates()
    {
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), null);
        Iterator<MockingTemplate> it = templates.iterator();
        assertNotNull(it);
        assertFalse(it.hasNext());
    }

    @Test
    public void should_iterate_over_templates()
    {
        List<MockingTemplate> tmplList = Arrays.asList(
                new MockingTemplate("id1"),
                new MockingTemplate("id2"),
                new MockingTemplate("id3"));
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), tmplList);

        int count = 0;
        for (MockingTemplate t : templates)
        {
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void should_find_template_by_id()
    {
        MockingTemplate t1 = new MockingTemplate("id1");
        MockingTemplate t2 = new MockingTemplate("id2");
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), Arrays.asList(t1, t2));

        assertEquals(t1, templates.findTemplate("id1"));
        assertEquals(t2, templates.findTemplate("id2"));
    }

    @Test
    public void should_return_null_when_template_not_found()
    {
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), new ArrayList<>());
        assertNull(templates.findTemplate("unknown"));
    }

    @Test
    public void should_return_null_when_searching_in_null_templates_list()
    {
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), null);
        assertNull(templates.findTemplate("any"));
    }

    @Test
    public void should_handle_multiple_templates_with_same_id_returns_first()
    {
        MockingTemplate t1 = new MockingTemplate("dup");
        MockingTemplate t2 = new MockingTemplate("dup");
        MockingTemplates templates = new MockingTemplates(new ArrayList<>(), Arrays.asList(t1, t2));

        assertEquals(t1, templates.findTemplate("dup"));
    }
}
