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
        return "MoreUnit Rename Method";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        if(! getArguments().getUpdateReferences())
        {
            return null;
        }
        List<Change> changes = new ArrayList<Change>();

        List<IMethod> allTestMethods = javaFileFacade.getCorrespondingTestMethodsByName(renamedMethod);
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