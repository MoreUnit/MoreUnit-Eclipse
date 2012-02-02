package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class TestMethodDivinerNoPraefixTest
{

    @Test
    public void getTestMethodNameFromMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertThat(testMethodDivinerNoPraefix.getTestMethodNameFromMethodName("getFoo")).isEqualTo("getFoo");
    }

    @Test
    public void testGetTestMethodNameAfterRename() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertThat(testMethodDivinerNoPraefix.getTestMethodNameAfterRename("getFoo", "get2Foo", "getFoo_getterWorks")).isEqualTo("get2Foo_getterWorks");
    }

    @Test
    public void getMethodNameFromTestMethodName() throws Exception
    {
        TestMethodDivinerNoPraefix testMethodDivinerNoPraefix = new TestMethodDivinerNoPraefix();
        assertThat(testMethodDivinerNoPraefix.getMethodNameFromTestMethodName("getFoo")).isEqualTo("getFoo");
    }

}
