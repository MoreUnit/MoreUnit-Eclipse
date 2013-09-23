package org.moreunit.ui;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodTreeContentProvider;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.elements.TestmethodCreator.TestMethodCreationSettings;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;

/**
 * @author vera, modified 10.08.2010 andreas BugID: 3042170.
 */
public class MethodPage extends Page implements IElementChangedListener, IDoubleClickListener
{
    private EditorPartFacade editorPartFacade;
    private TreeViewer treeViewer;

    Action addTestAction;
    Action filterPrivateAction;
    Action filterGetterAction;

    private MethodTreeContentProvider methodTreeContentProvider;

    public MethodPage(EditorPartFacade editorPartFacade)
    {
        super();

        this.editorPartFacade = editorPartFacade;
        JavaCore.addElementChangedListener(this);
    }

    @Override
    public void createControl(Composite parent)
    {
        this.treeViewer = new TreeViewer(parent);
        this.methodTreeContentProvider = new MethodTreeContentProvider(this.editorPartFacade.getCompilationUnit().findPrimaryType());
        this.treeViewer.setContentProvider(this.methodTreeContentProvider);
        this.treeViewer.setLabelProvider(new JavaElementLabelProvider());
        this.treeViewer.setInput(this);
        this.treeViewer.addDoubleClickListener(this);

        createToolbar();
    }

    public void setNewEditorPartFacade(EditorPartFacade editorPartFacade)
    {
        this.methodTreeContentProvider = new MethodTreeContentProvider(editorPartFacade.getCompilationUnit().findPrimaryType());
        this.methodTreeContentProvider.setGetterFiltered(filterGetterAction.isChecked());
        this.methodTreeContentProvider.setPrivateFiltered(filterPrivateAction.isChecked());
        this.editorPartFacade = editorPartFacade;
        this.treeViewer.setContentProvider(this.methodTreeContentProvider);
    }

    public IType getInputType()
    {
        return this.editorPartFacade.getCompilationUnit().findPrimaryType();
    }

    private void createToolbar()
    {
        this.filterPrivateAction = new Action("", IAction.AS_CHECK_BOX)
        {
            @Override
            public void run()
            {
                actionFilterPrivateMethods();
            }
        };
        this.filterPrivateAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/private.gif"));
        this.filterPrivateAction.setChecked(false);
        this.filterPrivateAction.setToolTipText("Filter private methods");

        this.filterGetterAction = new Action("", IAction.AS_CHECK_BOX)
        {
            @Override
            public void run()
            {
                actionFilterGetterMethods();
            }
        };
        this.filterGetterAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/getter.gif"));
        this.filterGetterAction.setChecked(false);
        this.filterGetterAction.setToolTipText("Filter getter/setter");

        this.addTestAction = new Action("Add...")
        {
            @Override
            public void run()
            {
                addItem();
            }
        };
        this.addTestAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/add.png"));
        this.addTestAction.setToolTipText("Add test");

        IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
        toolBarManager.add(this.filterPrivateAction);
        toolBarManager.add(this.filterGetterAction);
        toolBarManager.add(this.addTestAction);
    }

    private void actionFilterPrivateMethods()
    {
        this.methodTreeContentProvider.setPrivateFiltered(this.filterPrivateAction.isChecked());
        updateUI();
    }

    private void actionFilterGetterMethods()
    {
        this.methodTreeContentProvider.setGetterFiltered(this.filterGetterAction.isChecked());
        updateUI();
    }

    @SuppressWarnings("unchecked")
    private void addItem()
    {
        ITreeSelection selection = (ITreeSelection) this.treeViewer.getSelection();
        if(selection.isEmpty())
        {
            return;
        }

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(this.editorPartFacade.getEditorPart());
        CorrespondingTestCase testCase = classTypeFacade.getOneCorrespondingTestCase(true);

        if(! testCase.found() || ! testCase.get().exists())
        {
            return;
        }

        ProjectPreferences prefs = Preferences.forProject(this.editorPartFacade.getJavaProject());

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(this.editorPartFacade.getCompilationUnit(), testCase.get().getCompilationUnit())
                .testCaseJustCreated(testCase.hasJustBeenCreated())
                .testType(prefs.getTestType())
                .generateComments(prefs.shouldGenerateCommentsForTestMethod())
                .defaultTestMethodContent(prefs.getTestMethodDefaultContent()));

        testmethodCreator.createTestMethods(selection.toList());

        updateUI();
    }

    @Override
    public Control getControl()
    {
        if(this.treeViewer != null)
        {
            return this.treeViewer.getControl();
        }

        return null;
    }

    @Override
    public void setFocus()
    {
        getControl().setFocus();
    }

    public void updateUI()
    {
        this.treeViewer.refresh();
    }

    public void elementChanged(ElementChangedEvent event)
    {
        int type = event.getDelta().getElement().getElementType();
        switch (type)
        {
        case (IJavaElement.COMPILATION_UNIT):
            updateUIafterElementChangedEvent();
            break;
        default:
        {
        }
        }
    }

    private void updateUIafterElementChangedEvent()
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                updateUI();
            }
        });
    }

    @Override
    public void dispose()
    {
        JavaCore.removeElementChangedListener(this);
        super.dispose();
    }

    public void doubleClick(DoubleClickEvent event)
    {
        ITreeSelection selection = (ITreeSelection) this.treeViewer.getSelection();

        IMethod method = (IMethod) selection.getFirstElement();
        new EditorUI().open(method);
    }

}
