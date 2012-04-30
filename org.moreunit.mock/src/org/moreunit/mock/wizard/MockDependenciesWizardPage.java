package org.moreunit.mock.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.extensionpoints.NewTestCaseWizardPagePosition;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;

/**
 * Mostly copied from org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo.
 */
public class MockDependenciesWizardPage extends WizardPage implements INewTestCaseWizardPage
{
    private static final String PAGE_ID = MoreUnitMockPlugin.PLUGIN_ID + ".mockDependenciesWizardPage";

    private static final String DESCRIPTION = "Select dependencies for which mocks should be created.";
    private static final String CONSTRUCTOR_WARNING = "No constructor has been selected, and the class under test has no default one."
                                                      + " This will cause a compiler error in the generated test case.";

    private final IType classUnderTest;
    private final DependencyInjectionPointProvider injectionPointProvider;
    private final DependencyInjectionPointStore injectionPointStore;
    private final Preferences preferences;
    private final TemplateStyleSelector templateStyleSelector;
    private final Logger logger;
    private ContainerCheckedTreeViewer dependenciesTree;
    private Label selectedMembersLabel;

    public MockDependenciesWizardPage(IType classUnderTest, DependencyInjectionPointProvider injectionPointProvider, DependencyInjectionPointStore injectionPointStore, Preferences preferences, TemplateStyleSelector templateStyleSelector, Logger logger)
    {
        super(PAGE_ID);
        this.classUnderTest = classUnderTest;
        this.injectionPointProvider = injectionPointProvider;
        this.injectionPointStore = injectionPointStore;
        this.preferences = preferences;
        this.templateStyleSelector = templateStyleSelector;
        this.logger = logger;
        setTitle("Dependencies To Mock");
        setDescription(DESCRIPTION);
    }

    public String getId()
    {
        return PAGE_ID;
    }

    public IWizardPage getPage()
    {
        return this;
    }

    public NewTestCaseWizardPagePosition getPosition()
    {
        return NewTestCaseWizardPagePosition.after(INewTestCaseWizardPage.TEST_CASE_PAGE);
    }

    public void createControl(Composite parent)
    {
        Composite container = createContainer(parent);

        createTemplateSelector(container);

        createDependenciesTreeControls(container);

        setControl(container);
        Dialog.applyDialogFont(container);
    }

