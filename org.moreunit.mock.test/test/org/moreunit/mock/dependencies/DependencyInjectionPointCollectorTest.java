package org.moreunit.mock.dependencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class DependencyInjectionPointCollectorTest
{
    private static final String TEST_PACKAGE_NAME = "org.example";
    private IType classUnderTest;
    private IPackageFragment testCasePackage;
    private IMember aMember;

    private DependencyInjectionPointCollector collector;

    @Before
    public void init() throws Exception
    {
        classUnderTest = mockType();
        testCasePackage = mockPackageFragmentWithPackageName(TEST_PACKAGE_NAME);
        aMember = mock(IMember.class);

        collector = new DependencyInjectionPointCollector(classUnderTest, testCasePackage);
    }

    @Test
    public void member_should_not_be_visible_from_test_case_when_it_is_private() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccPrivate);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isFalse();
    }

    @Test
    public void member_should_not_be_visible_from_test_case_when_it_is_package_private_and_test_case_is_in_a_different_package() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccDefault);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isFalse();
    }

    @Test
    public void member_should_not_be_visible_from_test_case_when_it_is_protected_and_test_case_is_in_a_different_package() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccProtected);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isFalse();
    }

    @Test
    // synthetic means: created at runtime
    public void member_should_not_be_visible_from_test_case_when_it_is_synthetic() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccPublic & Flags.AccSynthetic);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isFalse();
    }

    @Test
    public void member_should_be_visible_from_test_case_when_it_is_public() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccPublic);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isTrue();
    }

    @Test
    public void member_should_be_visible_from_test_case_when_it_is_package_private_and_test_case_is_in_same_packagee() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccDefault);
        when(classUnderTest.getPackageFragment().getElementName()).thenReturn(TEST_PACKAGE_NAME);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isTrue();
    }

    @Test
    public void member_should_be_visible_from_test_case_when_it_is_protected_and_test_case_is_in_same_packagee() throws Exception
    {
        // given
        when(aMember.getFlags()).thenReturn(Flags.AccProtected);
        when(classUnderTest.getPackageFragment().getElementName()).thenReturn(TEST_PACKAGE_NAME);

        // then
        assertThat(collector.isVisibleToTestCase(aMember)).isTrue();
    }

    @Test
    public void should_not_collect_non_constructor_method_as_constructor() throws Exception
    {
        // given
        IMethod notAConstructor = method("notAConstructor", 1, Flags.AccPublic, false);
        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { notAConstructor });

        // then
        assertThat(collector.getConstructors()).isEmpty();
    }

    private IMethod method(String name, int argNum, int flags, boolean constructor) throws JavaModelException
    {
        IMethod method = mock(IMethod.class);
        when(method.getElementName()).thenReturn(name);
        when(method.getNumberOfParameters()).thenReturn(argNum);
        when(method.getFlags()).thenReturn(flags);
        when(method.isConstructor()).thenReturn(constructor);
        return method;
    }

    @Test
    public void should_not_collect_constructors_with_zero_arguments() throws Exception
    {
        // given
        IMethod noArgConstructor = method("ClassUnderTestConstructor", 0, Flags.AccPublic, true);
        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { noArgConstructor });

        // then
        assertThat(collector.getConstructors()).isEmpty();
    }

    @Test
    public void should_not_collect_invisible_constructors() throws Exception
    {
        // given
        IMethod packagePrivateConstructor = method("ClassUnderTestConstructor", 1, Flags.AccDefault, true);

        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { packagePrivateConstructor });

        // then
        assertThat(collector.getConstructors()).isEmpty();
    }

    @Test
    public void constructor_collection_happy_path() throws Exception
    {
        // given
        IMethod publicConstructor = method("ClassUnderTestConstructor", 1, Flags.AccPublic, true);

        IMethod packagePrivateConstructor = method("ClassUnderTestConstructor", 1, Flags.AccDefault, true);
        when(classUnderTest.getPackageFragment().getElementName()).thenReturn(TEST_PACKAGE_NAME);

        when(classUnderTest.getMethods()).thenReturn(new IMethod[] { publicConstructor, packagePrivateConstructor });

        // then
        assertThat(collector.getConstructors()).hasSize(2);
    }

    @Test
    public void should_not_collect_methods_which_name_does_not_match_setter_pattern() throws Exception
    {
        // given
        IMethod notASetter = method("notASetter", 1, Flags.AccPublic, false);
        classUnderTestHierarchyHasMethods(notASetter);

        // then
        assertThat(collector.getSetters()).isEmpty();
    }

    /**
     * Simulates retrieval of methods from {@link #classUnderTest}'s type
     * hierarchy.
     */
    private void classUnderTestHierarchyHasMethods(IMethod... methods) throws JavaModelException
    {
        IType aSuperType = createTypeHierarchyForClassUnderTestAndReturnASuperType();
        when(aSuperType.getMethods()).thenReturn(methods);
    }

    private IType createTypeHierarchyForClassUnderTestAndReturnASuperType() throws JavaModelException
    {
        ITypeHierarchy typeHierarchy = mock(ITypeHierarchy.class);
        when(classUnderTest.newSupertypeHierarchy(any(IProgressMonitor.class))).thenReturn(typeHierarchy);

        IType aSuperType = mock(IType.class);
        when(typeHierarchy.getAllClasses()).thenReturn(new IType[] { aSuperType });

        return aSuperType;
    }

    @Test
    public void should_not_collect_setters_with_zero_arguments() throws Exception
    {
        // given
        IMethod notASetter = method("setProperty", 0, Flags.AccPublic, false);
        classUnderTestHierarchyHasMethods(notASetter);

        // then
        assertThat(collector.getSetters()).isEmpty();
    }

    @Test
    public void should_not_collect_setters_with_several_arguments() throws Exception
    {
        // given
        IMethod notASetter = method("setProperty", 2, Flags.AccPublic, false);
        classUnderTestHierarchyHasMethods(notASetter);

        // then
        assertThat(collector.getSetters()).isEmpty();
    }

    @Test
    public void should_not_collect_invisible_setters() throws Exception
    {
        // given
        IMethod packagePrivateSetter = method("setProperty", 1, Flags.AccDefault, false);

        classUnderTestHierarchyHasMethods(packagePrivateSetter);

        // then
        assertThat(collector.getSetters()).isEmpty();
    }

    @Test
    public void setter_collection_happy_path() throws Exception
    {
        // given
        IMethod publicSetter = method("setProperty", 1, Flags.AccPublic, false);

        IMethod packagePrivateSetter = method("setOtherProperty", 1, Flags.AccDefault, true);
        when(classUnderTest.getPackageFragment().getElementName()).thenReturn(TEST_PACKAGE_NAME);

        classUnderTestHierarchyHasMethods(publicSetter, packagePrivateSetter);

        // then
        assertThat(collector.getSetters()).hasSize(2);
    }

    @Test
    public void should_collect_invisible_fields() throws Exception
    {
        // given
        IField packagePrivateField = field("aField", Flags.AccDefault);

        classUnderTestHierarchyHasFields(packagePrivateField);

        // then
        assertThat(collector.getFields()).hasSize(1);
    }

    private IField field(String name, int flags) throws JavaModelException
    {
        IField field = mock(IField.class);
        when(field.getElementName()).thenReturn(name);
        when(field.getFlags()).thenReturn(flags);
        return field;
    }

    /**
     * Simulates retrieval of fields from {@link #classUnderTest}'s type
     * hierarchy.
     */
    private void classUnderTestHierarchyHasFields(IField... fields) throws JavaModelException
    {
        IType aSuperType = createTypeHierarchyForClassUnderTestAndReturnASuperType();
        when(aSuperType.getFields()).thenReturn(fields);
    }

    @Test
    public void field_collection_happy_path() throws Exception
    {
        // given
        IField publicField = field("aField", Flags.AccDefault);

        IField packagePrivateField = field("anotherField", Flags.AccDefault);
        when(classUnderTest.getPackageFragment().getElementName()).thenReturn(TEST_PACKAGE_NAME);

        classUnderTestHierarchyHasFields(publicField, packagePrivateField);

        // then
        assertThat(collector.getFields()).hasSize(2);
    }

    private IType mockType()
    {
        IType type = mock(IType.class);
        IPackageFragment typePackageFragment = mock(IPackageFragment.class);
        when(type.getPackageFragment()).thenReturn(typePackageFragment);
        return type;
    }

    private IPackageFragment mockPackageFragmentWithPackageName(String packageName)
    {
        IPackageFragment fragment = mock(IPackageFragment.class);
        when(fragment.getElementName()).thenReturn(packageName);
        return fragment;
    }
}
