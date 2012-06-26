package org.moreunit.core.commands;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.config.Config;
import org.moreunit.core.extension.ExtensionPoints;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.languages.Language;
import org.moreunit.core.matching.FileMatchSelector;
import org.moreunit.core.preferences.LanguagePreferencesWriter;
import org.moreunit.core.preferences.Preferences;
import org.moreunit.core.ui.DrivableWizardDialog;
import org.moreunit.core.ui.WizardDriver;

public class JumpActionHandlerTest extends TmpProjectTestCase
{
    private static final String JUMP_COMMAND = "org.moreunit.core.commands.jumpCommand";

    private CapturingSelector capturingSelector = new CapturingSelector();

    private Preferences preferences = MoreUnitCore.get().getPreferences();

    @Before
    public void setUp() throws Exception
    {
        Config.messageDialogsActivated = false;
        Config.fileMatchSelector = capturingSelector;

        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}", "${srcProject}");
    }

    @After
    public void cleanPreferences() throws Exception
    {
        for (Language l : preferences.getLanguages())
        {
            preferences.get(project).activatePreferencesForLanguage(l.getExtension(), false);
            preferences.remove(l);
        }

        preferences.get(project).activatePreferencesForLanguage(LanguagePreferencesWriter.ANY_LANGUAGE, false);
        preferences.get(project).save();
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
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
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
        assertThat(getFileInActiveEditor()).isEqualTo(sourceFile);
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
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_only_find_test_file_when_in_right_folder() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.js");
        // wrong folder: should not be found
        createFile("src/SomeConceptTest.js");

        openEditor(sourceFile);

        Config.wizardDriver = new AutoCancelWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then: still in source file
        assertThat(getFileInActiveEditor()).isEqualTo(sourceFile);

        // given
        IFile testFile = createFile("test/SomeConceptTest.js");

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_only_find_source_file_when_in_right_folder() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile testFile = createFile("test/SomeConceptTest.js");

        // wrong folder: should not be found
        createFile("test/SomeConcept.js");

        openEditor(testFile);

        Config.wizardDriver = new AutoCancelWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then: still in test file
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);

        // given
        IFile sourceFile = createFile("src/SomeConcept.js");

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(sourceFile);
    }

    @Test
    public void should_use_project_preferences_when_defined() throws Exception
    {
        // given
        LanguagePreferencesWriter projectPrefs = preferences.get(project).writerForAnyLanguage();
        projectPrefs.setTestFileNameTemplate("${srcFile}TEST", "");
        projectPrefs.setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.js");
        IFile testFile = createFile("test/SomeConceptTEST.js");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_use_specific_preferences_when_defined() throws Exception
    {
        // given
        preferences.add(new Language("io", "IO"));

        LanguagePreferencesWriter langPrefs = preferences.writerForLanguage("io");
        langPrefs.setTestFileNameTemplate("${srcFile}-test", "-");
        langPrefs.setTestFolderPathTemplate("${srcProject}/sources", "${srcProject}/tests");

        IFile sourceFile = createFile("sources/some-concept.io");
        IFile testFile = createFile("tests/some-concept-test.io");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_use_project_specific_preferences_when_defined() throws Exception
    {
        // given
        preferences.add(new Language("io", "IO"));

        LanguagePreferencesWriter wsLangPrefs = preferences.writerForLanguage("io");
        wsLangPrefs.setTestFileNameTemplate("${srcFile}-test", "-");
        wsLangPrefs.setTestFolderPathTemplate("${srcProject}/sources", "${srcProject}/tests");

        preferences.get(project).activatePreferencesForLanguage("io", true);

        LanguagePreferencesWriter projectLangPrefs = preferences.get(project).writerForLanguage("io");
        projectLangPrefs.setTestFileNameTemplate("${srcFile}--test", "--");
        projectLangPrefs.setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/tst");

        IFile sourceFile = createFile("src/some--concept.io");
        IFile testFile = createFile("tst/some--concept--test.io");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
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
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_use_file_selector_when_several_files_match() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");

        IFile sourceFile = createFile("SomeConcept.js");
        IFile testFile1 = createFile("SomeConceptFirstTest.js");
        IFile testFile2 = createFile("SomeConceptSecondTest.js");

        capturingSelector.fileToReturn = testFile2;

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertThat(getFileInActiveEditor()).isEqualTo(testFile2);
        assertThat(capturingSelector.files).hasSize(2).contains(testFile1, testFile2);
    }

    @Test
    public void should_create_test_file_when_it_does_not_exist() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.thing");

        openEditor(sourceFile);

        Config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then
        IFile testFile = getFile("test/SomeConceptTest.thing");
        assertThat(getFileInActiveEditor()).isEqualTo(testFile);
    }

    @Test
    public void should_create_source_file_when_it_does_not_exist() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile testFile = getFile("test/SomeConceptTest.thing");

        openEditor(testFile);

        Config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then
        IFile sourceFile = createFile("src/SomeConcept.thing");
        assertThat(getFileInActiveEditor()).isEqualTo(sourceFile);
    }

    @Test
    public void should_undo_folder_creation_when_file_creation_is_aborted() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src/folder/with/several/parts", "${srcProject}/test/folder/having/many/parts");

        IFile sourceFile = createFile("src/folder/with/several/parts/SomeConcept.thing");

        createFolder("test/folder/having");

        openEditor(sourceFile);

        Config.wizardDriver = new WizardDriver()
        {
            public int open(DrivableWizardDialog dialog)
            {
                assertTrue(getFolder("test/folder/having/many/parts").exists());
                return Window.CANCEL;
            }
        };

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertTrue(getFolder("test/folder/having").exists());
        assertFalse(getFolder("test/folder/having/many/parts").exists());
    }

    private static class AutoCancelWizard implements WizardDriver
    {
        public int open(DrivableWizardDialog dialog)
        {
            return Window.CANCEL;
        }
    }

    private static class AutoPerformWizard implements WizardDriver
    {
        public int open(DrivableWizardDialog dialog)
        {
            dialog.getWizard().performFinish();
            return Window.OK;
        }
    }

    private static class CapturingSelector implements FileMatchSelector
    {
        private IFile fileToReturn;
        private Collection<IFile> files;

        public IFile select(Collection<IFile> files, IFile preferredFile)
        {
            this.files = files;

            for (IFile f : files)
            {
                if(f.equals(fileToReturn))
                {
                    return f;
                }
            }
            return null;
        }
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
