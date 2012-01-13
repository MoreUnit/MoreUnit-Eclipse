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
import org.moreunit.test.WorkspaceTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 */
@Context(SimpleJUnit4Project.class)
public class FilterMethodVisitorTest extends ContextTestCase
{
    private static final String PACKAGE_NAME = "org";
    private static final String SOURCES_FOLDER_NAME = "sources";

    private static IPackageFragment sourcesPackage;

    private static final String JAVA_CLASS_NAME = "FilterMethodVisitorUT";

    /*
    @BeforeClass
    public static void setUpSourceFolder() throws Exception
    {
        IPackageFragmentRoot sourcesFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCES_FOLDER_NAME);
        sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
    }
    */

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_one_private_method.java.txt")
    public void testGetPrivateMethods() throws CoreException
    {
        IType typeWithOnePrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOnePrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(1, privateMethods.size());
        assertEquals("getNumberOne", privateMethods.get(0).getName().toString());
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOnePrivateMethod});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_overloaded_private_method.java.txt")
    public void testGetPrivateMethodsOverloaded() throws CoreException
    {
        IType typeWithOverloadedPrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOverloadedPrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(2, privateMethods.size());
        assertEquals("getNumberOne", privateMethods.get(0).getName().toString());
        assertEquals("getNumberOne", privateMethods.get(1).getName().toString());
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOverloadedPrivateMethod});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_overloaded_private_method_2.java.txt")
    public void testGetPrivateMethodsOverloaded2() throws CoreException
    {
        IType typeWithOverloadedPrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOverloadedPrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertEquals(2, privateMethods.size());
        assertEquals("getNumberOne", privateMethods.get(0).getName().toString());
        assertEquals("getNumberOne", privateMethods.get(1).getName().toString());
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOverloadedPrivateMethod});
    }

    @Test
    public void testIsPrivateMethod() throws CoreException
    {
        TypeHandler createdClass = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.AnotherClass");
        MethodHandler privateMethod = createdClass.addMethod("private int getNumberOne()", "return 1");
        MethodHandler publicMethod = createdClass.addMethod("public int getNumberTwo()", "return 2");
        MethodHandler protectedMethod = createdClass.addMethod("protected int getNumberThree()", "return 3");
        MethodHandler defaultMethod = createdClass.addMethod("int getNumberFour()", "return 4");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(createdClass.get());
        assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod.get()));
        assertFalse(filterMethodVisitor.isPrivateMethod(publicMethod.get()));
        assertFalse(filterMethodVisitor.isPrivateMethod(protectedMethod.get()));
        assertFalse(filterMethodVisitor.isPrivateMethod(defaultMethod.get()));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {createdClass.get()});
    }

    @Test
    public void testIsPrivateMethodOverloaded() throws CoreException
    {
        TypeHandler createdClass = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.AnotherClass");
        MethodHandler privateMethod = createdClass.addMethod("private int getNumberOne()", "return 1");
        MethodHandler overloadedPrivateMethod = createdClass.addMethod("private int getNumberOne(String parameter)", "return 2");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(createdClass.get());
        assertTrue(filterMethodVisitor.isPrivateMethod(privateMethod.get()));
        assertTrue(filterMethodVisitor.isPrivateMethod(overloadedPrivateMethod.get()));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {createdClass.get()});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_fields.java.txt")
    public void testGetFieldDeclarations() throws CoreException
    {
        IType typeWithTwoFields = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoFields);
        List<FieldDeclaration> fieldDeclarations = filterMethodVisitor.getFieldDeclarations();
        assertEquals(2, fieldDeclarations.size());

        FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
        VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertEquals("fieldName1", variable.getName().getFullyQualifiedName());

        fieldDeclaration = fieldDeclarations.get(1);
        variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertEquals("fieldName2", variable.getName().getFullyQualifiedName());
        
        // cleanup
       typeWithTwoFields.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_getter_methods.java.txt")
    public void testGetGetterMethods() throws CoreException
    {
        IType typeWithTwoGetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoGetters);
        List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
        assertEquals(2, getterMethods.size());
        assertEquals("getFieldName1", getterMethods.get(0).getName().toString());
        assertEquals("getFieldName2", getterMethods.get(1).getName().toString());
        
        // cleanup
        typeWithTwoGetters.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_setter_methods.java.txt")
    public void testGetSetterMethods() throws CoreException
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        List<MethodDeclaration> setterMethods = filterMethodVisitor.getSetterMethods();
        assertEquals(2, setterMethods.size());
        assertEquals("setFieldName1", setterMethods.get(0).getName().toString());
        assertEquals("setFieldName2", setterMethods.get(1).getName().toString());
        
        // cleanup
        typeWithTwoSetters.getCompilationUnit().delete(true, null);

    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter.java.txt")
    public void testIsGetterMethod() throws CoreException
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        IMethod method = typeWithTwoSetters.getMethod("getFieldName1", new String[] {});
        assertTrue(filterMethodVisitor.isGetterMethod(method));
        
        // cleanup
        typeWithTwoSetters.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter_without_field.java.txt")
    public void testIsGetterMethodGetterWithoutField()
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        IMethod method = typeWithTwoSetters.getMethod("getTheWorld", new String[] {});
        assertFalse(filterMethodVisitor.isGetterMethod(method));
    }
}
