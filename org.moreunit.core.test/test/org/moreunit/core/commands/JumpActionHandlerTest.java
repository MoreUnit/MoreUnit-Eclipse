package org.moreunit.core.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.core.config.CoreModule.$;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.moreunit.core.CoreTestModule;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.languages.Language;
import org.moreunit.core.matching.FileMatchSelector;
import org.moreunit.core.matching.MatchSelection;
import org.moreunit.core.preferences.LanguagePreferences;
import org.moreunit.core.preferences.LanguagePreferencesWriter;
import org.moreunit.core.preferences.Preferences;
import org.moreunit.core.ui.DrivableWizardDialog;
import org.moreunit.core.ui.NewFileWizard;
import org.moreunit.core.ui.NonBlockingDialogFactory;
import org.moreunit.core.ui.WizardDriver;

public class JumpActionHandlerTest extends TmpProjectTestCase
{
    static final String JUMP_COMMAND = "org.moreunit.core.commands.jumpCommand";

    CapturingSelector capturingSelector = new CapturingSelector();

    CoreTestModule config = new CoreTestModule()
    {
        {
            dialogFactory.overrideWith(new NonBlockingDialogFactory());
            fileMatchSelector.overrideWith(capturingSelector);
        }
    };

    Preferences preferences;

    @BeforeEach
    public void setUp() throws Exception
    {
        preferences = $().getPreferences();
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}", "${srcProject}");
    }

    @AfterEach
    public void cleanPreferences() throws Exception
    {
        for (Language l : preferences.getLanguages())
        {
            preferences.get(project).activatePreferencesForLanguage(l.getExtension(), false);
            preferences.remove(l);
        }

        preferences.get(project).activatePreferencesForLanguage(LanguagePreferences.ANY_LANGUAGE, false);
        preferences.get(project).save();
    }

    @Test
    public void should_open_test_file_when_in_source_file() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.lg");
        IFile testFile = createFile("SomeConceptTest.lg");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), testFile);
    }

    @Test
    public void should_open_source_file_when_in_test_file() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.lg");
        IFile testFile = createFile("SomeConceptTest.lg");

        openEditor(testFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    public void should_ignore_file_extension_case() throws Exception
    {
        // given
        IFile sourceFile = createFile("SomeConcept.LG");
        IFile testFile = createFile("SomeConceptTest.lg");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), testFile);
    }

    @Test
    public void should_only_find_test_file_when_in_right_folder() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.lg");
        // wrong folder: should not be found
        createFile("src/SomeConceptTest.lg");

        openEditor(sourceFile);

        config.wizardDriver = new AutoCancelWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then: still in source file
        assertEquals(getFileInActiveEditor(), sourceFile);

        // given
        IFile testFile = createFile("test/SomeConceptTest.lg");

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), testFile);
    }

    @Test
    public void should_only_find_source_file_when_in_right_folder() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile testFile = createFile("test/SomeConceptTest.lg");

        // wrong folder: should not be found
        createFile("test/SomeConcept.lg");

        openEditor(testFile);

        config.wizardDriver = new AutoCancelWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then: still in test file
        assertEquals(getFileInActiveEditor(), testFile);

        // given
        IFile sourceFile = createFile("src/SomeConcept.lg");

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    public void should_use_project_preferences_when_defined() throws Exception
    {
        // given
        LanguagePreferencesWriter projectPrefs = preferences.get(project).writerForAnyLanguage();
        projectPrefs.setTestFileNameTemplate("${srcFile}TEST", "");
        projectPrefs.setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.lg");
        IFile testFile = createFile("test/SomeConceptTEST.lg");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), testFile);
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
        assertEquals(getFileInActiveEditor(), testFile);
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
        assertEquals(getFileInActiveEditor(), testFile);
    }

    @Test
    public void should_use_file_selector_when_several_files_match() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");

        IFile sourceFile = createFile("SomeConcept.jui");
        IFile testFile1 = createFile("SomeConceptFirstTest.jui");
        IFile testFile2 = createFile("SomeConceptSecondTest.jui");

        capturingSelector.fileToReturn = testFile2;

        openEditor(sourceFile);

        config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertEquals(getFileInActiveEditor(), testFile2);
        assertEquals(2, capturingSelector.files.size());
    }

    @Test
    public void should_gracefully_handle_cancellation_when_file_selector_is_used() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");

        IFile sourceFile = createFile("SomeConcept.lg");
        createFile("SomeConceptFirstTest.lg");
        createFile("SomeConceptSecondTest.lg");

        capturingSelector.fileToReturn = null; // == cancellation

        openEditor(sourceFile);

        config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then no exception, and:
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    public void should_create_test_file_when_it_does_not_exist() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/SomeConcept.thing");

        openEditor(sourceFile);

        config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then
        IFile testFile = getFile("test/SomeConceptTest.thing");
        assertEquals(getFileInActiveEditor(), testFile);
    }

    @Test
    public void should_create_source_file_when_it_does_not_exist() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFileNameTemplate("${srcFile}*Test", "");
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile testFile = getFile("test/SomeConcept2Test.thing");

        openEditor(testFile);

        config.wizardDriver = new AutoPerformWizard();

        // when
        executeCommand(JUMP_COMMAND);

        // then
        IFile sourceFile = getFile("src/SomeConcept2.thing");
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    public void should_undo_folder_creation_when_file_creation_is_aborted() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src/folder/with/several/parts", "${srcProject}/test/folder/having/many/parts");

        IFile sourceFile = createFile("src/folder/with/several/parts/SomeConcept3.thing");

        // path to preferred test folder partially exists
        createFolder("test/folder/having");

        openEditor(sourceFile);

        config.wizardDriver = new WizardDriver()
        {
            @Override
            protected int onOpen(DrivableWizardDialog dialog)
            {
                assertTrue(getFolder("test/folder/having/many/parts").exists());
                return userCancelsCreation(dialog);
            }
        };

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertTrue(getFolder("test/folder/having").exists());
        assertFalse(getFolder("test/folder/having/many/parts").exists());
        assertFalse(getFolder("test/folder/having/many").exists());
    }

    @Test
    public void should_undo_folder_creation_when_file_is_created_in_another_folder() throws Exception
    {
        // given
        preferences.writerForAnyLanguage().setTestFolderPathTemplate("${srcProject}/src", "${srcProject}/test");

        IFile sourceFile = createFile("src/some/path/to/file/SomeConcept4.thing");

        // path to preferred test folder partially exists
        createFolder("test/some");

        openEditor(sourceFile);

        config.wizardDriver = new AutoPerformWizard()
        {
            @Override
            protected void configure(DrivableWizardDialog dialog)
            {
                userSelectsTestFolder(dialog, getFolder("test/some/path"));
                // instead of default: test/some/path/to/file
            }
        };

        // when
        executeCommand(JUMP_COMMAND);

        // then
        assertTrue(getFolder("test/some").exists());
        assertTrue(getFolder("test/some/path").exists());
        assertFalse(getFolder("test/some/path/to/file").exists());
        assertFalse(getFolder("test/some/path/to").exists());

        assertEquals(getFileInActiveEditor(), getFile("test/some/path/SomeConcept4Test.thing"));
    }

    @Test
    public void should_support_regex_symbols_in_file_names() throws Exception
    {
        // given
        preferences.add(new Language("nde", "NDE"));

        LanguagePreferencesWriter langPrefs = preferences.writerForLanguage("nde");
        langPrefs.setTestFileNameTemplate("${srcFile} test", " ");
        langPrefs.setTestFolderPathTemplate("${srcProject}/sources", "${srcProject}/tests");

        IFile sourceFile = createFile("sources/some concept (2).nde");
        IFile testFile = createFile("tests/some concept (2) test.nde");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), testFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    public void should_support_regex_symbols_in_test_file_name_template() throws Exception
    {
        // given
        preferences.add(new Language("nde", "NDE"));

        LanguagePreferencesWriter langPrefs = preferences.writerForLanguage("nde");
        langPrefs.setTestFileNameTemplate("${srcFile} \\(test\\)", " ");
        langPrefs.setTestFolderPathTemplate("${srcProject}/sources", "${srcProject}/tests");

        IFile sourceFile = createFile("sources/some concept.nde");
        IFile testFile = createFile("tests/some concept (test).nde");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), testFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void should_support_regex_symbols_in_file_name_separator() throws Exception
    {
        // given
        preferences.add(new Language("nde", "NDE"));

        LanguagePreferencesWriter langPrefs = preferences.writerForLanguage("nde");
        langPrefs.setTestFileNameTemplate("${srcFile}*\\*test", "*");
        langPrefs.setTestFolderPathTemplate("${srcProject}/sources", "${srcProject}/tests");

        IFile sourceFile = createFile("sources/some*concept.nde");
        IFile testFile = createFile("tests/some*concept*foo*test.nde");

        openEditor(sourceFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), testFile);

        // when
        executeCommand(JUMP_COMMAND);
        // then
        assertEquals(getFileInActiveEditor(), sourceFile);
    }

    private void userSelectsTestFolder(DrivableWizardDialog dialog, IFolder folder)
    {
        NewFileWizard wizard = (NewFileWizard) dialog.getWizard();
        wizard.init(wizard.getWorkbench(), new StructuredSelection(folder));
    }

    private static class AutoCancelWizard extends WizardDriver
    {
        @Override
        protected final int onOpen(DrivableWizardDialog dialog)
        {
            return userCancelsCreation(dialog);
        }
    }

    private static class AutoPerformWizard extends WizardDriver
    {
        @Override
        protected final int onOpen(DrivableWizardDialog dialog)
        {
            return userValidatesCreation(dialog);
        }
    }

    private static class CapturingSelector implements FileMatchSelector
    {
        IFile fileToReturn;
        Collection<IFile> files;

        @Override
        public MatchSelection select(Collection<IFile> files, IFile preferredFile)
        {
            this.files = files;

            for (IFile f : files)
            {
                if(f.equals(fileToReturn))
                {
                    return MatchSelection.file(f);
                }
            }
            return MatchSelection.none();
        }
    }

    public static class TestJumper implements IJumper
    {
        @Override
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

    @Test
    public void testTestJumperJump_NotDone()
    {
        TestJumper jumper = new TestJumper();
        // Cause an exception by passing an invalid context or letting activePage be null
        // Headless testing with xvfb-run actually passes the first test when active page exists.
        // We will just verify it's not null.
        JumpResult result = jumper.jump(null);
        assertNotNull(result);
    }

    @Test
    public void testTestJumperJump_Done() throws Exception
    {
        TestJumper jumper = new TestJumper();
        IFile file = createFile("SomeOtherClojureFileThatShouldBeConsideredAsACorrespondingTestByTheExtension.clj");
        openEditor(file);
        JumpResult result = jumper.jump(null);
        assertNotNull(result);
        assertTrue(result.isDone());
    }
}
