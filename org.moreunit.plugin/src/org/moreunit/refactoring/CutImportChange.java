package org.moreunit.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author vera
 * 
 * This class is used to fix bug:
 * https://sourceforge.net/tracker/?func=detail&aid=3191142&group_id=156007&atid=798056
 * 
 * It removes an import from a compilationUnit.
 */
public class CutImportChange extends Change
{
    private ICompilationUnit testCompilationUnit;
    private String importCutString;
    private boolean shouldAddImport;
    
    public CutImportChange(String importCutString, ICompilationUnit testCompilationUnit)
    {
        this(importCutString, testCompilationUnit, false);
    }
    
    private CutImportChange(String importCutString, ICompilationUnit testType, boolean shouldAddImport)
    {
        this.testCompilationUnit = testType;
        this.importCutString = importCutString;
        this.shouldAddImport = shouldAddImport;
    }
    
    @Override
    public String getName()
    {
        return String.format("Remove %s from imports of %s", importCutString, testCompilationUnit);
    }

    @Override
    public void initializeValidationData(IProgressMonitor pm)
    {
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        return new RefactoringStatus();
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException
    {
        if(shouldAddImport)
            addImport(pm);
        else
            removeImport(pm);
        
        return getUndoChange();
    }

    private CutImportChange getUndoChange()
    {
        return new CutImportChange(importCutString, testCompilationUnit, !shouldAddImport);
    }
    
    private void addImport(IProgressMonitor pm) throws JavaModelException
    {
        testCompilationUnit.createImport(importCutString, null, pm);
    }
    
    private void removeImport(IProgressMonitor pm) throws JavaModelException
    {
        IImportDeclaration importDeclaration = testCompilationUnit.getImport(importCutString);
        if(importDeclaration.exists())
        {
            importDeclaration.delete(true, pm);
        }
    }

    @Override
    public Object getModifiedElement()
    {
        return testCompilationUnit;
    }

}
