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

import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

import java.util.function.UnaryOperator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.moreunit.actions.JumpAction;
import org.moreunit.actions.JumpFromCompilationUnitAction;
import org.moreunit.actions.JumpFromTypeAction;
import org.moreunit.core.util.Jobs;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.navigation.JumpLocation;
import org.moreunit.navigation.JumpNavigationHistory;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.ui.EditorUI;

/**
 * Executes the actions "Jump to *" launched from the handlers:<br>
 * <ul>
 * <li>key action: {@link JumpActionHandler}</li>
 * <li>menu action provided by the popup menu in the editor: {@link JumpAction}</li>
 * <li>menu action provided by the popup menu in the package explorer:
 * {@link JumpFromCompilationUnitAction} and {@link JumpFromTypeAction}</li>
 * </ul>
 * It also executes the actions "Jump Back" and "Jump Forward", which navigate
 * in the {@link JumpNavigationHistory} filled by the previous jumps.
 * This executor is a singleton.
 *
 * @author vera 25.10.2005
 * @version 30.09.2010
 */
public class JumpActionExecutor
{
    private static JumpActionExecutor instance;

    private final EditorUI editorUI;
    private final JumpNavigationHistory navigationHistory;

    // package-private for testing purposes
    JumpActionExecutor(EditorUI editorUI, JumpNavigationHistory navigationHistory)
    {
        this.editorUI = editorUI;
        this.navigationHistory = navigationHistory;
    }

    // package-private for testing purposes
    JumpActionExecutor(EditorUI editorUI)
    {
        this(editorUI, JumpNavigationHistory.getInstance());
    }

    private JumpActionExecutor()
    {
        this(new EditorUI());
    }

    public static JumpActionExecutor getInstance()
    {
        if(instance == null)
        {
            instance = new JumpActionExecutor();
        }
        return instance;
    }

    void revealInEditor(IEditorPart editorPart, IMethod method)
    {
        editorUI.reveal(editorPart, method);
    }

    public void executeJumpAction(IEditorPart editorPart)
    {
        final EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
        executeJumpAction(editorPartFacade.getCompilationUnit(), editorPartFacade.getFirstNonAnonymousMethodSurroundingCursorPosition(), locationOf(editorPart));
    }

    public void executeJumpAction(ICompilationUnit compilationUnit)
    {
        executeJumpAction(compilationUnit, null, JumpLocation.of(compilationUnit));
    }

    public void executeJumpAction(IFile file)
    {
        executeJumpAction(JavaCore.createCompilationUnitFrom(file));
    }

    /**
     * Goes back to the location the user jumped from.
     */
    public void executeJumpBackAction(IEditorPart currentEditorPart)
    {
        navigateInHistory(currentEditorPart, navigationHistory::goBack);
    }

    /**
     * Goes forward to the location the user jumped back from.
     */
    public void executeJumpForwardAction(IEditorPart currentEditorPart)
    {
        navigateInHistory(currentEditorPart, navigationHistory::goForward);
    }

    private void navigateInHistory(IEditorPart currentEditorPart, UnaryOperator<JumpLocation> navigation)
    {
        final JumpLocation location = navigation.apply(locationOf(currentEditorPart));
        if(location != null)
        {
            navigateTo(location);
        }
    }

    private JumpLocation locationOf(IEditorPart editorPart)
    {
        if(editorPart == null)
        {
            return null;
        }

        final EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
        final ICompilationUnit compilationUnit = editorPartFacade.getCompilationUnit();
        if(compilationUnit == null)
        {
            return null;
        }

        final ITextSelection textSelection = editorPartFacade.getTextSelection();
        return new JumpLocation(compilationUnit, textSelection == null ? JumpLocation.UNKNOWN_OFFSET : textSelection.getOffset());
    }

    private void navigateTo(JumpLocation location)
    {
        final IEditorPart openedEditor = editorUI.open(location.getCompilationUnit());
        editorUI.revealOffset(openedEditor, location.getOffset());
    }

    private void executeJumpAction(ICompilationUnit compilationUnit, IMethod methodUnderCursorPosition, JumpLocation origin)
    {
        Jobs.waitForIndexExecuteAndRunInUI("Jump to ... ", () -> {
            final MethodSearchMode searchMode = Preferences.getInstance().getMethodSearchMode(compilationUnit.getJavaProject());

            final TypeFacade typeFacade = TypeFacade.createFacade(compilationUnit);

            final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                    .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                    .withCurrentMethod(methodUnderCursorPosition) //
                    .methodSearchMode(searchMode) //
                    .createClassIfNoResult("Jump to...") //
                    .build();

            return typeFacade.getOneCorrespondingMember(request);
        }, memberToJump -> jumpToMember(origin, memberToJump));
    }

    /**
     * Jumps to the given member and registers the jump into the navigation
     * history, so that the user can go back to where (s)he jumped from.
     */
    public void jumpToMember(JumpLocation origin, IMember memberToJump)
    {
        if(memberToJump == null)
        {
            return;
        }

        final JumpLocation destination = JumpLocation.of(memberToJump);

        if(memberToJump instanceof final IMethod methodToJump)
        {
            final IEditorPart openedEditor = editorUI.open(methodToJump.getDeclaringType().getParent());
            revealInEditor(openedEditor, methodToJump);
        }
        else
        {
            editorUI.open(memberToJump.getParent());
        }

        navigationHistory.registerJump(origin, destination);
    }
}
