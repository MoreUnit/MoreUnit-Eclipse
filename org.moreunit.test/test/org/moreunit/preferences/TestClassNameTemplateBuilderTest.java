package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestClassNameTemplateBuilderTest
{
    private TestClassNameTemplateBuilder builder = new TestClassNameTemplateBuilder();

    @Test
    public void should_create_test_class_name_pattern_with_prefix() throws Exception
    {
        String template = builder.buildFromSettings(new String[] { "Pre" }, new String[] {}, false);

        assertEquals(template, "Pre${srcFile}");
    }

    @Test
    public void should_create_test_class_name_pattern_with_prefixes() throws Exception
    {
        String template = builder.buildFromSettings(new String[] { "Pre1", "Pre2" }, new String[] {}, false);

        assertEquals(template, "(Pre1|Pre2)${srcFile}");
    }

    @Test
    public void should_create_test_class_name_pattern_with_suffix() throws Exception
    {
        String template = builder.buildFromSettings(new String[] {}, new String[] { "Suf" }, false);

        assertEquals(template, "${srcFile}Suf");
    }

    @Test
    public void should_create_test_class_name_pattern_with_suffixes() throws Exception
    {
        String template = builder.buildFromSettings(new String[] {}, new String[] { "Suf1", "Suf2" }, false);

        assertEquals(template, "${srcFile}(Suf1|Suf2)");
    }

    public void should_create_test_class_name_pattern_with_prefix_and_flexible_naming() throws Exception
    {
        String template = builder.buildFromSettings(new String[] { "Pre" }, new String[] {}, true);

        assertEquals(template, "Pre*${srcFile}");
    }

    @Test
    public void should_create_test_class_name_pattern_with_suffix_and_flexible_naming() throws Exception
    {
        String template = builder.buildFromSettings(new String[] {}, new String[] { "Suf" }, true);

        assertEquals(template, "${srcFile}*Suf");
    }

    @Test
    public void should_create_test_class_name_pattern_with_prefixes_and_suffixes() throws Exception
    {
        String template = builder.buildFromSettings(new String[] { "Pre1", "Pre2" }, new String[] { "Suf1", "Suf2" }, false);

        assertEquals(template, "(Pre1|Pre2)${srcFile}(Suf1|Suf2)");
    }

    @Test
    public void should_create_test_class_name_pattern_with_prefixes_and_suffixes_and_flexible_naming() throws Exception
    {
        String template = builder.buildFromSettings(new String[] { "Pre1", "Pre2" }, new String[] { "Suf1", "Suf2" }, true);

        assertEquals(template, "(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)");
    }
}
