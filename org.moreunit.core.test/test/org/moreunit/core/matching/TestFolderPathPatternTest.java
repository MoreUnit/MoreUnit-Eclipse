package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.core.matching.TestFolderPathPattern.isValid;

import org.junit.jupiter.api.Test;
import org.moreunit.core.CoreTestModule;
import org.moreunit.core.resources.InMemoryPath;
import org.moreunit.core.resources.InMemoryWorkspace;
import org.moreunit.core.resources.Path;

public class TestFolderPathPatternTest
{
    private static String validSrcPath = "${srcProject}/src";
    private static String validTestPath = "${srcProject}/test";

    CoreTestModule config = new CoreTestModule()
    {
        {
            workspace.overrideWith(new InMemoryWorkspace());
        }
    };

    @Test
    public void isValid_should_return_false_when_path_is_blank() throws Exception
    {
        assertFalse(isValid(null, validTestPath));
        assertFalse(isValid("", validTestPath));
        assertFalse(isValid("  ", validTestPath));

        assertFalse(isValid(validSrcPath, null));
        assertFalse(isValid(validSrcPath, ""));
        assertFalse(isValid(validSrcPath, "  "));
    }

    @Test
    public void isValid_should_return_false_when_path_does_not_contain_project_name() throws Exception
    {
        assertFalse(isValid(validSrcPath, "path"));
        assertFalse(isValid("path", validTestPath));
    }

    @Test
    public void isValid_should_return_false_when_path_does_not_start_with_project_name() throws Exception
    {
        assertFalse(isValid(validSrcPath, "path/${srcProject}-test"));
        assertFalse(isValid("path/${srcProject}", validTestPath));
    }

    @Test
    public void isValid_should_return_true_when_path_starts_with_project_name() throws Exception
    {
        assertTrue(isValid("${srcProject}", "${srcProject}"));
        assertTrue(isValid("${srcProject}/path", validTestPath));

        assertTrue(isValid(validSrcPath, "${srcProject}-test/path"));
        assertTrue(isValid(validSrcPath, "test-${srcProject}/path"));
    }

    @Test
    public void isValid_should_ignore_leading_separator() throws Exception
    {
        assertTrue(isValid("/${srcProject}/path", validTestPath));

        assertTrue(isValid(validSrcPath, "/${srcProject}-test/path"));
        assertTrue(isValid(validSrcPath, "/test-${srcProject}/path"));
    }

