package org.moreunit.mock;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.mock.wizard.MockDependenciesWizard;
import org.moreunit.mock.wizard.MockDependenciesWizardPage;
import org.moreunit.mock.wizard.WizardFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * A driver that allows for simulating user actions when a
 * MockDependenciesWizard is opened.
 */
public final class WizardDriver
{
    private MockDependenciesPageIsOpenAction action;

    /**
     * Creates a {@link WizardFactory} producing wizards that can be driven by
     * this driver.
     */
    public WizardFactory createWizardFactory()
    {
        return new ConfigurableWizardFactory(this);
    }

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
                bind(WizardFactory.class).toInstance(createWizardFactory());
            }
        };
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

    private static class ConfigurableWizardFactory extends WizardFactory
    {
        private final WizardDriver driver;
        private MockDependenciesWizardPage page;

        public ConfigurableWizardFactory(WizardDriver driver)
        {
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

        public Members getCheckableElements()
        {
            return new Members(page.getCheckableElements());
        }

        public void checkAllElements()
        {
            page.checkElements(page.getCheckableElements());
        }

        public void checkElement(IMember member)
        {

        }
    }

    public static final class Members implements Iterable<IMember>
    {
        private static class SetterPredicate implements Predicate<IMember>
        {
            public boolean apply(IMember m)
            {
                try
                {
                    return m instanceof IMethod && ! ((IMethod) m).isConstructor();
                }
                catch (JavaModelException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        private static class FieldPredicate implements Predicate<IMember>
        {
            public boolean apply(IMember m)
            {
                return m instanceof IField;
            }
        }

        private static class ConstructorPredicate implements Predicate<IMember>
        {
            public boolean apply(IMember m)
            {
                try
                {
                    return m instanceof IMethod && ((IMethod) m).isConstructor();
                }
                catch (JavaModelException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        private static class NamePredicate implements Predicate<IMember>
        {
            private final String name;

            public NamePredicate(String name)
            {
                this.name = name;
            }

            public boolean apply(IMember m)
            {
                return m.getElementName().equals(name);
            }
        }

        private final List<IMember> members = newArrayList();

        public Members(Object[] members)
        {
            for (Object member : members)
            {
                this.members.add((IMember) member);
            }
        }

        public IMember get(int index)
        {
            return members.get(index);
        }

        public Iterator<IMember> iterator()
        {
            return members.iterator();
        }

        public IMethod getConstructor(final String name)
        {
            return (IMethod) find(members, and(new ConstructorPredicate(), new NamePredicate(name)));
        }

        public IField getField(final String name)
        {
            return (IField) find(members, and(new FieldPredicate(), new NamePredicate(name)));
        }

        public IMethod getSetter(final String name)
        {
            return (IMethod) find(members, and(new SetterPredicate(), new NamePredicate(name)));
        }

        @SuppressWarnings("unchecked")
        public List<IMethod> getConstructors()
        {
            return newArrayList((Iterable< ? extends IMethod>) Iterables.filter(members, new ConstructorPredicate()));
        }

        @SuppressWarnings("unchecked")
        public List<IField> getFields()
        {
            return newArrayList((Iterable< ? extends IField>) Iterables.filter(members, new FieldPredicate()));
        }

        @SuppressWarnings("unchecked")
        public List<IMethod> getSetters()
        {
            return newArrayList((Iterable< ? extends IMethod>) Iterables.filter(members, new SetterPredicate()));
        }
    }
}
