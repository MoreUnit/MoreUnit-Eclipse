package org.moreunit.elements;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.MemberChooseDialog;
import org.moreunit.util.MemberJumpHistory;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.util.MethodTestCallerFinder;
import org.moreunit.wizards.NewTestCaseWizard;

/**
 * @author vera 23.05.2006 20:21:57
 */
public abstract class TypeFacade
{

    ICompilationUnit compilationUnit;

    public static boolean isTestCase(IType type)
    {
        if(type == null)
        {
            return false;
        }

        IType primaryType = type.getCompilationUnit().findPrimaryType();
        if(primaryType == null)
        {
            return false;
        }

        String classname = primaryType.getElementName();
        Preferences preferences = Preferences.getInstance();
        String[] suffixes = preferences.getSuffixes(type.getJavaProject());
        for (String suffix : suffixes)
        {
            if((suffix.length() > 0) && classname.endsWith(suffix))
            {
                return true;
            }
        }

        String[] prefixes = preferences.getPrefixes(type.getJavaProject());
        for (String prefix : prefixes)
        {
            if((prefix.length() > 0) && classname.startsWith(prefix))
            {
                return true;
            }
        }

        return false;
    }

    public TypeFacade(ICompilationUnit compilationUnit)
    {
        this.compilationUnit = compilationUnit;
    }

    public TypeFacade(IFile file)
    {
        this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
    }

    public TypeFacade(IEditorPart editorPart)
    {
        IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
        this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
    }

    public IType getType()
    {
        return this.compilationUnit.findPrimaryType();
    }

    public ICompilationUnit getCompilationUnit()
    {
        return this.compilationUnit;
    }

    protected boolean doesMethodExist(String testMethodName)
    {
        try
        {
            IMethod[] vorhandeneTests = this.compilationUnit.findPrimaryType().getMethods();
            for (IMethod method : vorhandeneTests)
            {
                if(testMethodName.equals(method.getElementName()))
                {
                    return true;
                }
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return false;
    }

    public IJavaProject getJavaProject()
    {
        return compilationUnit.getJavaProject();
    }

    protected IMember getOneCorrespondingMember(IMethod method, boolean createIfNecessary, boolean extendedSearch, String promptText)
    {
        Set<IType> proposedClasses = getCorrespondingClasses();

        Set<IMethod> proposedMethods = new LinkedHashSet<IMethod>();
        if(method != null)
        {
            proposedMethods.addAll(getCorrespondingMethodsInClasses(method, proposedClasses));
            if(extendedSearch)
            {
                proposedMethods.addAll(getCallRelationshipFinder(method).getMatches(new NullProgressMonitor()));
            }
        }

        IMember memberToJump = null;
        boolean openDialog = false;
        if(proposedMethods.size() == 1)
        {
            memberToJump = proposedMethods.iterator().next();
        }
        else if(proposedMethods.size() > 1)
        {
            openDialog = true;
        }
        else
        {
            if(proposedClasses.size() == 1)
            {
                memberToJump = proposedClasses.iterator().next();
            }
            else if(proposedClasses.size() > 1)
            {
                openDialog = true;
            }
            else if(createIfNecessary)
            {
                memberToJump = new NewTestCaseWizard(getType()).open();
            }
        }

        if(openDialog)
        {
            memberToJump = openDialog(promptText, proposedClasses, proposedMethods, method);
        }

        registerJump(method, memberToJump);
        return memberToJump;
    }

    abstract protected Collection<IMethod> getCorrespondingMethodsInClasses(IMethod method, Set<IType> classes);

    abstract protected Set<IType> getCorrespondingClasses();

    abstract protected MethodCallFinder getCallRelationshipFinder(IMethod method);

    private IMember openDialog(String promptText, Set<IType> proposedClasses, Set<IMethod> proposedMethods, IMethod method)
    {
        IMember startMember = method != null ? method : getType();
        IMember defaultSelection = getDefaultSelection(proposedClasses, proposedMethods, startMember);
        return new MemberChooseDialog(promptText, proposedClasses, proposedMethods, defaultSelection).getChoice();
    }

    private IMember getDefaultSelection(Set<IType> proposedClasses, Set<IMethod> proposedMethods, IMember startMember)
    {
        IMember selection = MemberJumpHistory.getInstance().getLastCorrespondingJumpMember(startMember);
        if(proposedClasses.contains(selection) || proposedMethods.contains(selection))
        {
            return selection;
        }
        return null;
    }

    private void registerJump(IMethod fromMethod, IMember toMember)
    {
        if(toMember != null)
        {
            IMember startMember = fromMethod != null ? fromMethod : getType();
            MemberJumpHistory.getInstance().registerJump(startMember, toMember);
        }
    }
}
