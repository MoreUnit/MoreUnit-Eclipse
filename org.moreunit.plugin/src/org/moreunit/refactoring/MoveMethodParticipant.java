package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author vera 04.02.2006 22:29:07
 */
public class MoveMethodParticipant extends MoveParticipant
{

    IMethod movedMethod;
    private ClassTypeFacade javaFileFacade;
    private TestMethodDivinerFactory testMethodDivinerFactory;
    //private TestMethodDiviner testMethodDiviner;

    // JavaFileFacade javaFileFacade;

    protected boolean initialize(Object element)
    {
        movedMethod = (IMethod) element;
        if(TypeFacade.isTestCase(movedMethod.getCompilationUnit().findPrimaryType()))
            return false;
        
        javaFileFacade = new ClassTypeFacade(movedMethod.getCompilationUnit());
        testMethodDivinerFactory = new TestMethodDivinerFactory(movedMethod.getCompilationUnit());
        //testMethodDiviner = testMethodDivinerFactory.create();
        testMethodDivinerFactory.create();
        return true;
    }

    public String getName()
    {
        return "MoreUnit Move Method";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        SourceType destination = (SourceType) getArguments().getDestination();
        ICompilationUnit destinationCompilationUnit = (ICompilationUnit) destination.getParent();
        ClassTypeFacade destinationFacade = new ClassTypeFacade(destinationCompilationUnit);
        Collection<IType> correspondingTestCaseList = destinationFacade.getCorrespondingTestCases();
        
        // if no tests or more than one, don't do anything
        if(correspondingTestCaseList.size() != 1)
        {
            return null;
        }
        
        // get the destination for the testmethods
        IType targetType = correspondingTestCaseList.iterator().next();
        
        List<IMethod> allTestMethods = javaFileFacade.getCorrespondingTestMethodsByName(movedMethod);
        
        List<Change> changes = new ArrayList<Change>();
        if(allTestMethods == null)
        {
            return null;
        }
        
        for(IMethod testMethod : allTestMethods)
        {
            if(testMethod != null)
            {
               changes.add(new MoveMethodChange(javaFileFacade.getType(), targetType, testMethod));
            }
        }
        
        if(changes.size() == 1)
        {
            return changes.get(0);
        }
        
        if(changes.size() > 0)
        {
            return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
        }
        
        return null;
    }
}