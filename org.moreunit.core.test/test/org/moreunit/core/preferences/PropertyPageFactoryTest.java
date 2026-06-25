package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PropertyPageFactoryTest
{
    private boolean pageCreated;
    private String languageId;
    private String description;

    private PropertyPageFactory factory = new PropertyPageFactory()
    {
        @Override
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
        assertFalse(pageCreated);
    }

    @Test
    public void should_not_create_page_with_nonstring_data() throws Exception
    {
        // given
        factory.setInitializationData(null, null, 5);

        // when
        factory.create();

        // then
        assertFalse(pageCreated);
    }

    @Test
    public void should_create_page_without_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "py");

        // when
        factory.create();

        // then
        assertTrue(pageCreated);
        assertEquals(languageId, "py");
        assertNull(description);
    }

    @Test
    public void should_create_page_with_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "rb:Page for Ruby");

        // when
        factory.create();

        // then
        assertTrue(pageCreated);
        assertEquals(languageId, "rb");
        assertEquals(description, "Page for Ruby");
    }

    @Test
    public void should_ignore_empty_description() throws Exception
    {
        // given
        factory.setInitializationData(null, null, "clj:");

        // when
        factory.create();

        // then
        assertTrue(pageCreated);
        assertEquals(languageId, "clj");
        assertNull(description);
    }
}
