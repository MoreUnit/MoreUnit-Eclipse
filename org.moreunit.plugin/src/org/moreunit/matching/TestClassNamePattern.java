package org.moreunit.matching;

import org.eclipse.jdt.core.IType;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.util.JavaType;

public class TestClassNamePattern
{
    private static final String CAMEL_CASE_SEPARATOR = "";

    private final String packagePrefix;
    private final String packageSuffix;

    private final TestFileNamePattern regularPattern;
    private final TestFileNamePattern patternForcingEvaluationAsSourceFile;
    private final TestFileNamePattern patternForcingEvaluationAsTestFile;

    public TestClassNamePattern(String nameTemplate, String packagePrefix, String packageSuffix)
    {
        this.packagePrefix = packagePrefix;
        this.packageSuffix = packageSuffix;

        this.regularPattern = new TestFileNamePattern(nameTemplate, CAMEL_CASE_SEPARATOR);
        this.patternForcingEvaluationAsSourceFile = TestFileNamePattern.forceEvaluationAsSourceFile(nameTemplate, CAMEL_CASE_SEPARATOR);
        this.patternForcingEvaluationAsTestFile = TestFileNamePattern.forceEvaluationAsTestFile(nameTemplate, CAMEL_CASE_SEPARATOR);
    }

    public ClassNameEvaluation evaluate(IType type)
    {
        String packageName = type.getPackageFragment().getElementName();

        final TestFileNamePattern pattern;
        if(matchesTestPackagePattern(packageName))
        {
            // will evaluate whether file name matches test pattern
            pattern = regularPattern;
        }
        else
        {
            // won't evaluate file name (it can't be a test)
            // will only produce names for corresponding files
            pattern = patternForcingEvaluationAsSourceFile;
        }

        return evaluate(pattern, new JavaType(type.getElementName(), packageName));
    }

    private ClassNameEvaluation evaluate(TestFileNamePattern pattern, JavaType type)
    {
        FileNameEvaluation evaluation = pattern.evaluate(type.getSimpleName());
        return new ClassNameEvaluation(evaluation, packagePrefix, packageSuffix, type.getQualifier());
    }

    public JavaType nameTestCaseFor(IType classUnderTest)
    {
        ClassNameEvaluation evaluation = evaluate(patternForcingEvaluationAsSourceFile, new JavaType(classUnderTest.getFullyQualifiedName()));

        return evaluation.getPreferredCorrespondingClass();
    }

    public JavaType nameClassTestedBy(IType testCase)
    {
        ClassNameEvaluation evaluation = evaluate(patternForcingEvaluationAsTestFile, new JavaType(testCase.getFullyQualifiedName()));

        return evaluation.getPreferredCorrespondingClass();
    }

    private boolean matchesTestPackagePattern(String packageName)
    {
        if(packagePrefix != null && ! packageName.startsWith(packagePrefix + "."))
        {
            return false;
        }
        if(packageSuffix != null && ! packageName.endsWith("." + packageSuffix))
        {
            return false;
        }
        return true;
    }
}
