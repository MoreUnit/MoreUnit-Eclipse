package org.moreunit.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.SourceFolderHandler;

public abstract class NewClassyWizardTestCase extends ContextTestCase
{
    @Before
    public void resetDialogSettings()
    {
        if(context.isDefined())
        {
            IDialogSettings dialogSettings = MoreUnitPlugin.getDefault().getDialogSettings();
            if(context.getMainProjectHandler() != null)
            {
                dialogSettings.put(getWizardClass().getName() + ".packageFragmentRoot." + context.getMainProjectHandler().getName(), (String) null);
            }
            if(context.getTestProjectHandler() != null)
            {
                dialogSettings.put(getWizardClass().getName() + ".packageFragmentRoot." + context.getTestProjectHandler().getName(), (String) null);
            }
        }
    }

    abstract protected Class< ? extends NewClassyWizard> getWizardClass();

    protected void addMapping(ProjectHandler project, SourceFolderHandler mainSrcFolder, SourceFolderHandler testSrcFolder)
    {
        List<SourceFolderMapping> mappingList = Preferences.getInstance().getSourceMappingList(project.get());
        mappingList.add(new SourceFolderMapping(project.get(), mainSrcFolder.get(), testSrcFolder.get()));
        Preferences.getInstance().setMappingList(project.get(), mappingList);
    }

    protected void willAutomaticallyValidateWhenOpen(NewClassyWizard wizard)
    {
        wizard.setWizardDialogFactory(new WizardDialogFactory()
        {
            @Override
            public WizardDialog createWizardDialog(Shell shell, final NewClassyWizard wizard)
            {
                return new WizardDialog(shell, wizard)
                {
                    @Override
                    public int open()
                    {
                        setBlockOnOpen(false);

                        // simply simulating a click on the "Finish" button does
                        // not work...
                        super.open();
                        wizard.performFinish();
                        close();

                        return Window.OK;
                    }
                };
            }
        });
    }

    @Properties(testType = TestType.JUNIT3,
        testClassPrefixes = "Som,Some",
        testClassSuffixes = "Tes,Test",
        testPackagePrefix = "pre",
        testPackageSuffix = "suf",
        testMethodPrefix = true)
    protected static class JUnit3WithVariousPrefixesAndSuffixes {}
    
    @org.moreunit.test.context.Preferences(testType = TestType.JUNIT3,
        testClassPrefixes = "Som,Some",
        testClassSuffixes = "Tes,Test",
        testPackagePrefix = "pre",
        testPackageSuffix = "suf",
        testMethodPrefix = true,
        testSrcFolder = "default-test-src")
    protected static class JUnit3WithVariousPrefixesAndSuffixesPreferences {}
}
