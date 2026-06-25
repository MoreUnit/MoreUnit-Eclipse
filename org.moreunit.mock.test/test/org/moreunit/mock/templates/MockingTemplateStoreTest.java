package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateStoreTest
{
    private MockingTemplateStore templateStore;

    private Category category1;
    private Category category2;
    private MockingTemplate template1;

    @BeforeEach
    public void setUp() throws Exception
    {
        templateStore = new MockingTemplateStore();

        category1 = new Category("category1", "Category 1");
        category2 = new Category("category2", "Category 2");

        template1 = new MockingTemplate("template1", "category1");

        templateStore.store(new MockingTemplates(asList(category1), asList(template1)));
    }

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertNull(templateStore.get("unkown template ID"));
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("templateID", "category1");

        // when
        templateStore.store(new MockingTemplates(asList(category1), asList(template)));

        // then
        assertEquals(templateStore.get("templateID"), template);
    }

    @Test
    public void should_not_contain_template_anymore_when_cleared() throws Exception
    {
        // given
        MockingTemplate template2 = new MockingTemplate("template2", "category1");

        // when
        templateStore.store(new MockingTemplates(asList(category1), asList(template2)));

        // then
        assertEquals(templateStore.get("template2"), template2);
        assertEquals(templateStore.get("template1"), new MockingTemplate("template1"));

        // when
        templateStore.clear();

        // then
        assertNull(templateStore.get("template1"));
        assertNull(templateStore.get("template2"));
    }

    @Test
    public void should_keep_existing_templates_when_adding_new_ones() throws Exception
    {
        // when
        templateStore.store(new MockingTemplates(new ArrayList<>(), asList(new MockingTemplate("templateA", "category1"), new MockingTemplate("templateB", "category1"))));

        // then
        assertNotNull(templateStore.get("template1"));
        assertNotNull(templateStore.get("templateA"));
        assertNotNull(templateStore.get("templateB"));
    }

    @Test
    public void should_store_categories() throws Exception
    {
        // when
        templateStore.store(new MockingTemplates(asList(category2), new ArrayList<>()));

        // then
        assertEquals(new HashSet<>(templateStore.getCategories()), Set.of(category1, category2));
        assertEquals(templateStore.getCategory("category1"), category1);
        assertEquals(templateStore.getCategory("category2"), category2);
    }

    @Test
    public void should_retrieve_templates_by_category() throws Exception
    {
        // given
        MockingTemplate template2 = new MockingTemplate("template2", "category2");
        MockingTemplate template3 = new MockingTemplate("template3", "category1");

        // when
        templateStore.store(new MockingTemplates(asList(category2), asList(template2, template3)));

        // then
        assertEquals(templateStore.getTemplates(category1), Set.of(template1, template3));
        assertEquals(templateStore.getTemplates(category2), Set.of(template2));
    }

    @Test
    public void should_not_override_categories() throws Exception
    {
        // given
        Category category1bis = new Category("category1", "New name for category 1");

        // when
        templateStore.store(new MockingTemplates(asList(category1bis), new ArrayList<>()));

        // then
        assertEquals(templateStore.getCategory("category1").name(), "Category 1");
    }

    @Test
    public void should_reject_template_which_already_exists() throws Exception
    {
        MockingTemplate template1bis = new MockingTemplate("template1", "category1");

        {
            TemplateAlreadyDefinedException e = assertThrows(TemplateAlreadyDefinedException.class, () -> templateStore.store(new MockingTemplates(new ArrayList<>(), asList(template1bis))));
            assertEquals(((TemplateAlreadyDefinedException) e).getTemplateId(), template1bis.id());
        }
    }
}
