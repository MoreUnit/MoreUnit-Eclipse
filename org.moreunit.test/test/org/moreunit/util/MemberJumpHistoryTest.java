package org.moreunit.util;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.eclipse.jdt.core.IMember;
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
    public void testGetLastMemberReachedFromType()
    {
        IType fromType = mockType("FromType");
        IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertEquals(toType, history.getLastCorrespondingJumpMember(fromType));

        IType toType2 = mockType("ToType2");
        history.registerJump(fromType, toType2);
        assertEquals(toType2, history.getLastCorrespondingJumpMember(fromType));
    }

    private IType mockType(String typeName)
    {
        IType mock = mock(IType.class);
        when(mock.getElementName()).thenReturn(typeName);
        return mock;
    }

    @Test
    public void testGetLastMemberReachedFromMethod()
    {
        IMethod fromMethod = mockMethod(mockType("FromType"), "fromMethod");
        IType toType = mockType("ToType");
        history.registerJump(fromMethod, toType);
        assertEquals(toType, history.getLastCorrespondingJumpMember(fromMethod));

        IMethod toMethod = mockMethod(toType, "toMethod");
        history.registerJump(fromMethod, toMethod);
        assertEquals(toMethod, history.getLastCorrespondingJumpMember(fromMethod));

        IMethod toMethod2 = mockMethod(mockType("FromType2"), "toMethod2");
        history.registerJump(fromMethod, toMethod2);
        assertEquals(toMethod2, history.getLastCorrespondingJumpMember(fromMethod));
    }

    private IMethod mockMethod(IType declaringType, String methodName)
    {
        IMethod mock = mock(IMethod.class);
        when(mock.getElementName()).thenReturn(methodName);
        when(mock.getDeclaringType()).thenReturn(declaringType);
        return mock;
    }
    
    @Test
    public void testGetLastMemberHavingReachedType()
    {
        IType fromType = mockType("FromType");
        IType toType = mockType("ToType");
        history.registerJump(fromType, toType);
        assertEquals(fromType, history.getLastCorrespondingJumpMember(toType));

        IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toType);
        assertEquals(fromType2, history.getLastCorrespondingJumpMember(toType));
        
        IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toType);
        assertEquals(fromMethod, history.getLastCorrespondingJumpMember(toType));
        
        assertEquals(toType, history.getLastCorrespondingJumpMember(fromType));
        assertEquals(toType, history.getLastCorrespondingJumpMember(fromType2));
        assertEquals(toType, history.getLastCorrespondingJumpMember(fromMethod));
    }
    
    @Test
    public void testGetLastMemberHavingReachedMethod()
    {
        IType fromType = mockType("FromType");
        IMethod toMethod = mockMethod(mockType("ToType"), "toMethod");
        history.registerJump(fromType, toMethod);
        assertEquals(fromType, history.getLastCorrespondingJumpMember(toMethod));

        IType fromType2 = mockType("FromType2");
        history.registerJump(fromType2, toMethod);
        assertEquals(fromType2, history.getLastCorrespondingJumpMember(toMethod));
        
        IMethod fromMethod = mockMethod(fromType2, "fromMethod");
        history.registerJump(fromMethod, toMethod);
        assertEquals(fromMethod, history.getLastCorrespondingJumpMember(toMethod));
        
        assertEquals(toMethod, history.getLastCorrespondingJumpMember(fromType));
        assertEquals(toMethod, history.getLastCorrespondingJumpMember(fromType2));
        assertEquals(toMethod, history.getLastCorrespondingJumpMember(fromMethod));
    }
}
