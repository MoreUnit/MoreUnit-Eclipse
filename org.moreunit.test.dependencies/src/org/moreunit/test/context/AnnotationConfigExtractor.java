package org.moreunit.test.context;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

import org.moreunit.test.workspace.JavaType;

import com.google.common.base.Strings;

class AnnotationConfigExtractor
{
    private int maxDefinitionDepth = 10;

    public WorkspaceConfiguration extractFrom(AnnotatedElement annotatedElement, AnnotatedElement defaultAnnotatedElement)
    {
        checkNotNull(annotatedElement, "annotatedElement");

        annotatedElement = getFinalAnnotatedElement(annotatedElement);
        defaultAnnotatedElement = getFinalAnnotatedElement(defaultAnnotatedElement);

        if(annotatedElement == null && defaultAnnotatedElement == null)
        {
            return null;
        }

        WorkspaceConfiguration config = newWorkspaceConfig();

        Context context = getContext(annotatedElement, defaultAnnotatedElement);

        final Preferences preferences;
        if(context == null)
        {
            preferences = getAnnotation(annotatedElement, defaultAnnotatedElement, Preferences.class);

            Project project = getAnnotation(annotatedElement, defaultAnnotatedElement, Project.class);
            if(project == null)
            {
                throw new IllegalConfigurationException("No project defined for " + annotatedElement);
            }

            extractProjectConfiguration(config, project);
        }
        else
        {
            checkNoProjectOrPreferencesAnnotationDefined(annotatedElement);
            preferences = context.preferences();
            extractContextConfiguration(config, context);
        }

        if(preferences != null)
        {
            extractPreferencesConfiguration(config, preferences);
        }

        return config;
    }

    private AnnotatedElement getFinalAnnotatedElement(AnnotatedElement annotatedElement)
    {
        if(! hasAtLeastOneHandledAnnotationDefined(annotatedElement))
        {
            return null;
        }

        AnnotatedElement finalElement = annotatedElement;
        Context context = annotatedElement.getAnnotation(Context.class);

        if(context != null)
        {
            checkNoProjectOrPreferencesAnnotationDefined(finalElement);
        }

        int definitionDepth = 0;
        while (context != null && context.value() != null && ! context.value().equals(Undefined.class))
        {
            if(++definitionDepth > maxDefinitionDepth)
            {
                throw new IllegalConfigurationException("Too much recursion in @Context definitions");
            }

            final Class< ? > ctxtValue = context.value();

            finalElement = asAnnotatedElement(ctxtValue);

            if(! hasAtLeastOneHandledAnnotationDefined(finalElement))
            {
                throw new IllegalConfigurationException("No supported annotation found on " + ctxtValue);
            }

            context = ctxtValue.getAnnotation(Context.class);

            if(context != null)
            {
                checkNoProjectOrPreferencesAnnotationDefined(finalElement);
            }
        }

        return finalElement;
    }

