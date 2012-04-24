package org.moreunit.elements;

import java.util.Collection;
import java.util.Collections;
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
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.ChooseDialog;
import org.moreunit.ui.CreateNewClassAction;
import org.moreunit.ui.MemberContentProvider;
import org.moreunit.util.MemberJumpHistory;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.wizards.NewClassyWizard;

/**
 * @author vera 23.05.2006 20:21:57
 */
public abstract class TypeFacade
{

    public static enum MethodSearchMode
    {
        BY_CALL, BY_NAME
    }

    protected final ICompilationUnit compilationUnit;

    public static TypeFacade createFacade(ICompilationUnit compilationUnit)
    {
        if(isTestCase(compilationUnit.findPrimaryType()))
        {
            return new TestCaseTypeFacade(compilationUnit);
        }
        return new ClassTypeFacade(compilationUnit);
    }

    public static boolean isTestCase(IType type)
    {
        if(type == null)
        {
            return false;
        }
        return isTestCase(type.getCompilationUnit());
    }

    public static boolean isTestCase(ICompilationUnit compilationUnit)
    {
        IType primaryType = compilationUnit.findPrimaryType();
        if(primaryType == null)
        {
            return false;
        }

        String classname = primaryType.getElementName();
        Preferences preferences = Preferences.getInstance();
        String[] suffixes = preferences.getSuffixes(compilationUnit.getJavaProject());
        for (String suffix : suffixes)
        {
            if((suffix.length() > 0) && classname.endsWith(suffix))
            {
                return true;
            }
        }

        String[] prefixes = preferences.getPrefixes(compilationUnit.getJavaProject());
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

    /**
     * Returns one member corresponding to the given method of this type or to
     * this type (for instance a test method calling the given method if this
     * type is a class under test, or a method under test called by the given
     * test method if this type is a test case, or a test case corresponding to
     * this class, etc...). If there are several resulting members the user has
     * to make a choice via a dialog. If no member is found <tt>null</tt> is
     * returned, or a wizard opens to create a new type if
     * <tt>createIfNecessary</tt> is true.
     * 
     * @param request the details of the request for corresponding member.
     * @return one corresponding member or <code>null</code>
     * @see CorrespondingMemberRequest
     */
    public IMember getOneCorrespondingMember(CorrespondingMemberRequest request)
    {
        final Collection<IType> proposedClasses = getCorrespondingClasses(false);

        final OneCorrespondingMemberAction action;
        if(proposedClasses.isEmpty())
        {
            action = getLikelyCorrespondingClass(request);
        }
        else
        {
            action = getPerfectCorrespondingMember(request, proposedClasses);
        }

        if(action == null)
        {
            return null;
        }

        IMember memberToJump = action.getCorrespondingMember();

        registerJump(request.getCurrentMethod(), memberToJump);
        return memberToJump;
    }

    private OneCorrespondingMemberAction getPerfectCorrespondingMember(CorrespondingMemberRequest request, Collection<IType> proposedClasses)
    {
        Collection<IMethod> proposedMethods = findCorrespondingMethodsInClasses(request, proposedClasses);

        if(proposedMethods.size() == 1)
        {
            return new ReturnMember(proposedMethods.iterator().next());
        }
        else if(proposedMethods.size() > 1)
        {
            return new OpenChoiceDialog(request, proposedClasses, proposedMethods, true);
        }
        else
        {
            if(proposedClasses.size() == 1)
            {
                return new ReturnMember(proposedClasses.iterator().next());
            }
            else if(proposedClasses.size() > 1)
            {
                return new OpenChoiceDialog(request, proposedClasses, true);
            }
            else if(request.shouldCreateClassIfNoResult())
            {
                return new OpenNewClassWizard();
            }
        }
        return null;
    }

    private Collection<IMethod> findCorrespondingMethodsInClasses(CorrespondingMemberRequest request, Collection<IType> classes)
    {
        Collection<IMethod> proposedMethods = new LinkedHashSet<IMethod>();

        if(request.shouldReturn(MemberType.TYPE_OR_METHOD))
        {
            IMethod currentMethod = request.getCurrentMethod();
            if(currentMethod != null && ! classes.isEmpty())
            {
                proposedMethods.addAll(getCorrespondingMethodsInClasses(currentMethod, classes));
                if(request.shouldUseExtendedSearch())
                {
                    proposedMethods.addAll(getCallRelationshipFinder(currentMethod, classes).getMatches(new NullProgressMonitor()));
                }
            }
        }

        return proposedMethods;
    }

    private OneCorrespondingMemberAction getLikelyCorrespondingClass(CorrespondingMemberRequest request)
    {
        Collection<IType> proposedClasses = getCorrespondingClasses(true);
        if(! proposedClasses.isEmpty())
        {
            return new OpenChoiceDialog(request, proposedClasses, false);
        }
        else if(request.shouldCreateClassIfNoResult())
        {
            return new OpenNewClassWizard();
        }
        return null;
    }

    abstract protected Collection<IMethod> getCorrespondingMethodsInClasses(IMethod method, Collection<IType> classes);

    abstract protected Collection<IType> getCorrespondingClasses(boolean alsoIncludeLikelyMatches);

    abstract protected MethodCallFinder getCallRelationshipFinder(IMethod method, Collection<IType> searchScope);

    abstract protected NewClassyWizard newCorrespondingClassWizard(IType fromType);

    private IMember openDialog(CorrespondingMemberRequest request, Collection<IType> proposedClasses, Collection<IMethod> proposedMethods, boolean perfectMatches)
    {
        String promptText = request.getPromptText();
        String infoText = null;
        if(! perfectMatches)
        {
            promptText = String.format("%s %s%s", promptText, StringConstants.NEWLINE, "We could find the following classes, but their packages do not match:");
            infoText = "Please note that theses classes will not be considered for other MoreUnit features such as test launching or refactoring.";
        }

        IMember startMember = request.getCurrentMethod() != null ? request.getCurrentMethod() : getType();
        IMember defaultSelection = getDefaultSelection(proposedClasses, proposedMethods, startMember);
        MemberContentProvider contentProvider = new MemberContentProvider(proposedClasses, proposedMethods, defaultSelection).withAction(new CreateNewClassAction()
        {
            @Override
            public IType execute()
            {
                return newCorrespondingClassWizard(getType()).open();
            }
        });

        return new ChooseDialog<IMember>(promptText, infoText, contentProvider).getChoice();
    }

    private IMember getDefaultSelection(Collection<IType> proposedClasses, Collection<IMethod> proposedMethods, IMember startMember)
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

    private static interface OneCorrespondingMemberAction
    {
        IMember getCorrespondingMember();
    }

    public static class ReturnMember implements OneCorrespondingMemberAction
    {
        private final IMember member;

        public ReturnMember(IMember member)
        {
            this.member = member;
        }

        public IMember getCorrespondingMember()
        {
            return member;
        }
    }

    public class OpenChoiceDialog implements OneCorrespondingMemberAction
    {
        private final CorrespondingMemberRequest request;
        private final Collection<IType> proposedClasses;
        private final Collection<IMethod> proposedMethods;
        private final boolean perfectMatches;

        public OpenChoiceDialog(CorrespondingMemberRequest request, Collection<IType> proposedClasses, boolean perfectMatches)
        {
            this(request, proposedClasses, Collections.<IMethod> emptySet(), perfectMatches);
        }

        public OpenChoiceDialog(CorrespondingMemberRequest request, Collection<IType> proposedClasses, Collection<IMethod> proposedMethods, boolean perfectMatches)
        {
            this.request = request;
            this.proposedClasses = proposedClasses;
            this.proposedMethods = proposedMethods;
            this.perfectMatches = perfectMatches;
        }

        public IMember getCorrespondingMember()
        {
            return openDialog(request, proposedClasses, proposedMethods, perfectMatches);
        }
    }

    public class OpenNewClassWizard implements OneCorrespondingMemberAction
    {
        public IMember getCorrespondingMember()
        {
            return newCorrespondingClassWizard(getType()).open();
        }
    }
}
