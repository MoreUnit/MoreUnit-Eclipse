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
import org.moreunit.util.StringConstants;

/**
 * @author vera
 */
public class FilterMethodVisitor extends ASTVisitor {
	
	private List<MethodDeclaration> privateMethods = new ArrayList<MethodDeclaration>();
	private List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();
	private List<MethodDeclaration> getterMethods = new ArrayList<MethodDeclaration>();
	private List<MethodDeclaration> setterMethods = new ArrayList<MethodDeclaration>();
	
	public FilterMethodVisitor(IType classType) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(classType.getCompilationUnit());
		parser.createAST(null).accept(this);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if(isPrivateMethod(node))
			privateMethods.add(node);
		
		if(isGetterMethod(node))
			getterMethods.add(node);
		
		if(isSetterMethod(node))
			setterMethods.add(node);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		fieldDeclarations.add(node);
		
		return super.visit(node);
	}
	
	private boolean isPrivateMethod(MethodDeclaration methodDeclaration) {
		return Modifier.isPrivate(methodDeclaration.getModifiers());
	}
	
	private boolean isGetterMethod(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getName().getFullyQualifiedName().startsWith(MoreUnitContants.GETTER_PREFIX);
	}
	
	private boolean isSetterMethod(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getName().getFullyQualifiedName().startsWith(MoreUnitContants.SETTER_PREFIX);
	}

	public List<MethodDeclaration> getPrivateMethods() {
		return privateMethods;
	}	
	
	public List<FieldDeclaration> getFieldDeclarations() {
		return fieldDeclarations;
	}

	public List<MethodDeclaration> getGetterMethods() {
		return getterMethods;
	}

	public List<MethodDeclaration> getSetterMethods() {
		return setterMethods;
	}

	public boolean isPrivateMethod(IMethod method) {
		for (MethodDeclaration methodDeclaration : privateMethods) {
			if(sameMethodName(method, methodDeclaration) && sameParameters(method, methodDeclaration))
				return true;
		}
		
		return false;
	}
	
	public boolean isGetterMethod(IMethod method) {
		String getterVariableName = method.getElementName().replaceFirst(MoreUnitContants.GETTER_PREFIX, StringConstants.EMPTY_STRING);
		
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> variableDeclarationFragments = fieldDeclaration.fragments();
			for (VariableDeclarationFragment declarationFragment : variableDeclarationFragments) {
				if(sameVariableName(getterVariableName, declarationFragment) && sameVariableType(fieldDeclaration, method) && hasNoParameters(method))
					return true;
			}
		}
		return false;
	}

	private boolean hasNoParameters(IMethod method){
		try {
			return method.getParameterNames().length == 0;
		} catch (JavaModelException e) {
		}
		
		return false;
	}

	private boolean sameVariableName(String getterVariableName,	VariableDeclarationFragment declarationFragment) {
		return getterVariableName.toLowerCase().equals(declarationFragment.getName().getFullyQualifiedName().toLowerCase());
	}
	
	private boolean sameVariableType(FieldDeclaration fieldDeclaration, IMethod method) {
		try {
			String typeSignature = Signature.createTypeSignature(fieldDeclaration.getType().toString(), false);
			return typeSignature.equals(method.getReturnType());
		} catch (JavaModelException e) {
			return false;
		}
	}
	
	public boolean isSetterMethod(IMethod method) {
		return false;
	}
	
	private boolean sameMethodName(IMethod method, MethodDeclaration methodDeclaration) {
		return method.getElementName().equals(methodDeclaration.getName().getFullyQualifiedName());
	}
	
	private boolean sameParameters(IMethod method, MethodDeclaration methodDeclaration) {
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		String[] parameterTypes = method.getParameterTypes();
		
		if(parameters.size() != parameterTypes.length)
			return false;
		
		for (int i=0; i<parameters.size(); i++) {
			SingleVariableDeclaration singleVariableDeclaration = parameters.get(i);
			String parameterString = parameterTypes[i];
			
			String signatureMethodDeclaration = Signature.createTypeSignature(singleVariableDeclaration.getType().toString(), false);
			if(!parameterString.equals(signatureMethodDeclaration))
				return false;
		}
		return true;
	}
	
}
