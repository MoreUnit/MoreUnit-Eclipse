package org.moreunit.create;


import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moreunit.ConditionCursorLine;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ClassCreationTest extends JavaProjectSWTBotTestHelper
{
    private static final int FLAG_DEFAULT_PACKAGE = 0;

    @Project(mainSrc = "MethodCreation_class_with_method.txt",
            mainSrcFolder = "src",
            testSrcFolder = "junit",
            properties = @Properties(
                                     testType = TestType.JUNIT5,
                                     testClassNameTemplate = "${srcFile}Test",
                                     testMethodPrefix = true))
    @Test
    public void should_create_test_with_default_package_for_junit5() throws JavaModelException
    {
        openResource("TheWorld.java");
        moveCursorToMethod();

        getShortcutStrategy().pressGenerateShortcut();

        bot.waitUntil(Conditions.shellIsActive("New JUnit Test Case"));

        bot.button(IDialogConstants.FINISH_LABEL).click();

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                try {
                    context.getCompilationUnit("testing.TheWorldTest");
                } catch (IllegalArgumentException ex) {
                    return false;
                }
                return true;
            }

            @Override
            public String getFailureMessage()
            {
                return "Test not created testing.TheWorldTest";
            }
        }, 20000);
        ICompilationUnit compilationUnitOfTest = context.getCompilationUnit("testing.TheWorldTest");
        assertThat(compilationUnitOfTest.findPrimaryType().getFlags()).isEqualTo(FLAG_DEFAULT_PACKAGE);
        assertThat(compilationUnitOfTest.getImport("org.junit.jupiter.api.Test").exists()).isTrue();
    }

    private void moveCursorToMethod()
    {
        SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
        int lineNumberOfMethod = 6;
        cutEditor.navigateTo(lineNumberOfMethod, 9);
        bot.waitUntil(new ConditionCursorLine(cutEditor, lineNumberOfMethod));
    }
}
