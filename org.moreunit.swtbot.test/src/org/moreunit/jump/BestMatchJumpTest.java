package org.moreunit.jump;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.configs.SimpleJUnit4Properties;

@RunWith(SWTBotJunit4ClassRunner.class)
public class BestMatchJumpTest extends JavaProjectSWTBotTestHelper
{
	@Project(
			mainCls = "org:SomeClass",
			testCls = "org:SomeClassTest;net:SomeClassTest",
			properties = @Properties(SimpleJUnit4Properties.class))
	@Test
	public void should_jump_to_test_when_two_test_exist_but_one_is_prefect_match()
	{
	    testSimpleJump("SomeClass.java", "SomeClassTest.java");
	}
	
	@Project(
            mainCls = "org:SomeClass",
            testCls = "com:SomeClassTest;net:SomeClassTest",
            properties = @Properties(SimpleJUnit4Properties.class))
    @Test
    public void should_show_choose_dialog_when_two_tests_exists_and_no_perfect_match_exists()
    {
		
    	openResource("SomeClass.java");
    	getShortcutStrategy().pressJumpShortcut();
    	waitForChooseDialog();
    	
    	assertThat(bot.tree().rowCount()).isEqualTo(4);

    }
}
