package org.moreunit.jump;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.ConditionCursorLine;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

/**
 * Tests the navigation back and forward between the locations visited when
 * jumping between a class under test and its test case.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class JumpNavigationTest extends JavaProjectSWTBotTestHelper
{
	// 0-based line numbers of the resources below
	private static final int LINE_OF_METHOD_BODY = 6;
	private static final int LINE_OF_TEST_METHOD = 7;

	@BeforeEach
	public void before()
	{
		for(final SWTBotEditor editor : bot.editors())
		{
			editor.close();
		}
	}

	@Project(mainSrc = "JumpNavigation_class_with_method.txt",
			 testSrc = "JumpNavigation_test_with_testmethod.txt",
			 properties = @Properties(
				        testType = TestType.JUNIT4,
				        testClassNameTemplate = "${srcFile}Test",
				        testMethodPrefix = true))
	@Test
	public void should_go_back_and_forward_to_the_locations_visited_when_jumping()
	{
		openResource("HelloWorld.java");
		bot.activeEditor().toTextEditor().navigateTo(LINE_OF_METHOD_BODY, 0);
		bot.waitUntil(new ConditionCursorLine(bot.activeEditor().toTextEditor(), LINE_OF_METHOD_BODY));

		getShortcutStrategy().pressJumpShortcut();
		awaitActiveEditor("HelloWorldTest.java");
		bot.waitUntil(new ConditionCursorLine(bot.activeEditor().toTextEditor(), LINE_OF_TEST_METHOD));

		getShortcutStrategy().pressJumpBackShortcut();
		awaitActiveEditor("HelloWorld.java");
		bot.waitUntil(new ConditionCursorLine(bot.activeEditor().toTextEditor(), LINE_OF_METHOD_BODY));

		getShortcutStrategy().pressJumpForwardShortcut();
		awaitActiveEditor("HelloWorldTest.java");
		bot.waitUntil(new ConditionCursorLine(bot.activeEditor().toTextEditor(), LINE_OF_TEST_METHOD));
	}

	private void awaitActiveEditor(final String expectedEditorTitle)
	{
		bot.waitUntil(new DefaultCondition()
		{

			@Override
			public boolean test() throws Exception
			{
				return expectedEditorTitle.equals(JavaProjectSWTBotTestHelper.bot.activeEditor().getTitle());
			}

			@Override
			public String getFailureMessage()
			{
				return "Expected editor with title " + expectedEditorTitle + " is not active. Current active editor is: " + JavaProjectSWTBotTestHelper.bot.activeEditor().getTitle();
			}
		});
	}
}
