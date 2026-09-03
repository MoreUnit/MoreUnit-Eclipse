package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.moreunit.core.config.CoreModule.$;

import org.moreunit.core.commands.TmpProjectTestCase;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.SourceFolderPath;
import org.moreunit.core.preferences.Preferences;

public class ConcreteSrcFileTest extends TmpProjectTestCase
{
    @BeforeEach
    public void setUpPreferences()
    {
        $().getPreferences().writerForAnyLanguage().setTestFileNameTemplate("${srcFile}Test", "");
        $().getPreferences().writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");
    }

    @AfterEach
    public void resetPreferences()
    {
        $().getPreferences().writerForAnyLanguage().setTestFileNameTemplate(Preferences.DEFAULTS.getTestFileNameTemplate(), Preferences.DEFAULTS.getFileWordSeparator());
        $().getPreferences().writerForAnyLanguage().setTestFolderPathTemplate(Preferences.DEFAULTS.getSrcFolderPathTemplate(), Preferences.DEFAULTS.getTestFolderPathTemplate());
    }

    private ConcreteSrcFile createSrcFile(String projectRelativePath) throws Exception
    {
        return new ConcreteSrcFile(new EclipseFile(createFile(projectRelativePath)));
    }

    @Test
    public void should_create_from_ifile()
    {
        // covered by the other tests, kept to preserve the original scenario
    }

    @Test
    public void should_create_and_delete_file() throws Exception
    {
        final ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");

        assertTrue(srcFile.exists());

        srcFile.delete();

        assertFalse(srcFile.exists());
    }

    @Test
    public void should_delegate_file_properties() throws Exception
    {
        final ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");

        assertEquals("Foo.java", srcFile.getName());
        assertEquals("Foo", srcFile.getBaseNameWithoutExtension());
        assertEquals("java", srcFile.getExtension());
        assertTrue(srcFile.hasExtension());
        assertTrue(srcFile.isSupported());
        assertNotNull(srcFile.getPath());
        assertNotNull(srcFile.getParent());
        assertNotNull(srcFile.getProject());
        assertNotNull(srcFile.getUnderlyingPlatformFile());
        assertNotNull(srcFile.getUnderlyingPlatformResource());
    }

    @Test
    public void should_not_be_supported_when_file_has_no_extension() throws Exception
    {
        final ConcreteSrcFile srcFile = createSrcFile("src/Foo");

        assertFalse(srcFile.hasExtension());
        assertFalse(srcFile.isSupported());
        assertFalse(srcFile.hasDefaultSupport());
    }

    @Test
    public void should_evaluate_name_against_test_file_pattern() throws Exception
    {
        final ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");
        final ConcreteSrcFile testFile = createSrcFile("src/FooTest.java");

        assertFalse(srcFile.isTestFile());
        assertTrue(testFile.isTestFile());

        final FileNameEvaluation evaluation = srcFile.evaluateName();
        assertFalse(evaluation.isTestFile());
        assertSame(evaluation, srcFile.evaluateName(), "name evaluation should be cached");
    }

    @Test
    public void should_find_corresponding_folder_of_test_and_src_files() throws Exception
    {
        final ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");
        final ConcreteSrcFile testFile = createSrcFile("test/FooTest.java");

        final SourceFolderPath testFolder = srcFile.findCorrespondingSrcFolder();
        final SourceFolderPath srcFolder = testFile.findCorrespondingSrcFolder();

        assertNotNull(testFolder);
        assertNotNull(srcFolder);
        assertEquals(TEST_PROJECT + "/test", testFolder.toString());
        assertEquals(TEST_PROJECT + "/src", srcFolder.toString());
    }
}
