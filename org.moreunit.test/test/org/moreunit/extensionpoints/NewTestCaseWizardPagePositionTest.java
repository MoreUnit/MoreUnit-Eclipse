package org.moreunit.extensionpoints;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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

    @Test
    public void should_reject_null_page_id_when_creating_after_position() throws Exception
    {
        assertThrows(NullPointerException.class, () -> NewTestCaseWizardPagePosition.after(null));
    }

    @Test
    public void should_reject_empty_page_id_when_creating_after_position() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NewTestCaseWizardPagePosition.after(""));
    }

    @Test
    public void should_reject_blank_page_id_when_creating_after_position() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NewTestCaseWizardPagePosition.after("      "));
    }

    @Test
    public void should_reject_null_page_id_when_creating_before_position() throws Exception
    {
        assertThrows(NullPointerException.class, () -> NewTestCaseWizardPagePosition.before(null));
    }

    @Test
    public void should_reject_empty_page_id_when_creating_before_position() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NewTestCaseWizardPagePosition.before(""));
    }

    @Test
    public void should_reject_blank_page_id_when_creating_before_position() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NewTestCaseWizardPagePosition.before("      "));
    }
}
