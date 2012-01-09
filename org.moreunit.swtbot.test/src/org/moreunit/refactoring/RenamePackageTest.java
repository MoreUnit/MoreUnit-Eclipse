package org.moreunit.refactoring;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.ChildrenControlFinder;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * This should be a testcase for:
 * http://sourceforge.net/tracker/?func=detail&aid=3285663&group_id=156007&atid=798056
 * 
 * Renaming a package with use of package prefix and suffix
 * 
 * @author gianasista
 */
@Context(mainCls = "org:SomeClass")
public class RenamePackageTest extends ContextTestCase 
{
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void initialize() 
	{
		bot = new SWTWorkbenchBot();
		
		// Init keyboard layout
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

	@Test
	public void testRenamePackage() throws Exception 
	{
		switchToJavaPerspective();

		try 
		{
			SWTBotTreeItem packageToRename = selectAndReturnPackage();

			// Press rename shortcut
			packageToRename.pressShortcut(SWT.ALT | SWT.COMMAND, 'r');
			
			// fill dialog to rename package
			SWTBotText textWithLabel = bot.textWithLabel("New name:");
			assertNotNull(textWithLabel);
			textWithLabel.setText("some.name");
			bot.shell("Rename Package").activate();
			
			// start rename refactoring
			SWTBotButton okButton = bot.button("OK");
			assertNotNull(okButton);
			okButton.click();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			fail("Renaming a package must not throw an exception! ("+ e + ")");
		}
	}

	private SWTBotTreeItem selectAndReturnPackage() 
	{
		SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");

		List<Tree> findControls = new ChildrenControlFinder(packageExplorerView.getWidget()).findControls(WidgetOfType.widgetOfType(Tree.class));
		if (findControls.isEmpty())
			fail("Tree in Package Explorer View was not found.");

		SWTBotTree tree = new SWTBotTree((Tree) findControls.get(0));
		SWTBotTreeItem projectNode = tree.expandNode(getProjectNameFromContext());
		tree.select(projectNode);

		SWTBotTreeItem[] projectNodeChildren = projectNode.getItems();
		SWTBotTreeItem sourcesFolder = projectNodeChildren[1];
		sourcesFolder.select();
		sourcesFolder.expand();

		SWTBotTreeItem[] packagesInSourceFolder = sourcesFolder.getItems();
		packagesInSourceFolder[0].select();

		return packagesInSourceFolder[0];
	}

	private String getProjectNameFromContext() {
		return context.getProjectHandler().get().getElementName();
	}

	private void switchToJavaPerspective() 
	{
		bot.menu("Window").menu("Open Perspective").menu("Other...").click();
		SWTBotShell openPerspectiveShell = bot.shell("Open Perspective");
		openPerspectiveShell.activate();

		// select the dialog
		bot.table().select("Java");
		bot.button("OK").click();
	}

}
