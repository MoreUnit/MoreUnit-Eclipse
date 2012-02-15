/**
 * 
 */
package org.moreunit;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.moreunit.test.context.TestContextRule;

/**
 * @author gianasista
 *
 */
public class JavaProjectSWTBotTestHelper 
{
	@Rule
	public final TestContextRule context = new TestContextRule();
	
	protected static SWTWorkbenchBot bot;
	private static boolean isWorkspacePrepared;
	
	@BeforeClass
	public static void initialize() {
		bot = new SWTWorkbenchBot();
		
		if(!isWorkspacePrepared)
		{
			// Init keyboard layout
			SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
			switchToJavaPerspective();
			isWorkspacePrepared = true;
		}
	}
	
	private static void switchToJavaPerspective() 
	{
		bot.menu("Window").menu("Open Perspective").menu("Other...").click();
		SWTBotShell openPerspectiveShell = bot.shell("Open Perspective");
		openPerspectiveShell.activate();

		// select the dialog
		bot.table().select("Java");
		bot.button("OK").click();
	}
	
	protected void openResource(String resourceName)
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND | SWT.SHIFT, 'r');
    	SWTBotShell openTypeShell = bot.shell("Open Resource");
    	assertThat(openTypeShell).isNotNull();
    	openTypeShell.activate();
    	SWTBotText searchField = new SWTBotText(bot.widget(widgetOfType(Text.class)));
    	searchField.typeText(resourceName);
    	
    	bot.waitUntil(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				return bot.table().rowCount() > 0;
			}
			
			@Override
			public String getFailureMessage() {
				return null;
			}
		});
    	KeyboardFactory.getAWTKeyboard().pressShortcut(Keystrokes.DOWN);
    	bot.button("Open").click();
    	bot.waitUntilWidgetAppears(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				return !new SWTWorkbenchBot().editors().isEmpty();
			}
			
			@Override
			public String getFailureMessage() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
