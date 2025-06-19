package org.moreunit.codemining;

import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineEndCodeMining;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorPart;
import org.moreunit.core.util.Jobs;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.preferences.TestAnnotationMode;
import org.moreunit.ui.EditorUI;

/**
 * {@link ICodeMining} to "jump" from tested class/method to test class/method
 * and from test class/method to tested class/method
 */
public class JumpCodeMining extends LineEndCodeMining
{

    private final IJavaElement element;

    public JumpCodeMining(IJavaElement element, IDocument document, ICodeMiningProvider provider) throws JavaModelException, BadLocationException
    {
        super(document, getLineNumber(element, document), provider);
        this.element = element;
    }

    private static int getLineNumber(IJavaElement element, IDocument document) throws JavaModelException, BadLocationException
    {
        ISourceRange r = ((ISourceReference) element).getNameRange();
        int offset = r.getOffset();
        return document.getLineOfOffset(offset);
    }

    @Override
    protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor)
    {
        return CompletableFuture.runAsync(() -> {
            IMember member = (IMember) element;
            TypeFacade typeFacade = TypeFacade.createFacade(member.getCompilationUnit());
            String testOrTested = typeFacade instanceof TestCaseTypeFacade ? "tested" : "test";
            if(element instanceof IType)
            {
                boolean jumpable = false;
                if(typeFacade instanceof ClassTypeFacade)
                {
                    ClassTypeFacade classTypeFacade = (ClassTypeFacade) typeFacade;
                    jumpable = classTypeFacade.hasTestCase();
                }
                else if(typeFacade instanceof TestCaseTypeFacade)
                {
                    TestCaseTypeFacade testCaseTypeFacade = (TestCaseTypeFacade) typeFacade;
                    IType correspondingClassUnderTest = testCaseTypeFacade.getCorrespondingClassUnderTest();
                    jumpable = correspondingClassUnderTest != null;
                }
                if(jumpable)
                {
                    setLabel(" Jump to " + testOrTested + " class");
                }
                else
                {
                    setLabel("");
                }
            }
            else if(element instanceof IMethod)
            {
                IMethod method = (IMethod) element;
                boolean jumpable = false;
                if(typeFacade instanceof ClassTypeFacade)
                {
                    ClassTypeFacade classTypeFacade = (ClassTypeFacade) typeFacade;
                    jumpable = ! (classTypeFacade.getCorrespondingTestMethods(method, TestAnnotationMode.BY_CALL_AND_BY_NAME.getMethodSearchMode()).isEmpty());
                }
                else if(typeFacade instanceof TestCaseTypeFacade)
                {
                    TestCaseTypeFacade testCaseTypeFacade = (TestCaseTypeFacade) typeFacade;
                    IType correspondingClassUnderTest = testCaseTypeFacade.getCorrespondingClassUnderTest();
                    if(correspondingClassUnderTest != null)
                    {
                        jumpable = ! (testCaseTypeFacade.getCorrespondingTestedMethods(method, correspondingClassUnderTest).isEmpty());
                    }
                }
                if(jumpable)
                {
                    setLabel(" Jump to " + testOrTested + " method");
                }
                else
                {
                    setLabel("");
                }
            }
        });
    }

    @Override
    public Consumer<MouseEvent> getAction()
    {
        return e -> {
            Jobs.waitForIndexExecuteAndRunInUI("Jump to ... ", () -> {
            MethodSearchMode searchMode = Preferences.getInstance().getMethodSearchMode(element.getJavaProject());

            TypeFacade typeFacade = TypeFacade.createFacade(((IMember) element).getCompilationUnit());

            String testOrTested = typeFacade instanceof TestCaseTypeFacade ? "tested" : "test";
            CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                    .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                    .withCurrentMethod(element instanceof IMethod method ? method : null) //
                    .methodSearchMode(searchMode) //
                    .promptText(" Jump to " + testOrTested + " class...")
                    .build();

                return typeFacade.getOneCorrespondingMember(request);
            }, this::jumpToMember);
        };
    }

    private void jumpToMember(IMember memberToJump)
    {
        EditorUI editorUI = new EditorUI();
        if(memberToJump instanceof IMethod)
        {
            IMethod methodToJump = (IMethod) memberToJump;
            IEditorPart openedEditor = editorUI.open(methodToJump.getDeclaringType().getParent());
            editorUI.reveal(openedEditor, methodToJump);
        }
        else
        {
            editorUI.open(memberToJump.getParent());
        }
    }
}
