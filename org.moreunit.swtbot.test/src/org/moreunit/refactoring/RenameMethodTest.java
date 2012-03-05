package org.moreunit.refactoring;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;

@Preferences(testClassSuffixes = "Test", 
             testSrcFolder = "test", 
             testType = TestType.JUNIT4,
             testMethodPrefix = true)
public class RenameMethodTest extends JavaProjectSWTBotTestHelper
{
	@Project(
            mainSrc = "RenameMethod_class_with_method.txt",
            testSrc = "RenameMethod_test_with_testmethod.txt",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_rename_corresponding_test_methods_when_method_gets_renamed() throws JavaModelException
	{
		openResource("TheWorld.java");
		SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
		cutEditor.setFocus();
		int lineNumberOfMethodSignature = 4;
		cutEditor.navigateTo(lineNumberOfMethodSignature, 20);

		pressRenameShortcutTwiceAndWaitForDialog();
		renameMethodAndWaitUntilFinished();
		
		IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
		assertThat(methods).onProperty("elementName").containsOnly("testGetNumberOne");
	}
	
	private void pressRenameShortcutTwiceAndWaitForDialog() 
	{
		getShortcutStrategy().pressRenameShortcut();
		getShortcutStrategy().pressRenameShortcut();

		//bot.waitUntil(Conditions.shellIsActive("Rename Method"));
		// TODO line above does not work under Fedora
		bot.sleep(3000);
		//System.out.println("Shell: "+bot.activeShell().getText());
	}

	protected void renameMethodAndWaitUntilFinished() 
	{
		bot.textWithLabel("New name:").setText("getNumberOne");
		SWTBotShell renameDialog = bot.activeShell();
		bot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(renameDialog));
	}
}
