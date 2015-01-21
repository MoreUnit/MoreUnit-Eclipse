package org.moreunit.elements;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.matching.CorrespondingTypeSearcher;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.ui.ChooseDialog;
import org.moreunit.ui.CreateNewClassAction;
import org.moreunit.ui.MemberContentProvider;
import org.moreunit.util.MemberJumpHistory;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;
import org.moreunit.wizards.NewClassyWizard;

/**
 * @author vera 23.05.2006 20:21:57
 */
public abstract class TypeFacade
{
    protected final ICompilationUnit compilationUnit;
    protected final TestMethodDivinerFactory testMethodDivinerFactory;
    protected final TestMethodDiviner testMethodDiviner;

    private CorrespondingTypeSearcher correspondingTypeSearcher;

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

        ProjectPreferences prefs = Preferences.forProject(primaryType.getJavaProject());
        return prefs.getTestClassNamePattern().evaluate(primaryType).isTestCase();
    }

    protected TypeFacade(ICompilationUnit compilationUnit)
    {
        this.compilationUnit = compilationUnit;
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    private TypeFacade(IFile file)
    {
        this(JavaCore.createCompilationUnitFrom(file));
    }

    protected TypeFacade(IEditorPart editorPart)
    {
        this((IFile) editorPart.getEditorInput().getAdapter(IFile.class));
    }

    public IType getType()
    {
        return this.compilationUnit.findPrimaryType();
    }

    public ICompilationUnit getCompilationUnit()
    {
        return this.compilationUnit;
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
                if(request.getMethodSearchMode().searchByName)
                {
                    proposedMethods.addAll(getCorrespondingMethodsInClasses(currentMethod, classes));
                }
                if(request.getMethodSearchMode().searchByCall)
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

    public final Collection<IType> getCorrespondingClasses(boolean alsoIncludeLikelyMatches)
    {
        return getCorrespondingTypeSearcher().getMatches(alsoIncludeLikelyMatches);
    }

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
        if(selection != null && (proposedClasses.contains(selection) || proposedMethods.contains(selection)))
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

    /**
     * Getter uses lazy caching.
     */
    private CorrespondingTypeSearcher getCorrespondingTypeSearcher()
    {
        if(this.correspondingTypeSearcher == null)
        {
            this.correspondingTypeSearcher = new CorrespondingTypeSearcher(this.compilationUnit, Preferences.getInstance());
        }

        return this.correspondingTypeSearcher;
    }

    private static interface OneCorrespondingMemberAction
    {
        IMember getCorrespondingMember();
    }

    private static class ReturnMember implements OneCorrespondingMemberAction
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

    private class OpenChoiceDialog implements OneCorrespondingMemberAction
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

    private class OpenNewClassWizard implements OneCorrespondingMemberAction
    {
        public IMember getCorrespondingMember()
        {
            return newCorrespondingClassWizard(getType()).open();
        }
    }
}
