package org.moreunit.core.preferences;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PropertyPageFactoryTest
{
    private boolean pageCreated;
    private String languageId;
    private String description;

    private PropertyPageFactory factory = new PropertyPageFactory()
    {
        protected GenericPropertyPage createPage(String langId, String desc)
        {
            pageCreated = true;
            languageId = langId;
            description = desc;
            return null;
        }
    };

    @Test
    public void should_not_create_page_without_data() throws Exception
    {
        // given
        factory.setInitializationData(null, null, null);

        // when
        factory.create();

        // then
        assertThat(pageCreated).isFalse();
    }

    @Test
    public void should_not_create_page_with_nonstring_data() throws Exception
    {
        // given
        factory.setInitializationData(null, null, 5);

        // when
        factory.create();

        // then
        assertThat(pageCreated).isFalse();
    }

    @Test
    public void should_create_page_without_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "py");

        // when
        factory.create();

        // then
        assertThat(pageCreated).isTrue();
        assertThat(languageId).isEqualTo("py");
        assertThat(description).isNull();
    }

    @Test
    public void should_create_page_with_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "rb:Page for Ruby");

        // when
        factory.create();

        // then
        assertThat(pageCreated).isTrue();
        assertThat(languageId).isEqualTo("rb");
        assertThat(description).isEqualTo("Page for Ruby");
    }

    @Test
    public void should_ignore_empty_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "clj:");

        // when
        factory.create();

        // then
        assertThat(pageCreated).isTrue();
        assertThat(languageId).isEqualTo("clj");
        assertThat(description).isNull();
    }
}
