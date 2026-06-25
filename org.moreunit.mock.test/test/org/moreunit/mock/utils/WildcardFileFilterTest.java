package org.moreunit.mock.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class WildcardFileFilterTest
{
    @Test
    public void should_match_single_character_wildcard()
    {
        WildcardFileFilter filter = new WildcardFileFilter("test?.java");
        assertTrue(filter.accept(new File("testA.java")));
        assertTrue(filter.accept(new File("test1.java")));
        assertFalse(filter.accept(new File("testAB.java")));
        assertFalse(filter.accept(new File("test.java")));
    }

    @Test
    public void should_match_multiple_character_wildcard()
    {
        WildcardFileFilter filter = new WildcardFileFilter("*.xml");
        assertTrue(filter.accept(new File("config.xml")));
        assertTrue(filter.accept(new File(".xml")));
        assertFalse(filter.accept(new File("config.txt")));
        assertFalse(filter.accept(new File("config.xml.bak")));
    }

    @Test
    public void should_match_exact_name()
    {
        WildcardFileFilter filter = new WildcardFileFilter("pom.xml");
        assertTrue(filter.accept(new File("pom.xml")));
        assertFalse(filter.accept(new File("pom2.xml")));
        assertFalse(filter.accept(new File("pom.xml.bak")));
    }

    @Test
    public void should_match_with_directory_prefix()
    {
        WildcardFileFilter filter = new WildcardFileFilter("*.java");
        assertTrue(filter.accept(new File("src/Main.java")));
        assertFalse(filter.accept(new File("test/Main.txt")));
    }

    @Test
    public void should_handle_multiple_wildcards()
    {
        WildcardFileFilter filter = new WildcardFileFilter("*Test?.java");
        assertTrue(filter.accept(new File("ATestB.java")));
        assertFalse(filter.accept(new File("ATestBC.java")));
    }

    @Test
    public void should_escape_special_regex_characters()
    {
        WildcardFileFilter filter = new WildcardFileFilter("file[1].txt");
        assertTrue(filter.accept(new File("file[1].txt")));
        assertFalse(filter.accept(new File("file1.txt")));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void should_handle_backslash_in_pattern()
    {
        WildcardFileFilter filter = new WildcardFileFilter("path\\*.txt");
        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("path\\file.txt");
        assertTrue(filter.accept(mockFile));
    }

    @Test
    public void should_match_hidden_files_with_wildcard()
    {
        WildcardFileFilter filter = new WildcardFileFilter(".*");
        assertTrue(filter.accept(new File(".hidden")));
        assertTrue(filter.accept(new File(".gitignore")));
    }
}
