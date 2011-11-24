/**
 * 
 */
package org.moreunit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.DummyPreferencesForTesting;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * @author vera 06.12.2008 16:16:21
 */
public abstract class SimpleProjectTestCase extends WorkspaceTestCase
{

    private static final String TEST_FOLDER_NAME = "test";
    private static final String PACKAGE_NAME = "org";
    private static final String SOURCES_FOLDER_NAME = "sources";

    protected static IPackageFragmentRoot sourcesFolder;
    protected static IPackageFragment sourcesPackage;
    protected static IPackageFragmentRoot testFolder;
    protected static IPackageFragment testPackage;

    private Set<IType> typesToClean;

    @BeforeClass
    public static void setUpProject() throws Exception
    {
        sourcesFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCES_FOLDER_NAME);
        sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
        testFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, TEST_FOLDER_NAME);
        testPackage = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, PACKAGE_NAME);

        initPreferencesForTestCaseContext();
    }
    
    @AfterClass
    public static void tearDownProject() throws JavaModelException
    {
        sourcesPackage.delete(true, null);
        sourcesFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
        
        testPackage.delete(true, null);
        testFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
    }

    @Before
    public void setUpTestCase()
    {
        typesToClean = new HashSet<IType>();
    }

    @After
    public void tearDownTestCase() throws JavaModelException
    {
        WorkspaceHelper.deleteCompilationUnitsForTypes(typesToClean.toArray(new IType[typesToClean.size()]));
    }

    private static void initPreferencesForTestCaseContext()
    {
        Preferences preferences = new DummyPreferencesForTesting();
        preferences.setHasProjectSpecificSettings(workspaceTestProject, true);
        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(new SourceFolderMapping(workspaceTestProject, sourcesFolder, testFolder));
        preferences.setMappingList(workspaceTestProject, mappingList);

        preferences.setSuffixes(workspaceTestProject, new String[] { "Test" });
        preferences.setPrefixes(workspaceTestProject, new String[] {});
    }

    protected IType createJavaClass(String javaClassName, boolean deleteCompilationUnitAfterTest) throws JavaModelException
    {
        return createJavaClass(sourcesPackage, javaClassName, deleteCompilationUnitAfterTest);
    }

    protected IType createTestCase(String javaClassName, boolean deleteCompilationUnitAfterTest) throws JavaModelException
    {
        return createJavaClass(testPackage, javaClassName, deleteCompilationUnitAfterTest);
    }

    private IType createJavaClass(IPackageFragment packageFragment, String javaClassName, boolean deleteCompilationUnitAfterTest) throws JavaModelException
    {
        IType type = WorkspaceHelper.createJavaClass(packageFragment, javaClassName);
        if(deleteCompilationUnitAfterTest)
        {
            deleteAfterTest(type);
        }
        return type;
    }

    /**
     * Registers the given type to be removed from the workspace after test
     * completion.
     * 
     * @param type the type to delete
     * @return the type (convenient for method chaining)
     */
    protected IType deleteAfterTest(IType type)
    {
        if(typesToClean == null)
        {
            throw new IllegalStateException("Argument 'deleteCompilationUnitAfterTest' can only be used during the time between @Before and @After.");
        }
        typesToClean.add(type);
        return type;
    }
}
