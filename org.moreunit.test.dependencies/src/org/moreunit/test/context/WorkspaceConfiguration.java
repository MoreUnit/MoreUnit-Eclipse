package org.moreunit.test.context;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.JUnitCore;
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.DummyPreferencesForTesting;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.SourceFolderHandler;
import org.moreunit.test.workspace.WorkspaceHandler;
import org.moreunit.test.workspace.WorkspaceHelper;
import org.moreunit.util.SearchScopeSingelton;

@SuppressWarnings("restriction")
class WorkspaceConfiguration
{
    private static final Map<TestType, String> TEST_TYPE_TO_PREF_VALUE;
    static
    {
        final Map<TestType, String> m = new HashMap<>();
        m.put(TestType.JUNIT3, "junit3");
        m.put(TestType.JUNIT4, "junit4");
        m.put(TestType.JUNIT5, "junit5");
        m.put(TestType.TESTNG, "testng");
        TEST_TYPE_TO_PREF_VALUE = m;
    }

    private final Map<String, ProjectConfiguration> projectConfigs = new HashMap<>();
    private PreferencesConfiguration preferencesConfig;

    public WorkspaceHandler initWorkspace(Class< ? > loadingClass, String projectPrefix)
    {
        final WorkspaceHandler wsHandler = newWorkspaceHandler(loadingClass, projectPrefix);

        createSources(wsHandler);
        applyPreferences(wsHandler);

        return wsHandler;
    }

    protected WorkspaceHandler newWorkspaceHandler(Class< ? > loadingClass, String projectPrefix)
    {
        SourceFolderContext.getInstance().initContextForWorkspace();
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();

        return new WorkspaceHandler(loadingClass, projectPrefix);
    }

    private void createSources(WorkspaceHandler wsHandler)
    {
        for (final ProjectConfiguration projectConfig : getProjectConfigs())
        {
            final String projectName = projectConfig.getProjectName();
            final ProjectHandler projectHandler = wsHandler.addProject(projectName);

            createMainSources(projectHandler, projectConfig);

            final TestProjectConfiguration testProjectConfig = projectConfig.getTestProjectConfig();
            if(testProjectConfig != null)
            {
                createTestSources(projectHandler, testProjectConfig, wsHandler);
            }
            else
            {
                createTestSources(projectHandler, projectConfig);
            }
        }
    }

    private void createMainSources(ProjectHandler projectHandler, ProjectConfiguration projectConfig)
    {
        final String mainSrcFolderName = StringUtils.firstNonBlank(projectConfig.getMainSourceFolder(), Defaults.SRC_FOLDER_NAME);
        final SourceFolderHandler mainSrcHandler = newSourceFolderHandler(projectHandler, mainSrcFolderName);
        projectHandler.setMainSrcFolderHandler(mainSrcHandler);
        mainSrcHandler.createElementsFromSources(projectConfig.getMainSources());
        mainSrcHandler.createElements(projectConfig.getMainTypes());
    }

    protected SourceFolderHandler newSourceFolderHandler(ProjectHandler projectHandler, String folderName)
    {
        return new SourceFolderHandler(projectHandler, folderName);
    }

    private void createTestSources(ProjectHandler projectHandler, TestProjectConfiguration testProjectConfig, WorkspaceHandler wsHandler)
    {
        final String testProjectName = testProjectConfig.getProjectName();
        final ProjectHandler testProjectHandler = wsHandler.addProject(testProjectName);

        final String srcFolderName = StringUtils.firstNonBlank(testProjectConfig.getSourceFolder(), Defaults.SRC_FOLDER_NAME);
        final SourceFolderHandler testSrcHandler = newSourceFolderHandler(testProjectHandler, srcFolderName);
        testProjectHandler.setMainSrcFolderHandler(testSrcHandler);
        testSrcHandler.createElementsFromSources(testProjectConfig.getSources());
        testSrcHandler.createElements(testProjectConfig.getTypes());

        projectHandler.setTestSrcFolderHandler(testSrcHandler);
    }

    private void createTestSources(ProjectHandler projectHandler, ProjectConfiguration projectConfig)
    {
        String testSourcefolderName = projectConfig.getTestSourceFolder();
        if(StringUtils.isNullOrEmpty(testSourcefolderName) && preferencesConfig != null && ! StringUtils.isNullOrEmpty(preferencesConfig.getTestSourceFolder()))
        {
            testSourcefolderName = preferencesConfig.getTestSourceFolder();
        }
        if(StringUtils.isNullOrEmpty(testSourcefolderName))
        {
            testSourcefolderName = PreferenceConstants.PREF_JUNIT_PATH_DEFAULT;
        }

        final SourceFolderHandler testSrcHandler = newSourceFolderHandler(projectHandler, testSourcefolderName);
        projectHandler.setTestSrcFolderHandler(testSrcHandler);
        testSrcHandler.createElementsFromSources(projectConfig.getTestSources());
        testSrcHandler.createElements(projectConfig.getTestTypes());
    }

