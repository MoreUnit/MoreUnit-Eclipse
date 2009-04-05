package org.moreunit.util;

import junit.framework.TestCase;

public class TestMethodDivinerNoPraefixTest extends TestCase
{

    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        return null;
    }

    public void testGetTestMethodNameFromMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("getFoo", testMethodDivinerNoPraefix.getTestMethodNameFromMethodName("getFoo"));
    }

    public void testGetTestMethodNameAfterRename() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("get2Foo_getterWorks", testMethodDivinerNoPraefix.getTestMethodNameAfterRename("getFoo", "get2Foo", "getFoo_getterWorks"));
    }

    public void testGetMethodNameFromTestMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertEquals("getFoo", testMethodDivinerNoPraefix.getMethodNameFromTestMethodName("getFoo"));
    }

}
