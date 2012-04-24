package org.moreunit.core.commands;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.extension.ExtensionPoints;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.languages.Language;
import org.moreunit.core.preferences.Preferences;

public class JumpActionHandlerTest extends TmpProjectTestCase
{
    private static final String JUMP_COMMAND = "org.moreunit.core.commands.jumpCommand";

    private Preferences preferences = MoreUnitCore.get().getPreferences();

    @Before
    public void setUp() throws Exception
    {
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}Test", "");
    }

    @Test
    public void should_open_test_file_when_in_source_file() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.js");
        IFile testFile = createFile("SomeConceptTest.js");

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
        IFile sourceFile = createFile("SomeConcept.js");
        IFile testFile = createFile("SomeConceptTest.js");

        openEditor(testFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(sourceFile, getFileInActiveEditor());
    }

    @Test
    public void should_ignore_file_extension_case() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.JS");
        IFile testFile = createFile("SomeConceptTest.js");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    @Test
    public void should_use_project_preferences_when_defined() throws Exception
    {
        // given
        preferences.get(project).writerForAnyLanguage().setTestFileNameTemplate("${srcFile}TEST", "");

        IFile sourceFile = createFile("SomeConcept.js");
        IFile testFile = createFile("SomeConceptTEST.js");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    @Test
    public void should_use_specific_preferences_when_defined() throws Exception
    {
        // given
        preferences.add(new Language("io", "IO"));
        preferences.writerForLanguage("io").setTestFileNameTemplate("${srcFile}-test", "-");

        IFile sourceFile = createFile("some-concept.io");
        IFile testFile = createFile("some-concept-test.io");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    @Test
    public void should_use_project_specific_preferences_when_defined() throws Exception
    {
        // given
        preferences.add(new Language("io", "IO"));
        preferences.writerForLanguage("io").setTestFileNameTemplate("${srcFile}-test", "-");
        preferences.get(project).activatePreferencesForLanguage("io", true);
        preferences.get(project).writerForLanguage("io").setTestFileNameTemplate("${srcFile}--test", "--");

        IFile sourceFile = createFile("some--concept.io");
        IFile testFile = createFile("some--concept--test.io");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    @Test
    public void should_delegate_to_extension() throws Exception
    {
        // given
        addExtension("temp-lang_support", ExtensionPoints.LANGUAGES //
        , "<language fileExtension=\"clj\" name=\"Clojure\">" //
          + "<jumper class=\"" + TestJumper.class.getName() + "\" />" //
          + "</language>");

        IFile sourceFile = createFile("SomeClojureFile.clj");
        IFile testFile = createFile("SomeOtherClojureFileThatShouldBeConsideredAsACorrespondingTestByTheExtension.clj");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(testFile, getFileInActiveEditor());
    }

    public static class TestJumper implements IJumper
    {
        public JumpResult jump(IJumpContext context)
        {
            try
            {
                IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(TEST_PROJECT);
                openEditor(p.getFile("SomeOtherClojureFileThatShouldBeConsideredAsACorrespondingTestByTheExtension.clj"));
                return JumpResult.done();
            }
            catch (Exception e)
            {
                return JumpResult.notDone();
            }
        }
    }
}
