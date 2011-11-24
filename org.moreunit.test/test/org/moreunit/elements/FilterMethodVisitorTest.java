package org.moreunit.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moreunit.WorkspaceTestCase;
import org.moreunit.test.workspace.WorkspaceHelper;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 */
public class FilterMethodVisitorTest extends WorkspaceTestCase
{

    private static final String PACKAGE_NAME = "org";
    private static final String SOURCES_FOLDER_NAME = "sources";

    private static IPackageFragment sourcesPackage;

    private static final String JAVA_CLASS_NAME = "FilterMethodVisitorUT";

    @BeforeClass
    public static void setUpSourceFolder() throws Exception
    {
        IPackageFragmentRoot sourcesFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCES_FOLDER_NAME);
        sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
    }

    @Test
    public void testGetPrivateMethods() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
        WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "return 2");
        WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberThree()", "return 3");
        WorkspaceHelper.createMethodInJavaType(cutType, "int getNumberFour()", "return 4");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(1, privateMethods.size());
        WorkspaceHelper.assertSameMethodName(privateMethod, privateMethods.get(0));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testGetPrivateMethodsOverloaded() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
        WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(2, privateMethods.size());
        WorkspaceHelper.assertSameMethodName(privateMethod, privateMethods.get(0));
        WorkspaceHelper.assertSameMethodName(privateMethod, privateMethods.get(1));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testGetPrivateMethodsOverloaded2() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(boolean parameter)", "return 1");
        WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(2, privateMethods.size());
        WorkspaceHelper.assertSameMethodName(privateMethod, privateMethods.get(0));
        WorkspaceHelper.assertSameMethodName(privateMethod, privateMethods.get(1));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testIsPrivateMethod() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
        IMethod publicMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "return 2");
        IMethod protectedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberThree()", "return 3");
        IMethod defaultMethod = WorkspaceHelper.createMethodInJavaType(cutType, "int getNumberFour()", "return 4");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod));
        assertFalse(filterMethodVisitor.isPrivateMethod(publicMethod));
        assertFalse(filterMethodVisitor.isPrivateMethod(protectedMethod));
        assertFalse(filterMethodVisitor.isPrivateMethod(defaultMethod));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testIsPrivateMethodOverloaded() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod privateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne()", "return 1");
        IMethod overloadedPrivateMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getNumberOne(String parameter)", "return 1");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod));
        assertTrue(filterMethodVisitor.isPrivateMethod(overloadedPrivateMethod));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testGetFieldDeclarations() throws CoreException
    {
        ICompilationUnit compilationUnit = sourcesPackage.createCompilationUnit(String.format("%s.java", JAVA_CLASS_NAME), getClassSourceWithFields(JAVA_CLASS_NAME, "fieldName1", "fieldName2"), false, null);
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(compilationUnit.findPrimaryType());
        List<FieldDeclaration> fieldDeclarations = filterMethodVisitor.getFieldDeclarations();
        assertEquals(2, fieldDeclarations.size());

        FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
        VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertEquals("fieldName1", variable.getName().getFullyQualifiedName());

        fieldDeclaration = fieldDeclarations.get(1);
        variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertEquals("fieldName2", variable.getName().getFullyQualifiedName());
        
        // cleanup
       compilationUnit.delete(true, null);
    }

    private String getClassSourceWithFields(String className, String fieldname1, String fieldname2)
    {
        StringBuilder result = new StringBuilder();
        result.append(String.format("package %s;%s", sourcesPackage.getElementName(), StringConstants.NEWLINE));
        result.append(String.format("public class %s {%s", className, StringConstants.NEWLINE));

        result.append(String.format("private String %s;%s", fieldname1, StringConstants.NEWLINE));
        result.append(String.format("private String %s;%s", fieldname2, StringConstants.NEWLINE));

        result.append("}");

        return result.toString();
    }

    @Test
    public void testGetGetterMethods() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod fieldName1GetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getFieldName1()", "return 1");
        IMethod fieldName2GetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getFieldName2()", "return 2");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
        assertEquals(2, getterMethods.size());
        WorkspaceHelper.assertSameMethodName(fieldName1GetterMethod, getterMethods.get(0));
        WorkspaceHelper.assertSameMethodName(fieldName2GetterMethod, getterMethods.get(1));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testGetSetterMethods() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, JAVA_CLASS_NAME);
        IMethod fieldName1SetterMethod = WorkspaceHelper.createMethodInJavaType(cutType, "private int getFieldName1()", "return 1");
        WorkspaceHelper.createMethodInJavaType(cutType, "public int setFieldName2()", "return 2");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(cutType);
        List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
        assertEquals(1, getterMethods.size());
        WorkspaceHelper.assertSameMethodName(fieldName1SetterMethod, getterMethods.get(0));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType});
    }

    @Test
    public void testIsGetterMethod() throws CoreException
    {
        ICompilationUnit compilationUnit = sourcesPackage.createCompilationUnit(String.format("%s.java", JAVA_CLASS_NAME), getClassSourceWithFields(JAVA_CLASS_NAME, "fieldName1", "fieldName2"), false, null);
        IMethod fieldName1GetterMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private String getFieldName1()", "return fieldname1;");
        IMethod getterWithoutFieldMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private int getTheWorld()", "return 1;");
        IMethod fieldName1SetterMethod = WorkspaceHelper.createMethodInJavaType(compilationUnit.findPrimaryType(), "private int setFieldName1()", "return 1;");
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(compilationUnit.findPrimaryType());

        assertTrue(filterMethodVisitor.isGetterMethod(fieldName1GetterMethod));
        assertFalse(filterMethodVisitor.isGetterMethod(getterWithoutFieldMethod));
        assertFalse(filterMethodVisitor.isGetterMethod(fieldName1SetterMethod));
        
        // cleanup
        compilationUnit.delete(true, null);
    }
}
