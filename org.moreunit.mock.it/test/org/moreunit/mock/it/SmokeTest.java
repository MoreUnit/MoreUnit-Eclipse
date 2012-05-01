package org.moreunit.mock.it;

import static com.google.common.collect.Collections2.transform;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.moreunit.mock.InjectionRule;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class SmokeTest
{
    @Rule
    public InjectionRule injectionRule = new InjectionRule();

    @Inject
    private MockingTemplateStore templateStore;

    @Test
    public void should_have_loaded_templates() throws Exception
    {
        assertThat(templateStore).isNotNull();

        assertThat(transform(templateStore.getCategories(), new CategoryIdAccessor()))
                .contains("org.moreunit.mock.easymock", "org.moreunit.mock.mockito");

        Collection<MockingTemplate> easymockTemplates = templateStore.getTemplates(templateStore.getCategory("org.moreunit.mock.easymock"));

        assertThat(transform(easymockTemplates, new TemplateIdAccessor()))
                .contains("org.moreunit.mock.easymockDefault");

        Collection<MockingTemplate> mockitoTemplates = templateStore.getTemplates(templateStore.getCategory("org.moreunit.mock.mockito"));

        assertThat(transform(mockitoTemplates, new TemplateIdAccessor()))
                .contains("org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }

    private static class CategoryIdAccessor implements Function<Category, String>
    {
        public String apply(Category category)
        {
            return category.id();
        }
    }

    private static class TemplateIdAccessor implements Function<MockingTemplate, String>
    {
        public String apply(MockingTemplate template)
        {
            return template.id();
        }
    }
}
