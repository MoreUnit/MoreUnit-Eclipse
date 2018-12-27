package org.moreunit.jump;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.context.configs.SimpleJUnit4Project;


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
    	getShortcutStrategy().pressJumpShortcut();
    	assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClassTest.java");
    }

	@Test
    @Context(SimpleJUnit4Project.class)
    public void should_jump_to_cut_when_shortcut_is_pressed_in_testcase()
    {
    	openResource("SomeClassTest.java");
    	getShortcutStrategy().pressJumpShortcut();
    	assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClass.java");
    }
    
    @Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeClassTest;org:SomeClassTests",
            properties = @Properties(testType = TestType.JUNIT4,
                                     testClassNameTemplate = "${srcFile}(Test|Tests)"))
    @Test
    public void should_show_dialog_with_when_jump_shortcut_pressed_in_cut_and_multiple_tests_exists()
    {
    	openResource("SomeClass.java");
    	getShortcutStrategy().pressJumpShortcut();
    	waitForChooseDialog();
    	
    	// choose dialog should show 2 tests (and 2 more items for creation of a new test)
    	assertThat(bot.tree().rowCount()).isEqualTo(4);
    }

	@Project(mainSrc = "BasicJump_class_with_method.txt",
    		 testSrc = "BasicJump_test_with_testmethod.txt",
    		 properties = @Properties(
    			        testType = TestType.JUNIT4,
    			        testClassNameTemplate = "${srcFile}Test",
    			        testMethodPrefix = true))
    @Test
    public void should_jump_from_method_to_test_method()
    {
    	openResource("HelloWorld.java");
    	SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
    	// move cursor to method
    	int lineNumberOfMethod = 6;
		cutEditor.navigateTo(lineNumberOfMethod, 9);
    	getShortcutStrategy().pressJumpShortcut();
    	
    	final int lineNumberOfTestMethod = 7;
    	bot.waitUntil(new DefaultCondition()
        {
    	    
            @Override
            public boolean test() throws Exception
            {
                SWTBotEclipseEditor testEditor = BasicJumpTest.bot.activeEditor().toTextEditor();
                return testEditor.cursorPosition().line == lineNumberOfTestMethod;
            }
            
            @Override
            public String getFailureMessage()
            {
                return "It has not jumped to the right line number inside the test class.";
            }
        });
    }
}
