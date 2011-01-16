package org.moreunit.mock.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class MockingTemplateStoreTest
{
    private MockingTemplateStore templateStore;

    @Before
    public void setUp() throws Exception
    {
        templateStore = new MockingTemplateStore();

        MockingTemplate template = new MockingTemplate("template1");
        templateStore.store(template.id(), template);
    }

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertNull(templateStore.get("unkown template ID"));
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        MockingTemplate template = new MockingTemplate("templateID");
        templateStore.store(template.id(), template);
        assertEquals(template, templateStore.get("templateID"));
    }

    @Test
    public void should_not_contain_template_anymore_when_cleared() throws Exception
    {
        MockingTemplate template2 = new MockingTemplate("template2");
        templateStore.store(template2.id(), template2);
        assertEquals(template2, templateStore.get("template2"));
        assertEquals(new MockingTemplate("template1"), templateStore.get("template1"));

        templateStore.clear();
        assertNull(templateStore.get("template1"));
        assertNull(templateStore.get("template2"));
    }

    @Test
    public void should_keep_existing_templates_when_adding_new_ones() throws Exception
    {
        templateStore.store(new MockingTemplates(new MockingTemplate("templateA"), new MockingTemplate("templateB")));
        assertNotNull(templateStore.get("template1"));
        assertNotNull(templateStore.get("templateA"));
        assertNotNull(templateStore.get("templateB"));
    }
}
