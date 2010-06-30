package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MoreUnitContants;
import org.moreunit.util.StringConstants;

/**
 * @author vera 04.02.2006 22:19:33
 */
public class RenameClassParticipant extends RenameParticipant
{

    private ICompilationUnit compilationUnit;
    private ClassTypeFacade javaFileFacade;

    protected boolean initialize(Object element)
    {
        LogHandler.getInstance().handleInfoLog("RenameClassParticipant.initialize");
        compilationUnit = (ICompilationUnit) element;
        if(TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
            return false;

        javaFileFacade = new ClassTypeFacade(compilationUnit);
        return true;
    }

    public String getName()
    {
        LogHandler.getInstance().handleInfoLog("RenameClassParticipant.getName");
        return "MoreUnit Rename Class";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("RenameClassParticipant.checkConditions");
        return new RefactoringStatus();
    }

    /*
     * public Change createChange(IProgressMonitor pm) throws CoreException,
     * OperationCanceledException {
     * LogHandler.getInstance().handleInfoLog("RenameClassParticipant.createChange"
     * ); if (!getArguments().getUpdateReferences()) { return null; }
     * List<Change> changes = new ArrayList<Change>(); Set<IType> allTestcases =
     * javaFileFacade.getCorrespondingTestCaseList(); for (IType typeToRename :
     * allTestcases) { changes.add(new RenameClassChange(typeToRename,
     * getNewTestName(typeToRename))); } if (changes.size() == 1) { return
     * changes.get(0); } if (changes.size() > 0) { return new
     * CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
     * } return null; }
     */

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        if(! getArguments().getUpdateReferences())
        {
            return null;
        }

        try
        {
            List<Change> changes = new ArrayList<Change>();
            Set<IType> allTestcases = javaFileFacade.getCorrespondingTestCaseList();
            RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
            for (IType typeToRename : allTestcases)
            {
                RenameJavaElementDescriptor renameJavaElementDescriptor = (RenameJavaElementDescriptor) refactoringContribution.createDescriptor();
                renameJavaElementDescriptor.setJavaElement(typeToRename.getCompilationUnit());
                renameJavaElementDescriptor.setNewName(getNewTestName(typeToRename));
                RefactoringStatus refactoringStatus = new RefactoringStatus();
                Refactoring renameRefactoring = renameJavaElementDescriptor.createRefactoring(refactoringStatus);
                //RefactoringStatus checkAllConditions = renameRefactoring.checkAllConditions(pm);
                renameRefactoring.checkAllConditions(pm);
                changes.add(renameRefactoring.createChange(pm));
            }

            if(changes.size() == 1)
            {
                return changes.get(0);
            }

            if(changes.size() > 0)
            {
                return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
            }
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return null;
    }

    private String getNewTestName(IType typeToRename)
    {
        String newName = getArguments().getNewName();
        newName = newName.replaceFirst(MoreUnitContants.JAVA_FILE_EXTENSION, StringConstants.EMPTY_STRING);
        return typeToRename.getElementName().replaceFirst(compilationUnit.findPrimaryType().getElementName(), newName);
    }

}

// $Log: not supported by cvs2svn $
// Revision 1.6  2009/04/05 19:14:27  gianasista
// code formatter
//
// Revision 1.5 2008/02/20 19:23:02 gianasista
// Rename of classes for constants
//
// Revision 1.4 2008/01/23 19:34:55 gianasista
// Refactorings via descriptors
//
// Revision 1.3 2006/10/24 18:37:39 channingwalton
// made the properties page appear on the navigator view and fixed some gui text
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
// Revision 1.8 2006/05/23 19:39:50 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.7 2006/05/17 19:16:00 channingwalton
// enhanced rename refactoring to support undo and so that it is included in the
// preview with other changes.
//
// Revision 1.6 2006/05/16 20:15:02 gianasista
// removed deprecated method call
//
// Revision 1.5 2006/05/15 19:51:09 gianasista
// added todo
//
// Revision 1.4 2006/05/13 08:26:54 channingwalton
// corrected the english
//
// Revision 1.3 2006/02/19 21:46:33 gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests
// (configurable via properties)
//
// Revision 1.2 2006/02/12 20:50:06 gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.1 2006/02/04 21:54:27 gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//
