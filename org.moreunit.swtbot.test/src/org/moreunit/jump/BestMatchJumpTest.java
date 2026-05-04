package org.moreunit.jump;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.configs.SimpleJUnit4Properties;

@RunWith(SWTBotJunit5ClassRunner.class)
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
    	
        bot.waitUntil(new DefaultCondition()
        {
            
            @Override
            public boolean test() throws Exception
            {
             // choose dialog should show 2 tests (and 2 more items for creation of a new test)
                return bot.tree().rowCount() == 4;
            }
            
            @Override
            public String getFailureMessage()
            {
                return "Expecting 4 entries in the tree but got only: "+ bot.tree().rowCount() + "\n The tree text is "+bot.tree().getText();
            }
        });

    }
}
