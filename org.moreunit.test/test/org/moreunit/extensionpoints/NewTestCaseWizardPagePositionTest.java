package org.moreunit.extensionpoints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NewTestCaseWizardPagePositionTest
{
    @Test
    public void after()
    {
        NewTestCaseWizardPagePosition pagePosition = NewTestCaseWizardPagePosition.after("a page");
        assertTrue(pagePosition.isAfter("a page"));
        assertFalse(pagePosition.isBefore("a page"));

        assertFalse(pagePosition.isAfter("another page"));
        assertFalse(pagePosition.isBefore("another page"));
    }

    @Test
    public void before()
    {
        NewTestCaseWizardPagePosition pagePosition = NewTestCaseWizardPagePosition.before("a page");
        assertFalse(pagePosition.isAfter("a page"));
        assertTrue(pagePosition.isBefore("a page"));

        assertFalse(pagePosition.isAfter("another page"));
        assertFalse(pagePosition.isBefore("another page"));
    }

    @Test(expected = NullPointerException.class)
    public void should_reject_null_page_id_when_creating_after_position() throws Exception
    {
        NewTestCaseWizardPagePosition.after(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_empty_page_id_when_creating_after_position() throws Exception
    {
        NewTestCaseWizardPagePosition.after("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_blank_page_id_when_creating_after_position() throws Exception
    {
        NewTestCaseWizardPagePosition.after("      ");
    }

    @Test(expected = NullPointerException.class)
    public void should_reject_null_page_id_when_creating_before_position() throws Exception
    {
        NewTestCaseWizardPagePosition.before(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_empty_page_id_when_creating_before_position() throws Exception
    {
        NewTestCaseWizardPagePosition.before("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_blank_page_id_when_creating_before_position() throws Exception
    {
        NewTestCaseWizardPagePosition.before("      ");
    }
}
