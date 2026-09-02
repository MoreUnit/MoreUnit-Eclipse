package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.moreunit.core.commands.TmpProjectTestCase;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.SourceFolderPath;

public class ConcreteSrcFileTest extends TmpProjectTestCase
{
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
        ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");

        assertTrue(srcFile.exists());

        srcFile.delete();

        assertFalse(srcFile.exists());
    }

    @Test
    public void should_delegate_file_properties() throws Exception
    {
        ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");

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
        ConcreteSrcFile srcFile = createSrcFile("src/Foo");

        assertFalse(srcFile.hasExtension());
        assertFalse(srcFile.isSupported());
        assertFalse(srcFile.hasDefaultSupport());
    }

    @Test
    public void should_evaluate_name_against_test_file_pattern() throws Exception
    {
        ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");
        ConcreteSrcFile testFile = createSrcFile("src/FooTest.java");

        assertFalse(srcFile.isTestFile());
        assertTrue(testFile.isTestFile());

        FileNameEvaluation evaluation = srcFile.evaluateName();
        assertFalse(evaluation.isTestFile());
        assertSame(evaluation, srcFile.evaluateName(), "name evaluation should be cached");
    }

    @Test
    public void should_find_corresponding_folder_of_test_and_src_files() throws Exception
    {
        ConcreteSrcFile srcFile = createSrcFile("src/Foo.java");
        ConcreteSrcFile testFile = createSrcFile("src/FooTest.java");

        SourceFolderPath srcFolder = srcFile.findCorrespondingSrcFolder();
        SourceFolderPath testFolder = testFile.findCorrespondingSrcFolder();

        assertNotNull(srcFolder);
        assertNotNull(testFolder);
        assertEquals(srcFolder.toString(), testFolder.toString());
    }
}
