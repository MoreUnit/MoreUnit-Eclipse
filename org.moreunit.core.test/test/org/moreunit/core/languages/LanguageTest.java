package org.moreunit.core.languages;

import org.junit.Test;

public class LanguageTest
{
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_null_extension() throws Exception
    {
        new Language(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_empty_extension() throws Exception
    {
        new Language("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_blank_extension() throws Exception
    {
        new Language("   ");
    }

    @Test
    public void should_set_extension_and_label()
    {
        Language lang = new Language("ext", "Label");
        org.junit.Assert.assertEquals("ext", lang.getExtension());
        org.junit.Assert.assertEquals("Label", lang.getLabel());
        org.junit.Assert.assertEquals("Label[.ext]", lang.toString());
    }

    @Test
    public void should_use_extension_as_label_if_label_is_null()
    {
        Language lang = new Language("ext", null);
        org.junit.Assert.assertEquals("ext", lang.getExtension());
        org.junit.Assert.assertEquals("ext", lang.getLabel());
    }

    @Test
    public void should_trim_extension()
    {
        Language lang = new Language("  ext  ");
        org.junit.Assert.assertEquals("ext", lang.getExtension());
        org.junit.Assert.assertEquals("ext", lang.getLabel());
    }

    @Test
    public void test_equals_and_hashCode()
    {
        Language lang1 = new Language("ext", "Label");
        Language lang2 = new Language("ext", "Label2"); // equals only checks extension
        Language lang3 = new Language("ext3", "Label");

        org.junit.Assert.assertEquals(lang1, lang1);
        org.junit.Assert.assertEquals(lang1, lang2);
        org.junit.Assert.assertNotEquals(lang1, lang3);
        org.junit.Assert.assertNotEquals(lang1, null);
        org.junit.Assert.assertNotEquals(lang1, "ext");

        org.junit.Assert.assertEquals(lang1.hashCode(), lang2.hashCode());
    }

    @Test
    public void test_compareTo()
    {
        Language lang1 = new Language("extA", "Label A");
        Language lang2 = new Language("extB", "Label B");

        org.junit.Assert.assertTrue(lang1.compareTo(lang2) < 0);
        org.junit.Assert.assertTrue(lang2.compareTo(lang1) > 0);
        org.junit.Assert.assertEquals(0, lang1.compareTo(lang1));
    }
}
