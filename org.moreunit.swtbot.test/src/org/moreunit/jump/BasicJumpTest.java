package org.moreunit.jump;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.context.configs.SimpleJUnit4Properties;


public class BasicJumpTest extends JavaProjectSWTBotTestHelper
{
	@Before
	public void before()
	{
		for(SWTBotEditor editor : bot.editors())
		{
			editor.close();
		}
	}
	
    @Test
    @Context(SimpleJUnit4Project.class)
    public void should_jump_to_test_when_shortcut_is_pressed_in_cut()
    {
    	openResource("SomeClass.java");
    	pressJumpShortcut();
    	assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClassTest.java");
    }

	private void pressJumpShortcut() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.CTRL, 'j');
	}
    
    @Test
    @Context(SimpleJUnit4Project.class)
    public void should_jump_to_cut_when_shortcut_is_pressed_in_testcase()
    {
    	openResource("SomeClassTest.java");
    	pressJumpShortcut();
    	assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClass.java");
    }
    
    @Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeClassTest;org:SomeClassTests",
            properties = @Properties(testType = TestType.JUNIT4,
                                     testClassSuffixes = "Test,Tests"))
    @Test
    public void should_show_dialog_with_when_jump_shortcut_pressed_in_cut_and_multiple_tests_exists()
    {
    	openResource("SomeClass.java");
    	pressJumpShortcut();
    	waitForChooseDialog();
    	
    	// choose dialog should show 2 tests
    	assertThat(bot.tree().rowCount()).isEqualTo(2);
    }

	private void waitForChooseDialog() 
	{
		bot.waitUntil(new DefaultCondition() 
		{
			@Override
			public boolean test() throws Exception 
			{
				return bot.activeShell() != null;
			}
			
			@Override
			public String getFailureMessage() 
			{
				return "ChooseDialog did not appear.";
			}
		});
	}
    
    @Project(mainSrc = "BasicJump_class_with_method.txt",
    		 testSrc = "BasicJump_test_with_testmethod.txt",
    		 properties = @Properties(
    			        testType = TestType.JUNIT4,
    			        testClassSuffixes = "Test",
    			        testMethodPrefix = true))
    @Test
    public void should_jump_from_method_to_test_method()
    {
    	openResource("HelloWorld.java");
    	SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
    	// move cursor to method
    	int lineNumberOfMethod = 6;
		cutEditor.navigateTo(lineNumberOfMethod, 9);
    	pressJumpShortcut();
    	
    	SWTBotEclipseEditor testEditor = bot.activeEditor().toTextEditor();
    	final int lineNumberOfTestMethod = 7;
		assertThat(testEditor.cursorPosition().line).isEqualTo(lineNumberOfTestMethod);
    }
}
