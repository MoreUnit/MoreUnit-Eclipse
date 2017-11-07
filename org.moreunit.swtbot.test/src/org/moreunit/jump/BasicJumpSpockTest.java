package org.moreunit.jump;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
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
        openResource("SomeClass.java");
        getShortcutStrategy().pressJumpShortcut();
        assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClassSpec.java");
    }

    @Test
    @Context(SimpleSpockProject.class)
    public void should_jump_to_cut_when_shortcut_is_pressed_in_spock_testcase()
    {
        openResource("SomeClassSpec.java");
        getShortcutStrategy().pressJumpShortcut();
        assertThat(bot.activeEditor().getTitle()).isEqualTo("SomeClass.java");
    }
    
}
