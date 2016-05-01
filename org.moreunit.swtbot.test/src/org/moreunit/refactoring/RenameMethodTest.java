package org.moreunit.refactoring;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.internal.ui.refactoring.RefactoringUIMessages;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.SWTBotHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;

@Preferences(testClassNameTemplate = "${srcFile}Test", 
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
		
		SWTBotHelper.forceSWTBotShellsRecomputeNameCache(bot);

		bot.waitUntil(Conditions.shellIsActive(RefactoringMessages.RenameMethodWizard_defaultPageTitle));
	}

	protected void renameMethodAndWaitUntilFinished() 
	{
		bot.textWithLabel(RefactoringUIMessages.RenameResourceWizard_name_field_label).setText("getNumberOne");
		SWTBotShell renameDialog = bot.activeShell();
		bot.button(IDialogConstants.OK_LABEL).click();
		bot.waitUntil(Conditions.shellCloses(renameDialog));
	}
}
