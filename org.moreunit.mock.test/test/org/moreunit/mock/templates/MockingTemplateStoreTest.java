package org.moreunit.mock.templates;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateStoreTest
{
    private MockingTemplateStore templateStore;

    private Category category1;
    private Category category2;
    private MockingTemplate template1;

    @Before
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
        assertThat(templateStore.get("unkown template ID")).isNull();
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("templateID", "category1");

        // when
        templateStore.store(new MockingTemplates(asList(category1), asList(template)));

        // then
        assertThat(templateStore.get("templateID")).isEqualTo(template);
    }

    @Test
    public void should_not_contain_template_anymore_when_cleared() throws Exception
    {
        // given
        MockingTemplate template2 = new MockingTemplate("template2", "category1");

        // when
        templateStore.store(new MockingTemplates(asList(category1), asList(template2)));

        // then
        assertThat(templateStore.get("template2")).isEqualTo(template2);
        assertThat(templateStore.get("template1")).isEqualTo(new MockingTemplate("template1"));

        // when
        templateStore.clear();

        // then
        assertThat(templateStore.get("template1")).isNull();
        assertThat(templateStore.get("template2")).isNull();
    }

    @Test
    public void should_keep_existing_templates_when_adding_new_ones() throws Exception
    {
        // when
        templateStore.store(new MockingTemplates(new ArrayList<Category>(),
                                                 asList(new MockingTemplate("templateA", "category1"), new MockingTemplate("templateB", "category1"))));

        // then
        assertThat(templateStore.get("template1")).isNotNull();
        assertThat(templateStore.get("templateA")).isNotNull();
        assertThat(templateStore.get("templateB")).isNotNull();
    }

    @Test
    public void should_store_categories() throws Exception
    {
        // when
        templateStore.store(new MockingTemplates(asList(category2), new ArrayList<MockingTemplate>()));

        // then
        assertThat(newHashSet(templateStore.getCategories())).isEqualTo(newHashSet(category1, category2));
        assertThat(templateStore.getCategory("category1")).isEqualTo(category1);
        assertThat(templateStore.getCategory("category2")).isEqualTo(category2);
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
        assertThat(templateStore.getTemplates(category1)).isEqualTo(newHashSet(template1, template3));
        assertThat(templateStore.getTemplates(category2)).isEqualTo(newHashSet(template2));
    }

    @Test
    public void should_not_override_categories() throws Exception
    {
        // given
        Category category1bis = new Category("category1", "New name for category 1");

        // when
        templateStore.store(new MockingTemplates(asList(category1bis), new ArrayList<MockingTemplate>()));

        // then
        assertThat(templateStore.getCategory("category1").name()).isEqualTo("Category 1");
    }

    @Test
    public void should_reject_template_which_already_exists() throws Exception
    {
        MockingTemplate template1bis = new MockingTemplate("template1", "category1");

        try
        {
            // when
            templateStore.store(new MockingTemplates(new ArrayList<Category>(), asList(template1bis)));
            fail("expected TemplateAlreadyDefinedException");
        }
        catch (TemplateAlreadyDefinedException e)
        {
            // then
            assertThat(e.getTemplateId()).isEqualTo(template1bis.id());
        }
    }
}
