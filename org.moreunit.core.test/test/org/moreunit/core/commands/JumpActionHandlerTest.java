package org.moreunit.core.commands;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IFile;
import org.junit.Test;

public class JumpActionHandlerTest extends JsProjectTestCase
{
    private static final String JUMP_COMMAND = "org.moreunit.core.commands.jumpCommand";

    @Test
    public void should_open_test_file_when_in_source_file() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.js", "function someJsFun() { }");
        IFile testFile = createFile("SomeConceptTest.js", "assert(true);");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    @Test
    public void should_open_source_file_when_in_test_file() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.js", "function someJsFun() { }");
        IFile testFile = createFile("SomeConceptTest.js", "assert(true);");

        openEditor(testFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(sourceFile, getFileInActiveEditor());
    }
}
