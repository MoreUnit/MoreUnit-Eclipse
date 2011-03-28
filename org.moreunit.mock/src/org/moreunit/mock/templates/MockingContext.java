package org.moreunit.mock.templates;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.TemplateException;
import org.moreunit.mock.elements.Dependencies;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.InjectionType;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.Part;
import org.moreunit.mock.templates.resolvers.ConstructorInjectionPatternResolver;
import org.moreunit.mock.templates.resolvers.DependencyPatternsResolver;
import org.moreunit.mock.templates.resolvers.FieldInjectionPatternResolver;
import org.moreunit.mock.templates.resolvers.ObjectUnderTestPatternsResolver;
import org.moreunit.mock.templates.resolvers.SetterInjectionPatternResolver;

import static java.util.Arrays.asList;

/**
 * Holds information about the current mocking operation.
 */
public class MockingContext
{
    public final IType classUnderTest;
    public final ICompilationUnit testCaseCompilationUnit;
    private final List<PatternResolver> patternResolvers;
    private final Dependencies dependencies;
    private final Set<InjectionType> injectionTypesUsed = new HashSet<InjectionType>();

    private IMethod beforeInstanceMethod;
    private String beforeInstanceMethodName;

    public MockingContext(IType classUnderTest, ICompilationUnit testCase) throws MockingTemplateException
    {
        this(classUnderTest, testCase, null, null);
    }

    /**
     * For testing purposes.
     */
    public MockingContext(IType classUnderTest, ICompilationUnit testCase, Dependencies dependencies, List<PatternResolver> patternResolvers) throws MockingTemplateException
    {
        this.classUnderTest = classUnderTest;
        this.testCaseCompilationUnit = testCase;
        this.dependencies = createDependenciesIfNull(dependencies, classUnderTest, testCase);
        this.patternResolvers = createPatternResolversIfNull(patternResolvers);

        initialize();
    }

    private Dependencies createDependenciesIfNull(Dependencies dependencies, IType classUnderTest, ICompilationUnit testCase) throws MockingTemplateException
    {
        if(dependencies != null)
        {
            return dependencies;
        }

        try
        {
            NamingRules namingRules = new NamingRules(classUnderTest.getJavaProject());
            Dependencies d = new Dependencies(namingRules, classUnderTest, testCase.findPrimaryType());
            d.compute();
            return d;
        }
        catch (JavaModelException e)
        {
            throw new MockingTemplateException("Could not determine dependencies to mock", e, true);
        }
    }

    private List<PatternResolver> createPatternResolversIfNull(List<PatternResolver> patternResolvers)
    {
        if(patternResolvers != null)
        {
            return patternResolvers;
        }

        return asList(new ConstructorInjectionPatternResolver(this),
                      new SetterInjectionPatternResolver(this),
                      new FieldInjectionPatternResolver(this),
                      new DependencyPatternsResolver(this),
                      new ObjectUnderTestPatternsResolver(this));
    }

    private void initialize() throws MockingTemplateException
    {
        if(! dependencies.constructorDependencies.isEmpty())
        {
            injectionTypesUsed.add(InjectionType.constructor);
        }

        if(! dependencies.setterDependencies.isEmpty())
        {
            injectionTypesUsed.add(InjectionType.setter);
        }

        if(! dependencies.fieldDependencies.isEmpty())
        {
            injectionTypesUsed.add(InjectionType.field);
        }
    }

    public EclipseTemplate preEvaluate(CodeTemplate codeTemplate) throws MockingTemplateException
    {
        String pattern = codeTemplate.pattern();
        for (PatternResolver resolver : patternResolvers)
        {
            pattern = resolver.resolve(pattern);
        }

        return new EclipseTemplate(codeTemplate.part(), pattern);
    }

    public void prepareContext(MockingTemplate mockingTemplate, TemplateProcessor templateProcessor) throws JavaModelException, BadLocationException, TemplateException, MockingTemplateException
    {
        if(beforeInstanceMethodIsRequired(mockingTemplate))
        {
            beforeInstanceMethodName = "create" + classUnderTest.getElementName();
            beforeInstanceMethod = findBeforeMethod(beforeInstanceMethodName);

            if(beforeInstanceMethod == null)
            {
                beforeInstanceMethodName = createBeforeInstanceMethod(templateProcessor, beforeInstanceMethodName);
            }
        }
    }

    private boolean beforeInstanceMethodIsRequired(MockingTemplate mockingTemplate)
    {
        for (CodeTemplate codeTemplate : mockingTemplate.codeTemplates())
        {
            if(Part.BEFORE_INSTANCE_METHOD == codeTemplate.part())
            {
                return true;
            }
        }
        return false;
    }

    private IMethod findBeforeMethod(String beforeMethodName) throws JavaModelException
    {
        for (IMethod method : testCaseCompilationUnit.findPrimaryType().getMethods())
        {
            if(beforeMethodName.equals(method.getElementName()) && method.getNumberOfParameters() == 0 && method.getAnnotation("Before").exists())
            {
                return method;
            }
        }
        return null;
    }

    private String createBeforeInstanceMethod(TemplateProcessor templateProcessor, String methodBaseName) throws JavaModelException, BadLocationException, TemplateException, MockingTemplateException
    {
        String methodName = incrementMethodNameIfRequired(methodBaseName);
        String beforeMethodSource = "@${beforeAnnotation:newType(org.junit.Before)} public void " + methodName + "() throws Exception {}";

        CodeTemplate template = new CodeTemplate("org.moreunit.mock.beforeInstanceMethodCreation", Part.BEFORE_INSTANCE_METHOD_DEFINITION, beforeMethodSource);
        templateProcessor.applyTemplate(template, this);

        return methodName;
    }

    private String incrementMethodNameIfRequired(String methodBaseName) throws JavaModelException
    {
        String methodName = methodBaseName;
        for (int i = 2; hasMethod(testCaseCompilationUnit.findPrimaryType(), methodName); i++)
        {
            methodName = methodBaseName + i;
        }
        return methodName;
    }

    private boolean hasMethod(IType type, String methodName) throws JavaModelException
    {
        for (IMethod method : type.getMethods())
        {
            if(methodName.equals(method.getElementName()))
            {
                return true;
            }
        }
        return false;
    }

    public IMethod beforeInstanceMethod() throws JavaModelException
    {
        if(beforeInstanceMethod == null)
        {
            beforeInstanceMethod = testCaseCompilationUnit.findPrimaryType().getMethod(beforeInstanceMethodName, new String[0]);
        }
        return beforeInstanceMethod;
    }

    public boolean hasDependenciesToMock()
    {
        return ! dependencies.isEmpty();
    }

    public Dependencies dependenciesToMock()
    {
        return dependencies;
    }

    public boolean usesInjectionType(Enum<InjectionType> injectionType)
    {
        return injectionTypesUsed.contains(injectionType);
    }
}
