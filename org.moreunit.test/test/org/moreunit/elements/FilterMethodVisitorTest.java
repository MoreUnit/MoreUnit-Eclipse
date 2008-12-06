package org.moreunit.elements;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.moreunit.WorkspaceHelper;
import org.moreunit.WorkspaceTestCase;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 *
 */
public class FilterMethodVisitorTest extends WorkspaceTestCase {
	
	private static final String PACKAGE_NAME = "org";
	private static final String SOURCES_FOLDER_NAME = "sources";
	
	private IPackageFragment sourcesPackage;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		
		IPackageFragmentRoot sourcesFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCES_FOLDER_NAME);
		sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetPrivateMethods() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "return 2");
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberThree()", "return 3");
		WorkspaceHelper.createMethodInJavaType(cutType, "int getNumberFour()", "return 4");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		assertSameMethodName(privateMethod, privateMethods.get(0));
	}
	
	private void assertSameMethodName(IMethod method, MethodDeclaration methodDeclaration) {
		assertEquals(method.getElementName(), methodDeclaration.getName().getFullyQualifiedName());
	}
	
	public void testGetPrivateMethodsOverloaded() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
		WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
		assertEquals(2, privateMethods.size());
		assertSameMethodName(privateMethod, privateMethods.get(0));
		assertSameMethodName(privateMethod, privateMethods.get(1));
	}

	public void testGetPrivateMethodsOverloaded2() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(boolean parameter)", "return 1");
		WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
		assertEquals(2, privateMethods.size());
		assertSameMethodName(privateMethod, privateMethods.get(0));
		assertSameMethodName(privateMethod, privateMethods.get(1));
	}

	public void testIsPrivateMethod() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
		IMethod publicMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "return 2");
		IMethod protectedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberThree()", "return 3");
		IMethod defaultMethod = WorkspaceHelper.createMethodInJavaType(cutType, "int getNumberFour()", "return 4");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod));
		assertFalse(filterMethodVisitor.isPrivateMethod(publicMethod));
		assertFalse(filterMethodVisitor.isPrivateMethod(protectedMethod));
		assertFalse(filterMethodVisitor.isPrivateMethod(defaultMethod));
	}
	
	public void testIsPrivateMethodOverloaded() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
		IMethod overloadedPrivateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod));
		assertTrue(filterMethodVisitor.isPrivateMethod(overloadedPrivateMethod));
	}
	
	public void testGetFieldDeclarations() throws CoreException {
		String className = "Hello";
		ICompilationUnit compilationUnit = sourcesPackage.createCompilationUnit(String.format("%s.java", className), getClassSourceWithFields(className, "fieldName1", "fieldName2"), false, null);
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(compilationUnit.findPrimaryType());
		List<FieldDeclaration> fieldDeclarations = filterMethodVisitor.getFieldDeclarations();
		assertEquals(2, fieldDeclarations.size());
		
		FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
		VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("fieldName1", variable.getName().getFullyQualifiedName());
		
		fieldDeclaration = fieldDeclarations.get(1);
		variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("fieldName2", variable.getName().getFullyQualifiedName());
	}
	
	private String getClassSourceWithFields(String className, String fieldname1, String fieldname2) {
		StringBuilder result = new StringBuilder();
		result.append(String.format("package %s;%s",sourcesPackage.getElementName(), StringConstants.NEWLINE));
		result.append(String.format("public class %s {%s", className, StringConstants.NEWLINE));
		
		result.append(String.format("private String %s;%s", fieldname1, StringConstants.NEWLINE));
		result.append(String.format("private String %s;%s", fieldname2, StringConstants.NEWLINE));
		
		result.append("}");
		
		return result.toString();
	}

	public void testGetGetterMethods() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod fieldName1GetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getFieldName1()", "return 1");
		IMethod fieldName2GetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getFieldName2()", "return 2");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
		assertEquals(2, getterMethods.size());
		assertSameMethodName(fieldName1GetterMethod, getterMethods.get(0));
		assertSameMethodName(fieldName2GetterMethod, getterMethods.get(1));		
	}
	
	public void testGetSetterMethods() throws CoreException {
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		IMethod fieldName1SetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getFieldName1()", "return 1");
		WorkspaceHelper.createMethodInJavaType(cutType, "public int setFieldName2()", "return 2");
		
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
		List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
		assertEquals(1, getterMethods.size());
		assertSameMethodName(fieldName1SetterMethod, getterMethods.get(0));
	}
	
	public void testIsGetterMethod() throws CoreException {
		String className = "Hello";
		ICompilationUnit compilationUnit = sourcesPackage.createCompilationUnit(String.format("%s.java", className), getClassSourceWithFields(className, "fieldName1", "fieldName2"), false, null);
		IMethod fieldName1GetterMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private String getFieldName1()", "return fieldname1;");
		IMethod getterWithoutFieldMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private int getTheWorld()", "return 1;");
		IMethod fieldName1SetterMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private int setFieldName1()", "return 1;");
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(compilationUnit.findPrimaryType());
		
		assertTrue(filterMethodVisitor.isGetterMethod(fieldName1GetterMethod));
		assertFalse(filterMethodVisitor.isGetterMethod(getterWithoutFieldMethod));
		assertFalse(filterMethodVisitor.isGetterMethod(fieldName1SetterMethod));
	}
}
