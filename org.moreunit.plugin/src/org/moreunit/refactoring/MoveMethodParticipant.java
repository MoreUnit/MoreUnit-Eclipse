package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author vera 04.02.2006 22:29:07
 */
public class MoveMethodParticipant extends MoveParticipant
{

    IMethod movedMethod;
    private ClassTypeFacade javaFileFacade;
    TestMethodDivinerFactory testMethodDivinerFactory;
    TestMethodDiviner testMethodDiviner;

    // JavaFileFacade javaFileFacade;

    protected boolean initialize(Object element)
    {
        LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.initialize");
        movedMethod = (IMethod) element;
        if(TypeFacade.isTestCase(movedMethod.getCompilationUnit().findPrimaryType()))
            return false;
        
        javaFileFacade = new ClassTypeFacade(movedMethod.getCompilationUnit());
        testMethodDivinerFactory = new TestMethodDivinerFactory(movedMethod.getCompilationUnit());
        testMethodDiviner = testMethodDivinerFactory.create();
        return true;
    }

    public String getName()
    {
        LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.getName");
        return "MoreUnit Move Method";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.checkConditions");
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.createChange");

        SourceType destination = (SourceType) getArguments().getDestination();
        ICompilationUnit destinationCompilationUnit = (ICompilationUnit) destination.getParent();
        ClassTypeFacade destinationFacade = new ClassTypeFacade(destinationCompilationUnit);
        Set<IType> correspondingTestCaseList = destinationFacade.getCorrespondingTestCaseList();
        
        // if no tests or more than one, don't do anything
        if(correspondingTestCaseList.size() != 1)
        {
            return null;
        }
        
        // get the destination for the testmethods
        IType targetType = (IType) correspondingTestCaseList.toArray()[0];
        
        List<IMethod> allTestMethods = javaFileFacade.getCorrespondingTestMethods(movedMethod);
        
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

// $Log: not supported by cvs2svn $
// Revision 1.5  2009/04/05 19:14:27  gianasista
// code formatter
//
// Revision 1.4 2008/03/21 18:20:46 gianasista
// First version of new property page with source folder mapping
//
// Revision 1.3 2007/09/19 19:19:43 gianasista
// Started move refactoring support
//
// Revision 1.2 2006/10/24 18:37:39 channingwalton
// made the properties page appear on the navigator view and fixed some gui text
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:28 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.6 2006/05/23 19:42:01 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.5 2006/05/23 19:39:50 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.4 2006/05/20 16:10:39 gianasista
// todo-comment
//
// Revision 1.3 2006/04/14 19:42:09 gianasista
// *** empty log message ***
//
// Revision 1.2 2006/02/12 20:50:06 gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.1 2006/02/04 21:54:27 gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//
