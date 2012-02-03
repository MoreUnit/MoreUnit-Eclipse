package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

public class MemberJumpHistoryTest
{
    private MemberJumpHistory history;

    @Before
    public void setUp()
    {
        history = new MemberJumpHistory();
    }

    @Test
    public void getLastCorrespondingJumpMember_test_with_jump_from_type()
    {
        IType fromType = mockType("FromType");
        IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertThat(history.getLastCorrespondingJumpMember(fromType)).isEqualTo(toType);

        IType toType2 = mockType("ToType2");
        history.registerJump(fromType, toType2);
        assertThat(history.getLastCorrespondingJumpMember(fromType)).isEqualTo(toType2);
    }

    private IType mockType(String typeName)
    {
        IType mock = mock(IType.class);
        when(mock.getElementName()).thenReturn(typeName);
        return mock;
    }

    @Test
    public void getLastCorrespondingJumpMember_test_with_jump_from_method()
    {
        IMethod fromMethod = mockMethod(mockType("FromType"), "fromMethod");
        IType toType = mockType("ToType");
        history.registerJump(fromMethod, toType);
        assertThat(history.getLastCorrespondingJumpMember(fromMethod)).isEqualTo(toType);

        IMethod toMethod = mockMethod(toType, "toMethod");
        history.registerJump(fromMethod, toMethod);
        assertThat(history.getLastCorrespondingJumpMember(fromMethod)).isEqualTo(toMethod);

        IMethod toMethod2 = mockMethod(mockType("FromType2"), "toMethod2");
        history.registerJump(fromMethod, toMethod2);
        assertThat(history.getLastCorrespondingJumpMember(fromMethod)).isEqualTo(toMethod2);
    }

    private IMethod mockMethod(IType declaringType, String methodName)
    {
        IMethod mock = mock(IMethod.class);
        when(mock.getElementName()).thenReturn(methodName);
        when(mock.getDeclaringType()).thenReturn(declaringType);
        return mock;
    }
    
    @Test
    public void getLastCorrespondingJumpMember_test_having_reached_by_type()
    {
        IType fromType = mockType("FromType");
        IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertThat(history.getLastCorrespondingJumpMember(toType)).isEqualTo(fromType);

        IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toType);
        assertThat(history.getLastCorrespondingJumpMember(toType)).isEqualTo(fromType2);
        
        IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toType);
        assertThat(history.getLastCorrespondingJumpMember(toType)).isEqualTo(fromMethod);
        
        assertThat(history.getLastCorrespondingJumpMember(fromType)).isEqualTo(toType);
        assertThat(history.getLastCorrespondingJumpMember(fromType2)).isEqualTo(toType);
        assertThat(history.getLastCorrespondingJumpMember(fromMethod)).isEqualTo(toType);
    }
    
    @Test
    public void getLastCorrespondingJumpMember_test_having_reached_by_method()
    {
        IType fromType = mockType("FromType");
        IMethod toMethod = mockMethod(mockType("ToType"), "toMethod");
        history.registerJump(fromType, toMethod);
        assertThat(history.getLastCorrespondingJumpMember(toMethod)).isEqualTo(fromType);

        IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toMethod);
        assertThat(history.getLastCorrespondingJumpMember(toMethod)).isEqualTo(fromType2);
        
        IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toMethod);
        assertThat(history.getLastCorrespondingJumpMember(toMethod)).isEqualTo(fromMethod);
        
        assertThat(history.getLastCorrespondingJumpMember(fromType)).isEqualTo(toMethod);
        assertThat(history.getLastCorrespondingJumpMember(fromType2)).isEqualTo(toMethod);
        assertThat(history.getLastCorrespondingJumpMember(fromMethod)).isEqualTo(toMethod);
    }
}
