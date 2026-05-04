package org.moreunit.jump;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5ClassRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleSpockProject;

@RunWith(SWTBotJunit5ClassRunner.class)
public class BasicJumpSpockTest extends JavaProjectSWTBotTestHelper
{
	@BeforeEach
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
        testSimpleJump("SomeClass.java", "SomeClassSpec.java");
    }

    @Test
    @Context(SimpleSpockProject.class)
    public void should_jump_to_cut_when_shortcut_is_pressed_in_spock_testcase()
    {
        testSimpleJump("SomeClassSpec.java", "SomeClass.java");
    }
    
}
