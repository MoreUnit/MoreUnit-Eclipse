/**
 *
 */
package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.moreunit.util.MoreUnitContants;

/**
 * @author vera
 */
public class FilterMethodVisitor extends ASTVisitor
{

    private final List<MethodDeclaration> privateMethods = new ArrayList<>();
    private final List<FieldDeclaration> fieldDeclarations = new ArrayList<>();
    private final List<MethodDeclaration> getterMethods = new ArrayList<>();
    private final List<MethodDeclaration> setterMethods = new ArrayList<>();

    public FilterMethodVisitor(IType classType)
    {
        final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setSource(classType.getCompilationUnit());
        parser.createAST(null).accept(this);
    }

    @Override
    public boolean visit(MethodDeclaration node)
    {
        if(isPrivateMethod(node))
            privateMethods.add(node);

        if(isGetterMethod(node))
            getterMethods.add(node);

        if(isSetterMethod(node))
            setterMethods.add(node);

        return super.visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node)
    {
        fieldDeclarations.add(node);

        return super.visit(node);
    }

    private boolean isPrivateMethod(MethodDeclaration methodDeclaration)
    {
        return Modifier.isPrivate(methodDeclaration.getModifiers());
    }

    private boolean isGetterMethod(MethodDeclaration methodDeclaration)
    {
        return methodDeclaration.getName().getFullyQualifiedName().startsWith(MoreUnitContants.GETTER_PREFIX);
    }

    private boolean isSetterMethod(MethodDeclaration methodDeclaration)
    {
        return methodDeclaration.getName().getFullyQualifiedName().startsWith(MoreUnitContants.SETTER_PREFIX);
    }

    public List<MethodDeclaration> getPrivateMethods()
    {
        return privateMethods;
    }

    public List<FieldDeclaration> getFieldDeclarations()
    {
        return fieldDeclarations;
    }

    public List<MethodDeclaration> getGetterMethods()
    {
        return getterMethods;
    }

    public List<MethodDeclaration> getSetterMethods()
    {
        return setterMethods;
    }

    public boolean isPrivateMethod(IMethod method)
    {
        for (final MethodDeclaration methodDeclaration : privateMethods)
        {
            if(sameMethodName(method, methodDeclaration) && sameParameters(method, methodDeclaration))
                return true;
        }

        return false;
    }

    public boolean isGetterMethod(IMethod method)
    {
        // Performance optimization: Avoids regex compilation overhead of replaceFirst by using
        // startsWith and substring, which is faster for simple literal prefix removal.
        final String elementName = method.getElementName();
        final String getterVariableName = elementName.startsWith(MoreUnitContants.GETTER_PREFIX)
            ? elementName.substring(MoreUnitContants.GETTER_PREFIX.length())
            : elementName;

        for (final FieldDeclaration fieldDeclaration : fieldDeclarations)
        {
            @SuppressWarnings("unchecked")
            final
            List<VariableDeclarationFragment> variableDeclarationFragments = fieldDeclaration.fragments();
            for (final VariableDeclarationFragment declarationFragment : variableDeclarationFragments)
            {
                if(sameVariableName(getterVariableName, declarationFragment) && sameVariableType(fieldDeclaration, method) && hasNoParameters(method))
                    return true;
            }
        }
        return false;
    }

    private boolean hasNoParameters(IMethod method)
    {
        try
        {
            return method.getParameterNames().length == 0;
        }
        catch (final JavaModelException e)
        {
        }

        return false;
    }

    private boolean hasExactlyOneParameterOfFieldType(FieldDeclaration fieldDeclaration, IMethod method)
    {
        final String[] parameterTypes = method.getParameterTypes();

        // Getters must have exactly one parameter
        if(parameterTypes.length != 1)
            return false;

        final String fieldTypeSignature = Signature.createTypeSignature(fieldDeclaration.getType().toString(), false);
        return fieldTypeSignature.equals(parameterTypes[0]);
    }

    /**
     * This method should return true, if field and getterVariable have the same name
     * (the field may start with underscore e.g. _fieldName or may be prefixed with m
     *  e.g. mFieldName)
     */
    private boolean sameVariableName(String getterVariableName, VariableDeclarationFragment declarationFragment)
    {
        final String fieldName = declarationFragment.getName().getFullyQualifiedName().toLowerCase();

        // check exact name
        if(getterVariableName.equalsIgnoreCase(fieldName))
            return true;

        // check underscore
        final String getterWithUnderscore = "_%s".formatted(getterVariableName);
        if(getterWithUnderscore.equalsIgnoreCase(fieldName))
            return true;

        // check m-prefix
        final String getterWithMemberPrefix = "m%s".formatted(getterVariableName);
        if(getterWithMemberPrefix.equalsIgnoreCase(fieldName))
            return true;

        return false;
    }

    private boolean sameVariableType(FieldDeclaration fieldDeclaration, IMethod method)
    {
        try
        {
            final String typeSignature = Signature.createTypeSignature(fieldDeclaration.getType().toString(), false);
            return typeSignature.equals(method.getReturnType());
        }
        catch (final JavaModelException e)
        {
            return false;
        }
    }

    public boolean isSetterMethod(IMethod method)
    {
        // Performance optimization: Avoids regex compilation overhead of replaceFirst by using
        // startsWith and substring, which is faster for simple literal prefix removal.
        final String elementName = method.getElementName();
        final String setterVariableName = elementName.startsWith(MoreUnitContants.SETTER_PREFIX)
            ? elementName.substring(MoreUnitContants.SETTER_PREFIX.length())
            : elementName;

        for (final FieldDeclaration fieldDeclaration : fieldDeclarations)
        {
            @SuppressWarnings("unchecked")
            final
            List<VariableDeclarationFragment> variableDeclarationFragments = fieldDeclaration.fragments();
            for (final VariableDeclarationFragment declarationFragment : variableDeclarationFragments)
            {
                if(sameVariableName(setterVariableName, declarationFragment) && hasExactlyOneParameterOfFieldType(fieldDeclaration, method))
                    return true;
            }
        }
        return false;

    }

    private boolean sameMethodName(IMethod method, MethodDeclaration methodDeclaration)
    {
        return method.getElementName().equals(methodDeclaration.getName().getFullyQualifiedName());
    }

    private boolean sameParameters(IMethod method, MethodDeclaration methodDeclaration)
    {
        @SuppressWarnings("unchecked")
        final
        List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
        final String[] parameterTypes = method.getParameterTypes();

        if(parameters.size() != parameterTypes.length)
            return false;

        for (int i = 0; i < parameters.size(); i++)
        {
            final SingleVariableDeclaration singleVariableDeclaration = parameters.get(i);
            final String parameterString = parameterTypes[i];

            final String signatureMethodDeclaration = Signature.createTypeSignature(singleVariableDeclaration.getType().toString(), false);
            if(! parameterString.equals(signatureMethodDeclaration))
                return false;
        }
        return true;
    }

}
