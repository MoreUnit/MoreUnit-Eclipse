package org.moreunit.elements;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * @author vera
 */
@Context(SimpleJUnit4Project.class)
public class FilterMethodVisitorTest extends ContextTestCase
{
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_one_private_method.java.txt")
    public void getPrivateMethods_should_return_simple_getter() throws CoreException
    {
        IType typeWithOnePrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOnePrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertThat(privateMethods).hasSize(1);
        assertThat(privateMethods.get(0).getName().toString()).isEqualTo("getNumberOne");
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOnePrivateMethod});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_overloaded_private_method.java.txt")
    public void getPrivateMethods_should_return_overloaded_getters_with_different_parameter_count() throws CoreException
    {
        IType typeWithOverloadedPrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOverloadedPrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertThat(privateMethods).hasSize(2);
        assertThat(privateMethods.get(0).getName().toString()).isEqualTo("getNumberOne");
        assertThat(privateMethods.get(1).getName().toString()).isEqualTo("getNumberOne");
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOverloadedPrivateMethod});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_overloaded_private_method_2.java.txt")
    public void getPrivateMethods_should_return_overloaded_getters_with_different_parameter_types() throws CoreException
    {
        IType typeWithOverloadedPrivateMethod = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithOverloadedPrivateMethod);
        List<MethodDeclaration> privateMethods = filterMethodVisitor.getPrivateMethods();
        assertThat(privateMethods).hasSize(2);
        assertThat(privateMethods.get(0).getName().toString()).isEqualTo("getNumberOne");
        assertThat(privateMethods.get(1).getName().toString()).isEqualTo("getNumberOne");
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {typeWithOverloadedPrivateMethod});
    }

    @Test
    public void isPrivateMethod_test_getters_with_different_visibility() throws CoreException
    {
        TypeHandler createdClass = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.AnotherClass");
        MethodHandler privateMethod = createdClass.addMethod("private int getNumberOne()", "return 1");
        MethodHandler publicMethod = createdClass.addMethod("public int getNumberTwo()", "return 2");
        MethodHandler protectedMethod = createdClass.addMethod("protected int getNumberThree()", "return 3");
        MethodHandler defaultMethod = createdClass.addMethod("int getNumberFour()", "return 4");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(createdClass.get());
        assertThat(filterMethodVisitor.isPrivateMethod(privateMethod.get())).isTrue();
        assertThat(filterMethodVisitor.isPrivateMethod(publicMethod.get())).isFalse();
        assertThat(filterMethodVisitor.isPrivateMethod(protectedMethod.get())).isFalse();
        assertThat(filterMethodVisitor.isPrivateMethod(defaultMethod.get())).isFalse();
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {createdClass.get()});
    }

    @Test
    public void isPrivateMethod_test_overloaded_getters() throws CoreException
    {
        TypeHandler createdClass = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.AnotherClass");
        MethodHandler privateMethod = createdClass.addMethod("private int getNumberOne()", "return 1");
        MethodHandler overloadedPrivateMethod = createdClass.addMethod("private int getNumberOne(String parameter)", "return 2");

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(createdClass.get());
        assertThat(filterMethodVisitor.isPrivateMethod(privateMethod.get())).isTrue();
        assertThat(filterMethodVisitor.isPrivateMethod(overloadedPrivateMethod.get())).isTrue();
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {createdClass.get()});
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_fields.java.txt")
    public void getFieldDeclarations_should_return_two_fields() throws CoreException
    {
        IType typeWithTwoFields = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoFields);
        List<FieldDeclaration> fieldDeclarations = filterMethodVisitor.getFieldDeclarations();
        assertThat(fieldDeclarations).hasSize(2);

        FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
        VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertThat(variable.getName().getFullyQualifiedName()).isEqualTo("fieldName1");

        fieldDeclaration = fieldDeclarations.get(1);
        variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
        assertThat(variable.getName().getFullyQualifiedName()).isEqualTo("fieldName2");
        
        // cleanup
       typeWithTwoFields.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_getter_methods.java.txt")
    public void getGetterMethods_should_return_two_getters() throws CoreException
    {
        IType typeWithTwoGetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoGetters);
        List<MethodDeclaration> getterMethods = filterMethodVisitor.getGetterMethods();
        assertThat(getterMethods).hasSize(2);
        assertThat(getterMethods.get(0).getName().toString()).isEqualTo("getFieldName1");
        assertThat(getterMethods.get(1).getName().toString()).isEqualTo("getFieldName2");
        
        // cleanup
        typeWithTwoGetters.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_two_setter_methods.java.txt")
    public void getSetterMethods_should_return_two_setters() throws CoreException
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();

        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        List<MethodDeclaration> setterMethods = filterMethodVisitor.getSetterMethods();
        assertThat(setterMethods).hasSize(2);
        assertThat(setterMethods.get(0).getName().toString()).isEqualTo("setFieldName1");
        assertThat(setterMethods.get(1).getName().toString()).isEqualTo("setFieldName2");
        
        // cleanup
        typeWithTwoSetters.getCompilationUnit().delete(true, null);

    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter.java.txt")
    public void isGetterMethod_should_return_true_when_field_exists() throws CoreException
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        IMethod method = typeWithTwoSetters.getMethod("getFieldName1", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isTrue();
        
        // cleanup
        typeWithTwoSetters.getCompilationUnit().delete(true, null);
    }

    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter_without_field.java.txt")
    public void isGetterMethod_should_return_false_when_field_missing()
    {
        IType typeWithTwoSetters = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(typeWithTwoSetters);
        IMethod method = typeWithTwoSetters.getMethod("getTheWorld", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isFalse();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter_but_different_type.java.txt")
    public void isGetterMethod_should_return_false_when_field_has_different_type()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("getTheWorld", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isFalse();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter_and_fieldname_with_underscore.txt")
    public void isGetterMethod_should_return_true_when_field_starts_with_underscore()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("getFieldName1", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isTrue();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_getter_and_fieldname_with_member_prefix.txt")
    public void isGetterMethod_should_return_true_when_field_starts_with_member_prefix()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("getFieldName1", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isTrue();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_method_and_fieldname.java.txt")
    public void isGetterMethod_should_return_false_when_method_does_not_start_with_get()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("aFieldName1", new String[] {});
        assertThat(filterMethodVisitor.isGetterMethod(method)).isFalse();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_setter.java.txt")
    public void isSetterMethod_should_return_true_when_field_exists()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("setFieldName1", new String[] { "QString;"});
        assertThat(filterMethodVisitor.isSetterMethod(method)).isTrue();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_setter_with_two_parameters.java.txt")
    public void isSetterMethod_should_return_false_when_setter_has_more_than_one_parameter()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("setFieldName1", new String[] { "QString;", "I" });
        assertThat(filterMethodVisitor.isSetterMethod(method)).isFalse();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_setter_with_different_type.java.txt")
    public void isSetterMethod_should_return_false_when_setter_has_different_type()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("setFieldName1", new String[] { "QString;" });
        assertThat(filterMethodVisitor.isSetterMethod(method)).isFalse();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_setter_and_fieldname_with_underscore.java.txt")
    public void isSetterMethod_should_return_true_when_field_starts_with_underscore()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("setFieldName1", new String[] { "QString;"});
        assertThat(filterMethodVisitor.isSetterMethod(method)).isTrue();
    }
    
    @Test
    @Context(mainSrc = "FilterMethodVisitor_class_with_setter_and_fieldname_with_member_prefix.java.txt")
    public void isSetterMethod_should_return_true_when_field_starts_with_member_prefix()
    {
        IType type = context.getCompilationUnitHandler("te.st.SomeClass").getPrimaryTypeHandler().get();
        FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(type);
        IMethod method = type.getMethod("setFieldName1", new String[] { "QString;"});
        assertThat(filterMethodVisitor.isSetterMethod(method)).isTrue();
    }
    
    
}
