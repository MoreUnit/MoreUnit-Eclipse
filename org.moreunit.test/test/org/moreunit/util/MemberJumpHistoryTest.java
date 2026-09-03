package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberJumpHistoryTest
{
    private MemberJumpHistory history;

    @BeforeEach
    public void setUp()
    {
        history = new MemberJumpHistory();
    }

    @Test
    public void getInstance_should_return_same_instance() {
        final MemberJumpHistory instance1 = MemberJumpHistory.getInstance();
        final MemberJumpHistory instance2 = MemberJumpHistory.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    public void getLastCorrespondingJumpMember_test_with_jump_from_type()
    {
        final IType fromType = mockType("FromType");
        final IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertEquals(history.getLastCorrespondingJumpMember(fromType), toType);

        final IType toType2 = mockType("ToType2");
        history.registerJump(fromType, toType2);
        assertEquals(history.getLastCorrespondingJumpMember(fromType), toType2);
    }

    private IType mockType(String typeName)
    {
        final IType mock = mock(IType.class);
        when(mock.getElementName()).thenReturn(typeName);
        return mock;
    }

    @Test
    public void getLastCorrespondingJumpMember_test_with_jump_from_method()
    {
        final IMethod fromMethod = mockMethod(mockType("FromType"), "fromMethod");
        final IType toType = mockType("ToType");
        history.registerJump(fromMethod, toType);
        assertEquals(history.getLastCorrespondingJumpMember(fromMethod), toType);

        final IMethod toMethod = mockMethod(toType, "toMethod");
        history.registerJump(fromMethod, toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(fromMethod), toMethod);

        final IMethod toMethod2 = mockMethod(mockType("FromType2"), "toMethod2");
        history.registerJump(fromMethod, toMethod2);
        assertEquals(history.getLastCorrespondingJumpMember(fromMethod), toMethod2);
    }

    private IMethod mockMethod(IType declaringType, String methodName)
    {
        final IMethod mock = mock(IMethod.class);
        when(mock.getElementName()).thenReturn(methodName);
        when(mock.getDeclaringType()).thenReturn(declaringType);
        return mock;
    }

    @Test
    public void getLastCorrespondingJumpMember_test_having_reached_by_type()
    {
        final IType fromType = mockType("FromType");
        final IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertEquals(history.getLastCorrespondingJumpMember(toType), fromType);

        final IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toType);
        assertEquals(history.getLastCorrespondingJumpMember(toType), fromType2);

        final IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toType);
        assertEquals(history.getLastCorrespondingJumpMember(toType), fromMethod);

        assertEquals(history.getLastCorrespondingJumpMember(fromType), toType);
        assertEquals(history.getLastCorrespondingJumpMember(fromType2), toType);
        assertEquals(history.getLastCorrespondingJumpMember(fromMethod), toType);
    }

    @Test
    public void getLastCorrespondingJumpMember_test_having_reached_by_method()
    {
        final IType fromType = mockType("FromType");
        final IMethod toMethod = mockMethod(mockType("ToType"), "toMethod");
        history.registerJump(fromType, toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(toMethod), fromType);

        final IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(toMethod), fromType2);

        final IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(toMethod), fromMethod);

        assertEquals(history.getLastCorrespondingJumpMember(fromType), toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(fromType2), toMethod);
        assertEquals(history.getLastCorrespondingJumpMember(fromMethod), toMethod);
    }
}
