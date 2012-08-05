package org.moreunit.mock;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;
import org.moreunit.mock.wizard.MockDependenciesWizard;
import org.moreunit.mock.wizard.MockDependenciesWizardPage;
import org.moreunit.mock.wizard.WizardFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * A driver that allows for simulating user actions when a
 * MockDependenciesWizard is opened.
 */
public final class WizardDriver
{
    private MockDependenciesPageIsOpenAction action;

    /**
     * Creates a Guice module configured to use this driver.
     */
    public Module createModule()
    {
        return new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind(WizardDriver.class).toInstance(WizardDriver.this);
                bind(WizardFactory.class).toProvider(WizardFactoryProvider.class);
            }
        };
    }

    public MockDependenciesPageActions whenMockDependenciesPageIsOpen()
    {
        return new MockDependenciesPageActions(this);
    }

    public void whenMockDependenciesPageIsOpen(MockDependenciesPageIsOpenAction action)
    {
        this.action = action;
    }

    private void mockDependenciesPageOpen(MockDependenciesWizardPage page)
    {
        if(action != null)
        {
            action.execute(new MockDependenciesWizardDriver(page));
        }
    }

    private static class WizardFactoryProvider implements Provider<WizardFactory>
    {
        @Inject
        private WizardDriver wizardDriver;
        @Inject
        private Preferences preferences;
        @Inject
        private TemplateStyleSelector templateStyleSelector;
        @Inject
        private Logger logger;

        public WizardFactory get()
        {
            return new ConfigurableWizardFactory(wizardDriver, preferences, templateStyleSelector, logger);
        }
    }

    private static class ConfigurableWizardFactory extends WizardFactory
    {
        private final WizardDriver driver;
        private MockDependenciesWizardPage page;

        public ConfigurableWizardFactory(WizardDriver driver, Preferences preferences, TemplateStyleSelector templateStyleSelector, Logger logger)
        {
            super(preferences, templateStyleSelector, logger);
            this.driver = driver;
        }

        @Override
        public final MockDependenciesWizard createMockDependenciesWizard(MockDependenciesWizardPage page)
        {
            this.page = page;
            return new MockDependenciesWizard(this, page);
        }

        @Override
        public final WizardDialog createWizardDialog(Shell shell, MockDependenciesWizard wizard)
        {
            return new WizardDialog(shell, wizard)
            {
                public int open()
                {
                    setBlockOnOpen(false);
                    super.open();
                    driver.mockDependenciesPageOpen(page);
                    close();
                    return Window.OK;
                }
            };
        }
    }

    public static interface MockDependenciesPageIsOpenAction
    {
        void execute(MockDependenciesWizardDriver driver);
    }

    public static final class MockDependenciesWizardDriver
    {
        private final MockDependenciesWizardPage page;

        public MockDependenciesWizardDriver(MockDependenciesWizardPage page)
        {
            this.page = page;
        }

        public void checkAllElements()
        {
            page.checkElements(page.getTreeContentProvider().getTypes());
        }

        public void checkElements(String... elementNames)
        {
            Set<Object> elementsToCheck = newHashSet();

            for (String elName : elementNames)
            {
                for (Object type : page.getTreeContentProvider().getTypes())
                {
                    for (Object el : page.getTreeContentProvider().getChildren(type))
                    {
                        if(el instanceof IMember && ((IMember) el).getElementName().equals(elName))
                        {
                            elementsToCheck.add(el);
                            break;
                        }
                    }
                }
            }

            page.checkElements(elementsToCheck.toArray());
        }

        public void selectTemplate(String templateId)
        {
            page.selectTemplate(templateId);
        }
    }

    public static class MockDependenciesPageActions
    {
        private final WizardDriver driver;
        private String templateId;
        private boolean checkAllElements;
        private String[] elementsToCheck;

        public MockDependenciesPageActions(WizardDriver driver)
        {
            this.driver = driver;
        }

        public MockDependenciesPageActions selectTemplate(String templateId)
        {
            this.templateId = templateId;
            return this;
        }

        public MockDependenciesPageActions checkAllElements()
        {
            checkAllElements = true;
            return this;
        }

        public MockDependenciesPageActions checkElements(String... elementsToCheck)
        {
            this.elementsToCheck = elementsToCheck;
            return this;
        }

        public void done()
        {
            driver.whenMockDependenciesPageIsOpen(new MockDependenciesPageIsOpenAction()
            {
                public void execute(MockDependenciesWizardDriver driver)
                {
                    if(templateId != null)
                    {
                        driver.selectTemplate(templateId);
                    }
                    if(checkAllElements)
                    {
                        driver.checkAllElements();
                    }
                    if(elementsToCheck != null)
                    {
                        driver.checkElements(elementsToCheck);
                    }
                }
            });
        }
    }
}
