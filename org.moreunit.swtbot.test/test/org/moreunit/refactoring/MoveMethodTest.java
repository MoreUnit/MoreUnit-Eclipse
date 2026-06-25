package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.ConditionCursorLine;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;

@Preferences(testClassNameTemplate="${srcFile}Test",
             testSrcFolder="test",
             testMethodPrefix=true,
             testType=TestType.JUNIT4)
@ExtendWith(SWTBotJunit5Extension.class)
public class MoveMethodTest extends JavaProjectSWTBotTestHelper
{
	@Project(
            mainSrc = "MoveMethod_class_with_static_method.txt,MoveMethod_class_without_methods.txt",
            testSrc = "MoveMethod_test_with_testmethod.txt,MoveMethod_test_without_methods.txt",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_move_test_method_when_static_method_gets_moved() throws JavaModelException
	{
		openResource("TheWorld.java");
		SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
		cutEditor.setFocus();
		int lineNumberOfMethodSignature = 4;
		cutEditor.navigateTo(lineNumberOfMethodSignature, 27);
		bot.waitUntil(new ConditionCursorLine(cutEditor, lineNumberOfMethodSignature));
		getShortcutStrategy().pressMoveShortcut();
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive("Move Static Members"));
		bot.comboBox().setText("testing.TheMoon");
		SWTBotShell moveDialog = bot.activeShell();
		bot.button("OK").click();
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(moveDialog), 10000);

		// assert that testmethod moved from TheWorldTest to TheMoonTest
		final ICompilationUnit testBeforeMove = context.getCompilationUnit("testing.TheWorldTest");
		bot.waitUntil(new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                IMethod[] methods = testBeforeMove.findPrimaryType().getMethods();
                return methods == null || methods.length == 0;
            }

            @Override
            public String getFailureMessage()
            {
                return "Method not deleted from original class";
            }
        });
		assertEquals(0, testBeforeMove.findPrimaryType().getMethods().length);
		ICompilationUnit testAfterMove = context.getCompilationUnit("testing.TheMoonTest");
		IMethod[] movedMethods = testAfterMove.findPrimaryType().getMethods();
		assertEquals(1, movedMethods.length);
		assertEquals("testGetNumber1", movedMethods[0].getElementName());
	}
}
