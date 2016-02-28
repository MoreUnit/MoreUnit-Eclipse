package org.moreunit.preferences;

import static org.moreunit.util.PluginTools.guessTestFolderCorrespondingToMainSrcFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.log.Logger;
import org.moreunit.core.util.Strings;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.matching.TestClassNamePattern;
import org.moreunit.util.PluginTools;

public class Preferences
{
    private static final Pattern MAVEN_TEST_FOLDER = Pattern.compile("src/test/.*");

    /**
     * Cache for TestClassNamePattern instances.
     * <p>
     * It is OK to clear it entirely every time a preference changes ; that way
     * we can guarantee that entries won't be kept in memory without reason.
     */
    private static final Map<String, TestClassNamePattern> CLASS_NAME_PATTERN_CACHE = new HashMap<String, TestClassNamePattern>();
    private static final Object CACHE_LOCK = new Object();

    private static Preferences instance = new Preferences();

    private final Logger logger = MoreUnitPlugin.getDefault().getLogger();
    private final Map<IJavaProject, IPreferenceStore> preferenceMap = new HashMap<IJavaProject, IPreferenceStore>();

    protected Preferences()
    {
        migratePrefsIfRequired(initStore(getWorkbenchStore()));
    }

    public static Preferences getInstance()
    {
        return instance;
    }

    /**
     * Necessary for easier testing
     */
    protected static void setInstance(Preferences preferences)
    {
        instance = preferences;
    }

    protected static IPreferenceStore migratePrefsIfRequired(IPreferenceStore store)
    {
        new PreferencesMigrator(store).migrate();
        return store;
    }

    protected static final IPreferenceStore initStore(IPreferenceStore store)
    {
        store.setDefault(PreferenceConstants.PREF_JUNIT_PATH, PreferenceConstants.PREF_JUNIT_PATH_DEFAULT);
        store.setDefault(PreferenceConstants.TEST_TYPE, PreferenceConstants.DEFAULT_TEST_TYPE);
        store.setDefault(PreferenceConstants.SHOW_REFACTORING_DIALOG, true);
        store.setDefault(PreferenceConstants.SWITCH_TO_MATCHING_METHOD, PreferenceConstants.DEFAULT_SWITCH_TO_MATCHING_METHOD);
        store.setDefault(PreferenceConstants.TEST_PACKAGE_PREFIX, PreferenceConstants.DEFAULT_TEST_PACKAGE_PREFIX);
        store.setDefault(PreferenceConstants.TEST_SUPERCLASS, PreferenceConstants.DEFAULT_TEST_SUPERCLASS);
        store.setDefault(PreferenceConstants.TEST_METHOD_TYPE, PreferenceConstants.TEST_METHOD_TYPE_JUNIT3);
        store.setDefault(PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH, PreferenceConstants.DEFAULT_EXTENDED_TEST_METHOD_SEARCH);
        store.setDefault(PreferenceConstants.ENABLE_TEST_METHOD_SEARCH_BY_NAME, PreferenceConstants.DEFAULT_ENABLE_TEST_METHOD_SEARCH_BY_NAME);
        store.setDefault(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE, PreferenceConstants.DEFAULT_TEST_CLASS_NAME_TEMPLATE);
        store.setDefault(PreferenceConstants.TEST_METHOD_DEFAULT_CONTENT, PreferenceConstants.DEFAULT_TEST_METHOD_DEFAULT_CONTENT);
        store.setDefault(PreferenceConstants.TEST_ANNOTATION_MODE, PreferenceConstants.DEFAULT_TEST_ANNOTATION_MODE);
        return store;
    }

    public IPreferenceStore getWorkbenchStore()
    {
        return MoreUnitPlugin.getDefault().getPreferenceStore();
    }

    public boolean hasProjectSpecificSettings(IJavaProject javaProject)
    {
        if(javaProject == null)
            return false;

        return storeToRead(javaProject).getBoolean(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS);
    }

