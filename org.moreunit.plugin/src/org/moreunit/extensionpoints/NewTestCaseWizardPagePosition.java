package org.moreunit.extensionpoints;

import static org.moreunit.core.util.Preconditions.checkArgument;
import static org.moreunit.core.util.Preconditions.checkNotNull;

/**
 * The position of a page in the "New Test Case" wizard: the page may be
 * <tt>BEFORE</tt> or <tt>AFTER</tt> another page already provided by MoreUnit's
 * wizard or by another extension point.
 * <p>
 * Note that if the page relative to which this position is defined does not
 * exist when your page is included in the wizard, your page will be placed in
 * last position.
 * </p>
 * <p>
 * See {@link INewTestCaseWizardPage#TEST_CASE_PAGE}, <tt>TEST_METHODS_PAGE</tt>
 * , ... for MoreUnit's page IDs.
 * </p>
 * 
 * @see INewTestCaseWizardPage
 */
public final class NewTestCaseWizardPagePosition
{
    public static enum RelativePosition
    {
        BEFORE, AFTER
    }

    private final RelativePosition relativePosition;
    private final String relativePageId;

    private NewTestCaseWizardPagePosition(RelativePosition relativePosition, String relativePageId)
    {
        this.relativePosition = relativePosition;
        this.relativePageId = checkNotNull(relativePageId, "Relative page ID can not be null");
        checkArgument(this.relativePageId.trim().length() != 0, "Relative page ID can not be blank");
    }

    /**
     * Creates a position for a page that should be <em>before</em> the page of
     * ID: <tt>relativePageId</tt>.
     * 
     * @param relativePageId the ID of the page that will be just <em>after</em>
     *            this position (see {@link INewTestCaseWizardPage#getId()}).
     * @return the newly created position
     */
    public static NewTestCaseWizardPagePosition before(String relativePageId)
    {
        return new NewTestCaseWizardPagePosition(RelativePosition.BEFORE, relativePageId);
    }

    /**
     * Creates a position for a page that should be <em>after</em> the page of
     * ID: <tt>relativePageId</tt>.
     * 
     * @param relativePageId the ID of the page that will be just
     *            <em>before</em> this position (see
     *            {@link INewTestCaseWizardPage#getId()}).
     * @return the newly created position
     */
    public static NewTestCaseWizardPagePosition after(String relativePageId)
    {
        return new NewTestCaseWizardPagePosition(RelativePosition.AFTER, relativePageId);
    }

    /**
     * Tests whether this position id <em>just</em> after the page having the
     * given ID.
     * 
     * @param pageId the ID of the page to test for being just <em>before</em>
     *            this position
     * @return <tt>true</tt> if this position comes just after the given page
     */
    public boolean isAfter(String pageId)
    {
        return relativePosition == RelativePosition.AFTER && relativePageId.equals(pageId);
    }

    /**
     * Tests whether this position id <em>just</em> before the page having the
     * given ID.
     * 
     * @param pageId the ID of the page to test for being just <em>after</em>
     *            this position
     * @return <tt>true</tt> if this position comes just before the given page
     */
    public boolean isBefore(String pageId)
    {
        return relativePosition == RelativePosition.BEFORE && relativePageId.equals(pageId);
    }
}
