package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestMethodDivinerJunit3PraefixTest
{
    @Test
    public void getTestMethodName_with_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals(testMethodDiviner.getTestMethodNameFromMethodName("getValue"), "testGetValue");
    }

    @Test
    public void getTestMethodNameFromMethodName_null() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals(testMethodDiviner.getTestMethodNameFromMethodName(null), "");
    }

    @Test
    public void getTestMethodNameFromMethodName_emptyString() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals(testMethodDiviner.getTestMethodNameFromMethodName(""), "");
    }

    @Test
    public void getMethodNameFromTestMethodName_with_getValue_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertNull(testMethodDiviner.getMethodNameFromTestMethodName("getValue"));
    }

    @Test
    public void getMethodNameFromTestMethodName_with_testGetValue_returns_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals(testMethodDiviner.getMethodNameFromTestMethodName("testGetValue"), "getValue");
    }

    @Test
    public void getMethodNameFromTestMethodName_with_test_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertNull(testMethodDiviner.getMethodNameFromTestMethodName("test"));
    }

    @Test
    public void getTestMethodNameAfterRename()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();

        assertEquals(testMethodDiviner.getTestMethodNameAfterRename("countMembers", "countAllMembers", "testCountMembersSpecialCase"), "testCountAllMembersSpecialCase");
        assertEquals(testMethodDiviner.getTestMethodNameAfterRename("countMembers", "countAllMembers", "testCountMembers"), "testCountAllMembers");
    }

    @Test
    public void getTestMethodNameAfterRename_noMatch()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals(testMethodDiviner.getTestMethodNameAfterRename("countMembers", "countAllMembers", "testSomethingElse"), "testSomethingElse");
    }
}
