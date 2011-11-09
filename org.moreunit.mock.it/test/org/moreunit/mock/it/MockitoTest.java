package org.moreunit.mock.it;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Rule;
import org.junit.Test;
import org.moreunit.mock.ReplaceInjectionModuleRule;
import org.moreunit.mock.actions.MockDependenciesAction;

import com.google.inject.Inject;

public class MockitoTest
{
    @Rule
    public ReplaceInjectionModuleRule injectRule = new ReplaceInjectionModuleRule(new TestModule());

    @Inject
    private MockDependenciesAction mockDependenciesAction;

    @Test
    public void should_mock_all_dependencies() throws Exception
    {
        IJavaProject project = WorkspaceHelper.createJavaProject("test");
        IPackageFragmentRoot srcSourceFolder = WorkspaceHelper.createSourceFolderInProject(project, "src");
        IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(srcSourceFolder, "te.st");
        IType cut = WorkspaceHelper.createJavaClass(packageFragment, "SomeConcept");

        StringBuilder source = new StringBuilder()
                .append("package te.st;")
                .append("public class SomeConcept {")
                .append("  public Runnable runnable;")
                .append("  public Comparable<String> runnable;")
                .append("}");
        setSource(cut, source.toString());

        IPackageFragmentRoot testSourceFolder = WorkspaceHelper.createSourceFolderInProject(project, "junit");
        IPackageFragment testPackageFragment = WorkspaceHelper.createNewPackageInSourceFolder(testSourceFolder, "te.st");
        IType testClass = WorkspaceHelper.createJavaClass(testPackageFragment, "SomeConceptTest");

        StringBuilder testSource = new StringBuilder()
                .append("package te.st;")
                .append("public class SomeConceptTest { }");
        setSource(testClass, testSource.toString());

        mockDependenciesAction.setCompilationUnit(cut.getCompilationUnit());
        mockDependenciesAction.execute();

        // JavaUI.openInEditor(testClass);
        // mockDependenciesAction.run(null);

        System.err.println(testClass.getSource());
    }

    private void setSource(IType type, String source) throws JavaModelException
    {
        ICompilationUnit cu = type.getCompilationUnit();
        NullProgressMonitor progressMonitor = new NullProgressMonitor();
        if(! cu.isOpen())
        {
            cu.open(progressMonitor);
        }
        ICompilationUnit wc = cu.getWorkingCopy(progressMonitor);

        wc.getBuffer().setContents(source);
        wc.commitWorkingCopy(false, progressMonitor);
    }
}