    @Test
    public void isValid_should_return_false_when_test_path_contains_stars() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/", "${srcProject}/*/"));
    }

    @Test
    public void isValid_should_return_false_when_test_path_contains_parentheses() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/", "${srcProject}/(test)/"));
    }

    @Test
    public void isValid_should_return_false_when_group_is_not_closed() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(*/blah/", "${srcProject}/test/\\1"));
    }

    @Test
    public void isValid_should_return_true_when_variable_segment_is_not_captured() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/*/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_true_when_variable_segment_is_captured() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/(*)/", "${srcProject}/test/\\1/"));
    }

    @Test
    public void isValid_should_return_false_when_captured_variable_segment_is_not_used() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(*)/", "${srcProject}/test/"));
        assertFalse(isValid("${srcProject}/src/(*)/", "${srcProject}/test/\\"));
    }

    @Test
    public void isValid_should_return_true_when_variable_path_is_not_captured() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/**/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_true_when_variable_path_is_captured() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/(**)/", "${srcProject}/test/\\1/"));
    }

    @Test
    public void isValid_should_return_false_when_captured_variable_path_is_not_used() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(**)/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_true_when_star_is_used_for_part_of_segment() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/seg*ment/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_false_for_three_stars() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/***/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_false_when_double_star_is_used_for_part_of_segment() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/seg**ment/", "${srcProject}/test/"));
    }

    @Test
    public void isValid_should_return_true_when_all_captured_groups_are_used() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/(*)/(**)/blah-(*)/", "${srcProject}/test/\\3/abc-\\1/\\2"));
    }

    @Test
    public void isValid_should_return_true_when_group_references_follow_each_other() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/(*)/blah-(*)/", "${srcProject}/test/\\1\\2"));
    }

    @Test
    public void isValid_should_return_false_when_one_captured_group_is_not_used() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(*)/(**)/blah-(*)", "${srcProject}/test/\\3/\\2/"));
    }

    @Test
    public void isValid_should_return_false_when_one_group_number_is_unknown() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(*)/(**)/blah-(*)/", "${srcProject}/test/\\3/abc-\\1/\\4"));
    }

    @Test
    public void isValid_should_return_true_when_group_reference_is_escaped() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/", "${srcProject}/test/\\\\1"));
    }

    @Test
    public void isValid_should_return_true_when_group_reference_is_escaped_and_another_is_valid() throws Exception
    {
        assertTrue(isValid("${srcProject}/src/main/(*)", "${srcProject}/src/test\\\\1/\\1"));
    }

    @Test
    public void isValid_should_return_false_when_parentheses_contain_non_stars() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(a)/", "${srcProject}/test/\\1"));
    }

    @Test
    public void isValid_should_return_false_when_more_than_9_groups() throws Exception
    {
        assertFalse(isValid("${srcProject}/src/(*)1(*)2(*)3(*)4(*)5(*)6(*)7(*)8(*)9(*)/", "${srcProject}/test/\\1\\2\\3\\4\\5\\6\\7\\8\\9\\10"));
    }

    @Test
    public void getTestPathFor_should_find_test_path_when_no_variable_part() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getTestPathFor(path("js-project/src/")).toString(), "js-project/test");
    }

    @Test
    public void getTestPathFor_should_find_test_path_when_template_only_contains_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}", "${srcProject}");

        assertEquals(p.getTestPathFor(path("js-project")).toString(), "js-project");
    }

    @Test
    public void getTestPathFor_should_ignore_leading_and_trailing_separators_in_patterns() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("/${srcProject}/src/", "/${srcProject}/test/");

        assertEquals(p.getTestPathFor(path("js-project/src/")).toString(), "js-project/test");
    }

    @Test
    public void getTestPathFor_should_ignore_leading_and_trailing_separators_in_input() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src", "${srcProject}/test");

        assertEquals(p.getTestPathFor(path("/js-project/src/")).toString(), "js-project/test");
    }

    @Test
    public void getTestPathFor_should_reproduce_src_path_end() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getTestPathFor(path("js-project/src/some/path/to/the/code")).toString(), "js-project/test/some/path/to/the/code");
    }

    @Test
    public void getTestPathFor_should_find_test_path_when_variable_parts() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/*/some/path*/", "${srcProject}/test/");

        assertEquals(p.getTestPathFor(path("js-project/src/rb/some/path-to-the-code")).toString(), "js-project/test");
    }

    @Test
    public void getTestPathFor_should_find_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "pre-${srcProject}-suf/test/");

        assertEquals(p.getTestPathFor(path("lisp-project/src/")).toString(), "pre-lisp-project-suf/test");
    }

    @Test
    public void getTestPathFor_should_find_test_path_when_variable_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/**/java/", "${srcProject}/test/java");

        assertEquals(p.getTestPathFor(path("myproject/src/some/path/java")).toString(), "myproject/test/java");
    }

    @Test
    public void getTestPathFor_should_find_test_path_when_variable_path_in_last_position() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/**", "${srcProject}/test");

        assertEquals(p.getTestPathFor(path("myproject/src/java/code")).toString(), "myproject/test");
    }

    @Test
    public void getTestPathFor_should_use_captured_variable() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src-(*)/code", "${srcProject}/test-\\1/code");

        assertEquals(p.getTestPathFor(path("myproject/src-java/code")).toString(), "myproject/test-java/code");
    }

    @Test
    public void getTestPathFor_should_use_captured_variables() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src-(*)/code-(*)", "${srcProject}/test-\\1/code-\\2");

        assertEquals(p.getTestPathFor(path("myproject/src-A/code-B")).toString(), "myproject/test-A/code-B");
    }

    @Test
    public void getTestPathFor_should_use_captured_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/(**)/main", "${srcProject}/\\1/test");

        assertEquals(p.getTestPathFor(path("myproject/src/java/main/code")).toString(), "myproject/src/java/test/code");
    }

    @Test
    public void getTestPathFor_should_use_captured_path_when_in_last_position() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/(**)", "${srcProject}/test/\\1");

        assertEquals(p.getTestPathFor(path("myproject/src/java/code")).toString(), "myproject/test/java/code");
    }

    @Test
    public void getTestPathFor_should_use_captured_variables_and_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/a(*)/(**)/b(*)", "${srcProject}/x\\3/y\\1/\\2");

        assertEquals(p.getTestPathFor(path("myproject/a1/some/path/b2")).toString(), "myproject/x2/y1/some/path");
    }

    @Test
    public void getTestPathFor_should_handle_braces_in_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getTestPathFor(path("/foobar [1]/src/")).toString(), "foobar [1]/test");
        assertEquals(p.getTestPathFor(path("/foobar {1}/src/")).toString(), "foobar {1}/test");
        assertEquals(p.getTestPathFor(path("/foobar (1)/src/")).toString(), "foobar (1)/test");
    }

    @Test
    public void getTestPathFor_should_handle_range_like_parts_in_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");
        assertEquals(p.getTestPathFor(path("com.example/src/dir/with [rangelike-123]")).toString(), "com.example/test/dir/with [rangelike-123]");
    }

    @Test
    public void getSrcPathFor_should_find_src_path_when_no_variable_part() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getSrcPathFor(path("js-project/test/")).toString(), "js-project/src");
    }

    @Test
    public void getSrcPathFor_should_find_test_path_when_template_only_contains_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}", "${srcProject}");

        assertEquals(p.getSrcPathFor(path("js-project")).toString(), "js-project");
    }

    @Test
    public void getSrcPathFor_should_ignore_leading_and_trailing_separators_in_patterns() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("/${srcProject}/src/", "/${srcProject}/test/");

        assertEquals(p.getSrcPathFor(path("js-project/test/")).toString(), "js-project/src");
    }

    @Test
    public void getSrcPathFor_should_ignore_leading_and_trailing_separators_in_input() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src", "${srcProject}/test");

        assertEquals(p.getSrcPathFor(path("/js-project/test/")).toString(), "js-project/src");
    }

    @Test
    public void getSrcPathFor_should_reproduce_test_path_end() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getSrcPathFor(path("js-project/test/some/path/to/the/code")).toString(), "js-project/src/some/path/to/the/code");
    }

    @Test
    public void getSrcPathFor_should_find_test_path_when_variable_parts() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/*/some/path*/", "${srcProject}/test/");

        assertEquals(p.getSrcPathFor(path("js-project/test/")).toString(), "js-project/src/[^/]*/some/path[^/]*");
    }

    @Test
    public void getSrcPathFor_should_find_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "pre-${srcProject}-suf/test/");

        assertEquals(p.getSrcPathFor(path("pre-rb-project-suf/test/")).toString(), "rb-project/src");
    }

    @Test
    public void getSrcPathFor_should_find_test_path_when_variable_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/**/java/", "${srcProject}/test/java");

        assertEquals(p.getSrcPathFor(path("myproject/test/java")).toString(), "myproject/src/.*/java");
    }

    @Test
    public void getSrcPathFor_should_use_captured_variable() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src-(*)/code", "${srcProject}-test/test-\\1/code");

        assertEquals(p.getSrcPathFor(path("myproject-test/test-java/code")).toString(), "myproject/src-java/code");
    }

    @Test
    public void getSrcPathFor_should_throw_exception_when_no_match_found() throws Exception
    {
        // given
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src-(*)/code", "${srcProject}-test/test-\\1/code");

        // does not start with myproject-test
        Path tstPath = path("myproject/test-java/code");

        {
            DoesNotMatchConfigurationException e = assertThrows(DoesNotMatchConfigurationException.class, () -> p.getSrcPathFor(tstPath));
            assertEquals(((DoesNotMatchConfigurationException) e).getPath(), tstPath);
        }
    }

    @Test
    public void getSrcPathFor_should_use_captured_variables() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/X-(*)/Y-(*)/Z-(*)", "${srcProject}/U-\\1/V-\\2/W-\\3");

        assertEquals(p.getSrcPathFor(path("myproject/U-A/V-B/W-C")).toString(), "myproject/X-A/Y-B/Z-C");
    }

    @Test
    public void getSrcPathFor_should_respect_order_when_using_captured_variables() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/X-(*)/Y-(*)/Z-(*)", "${srcProject}/U-\\3/V-\\1/W-\\2");

        assertEquals(p.getSrcPathFor(path("myproject/U-A/V-B/W-C")).toString(), "myproject/X-B/Y-C/Z-A");
    }

    @Test
    public void getSrcPathFor_should_use_captured_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/(**)/main", "${srcProject}/\\1/test");

        assertEquals(p.getSrcPathFor(path("myproject/src/java/test/code")).toString(), "myproject/src/java/main/code");
    }

    @Test
    public void getSrcPathFor_should_use_captured_path_when_in_last_position() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/(**)", "${srcProject}/test/\\1");

        assertEquals(p.getSrcPathFor(path("myproject/test/java/code")).toString(), "myproject/src/java/code");
    }

    @Test
    public void getSrcPathFor_should_use_captured_variables_and_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/a(*)/(**)/b(*)", "${srcProject}/x\\3/y\\1/\\2");

        assertEquals(p.getSrcPathFor(path("myproject/x2/y1/some/path")).toString(), "myproject/a1/some/path/b2");
    }

    @Test
    public void getSrcPathFor_should_handle_braces_in_project_name() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");

        assertEquals(p.getSrcPathFor(path("/foobar [1]/test/")).toString(), "foobar [1]/src");
        assertEquals(p.getSrcPathFor(path("/foobar {1}/test/")).toString(), "foobar {1}/src");
        assertEquals(p.getSrcPathFor(path("/foobar (1)/test/")).toString(), "foobar (1)/src");
    }

    @Test
    public void getSrcPathFor_should_handle_range_like_parts_in_path() throws Exception
    {
        TestFolderPathPattern p = new TestFolderPathPattern("${srcProject}/src/", "${srcProject}/test/");
        assertEquals(p.getSrcPathFor(path("com.example/test/dir/with [rangelike-123]")).toString(), "com.example/src/dir/with [rangelike-123]");
    }

    private Path path(String pathStr)
    {
        return new InMemoryPath(pathStr);
    }
}
