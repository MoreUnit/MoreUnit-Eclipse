package org.moreunit.refactoring;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;

@Preferences(testClassSuffixes="Test", 
             testSrcFolder="test",
             testMethodPrefix=true,
             testType=TestType.JUNIT4)
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
		getShortcutStrategy().pressMoveShortcut();
		bot.waitUntil(Conditions.shellIsActive("Move Static Members"));
		bot.comboBox().setText("testing.TheMoon");
		SWTBotShell moveDialog = bot.activeShell();
		bot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(moveDialog), 10000);
		
		// assert that testmethod moved from TheWorldTest to TheMoonTest
		ICompilationUnit testBeforeMove = context.getCompilationUnit("testing.TheWorldTest");
		assertThat(testBeforeMove.findPrimaryType().getMethods()).isEmpty();
		ICompilationUnit testAfterMove = context.getCompilationUnit("testing.TheMoonTest");
		assertThat(testAfterMove.findPrimaryType().getMethods()).onProperty("elementName").containsOnly("testGetNumber1");
	}
}
