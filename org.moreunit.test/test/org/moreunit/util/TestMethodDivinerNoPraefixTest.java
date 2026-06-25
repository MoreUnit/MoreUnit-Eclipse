package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestMethodDivinerNoPraefixTest
{

    @Test
    public void getTestMethodNameFromMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals(testMethodDivinerNoPraefix.getTestMethodNameFromMethodName("getFoo"), "getFoo");
    }

    @Test
    public void testGetTestMethodNameAfterRename() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals(testMethodDivinerNoPraefix.getTestMethodNameAfterRename("getFoo", "get2Foo", "getFoo_getterWorks"), "get2Foo_getterWorks");
    }

    @Test
    public void testGetTestMethodNameAfterRename_noMatch() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals(testMethodDivinerNoPraefix.getTestMethodNameAfterRename("getFoo", "get2Foo", "somethingElse"), "somethingElse");
    }

    @Test
    public void getMethodNameFromTestMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals(testMethodDivinerNoPraefix.getMethodNameFromTestMethodName("getFoo"), "getFoo");
    }

}
