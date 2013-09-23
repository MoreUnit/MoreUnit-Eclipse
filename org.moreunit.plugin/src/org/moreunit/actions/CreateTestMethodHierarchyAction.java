package org.moreunit.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.elements.MethodCreationResult;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.elements.TestmethodCreator.TestMethodCreationSettings;
import org.moreunit.elements.TypeFacade;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.ui.EditorUI;

/**
 * This class delegates the action from the menu in the package explorer to
 * create a new testmethod.
 */
public class CreateTestMethodHierarchyAction implements IObjectActionDelegate
{
    private final EditorUI editorUI;
    private ISelection selection;

    public CreateTestMethodHierarchyAction()
    {
        this(new EditorUI());
    }

    public CreateTestMethodHierarchyAction(EditorUI editorUI)
    {
        this.editorUI = editorUI;
    }

    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart)
    {
        // void
    }

    @Override
    public void run(final IAction action)
    {
        if(! (this.selection instanceof IStructuredSelection))
            return;

        Object firstElement = ((IStructuredSelection) this.selection).getFirstElement();
        if(! (firstElement instanceof IMethod))
            return;

        createTestMethod((IMethod) firstElement);
    }

    private void createTestMethod(IMethod method)
    {
        ICompilationUnit cu = method.getCompilationUnit();
        ProjectPreferences prefs = preferencesFor(cu);
        if(prefs == null)
            return;

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cu)
                .testType(prefs.getTestType())
                .generateComments(prefs.shouldGenerateCommentsForTestMethod())
                .defaultTestMethodContent(prefs.getTestMethodDefaultContent()));

        MethodCreationResult result = testmethodCreator.createTestMethod(method);

        if(result.methodCreated())
        {
            editorUI.open(result.getMethod());
        }
    }

    private ProjectPreferences preferencesFor(ICompilationUnit cu)
    {
        final IJavaProject projectWithPrefs;
        if(TypeFacade.isTestCase(cu))
        {
            IType cut = new TestCaseTypeFacade(cu).getCorrespondingClassUnderTest();
            if(cut == null)
                return null;

            projectWithPrefs = cut.getJavaProject();
        }
        else
        {
            projectWithPrefs = cu.getJavaProject();
        }

        return Preferences.forProject(projectWithPrefs);
    }

    public void selectionChanged(final IAction action, final ISelection selection)
    {
        this.selection = selection;
    }
}
