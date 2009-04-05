package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author vera 04.02.2006 21:55:31
 */
public class RenameMethodParticipant extends RenameParticipant
{

    private IMethod renamedMethod;
    private ClassTypeFacade javaFileFacade;
    TestMethodDivinerFactory testMethodDivinerFactory;
    TestMethodDiviner testMethodDiviner;

    protected boolean initialize(Object element)
    {
        LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.initialize");
        setMethod(element);
        if(TypeFacade.isTestCase(renamedMethod.getCompilationUnit().findPrimaryType()))
            return false;

        javaFileFacade = new ClassTypeFacade(renamedMethod.getCompilationUnit());
        testMethodDivinerFactory = new TestMethodDivinerFactory(renamedMethod.getCompilationUnit());
        testMethodDiviner = testMethodDivinerFactory.create();
        return true;
    }

    public void setMethod(Object element)
    {
        renamedMethod = (IMethod) element;
    }

    public String getName()
    {
        LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.getName");
        return "MoreUnit Rename Method";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.checkConditions");
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.createChange");

        if(! getArguments().getUpdateReferences())
        {
            return null;
        }
        List<Change> changes = new ArrayList<Change>();

        List<IMethod> allTestMethods = javaFileFacade.getCorrespondingTestMethods(renamedMethod);
        if(allTestMethods == null)
        {
            return null;
        }
        for (IMethod testMethod : allTestMethods)
        {
            if(testMethod != null)
            {
                String newTestMethodName = testMethodDiviner.getTestMethodNameAfterRename(renamedMethod.getElementName(), getArguments().getNewName(), testMethod.getElementName());
                changes.add(new RenameMethodChange(testMethod, newTestMethodName));
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
// Revision 1.6 2009/01/08 19:58:30 gianasista
// Patch from Zach for more flexible test method naming
//
// Revision 1.5 2008/02/04 20:05:14 gianasista
// Move tests to org.moreunit.test
//
// Revision 1.4 2006/10/24 18:37:39 channingwalton
// made the properties page appear on the navigator view and fixed some gui text
//
// Revision 1.3 2006/10/18 22:13:16 channingwalton
// fixes for [ 1580067 ] Renaming a method renames only one test method and [
// 1579278 ] Renaming methods truncates some testcase method names
//
// Revision 1.2 2006/08/21 06:18:34 channingwalton
// removed some unnecessary casts, fixed a NPE
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
// Revision 1.9 2006/06/01 21:00:49 channingwalton
// made rename methods support undo, it would be nice to figure out how to show
// a preview too...
//
// Revision 1.8 2006/05/23 19:42:01 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.7 2006/05/23 19:39:50 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.6 2006/05/20 16:11:00 gianasista
// Integration of switchunit preferences
//
// Revision 1.5 2006/04/14 17:14:22 gianasista
// Refactoring Support with dialog
//
// Revision 1.4 2006/02/19 21:46:21 gianasista
// *** empty log message ***
//
// Revision 1.3 2006/02/12 20:50:06 gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.2 2006/02/05 21:14:34 gianasista
// First try to delegate refactoring to rename testmethods
//
// Revision 1.1 2006/02/04 21:54:27 gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//
