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
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;

/**
 * This should be a testcase for:
 * http://sourceforge.net/tracker/?func=detail&aid=3285663&group_id=156007&atid=798056
 * 
 * Renaming a package with use of package prefix and suffix
 * 
 * @author gianasista
 */
public class RenamePackageTest extends SimpleProjectTestCase 
{
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void initialize() 
	{
		bot = new SWTWorkbenchBot();
	}

	@Test
	public void testRenamePackage() throws Exception 
	{
		String packageName = "somepackage";
		initProjectWithPackage(packageName);

		switchToJavaPerspective();

		try 
		{
			SWTBotTreeItem packageToRename = selectAndReturnPackage();

			SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
			packageToRename.pressShortcut(SWT.ALT | SWT.COMMAND, 'r');
			SWTBotText textWithLabel = bot.textWithLabel("New name:");
			assertNotNull(textWithLabel);
			textWithLabel.setText("some.name");
			bot.shell("Rename Package").activate();
			SWTBotButton okButton = bot.button("OK");
			assertNotNull(okButton);
			okButton.click();
		} 
		catch (Exception e) 
		{
			fail("Renaming a package must not throw an exception! ("+ e.getMessage() + ")");
		}
	}

	private SWTBotTreeItem selectAndReturnPackage() 
	{
		SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");

		List<Tree> findControls = new ChildrenControlFinder(packageExplorerView.getWidget()).findControls(WidgetOfType.widgetOfType(Tree.class));
		if (findControls.isEmpty())
			fail("Tree in Package Explorer View was not found.");

		SWTBotTree tree = new SWTBotTree((Tree) findControls.get(0));
		SWTBotTreeItem projectNode = tree.expandNode("WorkspaceTestProject");
		tree.select(projectNode);

		SWTBotTreeItem[] projectNodeChildren = projectNode.getItems();
		projectNodeChildren[1].select();
		projectNodeChildren[1].expand();

		SWTBotTreeItem[] sourcesFolderChildren = projectNodeChildren[1].getItems();
		sourcesFolderChildren[1].select();

		return sourcesFolderChildren[1];
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

	private void initProjectWithPackage(String packageName) throws JavaModelException 
	{
		IPackageFragment somePackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, packageName);
		WorkspaceHelper.createJavaClass(somePackage, "SomeClass");
	}
}
