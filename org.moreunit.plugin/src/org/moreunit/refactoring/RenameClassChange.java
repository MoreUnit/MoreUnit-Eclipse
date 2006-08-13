package org.moreunit.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class RenameClassChange extends Change {

	private final IType		typeToRename;
	private final String	newName;

	public RenameClassChange(IType typeToRename, String newName) {
		this.typeToRename = typeToRename;
		this.newName = newName;
	}

	public Object getModifiedElement() {
		return typeToRename;
	}

	public String getName() {
		return "Rename " + typeToRename.getElementName() + " to " + newName;
	}

	public void initializeValidationData(IProgressMonitor pm) {
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		String oldName = typeToRename.getElementName();
		typeToRename.rename(newName, false, pm);
		return getUndo(oldName);
	}

	/**
	 * Get the undo Change for this rename.
	 * Currently limited to undoing renames of types that are the main type in a compilation unit.
	 */
	private Change getUndo(String oldName) {
		IType newType = getNewType();
		if (newType == null) {
			return null;
		}
		return new RenameClassChange(newType, oldName);
	}

	private IType getNewType() {
		if (typeToRename.getParent().getElementType() != IJavaElement.COMPILATION_UNIT) {
			return null;
		}
		IPackageFragment packge = (IPackageFragment) typeToRename.getParent().getParent();
		IType newType = packge.getCompilationUnit(newName + ".java").getType(newName);
		return newType;
	}
	
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.1  2006/06/01 21:00:49  channingwalton
// made rename methods support undo, it would be nice to figure out how to show a preview too...
//
// Revision 1.1  2006/05/17 19:16:00  channingwalton
// enhanced rename refactoring to support undo and so that it is included in the preview with other changes.
//