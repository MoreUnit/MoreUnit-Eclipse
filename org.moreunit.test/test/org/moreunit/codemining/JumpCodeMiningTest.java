package org.moreunit.codemining;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link JumpCodeMining} against a real compilation unit.
 */
@Context(SimpleJUnit4Project.class)
public class JumpCodeMiningTest extends ContextTestCase
{
    @AfterEach
    public void closeOpenedEditors()
    {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if(page != null)
        {
            page.closeAllEditors(false);
        }
    }

    private void resolve(JumpCodeMining mining) throws Exception
    {
        java.lang.reflect.Method doResolve = JumpCodeMining.class.getDeclaredMethod("doResolve", ITextViewer.class, org.eclipse.core.runtime.IProgressMonitor.class);
        doResolve.setAccessible(true);
        ((java.util.concurrent.CompletableFuture< ? >) doResolve.invoke(mining, mock(ITextViewer.class), new NullProgressMonitor())).join();
    }

    private JumpCodeMining miningFor(IJavaElement element) throws Exception
    {
        ICompilationUnit compilationUnit = ((IType) element).getCompilationUnit();
        IDocument document = new org.eclipse.jface.text.Document(compilationUnit.getSource());
        return new JumpCodeMining(element, document, mock(ICodeMiningProvider.class));
    }

    private JumpCodeMining miningFor(IMethod method) throws Exception
    {
        IDocument document = new org.eclipse.jface.text.Document(method.getCompilationUnit().getSource());
        return new JumpCodeMining(method, document, mock(ICodeMiningProvider.class));
    }

    @Test
    public void doResolve_should_propose_jump_to_test_class_when_it_exists() throws Exception
    {
        JumpCodeMining mining = miningFor(context.getPrimaryTypeHandler("org.SomeClass").get());
        resolve(mining);

        assertEquals(" Jump to test class", mining.getLabel());
    }

    @Test
    public void doResolve_should_not_propose_jump_when_class_has_no_test_case() throws Exception
    {
        IType typeWithoutTest = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.ClassWithoutTest").get();
        JumpCodeMining mining = miningFor(typeWithoutTest);
        resolve(mining);

        assertEquals("", mining.getLabel());
    }

    @Test
    public void doResolve_should_propose_jump_to_tested_class_when_type_is_a_test_case() throws Exception
    {
        JumpCodeMining mining = miningFor(context.getPrimaryTypeHandler("org.SomeClassTest").get());
        resolve(mining);

        assertEquals(" Jump to tested class", mining.getLabel());
    }

    @Test
    public void doResolve_should_propose_jump_to_test_method_when_it_exists() throws Exception
    {
        IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();");

        JumpCodeMining mining = miningFor(method);
        resolve(mining);

        assertEquals(" Jump to test method", mining.getLabel());
    }

    @Test
    public void doResolve_should_not_propose_jump_when_method_has_no_test() throws Exception
    {
        IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberTwo()", "return 2;").get();

        JumpCodeMining mining = miningFor(method);
        resolve(mining);

        assertEquals("", mining.getLabel());
    }

    @Test
    public void getAction_should_jump_to_the_corresponding_test_method() throws Exception
    {
        IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();");

        JumpCodeMining mining = miningFor(method);
        mining.getAction().accept(null);

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        long deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline)
        {
            while (Display.getDefault().readAndDispatch())
            {
            }
            IEditorPart editor = page.getActiveEditor();
            if(editor != null && "SomeClassTest.java".equals(editor.getEditorInput().getName()))
            {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError("editor on SomeClassTest was not opened");
    }
}
