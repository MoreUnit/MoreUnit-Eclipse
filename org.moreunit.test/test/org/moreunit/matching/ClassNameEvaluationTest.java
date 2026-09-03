package org.moreunit.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.util.JavaType;

// white-box test: uses internal types on purpose
@SuppressWarnings("restriction")
public class ClassNameEvaluationTest {

    @Test
    public void should_strip_package_prefix() {
        final FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        final ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", null, "com.test.example");
        final JavaType javaType = eval.getPreferredCorrespondingClass();
        assertEquals(javaType.getQualifier(), "example");
    }

    @Test
    public void should_not_strip_package_prefix_if_no_match() {
        final FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        final ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", null, "org.example");
        final JavaType javaType = eval.getPreferredCorrespondingClass();
        assertEquals(javaType.getQualifier(), "org.example");
    }

    @Test
    public void should_strip_package_suffix() {
        final FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        final ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, null, "test", "org.example.test");
        final JavaType javaType = eval.getPreferredCorrespondingClass();
        assertEquals(javaType.getQualifier(), "org.example");
    }

    @Test
    public void should_not_strip_package_suffix_if_no_match() {
        final FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        final ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, null, "test", "org.example.dev");
        final JavaType javaType = eval.getPreferredCorrespondingClass();
        assertEquals(javaType.getQualifier(), "org.example.dev");
    }

    @Test
    public void should_add_package_prefix_and_suffix_for_non_test_file() {
        final FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(false);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClassTest");

        final ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", "integration", "org.example");
        final JavaType javaType = eval.getPreferredCorrespondingClass();
        assertEquals(javaType.getQualifier(), "com.test.org.example.integration");
    }
}
