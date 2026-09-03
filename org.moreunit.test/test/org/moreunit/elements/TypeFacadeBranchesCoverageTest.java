package org.moreunit.elements;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

public class TypeFacadeBranchesCoverageTest extends ContextTestCase
{
    @Project(mainCls = "Hello")
    @Preferences(testClassNameTemplate = "${srcFile}Test")
    @Test
    public void should_return_null_when_no_corresponding_member_exists_and_creation_is_not_requested() throws JavaModelException
    {
        // given a class without any corresponding test case
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("Hello"));
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .build();

        // when
        final IMember result = facade.getOneCorrespondingMember(request);

        // then
        assertNull(result);
    }

    @Project(mainCls = "com:Foo", testCls = "com:FooTest", mainSrcFolder = "src", testSrcFolder = "test")
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
    @Test
    public void should_return_matching_class_when_single_perfect_match_exists() throws JavaModelException
    {
        // given a class with exactly one corresponding test case
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("com.Foo"));
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .build();

        // when
        final IMember result = facade.getOneCorrespondingMember(request);

        // then the test case is returned without any dialog
        assertEquals(context.getPrimaryTypeHandler("com.FooTest").get(), result);
    }

    @Project(mainCls = "Hello")
    @Preferences(testClassNameTemplate = "${srcFile}Test")
    @Test
    public void should_build_choice_action_for_multiple_perfect_matches() throws Exception
    {
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("Hello"));
        final CorrespondingMemberRequest request = mock(CorrespondingMemberRequest.class);
        when(request.shouldReturn(MemberType.TYPE_OR_METHOD)).thenReturn(false);

        final Object action = callGetPerfectCorrespondingMember(facade, request, asList(mock(IType.class), mock(IType.class)));

        assertNotNull(action);
    }

    @Project(mainCls = "Hello")
    @Preferences(testClassNameTemplate = "${srcFile}Test")
    @Test
    public void should_build_wizard_action_when_creation_is_requested_without_match() throws Exception
    {
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("Hello"));
        final CorrespondingMemberRequest request = mock(CorrespondingMemberRequest.class);
        when(request.shouldReturn(MemberType.TYPE_OR_METHOD)).thenReturn(false);
        when(request.shouldCreateClassIfNoResult()).thenReturn(true);

        final Object action = callGetPerfectCorrespondingMember(facade, request, emptyList());

        assertNotNull(action);
    }

    @Project(mainCls = "Hello")
    @Preferences(testClassNameTemplate = "${srcFile}Test")
    @Test
    public void should_build_null_action_without_match_and_without_creation() throws Exception
    {
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("Hello"));
        final CorrespondingMemberRequest request = mock(CorrespondingMemberRequest.class);
        when(request.shouldReturn(MemberType.TYPE_OR_METHOD)).thenReturn(false);
        when(request.shouldCreateClassIfNoResult()).thenReturn(false);

        final Object action = callGetPerfectCorrespondingMember(facade, request, emptyList());

        assertNull(action);
    }

    @Project(mainCls = "Hello")
    @Preferences(testClassNameTemplate = "${srcFile}Test")
    @Test
    public void should_build_likely_class_actions_without_opening_any_dialog() throws Exception
    {
        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("Hello"));

        final CorrespondingMemberRequest withCreation = mock(CorrespondingMemberRequest.class);
        when(withCreation.shouldCreateClassIfNoResult()).thenReturn(true);
        assertNotNull(callGetLikelyCorrespondingClass(facade, withCreation));

        final CorrespondingMemberRequest withoutCreation = mock(CorrespondingMemberRequest.class);
        when(withoutCreation.shouldCreateClassIfNoResult()).thenReturn(false);
        assertNull(callGetLikelyCorrespondingClass(facade, withoutCreation));
    }

    private static Object callGetPerfectCorrespondingMember(TypeFacade facade, CorrespondingMemberRequest request, Collection<IType> classes) throws Exception
    {
        final Method method = TypeFacade.class.getDeclaredMethod("getPerfectCorrespondingMember", CorrespondingMemberRequest.class, Collection.class);
        method.setAccessible(true);
        try
        {
            return method.invoke(facade, request, classes);
        }
        catch (final InvocationTargetException e)
        {
            throw new AssertionError("unexpected invocation failure", e.getCause());
        }
    }

    private static Object callGetLikelyCorrespondingClass(TypeFacade facade, CorrespondingMemberRequest request) throws Exception
    {
        final Method method = TypeFacade.class.getDeclaredMethod("getLikelyCorrespondingClass", CorrespondingMemberRequest.class);
        method.setAccessible(true);
        try
        {
            return method.invoke(facade, request);
        }
        catch (final InvocationTargetException e)
        {
            throw new AssertionError("unexpected invocation failure", e.getCause());
        }
    }
}
