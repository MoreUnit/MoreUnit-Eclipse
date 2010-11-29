package org.moreunit.mock.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
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
    public void shouldReturnNullWhenIdIsUnknwon() throws Exception
    {
        assertNull(templateStore.get("unkown template ID"));
    }

    @Test
    public void shouldReturnTemplateWhenIdIsKnown() throws Exception
    {
        MockingTemplate template = new MockingTemplate("templateID");
        templateStore.store(template.id(), template);
        assertEquals(template, templateStore.get("templateID"));
    }

    @Test
    public void shouldNotContainTemplateAnymoreWhenCleared() throws Exception
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
    public void shouldKeepExistingTemplatesWhenAddingNewOnes()
    {
        templateStore.store(new MockingTemplates(new MockingTemplate("templateA"), new MockingTemplate("templateB")));
        assertNotNull(templateStore.get("template1"));
        assertNotNull(templateStore.get("templateA"));
        assertNotNull(templateStore.get("templateB"));
    }
}
