package org.moreunit.refactoring;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.anyOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withLabel;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.ChildrenControlFinder;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.WorkspaceTestCase;

/**
 * @author vera
 */
public class RenamePackageTest extends SimpleProjectTestCase
{
	@Test
	public void testRenamePackage() throws Exception
	{
		IPackageFragment somePackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, "somepackage");
		WorkspaceHelper.createJavaClass(somePackage, "SomeClass");
		
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		
		bot.menu("Window").menu("Open Perspective").menu("Other...").click();
		SWTBotShell openPerspectiveShell = bot.shell("Open Perspective");
		openPerspectiveShell.activate();
		 
		// select the dialog
		bot.table().select("Java");
		bot.button("OK").click();
		
		SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");
		
		 List<Tree> findControls = new ChildrenControlFinder(packageExplorerView.getWidget()).findControls(WidgetOfType.widgetOfType(Tree.class));
	     if (findControls.isEmpty())
	    	 fail("Tree in Package Explorer View was not found.");
	     
	     SWTBotTree tree = new SWTBotTree((Tree) findControls.get(0));
	     SWTBotTreeItem projectNode = tree.expandNode("WorkspaceTestProject");
	     SWTBotTree projectNodeTree = tree.select(projectNode);
	     //SWTBotTreeItem sourcesTreeItem = tree.getTreeItem("sources");
	     //assertNotNull(sourcesTreeItem);
	     
	     SWTBotTreeItem[] projectNodeChildren = projectNode.getItems();
	     projectNodeChildren[1].select();
	     projectNodeChildren[1].expand();
	     
	     SWTBotTreeItem[] sourcesFolderChildren = projectNodeChildren[1].getItems();
	     sourcesFolderChildren[1].select();
	     
	     //SWTBotMenu refactorMenu = sourcesFolderChildren[1].contextMenu("Refactor");
	     //KeyStroke[] rKey = Keystrokes.toKeys(KeyStroke.NO_KEY, 'r');
	     //KeyStroke[] allKeys = new KeyStroke[rKey.length+2];
	     
	     SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	     sourcesFolderChildren[1].pressShortcut(SWT.ALT|SWT.COMMAND, 'r');
	     //SWTBotView renamePackageView = bot.viewByTitle("Rename package");
	     SWTBotText textWithLabel = bot.textWithLabel("New name:");
	     assertNotNull(textWithLabel);
	     textWithLabel.setText("some.name");
	     System.out.println("Active shell: "+bot.activeShell().getText());
	     //Matcher<Widget> matcher = anyOf(widgetOfType(Button.class), withLabel("OK"));
	     //Widget widget = bot.widget(matcher);
	     //SWTBotButton button = new SWTBotButton((Button) widget);
	     //button.click();
	     //assertNotNull(widget);
	     //bot.
	     bot.shell("Rename Package").activate();
	     SWTBotButton okButton = bot.button("OK");
	     assertNotNull(okButton);
	     okButton.click();
	     
	     //assertNotNull(refactorMenu);
	     //assertNotNull(projectNodeTree);
	     //SWTBotTreeItem sourcesNode = projectNodeTree.expandNode("sources");
	     //SWTBotTree sourcesNodeTree = projectNodeTree.select(sourcesNode);
	     //SWTBotTree packageNode = sourcesNodeTree.select("somepackage");
	     //assertNotNull(packageNode);
	     //SWTBotMenu refactorMenu = packageNode.contextMenu("Refactor");
	     //assertNotNull(refactorMenu);
		/*
		SWTBotView view = bot.viewByTitle("Package Explorer");
        List controls = new ChildrenControlFinder(view.getWidget()).findControls(WidgetOfType.widgetOfType(Tree.class));
        if (controls.isEmpty())
            fail("Tree in Package Explorer View was not found.");
        SWTBotTree tree = new SWTBotTree((Tree) controls.get(0));
       
        SWTBotTreeItem item = tree.getTreeItem("AndroidPdfViewer");
        if(item == null){
             throw new WidgetNotFoundException("Could not find menu: test");         }
        item.setFocus(); 
		 */
		packageExplorerView.setFocus();
		//SWTBotTable table = bot.table();
		//assertNotNull(table);
		assertEquals(2, 2);
	}
}