    public void setHasProjectSpecificSettings(IJavaProject javaProject, boolean hasProjectSpecificSettings)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS, hasProjectSpecificSettings);
    }

    public void setMappingList(IJavaProject javaProject, List<SourceFolderMapping> mappingList)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.UNIT_SOURCE_FOLDER, PreferencesConverter.convertSourceMappingsToString(mappingList));
    }

    public List<SourceFolderMapping> getSourceMappingList(IJavaProject javaProject)
    {
        if(hasProjectSpecificSettings(javaProject))
        {
            return getProjectSpecificSourceMappingList(javaProject);
        }
        else
        {
            return getWorkspaceSpecificSourceMappingList(javaProject);
        }
    }

    private List<SourceFolderMapping> getProjectSpecificSourceMappingList(IJavaProject javaProject)
    {
        String mappingString = storeToRead(javaProject).getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
        return PreferencesConverter.convertStringToSourceMappingList(mappingString);
    }

    private List<SourceFolderMapping> getWorkspaceSpecificSourceMappingList(IJavaProject javaProject)
    {
        List<SourceFolderMapping> mappings = new ArrayList<SourceFolderMapping>();

        List<IPackageFragmentRoot> javaSourceFolders = PluginTools.findJavaSourceFoldersFor(javaProject);

        IPackageFragmentRoot testSourceFolder = findDefaultTestSourceFolder(javaSourceFolders);

        List<IPackageFragmentRoot> possibleMainSrcFolders = findPossibleMainSourceFolders(javaSourceFolders);

        if(testSourceFolder != null)
        {
            for (IPackageFragmentRoot mainSourceFolder : possibleMainSrcFolders)
            {
                mappings.add(new SourceFolderMapping(javaProject, mainSourceFolder, testSourceFolder));
            }
        }

        return mappings;
    }

    private IPackageFragmentRoot findDefaultTestSourceFolder(List<IPackageFragmentRoot> sourceFolders)
    {
        String defaultTestSourceFolderPath = getWorkbenchStore().getString(PreferenceConstants.PREF_JUNIT_PATH);

        for (IPackageFragmentRoot sourceFolder : sourceFolders)
        {
            String sourceFolderPath = PluginTools.getPathStringWithoutProjectName(sourceFolder);

            if(sourceFolderPath.equals(defaultTestSourceFolderPath))
            {
                return sourceFolder;
            }
        }

        return null;
    }

    private List<IPackageFragmentRoot> findPossibleMainSourceFolders(List<IPackageFragmentRoot> javaSourceFolders)
    {
        List<IPackageFragmentRoot> possibleMainSrcFolders = new ArrayList<IPackageFragmentRoot>();

        String defaultTestSourceFolderPath = getWorkbenchStore().getString(PreferenceConstants.PREF_JUNIT_PATH);

        for (IPackageFragmentRoot sourceFolder : javaSourceFolders)
        {
            String sourceFolderPath = PluginTools.getPathStringWithoutProjectName(sourceFolder);

            if(! (sourceFolderPath.equals(defaultTestSourceFolderPath) || isMavenLikeTestFolder(sourceFolderPath)))
            {
                possibleMainSrcFolders.add(sourceFolder);
            }
        }

        return possibleMainSrcFolders;
    }

    private static boolean isMavenLikeTestFolder(String srcFolderPath)
    {
        return MAVEN_TEST_FOLDER.matcher(srcFolderPath).matches();
    }

    public String getJunitDirectoryFromPreferences(IJavaProject javaProject)
    {
        return storeToRead(javaProject).getString(PreferenceConstants.PREF_JUNIT_PATH);
    }

    public void setJunitDirectory(String directory)
    {
        getProjectStore(null).setValue(PreferenceConstants.PREF_JUNIT_PATH, directory);
    }

    public String getTestMethodType(IJavaProject javaProject)
    {
        return storeToRead(javaProject).getString(PreferenceConstants.TEST_METHOD_TYPE);
    }

    public void setTestMethodTypeShouldUsePrefix(IJavaProject javaProject, boolean shouldUsePrefix)
    {
        String value;
        if(shouldUsePrefix)
            value = PreferenceConstants.TEST_METHOD_TYPE_JUNIT3;
        else
            value = PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX;

        getProjectStore(javaProject).setValue(PreferenceConstants.TEST_METHOD_TYPE, value);
    }

    public String getTestMethodDefaultContent(IJavaProject javaProject)
    {
        return storeToRead(javaProject).getString(PreferenceConstants.TEST_METHOD_DEFAULT_CONTENT);
    }

    public void setTestMethodDefaultContent(IJavaProject javaProject, String methodContent)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.TEST_METHOD_DEFAULT_CONTENT, methodContent);
    }

    public String getTestSuperClass(IJavaProject javaProject)
    {
        return getStringValue(PreferenceConstants.TEST_SUPERCLASS, javaProject);
    }

    public void setTestSuperClass(IJavaProject javaProject, String testSuperClass)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.TEST_SUPERCLASS, testSuperClass);
    }

    private boolean getBooleanValue(final String key, IJavaProject javaProject)
    {
        if(storeToRead(javaProject).contains(key))
        {
            return storeToRead(javaProject).getBoolean(key);
        }
        return storeToRead(javaProject).getDefaultBoolean(key);
    }

    private String getStringValue(final String key, IJavaProject javaProject)
    {
        if(storeToRead(javaProject).contains(key))
        {
            return storeToRead(javaProject).getString(key);
        }
        return storeToRead(javaProject).getDefaultString(key);
    }

    public String getTestType(IJavaProject javaProject)
    {
        if(storeToRead(javaProject).contains(PreferenceConstants.TEST_TYPE))
        {
            return storeToRead(javaProject).getString(PreferenceConstants.TEST_TYPE);
        }
        return PreferenceConstants.DEFAULT_TEST_TYPE;
    }

    public void setTestType(IJavaProject javaProject, String testType)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.TEST_TYPE, testType);
    }

    public String getTestPackagePrefix(IJavaProject javaProject)
    {
        return getStringValue(PreferenceConstants.TEST_PACKAGE_PREFIX, javaProject);
    }

    public void setTestPackagePrefix(IJavaProject javaProject, String packagePrefix)
    {
        synchronized (CACHE_LOCK)
        {
            CLASS_NAME_PATTERN_CACHE.clear();
            getProjectStore(javaProject).setValue(PreferenceConstants.TEST_PACKAGE_PREFIX, packagePrefix);
        }
    }

    public String getTestPackageSuffix(IJavaProject javaProject)
    {
        return getStringValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, javaProject);
    }

    public void setTestPackageSuffix(IJavaProject javaProject, String packageSuffix)
    {
        synchronized (CACHE_LOCK)
        {
            CLASS_NAME_PATTERN_CACHE.clear();
            getProjectStore(javaProject).setValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, packageSuffix);
        }
    }

    private IPreferenceStore storeToRead(IJavaProject javaProject)
    {
        IPreferenceStore resultStore = getProjectStore(javaProject);

        if(resultStore.getBoolean(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS))
            return resultStore;

        return getWorkbenchStore();
    }

    public IPreferenceStore getProjectStore(IJavaProject javaProject)
    {
        if(javaProject == null)
        {
            return getWorkbenchStore();
        }

        if(preferenceMap.containsKey(javaProject))
        {
            return preferenceMap.get(javaProject);
        }

        return getOrCreateProjectStore(javaProject);
    }

    private synchronized IPreferenceStore getOrCreateProjectStore(IJavaProject javaProject)
    {
        IPreferenceStore resultStore = null;

        // second check, synchronized
        if(preferenceMap.containsKey(javaProject))
        {
            resultStore = preferenceMap.get(javaProject);
        }
        else
        {
            ProjectScope projectScopeContext = new ProjectScope(javaProject.getProject());
            ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(projectScopeContext, MoreUnitPlugin.PLUGIN_ID);
            preferenceStore.setSearchContexts(new IScopeContext[] { projectScopeContext });
            preferenceMap.put(javaProject, preferenceStore);
            resultStore = initStore(migratePrefsIfRequired(preferenceStore));
            saveMigrationResultIfRequired(preferenceStore, javaProject);
        }

        return resultStore;
    }

    private void saveMigrationResultIfRequired(ScopedPreferenceStore store, IJavaProject javaProject)
    {
        if(store.getBoolean(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS) && store.needsSaving())
        {
            if(logger.debugEnabled())
            {
                logger.debug("Saving preferences for " + javaProject + " (template: " + store.getString(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE) + ")");
            }

            try
            {
                store.save();
            }
            catch (IOException e)
            {
                logger.error("Could not save preferences for project " + javaProject, e);
            }
        }
    }

    public void clearProjectCache()
    {
        synchronized (preferenceMap)
        {
            preferenceMap.clear();
        }
    }

    public IPackageFragmentRoot getTestSourceFolder(IJavaProject project, IPackageFragmentRoot mainSrcFolder)
    {
        // check for project specific settings
        List<SourceFolderMapping> mappings = getSourceMappingList(project);

        for (SourceFolderMapping mapping : mappings)
        {
            if(mapping.getSourceFolderList().contains(mainSrcFolder))
            {
                return mapping.getTestFolder();
            }
        }

        if(! mappings.isEmpty())
        {
            // falls back to first test folder defined
            return mappings.get(0).getTestFolder();
        }

        // no mapping exists: falls back to un-mapped source folders
        String junitFolder = getJunitDirectoryFromPreferences(project);
        List<IPackageFragmentRoot> javaSrcFolders = PluginTools.findJavaSourceFoldersFor(project);

        for (IPackageFragmentRoot packageFragmentRoot : javaSrcFolders)
        {
            if(PluginTools.getPathStringWithoutProjectName(packageFragmentRoot).equals(junitFolder))
            {
                return packageFragmentRoot;
            }
        }

        // attempt to make everyone happy
        IPackageFragmentRoot mvnTestFolder = guessTestFolderCorrespondingToMainSrcFolder(project, mainSrcFolder);
        if(mvnTestFolder != null)
            return mvnTestFolder;

        // falls back to given source folder
        return mainSrcFolder;
    }

    public IPackageFragmentRoot getMainSourceFolder(IJavaProject mainProject, IPackageFragmentRoot testSrcFolder)
    {
        List<SourceFolderMapping> mappings = getSourceMappingList(mainProject);

        for (SourceFolderMapping mapping : mappings)
        {
            if(mapping.getTestFolder().equals(testSrcFolder))
            {
                return mapping.getSourceFolderList().get(0);
            }
        }

        if(! mappings.isEmpty())
        {
            // falls back to first main folder defined
            return mappings.get(0).getSourceFolderList().get(0);
        }

        // no mapping exists: falls back to un-mapped source folders
        List<IPackageFragmentRoot> possibleMainSourceFolders = findPossibleMainSourceFolders(PluginTools.findJavaSourceFoldersFor(mainProject));
        if(! possibleMainSourceFolders.isEmpty())
        {
            return possibleMainSourceFolders.get(0);
        }

        // falls back to given source folder
        return testSrcFolder;
    }

    public IJavaProject getMainProject(IJavaProject testProject)
    {
        for (IPreferenceStore store : stores())
        {
            String mappingString = store.getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
            List<SourceFolderMapping> mappings = PreferencesConverter.convertStringToSourceMappingList(mappingString);
            for (SourceFolderMapping mapping : mappings)
            {
                if(mapping.getTestFolder().getJavaProject().equals(testProject))
                {
                    return mapping.getJavaProject();
                }
            }
        }

        // falls back to given project
        return testProject;
    }

    private Collection<IPreferenceStore> stores()
    {
        return preferenceMap.values();
    }

    public boolean shouldGenerateCommentsForTestMethod(IJavaProject javaProject)
    {
        if(storeToRead(javaProject).contains(PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD))
        {
            return storeToRead(javaProject).getBoolean(PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD);
        }
        return storeToRead(javaProject).getDefaultBoolean(PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD);
    }

    public void setGenerateCommentsForTestMethod(IJavaProject javaProject, boolean addComments)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD, addComments);
    }

    public MethodSearchMode getMethodSearchMode(IJavaProject javaProject)
    {
        boolean searchByCall = getBooleanValue(PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH, javaProject);
        boolean searchByName = ! searchByCall || getBooleanValue(PreferenceConstants.ENABLE_TEST_METHOD_SEARCH_BY_NAME, javaProject);
        return new MethodSearchMode(searchByCall, searchByName);
    }

    public void setShouldUseTestMethodExtendedSearch(IJavaProject javaProject, boolean shouldUseExtendedSearch)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH, shouldUseExtendedSearch);
    }

    public void setShouldUseTestMethodSearchByName(IJavaProject javaProject, boolean shouldUseTestMethodSearchByName)
    {
        getProjectStore(javaProject).setValue(PreferenceConstants.ENABLE_TEST_METHOD_SEARCH_BY_NAME, shouldUseTestMethodSearchByName);
    }

    public static ProjectPreferences forProject(IJavaProject project)
    {
        return getInstance().getProjectView(project);
    }

    public ProjectPreferences getProjectView(IJavaProject project)
    {
        return new ProjectPreferences(this, project);
    }

    public ProjectPreferences getWorkspaceView()
    {
        return new ProjectPreferences(this, null);
    }

    public String getTestAnnotationMode(IJavaProject project)
    {
        return getStringValue(PreferenceConstants.TEST_ANNOTATION_MODE, project);
    }

    public void setTestAnnotationMode(IJavaProject project, TestAnnotationMode mode)
    {
        getProjectStore(project).setValue(PreferenceConstants.TEST_ANNOTATION_MODE, mode.toString());
    }

    public static class MethodSearchMode
    {
        public static final MethodSearchMode BY_CALL = new MethodSearchMode(true, false);
        public static final MethodSearchMode BY_NAME = new MethodSearchMode(false, true);
        public static final MethodSearchMode BY_CALL_AND_BY_NAME = new MethodSearchMode(true, true);
        public static final MethodSearchMode DEFAULT = BY_CALL;

        public final boolean searchByCall;
        public final boolean searchByName;

        public MethodSearchMode(boolean byCall, boolean byName)
        {
            searchByCall = byCall;
            searchByName = byName;
        }
    }

    public static class ProjectPreferences
    {
        private final Preferences prefs;
        private final IJavaProject project;

        public ProjectPreferences(Preferences prefs, IJavaProject project)
        {
            this.prefs = prefs;
            this.project = project;
        }

        public IPackageFragmentRoot getMainSourceFolder(IPackageFragmentRoot testSrcFolder)
        {
            return prefs.getMainSourceFolder(project, testSrcFolder);
        }

        public String getPackagePrefix()
        {
            return Strings.nullIfBlank(prefs.getTestPackagePrefix(project));
        }

        public String getPackageSuffix()
        {
            return Strings.nullIfBlank(prefs.getTestPackageSuffix(project));
        }

        public List<SourceFolderMapping> getSourceFolderMappings()
        {
            return prefs.getSourceMappingList(project);
        }

        public IPackageFragmentRoot getTestSourceFolder(IPackageFragmentRoot mainSrcFolder)
        {
            return prefs.getTestSourceFolder(project, mainSrcFolder);
        }

        public String getTestMethodDefaultContent()
        {
            return prefs.getTestMethodDefaultContent(project);
        }

        public String getTestMethodType()
        {
            return prefs.getTestMethodType(project);
        }

        public String getTestSuperClass()
        {
            return prefs.getTestSuperClass(project);
        }

        public String getTestType()
        {
            return prefs.getTestType(project);
        }

        public boolean shouldGenerateCommentsForTestMethod()
        {
            return prefs.shouldGenerateCommentsForTestMethod(project);
        }

        public boolean shouldUseJunit4Type()
        {
            return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(getTestType());
        }

        public boolean shouldUseJunit3Type()
        {
            return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(getTestType());
        }

        public boolean shouldUseTestNgType()
        {
            return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(getTestType());
        }

        public boolean hasSpecificSettings()
        {
            return prefs.hasProjectSpecificSettings(project);
        }

        public MethodSearchMode getMethodSearchMode()
        {
            return prefs.getMethodSearchMode(project);
        }

        public TestClassNamePattern getTestClassNamePattern()
        {
            synchronized (CACHE_LOCK)
            {
                String template = getTestClassNameTemplate();
                String prefix = getPackagePrefix();
                String suffix = getPackageSuffix();
                String key = (template == null ? "" : template.length() + template) //
                             + (prefix == null ? "" : prefix.length() + prefix) //
                             + (suffix == null ? "" : suffix.length() + suffix);

                TestClassNamePattern pattern = CLASS_NAME_PATTERN_CACHE.get(key);
                if(pattern == null)
                {
                    pattern = new TestClassNamePattern(template, prefix, suffix);
                    CLASS_NAME_PATTERN_CACHE.put(key, pattern);
                }

                return pattern;
            }
        }

        public void setTestClassNameTemplate(String template)
        {
            synchronized (CACHE_LOCK)
            {
                CLASS_NAME_PATTERN_CACHE.clear();
                prefs.getProjectStore(project).setValue(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE, template);
            }
        }

        public String getTestClassNameTemplate()
        {
            return prefs.getStringValue(PreferenceConstants.TEST_CLASS_NAME_TEMPLATE, project);
        }

        public TestAnnotationMode getTestAnnotationMode()
        {
            String mode = prefs.getTestAnnotationMode(project);
            return mode == null || mode.isEmpty() ? TestAnnotationMode.OFF : TestAnnotationMode.valueOf(mode);
        }
    }
}
