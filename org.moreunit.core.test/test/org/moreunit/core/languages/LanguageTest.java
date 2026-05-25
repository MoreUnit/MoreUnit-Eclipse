package org.moreunit.core.languages;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LanguageTest
{
    @Test
    public void should_reject_null_extension() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new Language(null));
    }

    @Test
    public void should_reject_empty_extension() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new Language(""));
    }

    @Test
    public void should_reject_blank_extension() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new Language("   "));
    }

    @Test
    public void should_set_extension_and_label()
    {
        Language lang = new Language("ext", "Label");
        org.junit.jupiter.api.Assertions.assertEquals("ext", lang.getExtension());
        org.junit.jupiter.api.Assertions.assertEquals("Label", lang.getLabel());
        org.junit.jupiter.api.Assertions.assertEquals("Label[.ext]", lang.toString());
    }

    @Test
    public void should_use_extension_as_label_if_label_is_null()
    {
        Language lang = new Language("ext", null);
        org.junit.jupiter.api.Assertions.assertEquals("ext", lang.getExtension());
        org.junit.jupiter.api.Assertions.assertEquals("ext", lang.getLabel());
    }

    @Test
    public void should_use_extension_as_label_if_label_is_null_and_trim_extension()
    {
        Language lang = new Language("  ext  ", null);
        org.junit.jupiter.api.Assertions.assertEquals("ext", lang.getExtension());
        org.junit.jupiter.api.Assertions.assertEquals("  ext  ", lang.getLabel());
    }

    @Test
    public void should_trim_extension()
    {
        Language lang = new Language("  ext  ");
        org.junit.jupiter.api.Assertions.assertEquals("ext", lang.getExtension());
        org.junit.jupiter.api.Assertions.assertEquals("  ext  ", lang.getLabel());
    }

    @Test
    public void test_equals_and_hashCode()
    {
        Language lang1 = new Language("ext", "Label");
        Language lang2 = new Language("ext", "Label2"); // equals only checks extension
        Language lang3 = new Language("ext3", "Label");

        org.junit.jupiter.api.Assertions.assertEquals(lang1, lang1);
        org.junit.jupiter.api.Assertions.assertEquals(lang1, lang2);
        org.junit.jupiter.api.Assertions.assertNotEquals(lang1, lang3);
        org.junit.jupiter.api.Assertions.assertNotEquals(lang1, null);
        org.junit.jupiter.api.Assertions.assertNotEquals(lang1, "ext");

        org.junit.jupiter.api.Assertions.assertEquals(lang1.hashCode(), lang2.hashCode());
    }

    @Test
    public void test_compareTo()
    {
        Language lang1 = new Language("extA", "Label A");
        Language lang2 = new Language("extB", "Label B");

        org.junit.jupiter.api.Assertions.assertTrue(lang1.compareTo(lang2) < 0);
        org.junit.jupiter.api.Assertions.assertTrue(lang2.compareTo(lang1) > 0);
        org.junit.jupiter.api.Assertions.assertEquals(0, lang1.compareTo(lang1));
    }
}
