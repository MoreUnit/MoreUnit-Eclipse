package org.moreunit;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.moreunit.log.LogHandler;

public class MacShortcutStrategy extends ShortcutStrategy 
{
	@Override
	public void pressMoveShortcut() 
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.ALT | SWT.COMMAND, 'v');
	}

	@Override
	public void openPreferences() 
	{
	    LogHandler.getInstance().handleInfoLog("Opening Preferences usign Mac Shortcut command");
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.COMMAND, ',');		
	}
	
	public void pressRenameShortcutTwiceAndWaitForDialog() 
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
		bot.waitUntil(Conditions.shellIsActive("Rename Method"));
	}

	@Override
	public void pressRenameShortcut() 
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
	}

	@Override
	public void openProperties(SWTBotTreeItem projectItem)
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.COMMAND, 'i');		
	}
}
