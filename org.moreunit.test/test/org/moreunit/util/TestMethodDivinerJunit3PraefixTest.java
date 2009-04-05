package org.moreunit.util;

import junit.framework.TestCase;

public class TestMethodDivinerJunit3PraefixTest extends TestCase
{
    public void testGetTestMethodName_with_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals("testGetValue", testMethodDiviner.getTestMethodNameFromMethodName("getValue"));
    }

    public void testGetTestMethodNameFromMethodName_null() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals("", testMethodDiviner.getTestMethodNameFromMethodName(null));
    }

    public void testGetTestMethodNameFromMethodName_emptyString() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals("", testMethodDiviner.getTestMethodNameFromMethodName(""));
    }

    public void testGetMethodNameFromTestMethodName_with_getValue_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertNull(testMethodDiviner.getMethodNameFromTestMethodName("getValue"));
    }

    public void testGetMethodNameFromTestMethodName_with_testGetValue_returns_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertEquals("getValue", testMethodDiviner.getMethodNameFromTestMethodName("testGetValue"));
    }

    public void testGetMethodNameFromTestMethodName_with_test_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertNull(testMethodDiviner.getMethodNameFromTestMethodName("test"));
    }

    public void testGetTestMethodNameAfterRename()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();

        assertEquals("testCountAllMembersSpecialCase", testMethodDiviner.getTestMethodNameAfterRename("countMembers", "countAllMembers", "testCountMembersSpecialCase"));
        assertEquals("testCountAllMembers", testMethodDiviner.getTestMethodNameAfterRename("countMembers", "countAllMembers", "testCountMembers"));

    }

    // public void testGetNewTestMethodNameWhenThereIsNoSuffix(){
    // TestMethodDivinerJunit3Praefix testMethodDiviner = new
    // TestMethodDivinerJunit3Praefix();
    // assertEquals("testGetBar",
    // testMethodDiviner.getNewTestMethodName("testGetFoo", "getFoo",
    // "getBar"));
    // }
    //
    // public void testGetNewTestMethodNameWhenThereIsASuffix(){
    // TestMethodDivinerJunit3Praefix testMethodDiviner = new
    // TestMethodDivinerJunit3Praefix();
    // assertEquals("testGetBarDoesSomething",
    // testMethodDiviner.getNewTestMethodName("testGetFooDoesSomething",
    // "getFoo", "getBar"));
    // }
    //
    // public void
    // test_GetNewTestMethodName_isSameAsGetTestMethodNameAfterRename() throws
    // Exception{
    // TestMethodDivinerJunit3Praefix testMethodDiviner = new
    // TestMethodDivinerJunit3Praefix();
    // assertEquals("testCountAllMembersSpecialCase",
    // testMethodDiviner.getNewTestMethodName("testCountMembersSpecialCase",
    // "countMembers", "countAllMembers"));
    // assertEquals("testCountAllMembers",
    // testMethodDiviner.getNewTestMethodName("testCountMembers",
    // "countMembers", "countAllMembers"));
    // }
}
