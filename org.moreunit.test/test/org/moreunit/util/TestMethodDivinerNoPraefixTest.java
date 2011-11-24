package org.moreunit.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMethodDivinerNoPraefixTest
{

    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        return null;
    }

    @Test
    public void testGetTestMethodNameFromMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("getFoo", testMethodDivinerNoPraefix.getTestMethodNameFromMethodName("getFoo"));
    }

    @Test
    public void testGetTestMethodNameAfterRename() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("get2Foo_getterWorks", testMethodDivinerNoPraefix.getTestMethodNameAfterRename("getFoo", "get2Foo", "getFoo_getterWorks"));
    }

    @Test
    public void testGetMethodNameFromTestMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("getFoo", testMethodDivinerNoPraefix.getMethodNameFromTestMethodName("getFoo"));
    }

}
