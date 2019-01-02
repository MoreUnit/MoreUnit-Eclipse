/**
 * 
 */
package org.moreunit;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartName;
import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForView;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.ChildrenControlFinder;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardLayout;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.internal.ide.handlers.OpenResourceHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.moreunit.log.LogHandler;
import org.moreunit.test.context.TestContextRule;

/**
 * @author gianasista
 */
public class JavaProjectSWTBotTestHelper
{
    static ShortcutStrategy shortcutStrategy = ShortcutStrategy.createShortcutStrategy();

    @Rule
    public final TestContextRule context = new TestContextRule();

    protected static SWTWorkbenchBot bot;
    private static boolean isWorkspacePrepared;

    @BeforeClass
    public static void initialize()
    {
        bot = new SWTWorkbenchBot();

        if(! isWorkspacePrepared)
        {
            // SWTBotPreferences.PLAYBACK_DELAY = 10;
            
            // not all keyboard layouts are supported by SWTBot
            // e.g. MAC_DE is missing (see http://wiki.eclipse.org/SWTBot/Keyboard_Layouts)
            // therefore we do check if SWTBot can find the default layout
            // and if not we do set it to a default value which is supported
            try
            {
                KeyboardLayout.getDefaultKeyboardLayout();
            }
            catch(IllegalArgumentException exc)
            {
                SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
            }
            switchToJavaPerspective();
            isWorkspacePrepared = true;
        }
    }

    /*
     * Weird behavior occurs, when editor do not get closed before and after
     * each test, e.g. methods are missing from a class...
     */
    @Before
    @After
    public void afterAndAfter()
    {
        for (SWTBotEditor editor : bot.editors())
        {
            editor.close();
        }
    }

    private static void switchToJavaPerspective()
    {
        closeWelcomeViewIfActive();

        bot.perspectiveById("org.eclipse.jdt.ui.JavaPerspective").activate();

        // activating the java perspective takes a short moment
        bot.waitUntilWidgetAppears(waitForView(withTitle("Outline")));
    }

    private static void closeWelcomeViewIfActive()
    {
        try
        {
            SWTBotView welcomeView = bot.viewByTitle("Welcome");
            if(welcomeView.isActive())
            {
                welcomeView.close();
            }
        }
        catch (WidgetNotFoundException e)
        {
            // ignored
        }
    }

    private static Matcher<IViewReference> withTitle(final String title)
    {
        return withPartName(title);
    }

    protected void openResource(String resourceName)
    {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run()
            {
                try
                {
                    new OpenResourceHandler().execute(new ExecutionEvent());
                }
                catch (ExecutionException e)
                {
                    LogHandler.getInstance().handleExceptionLog(e);
                }
            }
           
        });
        SWTBotHelper.forceSWTBotShellsRecomputeNameCache(bot);
        bot.waitUntil(Conditions.shellIsActive("Open Resource"));
        SWTBotText searchField = new SWTBotText(bot.widget(widgetOfType(Text.class)));
        searchField.setText(resourceName);

        bot.waitUntil(new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return bot.table().rowCount() > 0;
            }

            @Override
            public String getFailureMessage()
            {
                return null;
            }
        });
        KeyboardFactory.getAWTKeyboard().pressShortcut(Keystrokes.DOWN);
        bot.button("Open").click();
        bot.waitUntilWidgetAppears(new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return ! new SWTWorkbenchBot().editors().isEmpty();
            }

            @Override
            public String getFailureMessage()
            {
                return null;
            }
        });
    }

    protected void waitForChooseDialog()
    {
        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                return bot.activeShell() != null;
            }

            @Override
            public String getFailureMessage()
            {
                return "ChooseDialog did not appear.";
            }
        });
    }

    protected IJavaProject getJavaProjectFromContext()
    {
        return context.getProjectHandler().get();
    }

    protected SWTBotTreeItem selectAndReturnJavaProjectFromPackageExplorer()
    {
        SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");

        List<Tree> findControls = new ChildrenControlFinder(packageExplorerView.getWidget()).findControls(WidgetOfType.widgetOfType(Tree.class));
        if(findControls.isEmpty())
            fail("Tree in Package Explorer View was not found.");

        SWTBotTree tree = new SWTBotTree(findControls.get(0));

        SWTBotTreeItem projectNode = tree.expandNode(getProjectNameFromContext());
        tree.select(projectNode);

        return projectNode;
    }

    private String getProjectNameFromContext()
    {
        return context.getProjectHandler().get().getElementName();
    }

    protected SWTBotTreeItem selectAndReturnPackageWithName(String packageName)
    {
        SWTBotTreeItem projectNode = selectAndReturnJavaProjectFromPackageExplorer();

        SWTBotTreeItem sourcesFolder = projectNode.getNode("src");
        sourcesFolder.select();
        sourcesFolder.expand();

        SWTBotTreeItem orgPackage = sourcesFolder.getNode(packageName);
        orgPackage.select();

        return orgPackage;
    }

    protected ShortcutStrategy getShortcutStrategy()
    {
        return shortcutStrategy;
    }

    protected int getCmdOrStrgKeyForShortcutsDependentOnPlattform()
    {
        return isRunningOnLinuxOrWindows() ? SWT.CTRL : SWT.COMMAND;
    }

    protected static boolean isRunningOnLinuxOrWindows() 
    {
        String osName = System.getProperty("os.name");
        return osName.contains("Linux") || osName.contains("Win");
    }

    protected Matcher<Shell> shellWithTextStartingWith(final String textStart)
    {
        return new BaseMatcher<Shell>()
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("with text starting with '").appendText(textStart).appendText("'"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            @Override
            public boolean matches(Object item)
            {
                final Shell shell = (Shell) item;
                String text = UIThreadRunnable.syncExec(shell.getDisplay(), new Result<String>()
                {
                    public String run()
                    {
                        return shell.getText();
                    }
                });
                return text.startsWith(textStart);
            }
        };
    }
}
