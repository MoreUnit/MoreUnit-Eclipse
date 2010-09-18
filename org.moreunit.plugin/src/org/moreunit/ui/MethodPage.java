package org.moreunit.ui;

import java.util.Iterator;

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
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodTreeContentProvider;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.extensionpoints.AddTestMethodParticipatorHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera, modified 10.08.2010 andreas BugID: 3042170.
 */
public class MethodPage extends Page implements IElementChangedListener
{

    //private ListViewer listViewer;
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

        createToolbar();
    }

    public void setNewEditorPartFacade(EditorPartFacade editorPartFacade)
    {
        this.methodTreeContentProvider = new MethodTreeContentProvider(editorPartFacade.getCompilationUnit().findPrimaryType());
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
        this.filterPrivateAction.setChecked(true);
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
        this.filterGetterAction.setChecked(true);
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

    private void addItem()
    {
        ITreeSelection selection = (ITreeSelection) this.treeViewer.getSelection();
        if(selection.isEmpty())
        {
            return;
        }

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(this.editorPartFacade.getEditorPart());
        IType typeOfTestCaseClassFromJavaFile = classTypeFacade.getOneCorrespondingTestCase(true);

        if((typeOfTestCaseClassFromJavaFile == null) || ! typeOfTestCaseClassFromJavaFile.exists())
        {
            return;
        }

        //TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(typeOfTestCaseClassFromJavaFile.getCompilationUnit());
        for (Iterator<?> allSelected = selection.iterator(); allSelected.hasNext();)
        {
            IMethod methodUnderTest = (IMethod) allSelected.next();
            TestmethodCreator testmethodCreator = new TestmethodCreator(this.editorPartFacade.getCompilationUnit(), Preferences.getInstance().getTestType(this.editorPartFacade.getJavaProject()), Preferences.getInstance().getTestMethodDefaultContent(this.editorPartFacade.getJavaProject()));
            IMethod createdMethod = testmethodCreator.createTestMethod(methodUnderTest);
            
            // Call extensions on extension point, allowing to modify the created testmethod
            AddTestMethodParticipatorHandler.getInstance().callExtension(createdMethod, methodUnderTest);
        }

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

}
