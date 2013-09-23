/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.handler;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.actions.CreateTestMethodEditorAction;
import org.moreunit.actions.CreateTestMethodHierarchyAction;
import org.moreunit.annotation.MoreUnitAnnotationModel;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodCreationResult;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.elements.TestmethodCreator.TestMethodCreationSettings;
import org.moreunit.elements.TypeFacade;
import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.ui.EditorUI;
import org.moreunit.util.MoreUnitContants;

/**
 * Executes the action "Create test method" launched from the handlers:<br>
 * <ul>
 * <li>key action: {@link CreateTestMethodActionHandler}</li>
 * <li>menu action provided by the popup menu in the editor:
 * {@link CreateTestMethodEditorAction}</li>
 * <li>menu action provided by the popup menu in the package explorer:
 * {@link CreateTestMethodHierarchyAction}</li>
 * </ul>
 * This executor is a singleton.
 * <p>
 * 30.09.2010 Gro The value of
 * {@link IAddTestMethodContext#isNewTestClassCreated()} is now correctly taken
 * from {@link ClassTypeFacade#isNewTestClassCreated()} instead of
 * {@link TestmethodCreator}
 *
 * @author vera 25.10.2005
 * @version 30.09.2010
 */
public class CreateTestMethodActionExecutor
{
    private static CreateTestMethodActionExecutor instance;

    private final EditorUI editorUI;
    private final Preferences preferences;

    // package-private for testing purposes
    CreateTestMethodActionExecutor(EditorUI editorUI, Preferences preferences)
    {
        this.editorUI = editorUI;
        this.preferences = preferences;
    }

    private CreateTestMethodActionExecutor()
    {
        this(new EditorUI(), Preferences.getInstance());
    }

    public static CreateTestMethodActionExecutor getInstance()
    {
        if(instance == null)
        {
            instance = new CreateTestMethodActionExecutor();
        }
        return instance;
    }

    public void executeCreateTestMethodAction(IEditorPart editorPart)
    {
        EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
        ICompilationUnit compilationUnit = editorPartFacade.getCompilationUnit();
        IMethod originalMethod = editorPartFacade.getFirstNonAnonymousMethodSurroundingCursorPosition();

        // Creates an intermediate object to clarify code that follows
        CreationContext context = createContext(compilationUnit, originalMethod);

        // Creates test method template
        ProjectPreferences prefs = preferences.getProjectView(editorPartFacade.getJavaProject());
        TestmethodCreator creator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(compilationUnit, context.testCaseUnit)
                .testCaseJustCreated(context.newTestClassCreated)
                .testType(prefs.getTestType())
                .generateComments(prefs.shouldGenerateCommentsForTestMethod())
                .defaultTestMethodContent(prefs.getTestMethodDefaultContent()));
        MethodCreationResult creationResult = creator.createTestMethod(originalMethod);

        if(creationResult.methodAlreadyExists())
        {
            editorUI.open(creationResult.getMethod());
        }
        else if(creationResult.methodCreated())
        {
            IMethod createdMethod = creationResult.getMethod();

            editorUI.open(createdMethod);

            if(createdMethod.getElementName().endsWith(MoreUnitContants.SUFFIX_NAME))
            {
                markMethodSuffix(editorPartFacade, createdMethod);
            }

            if(editorPart instanceof ITextEditor)
            {
                MoreUnitAnnotationModel.updateAnnotations((ITextEditor) editorPart);
            }
        }
    }

    private CreationContext createContext(ICompilationUnit currentlyEditedUnit, IMethod currentlyEditedMethod)
    {
        if(TypeFacade.isTestCase(currentlyEditedUnit.findPrimaryType()))
        {
            return new CreationContext(currentlyEditedUnit, false);
        }
        else
        {
            ClassTypeFacade classUnderTest = new ClassTypeFacade(currentlyEditedUnit);
            CorrespondingTestCase testCase = classUnderTest.getOneCorrespondingTestCase(true);

            // if the user cancels the test case selection wizard
            if(! testCase.found())
            {
                return null;
            }
            return new CreationContext(testCase.get().getCompilationUnit(), testCase.hasJustBeenCreated());
        }
    }

    private void markMethodSuffix(EditorPartFacade testCaseTypeFacade, IMethod newMethod)
    {
        ISelectionProvider selectionProvider = testCaseTypeFacade.getEditorPart().getSite().getSelectionProvider();

        ISelection exactSelection = null;
        try
        {
            ISourceRange range = newMethod.getNameRange();
            int offset = range.getOffset();
            int length = range.getLength();

            int suffixLength = MoreUnitContants.SUFFIX_NAME.length();
            exactSelection = new TextSelection(offset + length - suffixLength, suffixLength);
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        editorUI.reveal(testCaseTypeFacade.getEditorPart(), newMethod);
        selectionProvider.setSelection(exactSelection);
    }

    private static class CreationContext
    {
        final ICompilationUnit testCaseUnit;
        final boolean newTestClassCreated;

        CreationContext(ICompilationUnit testCaseUnit, boolean newTestClassCreated)
        {
            this.testCaseUnit = testCaseUnit;
            this.newTestClassCreated = newTestClassCreated;
        }
    }
}
