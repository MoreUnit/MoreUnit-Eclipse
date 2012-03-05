package org.moreunit;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class MacShortcutStrategy extends ShortcutStrategy 
{
	@Override
	public void pressMoveShortcut() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.ALT | SWT.COMMAND, 'v');
	}

	@Override
	public void openPreferences() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND, ',');		
	}
	
	public void pressRenameShortcutTwiceAndWaitForDialog() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
		bot.waitUntil(Conditions.shellIsActive("Rename Method"));
	}

	@Override
	public void pressRenameShortcut() 
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND | SWT.ALT, 'r');
	}

	@Override
	public void openProperties(SWTBotTreeItem projectItem)
	{
		KeyboardFactory.getAWTKeyboard().pressShortcut(SWT.COMMAND, 'i');		
	}
}
