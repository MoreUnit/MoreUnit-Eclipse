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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import org.moreunit.actions.JumpAction;
import org.moreunit.actions.JumpFromCompilationUnitAction;
import org.moreunit.actions.JumpFromTypeAction;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TypeFacade;
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
 * This executor is a singleton.
 * 
 * @author vera 25.10.2005
 * @version 30.09.2010
 */
public class JumpActionExecutor
{
    private static JumpActionExecutor instance;

    private final EditorUI editorUI;

    // package-private for testing purposes
    JumpActionExecutor(EditorUI editorUI)
    {
        this.editorUI = editorUI;
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
        EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
        executeJumpAction(editorPartFacade.getCompilationUnit(), editorPartFacade.getFirstNonAnonymousMethodSurroundingCursorPosition());
    }

    public void executeJumpAction(ICompilationUnit compilationUnit)
    {
        executeJumpAction(compilationUnit, null);
    }

    public void executeJumpAction(IFile file)
    {
        executeJumpAction(JavaCore.createCompilationUnitFrom(file));
    }

    private void executeJumpAction(ICompilationUnit compilationUnit, IMethod methodUnderCursorPosition)
    {
        MethodSearchMode searchMode = Preferences.getInstance().getMethodSearchMode(compilationUnit.getJavaProject());

        TypeFacade typeFacade = TypeFacade.createFacade(compilationUnit);

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
            .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
            .withCurrentMethod(methodUnderCursorPosition) //
            .methodSearchMode(searchMode) //
            .createClassIfNoResult("Jump to...") //
            .build();

        IMember memberToJump = typeFacade.getOneCorrespondingMember(request);
        if(memberToJump != null)
        {
            jumpToMember(memberToJump);
        }
    }

    private void jumpToMember(IMember memberToJump)
    {
        if(memberToJump instanceof IMethod)
        {
            IMethod methodToJump = (IMethod) memberToJump;
            IEditorPart openedEditor = editorUI.open(methodToJump.getDeclaringType().getParent());
            revealInEditor(openedEditor, methodToJump);
        }
        else
        {
            editorUI.open(memberToJump.getParent());
        }
    }
}