    private AnnotatedElement asAnnotatedElement(final Class< ? > clazz)
    {
        return new AnnotatedElement()
        {
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return clazz.getAnnotation(annotationClass);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private boolean hasAtLeastOneHandledAnnotationDefined(AnnotatedElement annotatedElement)
    {
        if(annotatedElement != null)
        {
            for (Class< ? extends Annotation> annotationType : asList(Context.class, Project.class, Preferences.class))
            {
                if(annotatedElement.getAnnotation(annotationType) != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected WorkspaceConfiguration newWorkspaceConfig()
    {
        return new WorkspaceConfiguration();
    }

    private Context getContext(AnnotatedElement annotatedElement, AnnotatedElement defaultAnnotatedElement)
    {
        if(annotatedElement != null)
        {
            Context context = annotatedElement.getAnnotation(Context.class);
            if(context != null || defaultAnnotatedElement == null)
            {
                return context;
            }
        }
        return defaultAnnotatedElement.getAnnotation(Context.class);
    }

    @SuppressWarnings("unchecked")
    private void checkNoProjectOrPreferencesAnnotationDefined(AnnotatedElement annotatedElement)
    {
        if(annotatedElement == null)
        {
            return;
        }

        for (Class< ? extends Annotation> annotationType : asList(Project.class, Preferences.class))
        {
            if(annotatedElement.getAnnotation(annotationType) != null)
            {
                bothAnnotationsDefined(annotatedElement, Context.class, annotationType);
            }
        }
    }

    private <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, AnnotatedElement defaultAnnotatedElement, Class<A> annotationClass)
    {
        if(annotatedElement != null)
        {
            A annotation = annotatedElement.getAnnotation(annotationClass);
            if(annotation != null || defaultAnnotatedElement == null)
            {
                return annotation;
            }
        }
        return defaultAnnotatedElement.getAnnotation(annotationClass);
    }

    private void bothAnnotationsDefined(AnnotatedElement annotatedElement, Class< ? extends Annotation> annotationType1, Class< ? extends Annotation> annotationType2)
    {
        throw new IllegalConfigurationException(String.format("%s defines both a %s and a %s", annotatedElement, annotationType1.getSimpleName(), annotationType2.getSimpleName()));
    }

    private void extractProjectConfiguration(WorkspaceConfiguration config, Project project)
    {
        int definitionDepth = 0;
        while (project.value() != null && ! project.value().equals(Undefined.class))
        {
            if(++definitionDepth > maxDefinitionDepth)
            {
                throw new IllegalConfigurationException("Too much recursion in @Project definitions");
            }

            Class< ? > projValue = project.value();
            project = projValue.getAnnotation(Project.class);
            if(project == null)
            {
                throw new IllegalConfigurationException("@Project not found on " + projValue);
            }
        }

        String projectName = StringUtils.firstNonBlank(project.name(), Defaults.PROJECT_NAME);
        ProjectConfiguration projectConfig = config.createProject(projectName);
        projectConfig.setMainTypes(splitTypes(project.mainCls()));
        projectConfig.setMainSources(splitSources(project.mainSrc()));
        projectConfig.setMainSourceFolder(project.mainSrcFolder().trim());
        projectConfig.setTestTypes(splitTypes(project.testCls()));
        projectConfig.setTestSources(splitSources(project.testSrc()));
        projectConfig.setTestSourceFolder(project.testSrcFolder().trim());

        TestProject testProject = project.testProject();
        if(hasPropertiesDefined(testProject))
        {
            if(! Strings.isNullOrEmpty(project.testCls()))
            {
                throw new IllegalConfigurationException("Both testCls and testProject are defined for @Project");
            }
            if(! Strings.isNullOrEmpty(project.testSrc()))
            {
                throw new IllegalConfigurationException("Both testSrc and testProject are defined for @Project");
            }

            String testProjectName = StringUtils.firstNonBlank(testProject.name(), Defaults.TEST_PROJECT_NAME);
            TestProjectConfiguration testProjectConfig = new TestProjectConfiguration(testProjectName);
            testProjectConfig.setSources(splitSources(testProject.src()));
            testProjectConfig.setTypes(splitTypes(testProject.cls()));
            testProjectConfig.setSourceFolder(testProject.srcFolder().trim());
            projectConfig.setTestProjectConfig(testProjectConfig);
        }

        Properties properties = project.properties();
        if(properties != null)
        {
            extractPropertiesConfiguration(projectConfig, properties);
        }
    }

    private boolean hasPropertiesDefined(TestProject testProject)
    {
        return testProject != null && StringUtils.atLeastOneNotEmpty(testProject.name(), testProject.src(), testProject.cls(), testProject.srcFolder());
    }

    private Collection<JavaType> splitTypes(String classes)
    {
        Set<JavaType> types = newHashSet();

        String[] typeDefsByPackage = StringUtils.split(classes, ";");
        if(typeDefsByPackage.length == 0)
        {
            typeDefsByPackage = new String[] { classes };
        }

        for (String typeDefsForPackage : typeDefsByPackage)
        {
            final String packageName;
            final String typeDefs;

            int columnIdx = typeDefsForPackage.indexOf(":");
            if(columnIdx == - 1)
            {
                packageName = "";
                typeDefs = typeDefsForPackage;
            }
            else
            {
                packageName = typeDefsForPackage.substring(0, columnIdx);
                typeDefs = typeDefsForPackage.substring(columnIdx + 1);
            }

            for (String typeDef : StringUtils.split(typeDefs, ","))
            {
                if(typeDef.startsWith("enum "))
                {
                    types.add(JavaType.newEnum(packageName, typeDef.substring(5)));
                }
                else if(typeDef.startsWith("class "))
                {
                    types.add(JavaType.newClass(packageName, typeDef.substring(6)));
                }
                else
                {
                    types.add(JavaType.newClass(packageName, typeDef));
                }
            }
        }

        return types;
    }

    private String[] splitSources(String sources)
    {
        return StringUtils.split(sources, ",");
    }

    private void extractPropertiesConfiguration(ProjectConfiguration projectConfig, Properties properties)
    {
        int definitionDepth = 0;
        while (properties.value() != null && ! properties.value().equals(Undefined.class))
        {
            if(++definitionDepth > maxDefinitionDepth)
            {
                throw new IllegalConfigurationException("Too much recursion in @Properties definitions");
            }

            Class< ? > propValue = properties.value();
            properties = propValue.getAnnotation(Properties.class);
            if(properties == null)
            {
                throw new IllegalConfigurationException("@Properties not found on " + propValue);
            }
        }

        PropertiesConfiguration propertiesConfig = new PropertiesConfiguration();
        propertiesConfig.setExtendedMethodSearch(properties.extendedMethodSearch());
        propertiesConfig.setFlexibleNaming(properties.flexibleNaming());
        propertiesConfig.setMethodPrefix(properties.testMethodPrefix());
        propertiesConfig.setTestClassPrefixes(properties.testClassPrefixes());
        propertiesConfig.setTestClassSuffixes(properties.testClassSuffixes());
        propertiesConfig.setTestPackagePrefix(properties.testPackagePrefix());
        propertiesConfig.setTestPackageSuffix(properties.testPackageSuffix());
        propertiesConfig.setTestSuperClass(properties.testSuperClass());
        propertiesConfig.setTestType(properties.testType());
        propertiesConfig.setUserSetProperties(properties.userSetProperties());
        projectConfig.setPropertiesConfig(propertiesConfig);
    }

    private void extractContextConfiguration(WorkspaceConfiguration config, Context context)
    {
        ProjectConfiguration projectConfig = config.createProject(Defaults.PROJECT_NAME);
        projectConfig.setMainTypes(splitTypes(context.mainCls()));
        projectConfig.setMainSources(splitSources(context.mainSrc()));
        projectConfig.setTestTypes(splitTypes(context.testCls()));
        projectConfig.setTestSources(splitSources(context.testSrc()));
    }

    private void extractPreferencesConfiguration(WorkspaceConfiguration config, Preferences preferences)
    {
        if(None.class.equals(preferences.value()))
        {
            return;
        }

        int definitionDepth = 0;
        while (preferences.value() != null && ! preferences.value().equals(Undefined.class))
        {
            if(++definitionDepth > maxDefinitionDepth)
            {
                throw new IllegalConfigurationException("Too much recursion in @Preferences definitions");
            }

            Class< ? > prefValue = preferences.value();
            preferences = preferences.value().getAnnotation(Preferences.class);
            if(preferences == null)
            {
                throw new IllegalConfigurationException("@Preferences not found on " + prefValue);
            }
        }

        PreferencesConfiguration preferencesConfig = new PreferencesConfiguration();
        preferencesConfig.setExtendedMethodSearch(preferences.extendedMethodSearch());
        preferencesConfig.setFlexibleNaming(preferences.flexibleNaming());
        preferencesConfig.setMethodPrefix(preferences.testMethodPrefix());
        preferencesConfig.setTestClassPrefixes(preferences.testClassPrefixes());
        preferencesConfig.setTestClassSuffixes(preferences.testClassSuffixes());
        preferencesConfig.setTestPackagePrefix(preferences.testPackagePrefix());
        preferencesConfig.setTestPackageSuffix(preferences.testPackageSuffix());
        preferencesConfig.setTestSourceFolder(preferences.testSourcefolder());
        preferencesConfig.setTestSuperClass(preferences.testSuperClass());
        preferencesConfig.setTestType(preferences.testType());
        config.setPreferencesConfig(preferencesConfig);
    }

    void setMaximimDefinitionDepth(int depth)
    {
        maxDefinitionDepth = depth;
    }
}
