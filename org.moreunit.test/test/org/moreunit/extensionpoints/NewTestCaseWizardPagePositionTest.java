package org.moreunit.extensionpoints;

import static org.fest.assertions.Assertions.assertThat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NewTestCaseWizardPagePositionTest
{
    @Test
    public void after()
    {
        NewTestCaseWizardPagePosition pagePosition = NewTestCaseWizardPagePosition.after("a page");
        assertThat(pagePosition.isAfter("a page")).isTrue();
        assertThat(pagePosition.isBefore("a page")).isFalse();

        assertThat(pagePosition.isAfter("another page")).isFalse();
        assertThat(pagePosition.isBefore("another page")).isFalse();
    }

    @Test
    public void before()
    {
        NewTestCaseWizardPagePosition pagePosition = NewTestCaseWizardPagePosition.before("a page");
        assertThat(pagePosition.isAfter("a page")).isFalse();
        assertThat(pagePosition.isBefore("a page")).isTrue();

        assertThat(pagePosition.isAfter("another page")).isFalse();
        assertThat(pagePosition.isBefore("another page")).isFalse();
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
