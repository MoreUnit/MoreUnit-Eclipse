package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMethodDivinerJunit3PraefixTest
{
    @Test
    public void getTestMethodName_with_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getTestMethodNameFromMethodName("getValue")).isEqualTo("testGetValue");
    }

    @Test
    public void getTestMethodNameFromMethodName_null() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getTestMethodNameFromMethodName(null)).isEqualTo("");
    }

    @Test
    public void getTestMethodNameFromMethodName_emptyString() throws Exception
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getTestMethodNameFromMethodName("")).isEqualTo("");
    }

    @Test
    public void getMethodNameFromTestMethodName_with_getValue_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getMethodNameFromTestMethodName("getValue")).isNull();
    }

    @Test
    public void getMethodNameFromTestMethodName_with_testGetValue_returns_getValue()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getMethodNameFromTestMethodName("testGetValue")).isEqualTo("getValue");
    }

    @Test
    public void getMethodNameFromTestMethodName_with_test_returns_null()
    {
        TestMethodDiviner testMethodDiviner = new TestMethodDivinerJunit3Praefix();
        assertThat(testMethodDiviner.getMethodNameFromTestMethodName("test")).isNull();
    }

    @Test
    public void getTestMethodNameAfterRename()
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
