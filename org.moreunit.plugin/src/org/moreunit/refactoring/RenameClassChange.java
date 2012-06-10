package org.moreunit.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

// TODO Vera: it looks like this class is never used, should we remove it?
public class RenameClassChange extends Change
{
    private final IType typeToRename;
    private final String newName;

    public RenameClassChange(IType typeToRename, String newName)
    {
        this.typeToRename = typeToRename;
        this.newName = newName;
    }

    public Object getModifiedElement()
    {
        return typeToRename;
    }

    public String getName()
    {
        return "Rename " + typeToRename.getElementName() + " to " + newName;
    }

    public void initializeValidationData(IProgressMonitor pm)
    {
    }

    public RefactoringStatus isValid(IProgressMonitor pm) throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    public Change perform(IProgressMonitor pm) throws CoreException
    {
        String oldName = typeToRename.getElementName();
        typeToRename.rename(newName, false, pm);
        return getUndo(oldName);
    }

    /**
     * Get the undo Change for this rename. Currently limited to undoing renames
     * of types that are the main type in a compilation unit.
     */
    private Change getUndo(String oldName)
    {
        IType newType = getNewType();
        if(newType == null)
        {
            return null;
        }
        return new RenameClassChange(newType, oldName);
    }

    private IType getNewType()
    {
        if(typeToRename.getParent().getElementType() != IJavaElement.COMPILATION_UNIT)
        {
            return null;
        }
        String ext = typeToRename.getPath().getFileExtension();
        IPackageFragment packge = (IPackageFragment) typeToRename.getParent().getParent();
        return packge.getCompilationUnit(newName + "." + ext).getType(newName);
    }

}
