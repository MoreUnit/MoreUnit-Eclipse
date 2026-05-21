package org.moreunit.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.util.JavaType;

public class ClassNameEvaluationTest {

    @Test
    public void should_strip_package_prefix() {
        FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", null, "com.test.example");
        JavaType javaType = eval.getPreferredCorrespondingClass();
        assertThat(javaType.getQualifier()).isEqualTo("example");
    }

    @Test
    public void should_not_strip_package_prefix_if_no_match() {
        FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", null, "org.example");
        JavaType javaType = eval.getPreferredCorrespondingClass();
        assertThat(javaType.getQualifier()).isEqualTo("org.example");
    }

    @Test
    public void should_strip_package_suffix() {
        FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, null, "test", "org.example.test");
        JavaType javaType = eval.getPreferredCorrespondingClass();
        assertThat(javaType.getQualifier()).isEqualTo("org.example");
    }

    @Test
    public void should_not_strip_package_suffix_if_no_match() {
        FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(true);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClass");

        ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, null, "test", "org.example.dev");
        JavaType javaType = eval.getPreferredCorrespondingClass();
        assertThat(javaType.getQualifier()).isEqualTo("org.example.dev");
    }

    @Test
    public void should_add_package_prefix_and_suffix_for_non_test_file() {
        FileNameEvaluation mockEvaluation = mock(FileNameEvaluation.class);
        when(mockEvaluation.isTestFile()).thenReturn(false);
        when(mockEvaluation.getPreferredCorrespondingFileName()).thenReturn("TheClassTest");

        ClassNameEvaluation eval = new ClassNameEvaluation(mockEvaluation, "com.test", "integration", "org.example");
        JavaType javaType = eval.getPreferredCorrespondingClass();
        assertThat(javaType.getQualifier()).isEqualTo("com.test.org.example.integration");
    }
}