    private Composite createContainer(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);
        return container;
    }

    private void createTemplateSelector(Composite parent)
    {
        // uses project or workspace preferences, depending on user choice
        IJavaProject project = classUnderTest.getJavaProject();
        if(! preferences.hasSpecificSettings(project))
        {
            project = null;
        }
        templateStyleSelector.createContents(parent, project);
    }

    private void createDependenciesTreeControls(Composite container)
    {
        createAvailableDependenciesLabel(container);
        createDependenciesTree(container);
        createSideButtons(container);
        createSelectedDependenciesLabel(container);
    }

    private void createAvailableDependenciesLabel(Composite container)
    {
        Label label = new Label(container, SWT.LEFT | SWT.WRAP);
        label.setFont(container.getFont());
        label.setText("Available dependencies:");
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);
    }

    private void createDependenciesTree(Composite container)
    {
        dependenciesTree = new ContainerCheckedTreeViewer(container, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        gd.heightHint = 180;
        dependenciesTree.getTree().setLayoutData(gd);

        dependenciesTree.setLabelProvider(new JavaElementLabelProvider());
        dependenciesTree.setAutoExpandLevel(2);
        dependenciesTree.addCheckStateListener(new ICheckStateListener()
        {
            public void checkStateChanged(CheckStateChangedEvent event)
            {
                doCheckedStateChanged();
            }
        });
    }

    private void doCheckedStateChanged()
    {
        List<IMember> members = getCheckedInjectionPoints();

        injectionPointStore.setInjectionPoints(members);

        updateSelectedMembersLabel(members);

        updateMessage(members);
    }

    private List<IMember> getCheckedInjectionPoints()
    {
        Object[] checked = dependenciesTree.getCheckedElements();
        List<IMember> members = new ArrayList<IMember>(checked.length);

        for (Object checkedElement : checked)
        {
            if(checkedElement instanceof IMethod || checkedElement instanceof IField)
            {
                members.add((IMember) checkedElement);
            }
        }
        return members;
    }

    private void updateSelectedMembersLabel(List<IMember> members)
    {
        final String label;
        if(members.size() == 1)
        {
            label = MessageFormat.format("{0} member selected", new Object[] { members.size() });
        }
        else
        {
            label = MessageFormat.format("{0} members selected", new Object[] { members.size() });
        }
        selectedMembersLabel.setText(label);
    }

    private void updateMessage(List<IMember> selectedMembers)
    {
        Collection<IMethod> selectedConstructors;
        try
        {
            selectedConstructors = injectionPointStore.getConstructors();
        }
        catch (JavaModelException e)
        {
            // ignored
            selectedConstructors = Collections.<IMethod> emptyList();
        }

        if(! selectedMembers.isEmpty() && selectedConstructors.isEmpty() && ! hasDefaultConstructor(classUnderTest))
        {
            setMessage(CONSTRUCTOR_WARNING, IMessageProvider.WARNING);
        }
        else
        {
            setMessage(DESCRIPTION);
        }
    }

    private boolean hasDefaultConstructor(IType type)
    {
        int constructorCount = 0;
        try
        {
            for (IMethod m : type.getMethods())
            {
                if(m.isConstructor())
                {
                    if(m.getNumberOfParameters() == 0)
                    {
                        return true;
                    }

                    constructorCount++;
                }
            }
        }
        catch (JavaModelException e)
        {
            // will return false
        }
        return constructorCount == 0;
    }

    private void createSideButtons(Composite container)
    {
        Composite buttonContainer = new Composite(container, SWT.NONE);
        buttonContainer.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonContainer.setLayout(buttonLayout);

        createSideButton(buttonContainer, "Select All", new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                checkElements(getCheckableElements());
            }
        });

        createSideButton(buttonContainer, "Deselect All", new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                checkElements(new Object[0]);
            }
        });
    }

    private Button createSideButton(Composite buttonContainer, String text, SelectionListener selectionListener)
    {
        Button button = new Button(buttonContainer, SWT.PUSH);
        button.setText(text);
        button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        button.addSelectionListener(selectionListener);
        LayoutUtil.setButtonDimensionHint(button);
        return button;
    }

    public Object[] getCheckableElements()
    {
        return (Object[]) dependenciesTree.getInput();
    }

    public void checkElements(Object[] elements)
    {
        dependenciesTree.setCheckedElements(elements);
        doCheckedStateChanged();
    }

    private void createSelectedDependenciesLabel(Composite container)
    {
        selectedMembersLabel = new Label(container, SWT.LEFT);
        selectedMembersLabel.setFont(container.getFont());

        doCheckedStateChanged();

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        selectedMembersLabel.setLayoutData(gd);

        Label emptyLabel = new Label(container, SWT.LEFT);
        gd = new GridData();
        gd.horizontalSpan = 1;
        emptyLabel.setLayoutData(gd);
    }

    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if(! visible)
        {
            return;
        }

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, injectionPointProvider, logger);
        dependenciesTree.setContentProvider(contentProvider);
        dependenciesTree.setInput(contentProvider.getTypes());
        dependenciesTree.setSelection(new StructuredSelection(classUnderTest), true);

        doCheckedStateChanged();

        dependenciesTree.getControl().setFocus();
    }

    public IType getClassUnderTest()
    {
        return classUnderTest;
    }

    public DependencyInjectionPointStore getInjectionPointStore()
    {
        return injectionPointStore;
    }

    public void validated()
    {
        // saves new user settings for project or workspace
        templateStyleSelector.savePreferences();
    }

    public void selectTemplate(String templateId)
    {
        templateStyleSelector.selectTemplate(templateId);
    }
}
