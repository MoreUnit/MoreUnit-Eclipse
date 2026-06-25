package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;

/**
 * @author gianasista
 */
@Context(mainCls = "org:SomeClass")
@ExtendWith(SWTBotJunit5Extension.class)
public class RenamePackageTest extends JavaProjectSWTBotTestHelper
{
	/*
	 * This should be a testcase for:
	 * http://sourceforge.net/tracker/?func=detail&aid=3285663&group_id=156007&atid=798056
	 *
	 * Renaming a package with use of package prefix and suffix
	 */
	@Test
	public void should_not_throw_exception_when_renaming_package_while_using_package_prefix_and_suffix() throws Exception
	{
		assertDoesNotThrow(() ->
		{
			selectAndReturnPackageWithName("org");

			// Press rename shortcut
			getShortcutStrategy().pressRenameShortcut();

			// fill dialog to rename package
			SWTBotText textWithLabel = bot.textWithLabel("New name:");
			assertNotNull(textWithLabel);
			textWithLabel.setText("some.name");
			bot.shell("Rename Package").activate();

			// start rename refactoring
			SWTBotButton okButton = bot.button("OK");
			assertNotNull(okButton);
			okButton.click();
		});
	}

	@Project(mainCls="some:First,some:Second",
			 mainSrcFolder="src",
			 testSrc="test",
			 testCls="some:SecondTest")
	@Preferences(testClassNameTemplate="${srcFile}Test",
	             testType=TestType.JUNIT4)
	public void should_rename_testpackage_when_renaming_package()
	{
		selectAndReturnPackageWithName("org");

		// Press rename shortcut
		getShortcutStrategy().pressRenameShortcut();

		// fill dialog to rename package
		SWTBotText textWithLabel = bot.textWithLabel("New name:");
		assertNotNull(textWithLabel);
		textWithLabel.setText("any");
		bot.shell("Rename Package").activate();

		// start rename refactoring
		SWTBotButton okButton = bot.button("OK");
		okButton.click();

		bot.waitUntil(new DefaultCondition()
		{
			@Override
			public boolean test() throws Exception
			{
				return bot.activeShell() == null;
			}

			@Override
			public String getFailureMessage()
			{
				return "Rename dialog did not disappear.";
			}
		});

		ICompilationUnit testcase = context.getCompilationUnit("any.SecondTest");
		assertNotNull(testcase);
	}
}