    protected void applyPreferences(WorkspaceHandler wsHandler)
    {
        final DummyPreferencesForTesting prefs = new DummyPreferencesForTesting();
        applyWorkspacePreferences(prefs);
        applyProjectProperties(wsHandler, prefs);
        applyClasspathUpdate(wsHandler);
    }

    private void applyWorkspacePreferences(DummyPreferencesForTesting prefs)
    {
        if(preferencesConfig != null && prefs != null)
        {
            prefs.setJunitDirectory(preferencesConfig.getTestSourceFolder());
            applyBasePreferences(prefs, null, preferencesConfig);
        }
    }

    private void applyBasePreferences(DummyPreferencesForTesting prefs, IJavaProject project, PreferencesBaseConfiguration prefBaseConfig)
    {
        prefs.setShouldUseTestMethodExtendedSearch(project, prefBaseConfig.isExtendedMethodSearch());
        prefs.setShouldUseTestMethodSearchByName(project, prefBaseConfig.isMethodSearchByName());
        prefs.setTestMethodTypeShouldUsePrefix(project, prefBaseConfig.isTestMethodPrefix());
        prefs.setTestSuperClass(project, prefBaseConfig.getTestSuperClass());
        prefs.setTestPackagePrefix(project, prefBaseConfig.getTestPackagePrefix());
        prefs.setTestPackageSuffix(project, prefBaseConfig.getTestPackageSuffix());
        prefs.getProjectView(project).setTestClassNameTemplate(prefBaseConfig.getTestClassNameTemplate());
        if(prefBaseConfig.getTestType() != TestType.UNDEFINED)
        {
            prefs.setTestType(project, TEST_TYPE_TO_PREF_VALUE.get(prefBaseConfig.getTestType()));
        }
    }

    private void applyProjectProperties(WorkspaceHandler workspaceHandler, DummyPreferencesForTesting prefs)
    {
        for (final ProjectConfiguration projectConfig : projectConfigs.values())
        {
            final PropertiesConfiguration propertiesConfig = projectConfig.getPropertiesConfig();
            if(propertiesConfig != null)
            {
                final ProjectHandler projectHandler = workspaceHandler.getProjectHandler(projectConfig.getProjectName());
                final IJavaProject project = projectHandler.get();

                prefs.setHasProjectSpecificSettings(project, true);

                applyBasePreferences(prefs, project, propertiesConfig);

                if(projectHandler.getTestSrcFolderHandler() != null)
                {
                    final IPackageFragmentRoot mainSrcFolder = projectHandler.getMainSrcFolderHandler().get();
                    final IPackageFragmentRoot testSrcFolder = projectHandler.getTestSrcFolderHandler().get();
                    prefs.setMappingList(project, asList(new SourceFolderMapping(project, mainSrcFolder, testSrcFolder)));
                }
            }
        }
    }

    private void applyClasspathUpdate(WorkspaceHandler workspaceHandler)
    {
        for (final ProjectConfiguration projectConfiguration : projectConfigs.values())
        {
            final ProjectHandler projectHandler = workspaceHandler.getProjectHandler(projectConfiguration.getProjectName());
            final IJavaProject project = projectHandler.get();
            final String testType = Preferences.getInstance().getTestType(project);
            IPath containerPath = null;
            if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
            {
                containerPath = JUnitCore.JUNIT3_CONTAINER_PATH;
            }
            else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
            {
                containerPath = JUnitCore.JUNIT4_CONTAINER_PATH;
            }
            else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5.equals(testType))
            {
                containerPath = new Path(JUnitCore.JUNIT_CONTAINER_ID).append("5"); //$NON-NLS-1$
            }
            else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
            {
                containerPath = new Path("org.testng.TESTNG_CONTAINER");
            }
            else
            {
                throw new IllegalArgumentException("Unknown test type: '" + testType + "' (project: '" + project.getElementName() + "')");
            }
            try
            {
                final IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(containerPath, project);
                if(classpathContainer == null)
                {
                    throw new RuntimeException("Could not find classpath container %s for project %s".formatted(containerPath, project));
                }
                WorkspaceHelper.addContainerToProject(project, classpathContainer);
            }
            catch (final IOException e)
            {
                throw new RuntimeException("Could not apply classpath update: ", e);
            }
            catch (final JavaModelException e)
            {
                throw new RuntimeException("Could not apply classpath update: ", e);
            }
        }
    }

    public ProjectConfiguration createProject(String projectName)
    {
        ProjectConfiguration projectConfig = projectConfigs.get(projectName);
        if(projectConfig == null)
        {
            projectConfig = new ProjectConfiguration(projectName);
            projectConfigs.put(projectName, projectConfig);
        }
        return projectConfig;
    }

    public PreferencesConfiguration getPreferencesConfig()
    {
        return preferencesConfig;
    }

    public void setPreferencesConfig(PreferencesConfiguration preferencesConfig)
    {
        this.preferencesConfig = preferencesConfig;
    }

    public ProjectConfiguration getProject(String projectName)
    {
        return projectConfigs.get(projectName);
    }

    public Collection<ProjectConfiguration> getProjectConfigs()
    {
        return projectConfigs.values();
    }
}
