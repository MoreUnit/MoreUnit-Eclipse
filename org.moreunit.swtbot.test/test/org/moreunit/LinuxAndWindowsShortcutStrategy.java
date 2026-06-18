package org.moreunit;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class LinuxAndWindowsShortcutStrategy extends ShortcutStrategy
{

	@Override
	public void pressMoveShortcut()
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.ALT | SWT.SHIFT, 'v');
	}

	@Override
	public void openPreferences() 
	{
	    bot.menu("Window").menu("Preferences...").click();
	}

	@Override
	public void pressRenameShortcut() 
	{
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.SHIFT | SWT.ALT, 'r');	
	}

	@Override
	public void openProperties(SWTBotTreeItem projectItem)
	{
		bot.menu("File").menu("Properties").click();
	}
}
