package org.moreunit.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author vera
 *
 * 23.03.2010 20:34:10
 */
public class MoveMethodChange extends Change
{

    private IType sourceType;
    private IType destinationType;
    private IMethod methodToMove;
    
    
    public MoveMethodChange(IType sourceType, IType destinationType, IMethod methodToMove)
    {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.methodToMove = methodToMove;
    }
   
    @Override
    public Object getModifiedElement()
    {
        return methodToMove;
    }

    @Override
    public String getName()
    {
        return String.format("Move method %s from %s to %s", methodToMove.getElementName(), sourceType.getElementName(), destinationType.getElementName());
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
        methodToMove.move(destinationType, null, null, false, null);
        return getUndo();
    }
    
    private Change getUndo()
    {
        return new MoveMethodChange(destinationType, sourceType, methodToMove);
    }

}
