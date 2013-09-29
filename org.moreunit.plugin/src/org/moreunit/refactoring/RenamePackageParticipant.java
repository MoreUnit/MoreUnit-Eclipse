package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
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
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.util.PluginTools;

/**
 * @author vera 24.11.2008 20:18:09
 */
public class RenamePackageParticipant extends RenameParticipant
{

    private IPackageFragment packageFragment;
    private IPackageFragmentRoot packageFragmentRoot;
    private List<IPackageFragmentRoot> correspondingPackageFragmentRoots;

    @Override
    protected boolean initialize(Object element)
    {
        packageFragment = (IPackageFragment) element;

        IJavaElement fragment = packageFragment;
        while (! (fragment instanceof IPackageFragmentRoot))
        {
            fragment = fragment.getParent();
        }
        packageFragmentRoot = (IPackageFragmentRoot) fragment;
        correspondingPackageFragmentRoots = getSourceFolderFromContext();

        return ! isTestSourceFolder();
    }

    private boolean isTestSourceFolder()
    {
        List<SourceFolderMapping> sourceMappingList = Preferences.getInstance().getSourceMappingList(packageFragment.getJavaProject());
        for (SourceFolderMapping mapping : sourceMappingList)
        {
            if(packageFragmentRoot.equals(mapping.getTestFolder()))
                return true;
        }

        return false;
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        if(! getArguments().getUpdateReferences())
        {
            return null;
        }

        String cutPackageName = packageFragment.getElementName();
        ProjectPreferences prefs = Preferences.forProject(packageFragment.getJavaProject());

        List<Change> changes = new ArrayList<Change>();

        for (IPackageFragmentRoot packageRoot : correspondingPackageFragmentRoots)
        {
            IPackageFragment packageToRename = packageRoot.getPackageFragment(PluginTools.getTestPackageName(cutPackageName, prefs));
            if(packageToRename != null && packageToRename.exists())
            {
                RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_PACKAGE);
                RenameJavaElementDescriptor renameJavaElementDescriptor = (RenameJavaElementDescriptor) refactoringContribution.createDescriptor();
                renameJavaElementDescriptor.setJavaElement(packageToRename);
                renameJavaElementDescriptor.setNewName(PluginTools.getTestPackageName(getArguments().getNewName(), prefs));

                RefactoringStatus refactoringStatus = new RefactoringStatus();
                Refactoring renameRefactoring = renameJavaElementDescriptor.createRefactoring(refactoringStatus);
                renameRefactoring.checkAllConditions(pm);

                changes.add(renameRefactoring.createChange(pm));
            }
        }

        return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
    }

    private List<IPackageFragmentRoot> getSourceFolderFromContext()
    {
        List<IPackageFragmentRoot> result = new ArrayList<IPackageFragmentRoot>();
        for (IPackageFragmentRoot folder : SourceFolderContext.getInstance().getSourceFolderToSearch(packageFragmentRoot))
        {
            // this case may happens if tests are in the same source folder as
            // production code
            if(! folder.equals(packageFragmentRoot))
            {
                result.add(folder);
            }
        }
        return result;
    }

    @Override
    public String getName()
    {
        return "MoreUnit Rename Package";
    }
}
