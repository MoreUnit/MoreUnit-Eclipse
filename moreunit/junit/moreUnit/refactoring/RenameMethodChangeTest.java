package moreUnit.refactoring;

import moreUnit.AbstractMoreUnitTest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.jmock.Mock;

public class RenameMethodChangeTest extends AbstractMoreUnitTest {

	private Mock				declaringType	= mock(IType.class);
	private Mock				method			= mock(IMethod.class);
	private Mock				renamedMethod	= mock(IMethod.class);
	private NullProgressMonitor	progressMonitor	= new NullProgressMonitor();
	private RenameMethodChange	change;

	protected void setUp() throws Exception {
		setUpMethod(method, "getKebab", declaringType);
		setUpMethod(renamedMethod, "getShishKebab", declaringType);
		change = new RenameMethodChange((IMethod) method.proxy(), "getShishKebab");

		// this is not good but I cannot work out how to make mocks work for this test
		declaringType.stubs().method("getMethods").will(returnValue(new IMethod[] { (IMethod) method.proxy(), (IMethod) renamedMethod.proxy() }));
	}

	public void testPerformReturnsUndoForRenameMethod() throws CoreException {
		NullProgressMonitor pm = new NullProgressMonitor();
		method.expects(once()).method("rename").with(eq("getShishKebab"), eq(false), same(pm));
		Change undo = change.perform(pm);

		assertSame(renamedMethod.proxy(), undo.getModifiedElement());
	}

	public void testPerformUndo() throws CoreException {
		method.expects(once()).method("rename").with(eq("getShishKebab"), eq(false), same(progressMonitor));
		Change undo = change.perform(progressMonitor);

		renamedMethod.expects(once()).method("rename").with(eq("getKebab"), eq(false), same(progressMonitor));
		Change redo = undo.perform(progressMonitor);

		assertSame(method.proxy(), redo.getModifiedElement());
	}
}

// $Log$
//