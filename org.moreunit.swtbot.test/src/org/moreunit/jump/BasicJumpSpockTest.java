package org.moreunit.jump;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleSpockProject;


public class BasicJumpSpockTest extends JavaProjectSWTBotTestHelper
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
    @Context(SimpleSpockProject.class)
    public void should_jump_to_spock_test_when_shortcut_is_pressed_in_cut()
    {
        testJump("SomeClass.java", "SomeClassSpec.java");
    }

    @Test
    @Context(SimpleSpockProject.class)
    public void should_jump_to_cut_when_shortcut_is_pressed_in_spock_testcase()
    {
        testJump("SomeClassSpec.java", "SomeClass.java");
    }

    private void testJump(String originalFile, final String expectedJumpToFile)
    {
        openResource(originalFile);
        getShortcutStrategy().pressJumpShortcut();
        bot.waitUntil(new DefaultCondition()
        {
            
            @Override
            public boolean test() throws Exception
            {
                return expectedJumpToFile.equals(JavaProjectSWTBotTestHelper.bot.activeEditor().getTitle());
            }
            
            @Override
            public String getFailureMessage()
            {
                return "Expected editor with title "+expectedJumpToFile+" is not active. Current active editor is: "+ JavaProjectSWTBotTestHelper.bot.activeEditor().getTitle();
            }
        });
    }
    
}
