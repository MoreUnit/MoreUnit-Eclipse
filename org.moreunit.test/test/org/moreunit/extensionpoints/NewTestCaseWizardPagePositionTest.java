package org.moreunit.extensionpoints;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
