package org.moreunit;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public abstract class ShortcutStrategy 
{
	protected SWTBot bot = new SWTWorkbenchBot();
	
	public static ShortcutStrategy createShortcutStrategy()
	{
	    if(isRunningOnLinuxOrWindows())
	    {
	        return new LinuxAndWindowsShortcutStrategy();
	    }
	    else
	    {
	        return new MacShortcutStrategy();
	    }
	}
	
	protected static boolean isRunningOnLinuxOrWindows() 
	{
		String osName = System.getProperty("os.name");
        return osName.contains("Linux") || osName.contains("Win");
	}
	
	public abstract void pressMoveShortcut();
	public abstract void openPreferences();
	public abstract void pressRenameShortcut();
	public abstract void openProperties(SWTBotTreeItem projectItem);
	
	public void pressGenerateShortcut() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.CTRL, 'u');
	}
	
	public void pressJumpShortcut() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.CTRL, 'j');
	}
}
