package org.moreunit.refactoring;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.jmock.Mock;
import org.moreunit.AbstractMoreUnitTest;
import org.moreunit.refactoring.RenameClassChange;

public class RenameClassChangeTest extends AbstractMoreUnitTest {

	private Mock				typeToRename			= mock(IType.class);
	private Mock				renamedType				= mock(IType.class);
	private Mock				parentPackage			= mock(IPackageFragment.class);
	private Mock				compilationUnitToRename	= mock(ICompilationUnit.class);
	private Mock				renamedCompilationUnit	= mock(ICompilationUnit.class);
	private NullProgressMonitor	progressMonitor			= new NullProgressMonitor();
	private RenameClassChange	change;

	protected void setUp() throws Exception {
		setUpType(typeToRename, "KebabHouse", compilationUnitToRename, parentPackage);
		setUpType(renamedType, "KebabEmporium", renamedCompilationUnit, parentPackage);
		change = new RenameClassChange((IType) typeToRename.proxy(), "KebabEmporium");
	}

	public void testPerformReturnsUndoForRenameType() throws CoreException {
		NullProgressMonitor pm = new NullProgressMonitor();
		typeToRename.expects(once()).method("rename").with(eq("KebabEmporium"), eq(false), same(pm));
		Change undo = change.perform(pm);

		assertSame(renamedType.proxy(), undo.getModifiedElement());
	}

	public void testPerformUndo() throws CoreException {
		typeToRename.expects(once()).method("rename").with(eq("KebabEmporium"), eq(false), same(progressMonitor));
		Change undo = change.perform(progressMonitor);

		renamedType.expects(once()).method("rename").with(eq("KebabHouse"), eq(false), same(progressMonitor));
		Change redo = undo.perform(progressMonitor);

		assertSame(typeToRename.proxy(), redo.getModifiedElement());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:11:29  gianasista
// CVS Refactoring
//
// Revision 1.1  2006/06/01 21:00:49  channingwalton
// made rename methods support undo, it would be nice to figure out how to show a preview too...
//
// Revision 1.1 2006/05/17 19:16:00 channingwalton
// enhanced rename refactoring to support undo and so that it is included in the preview with other changes.
//