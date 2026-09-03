package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.preferences.Preferences.MethodSearchMode;

public class CorrespondingMemberRequestTest
{
    @Test
    public void default_request_should_use_default_values()
    {
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest().build();

        assertFalse(request.shouldCreateClassIfNoResult());
        assertEquals(MethodSearchMode.DEFAULT, request.getMethodSearchMode());
        assertNull(request.getPromptText());
        assertNull(request.getCurrentMethod());
        assertTrue(request.shouldReturn(MemberType.TYPE_OR_METHOD));
        assertFalse(request.shouldReturn(MemberType.TYPE));
    }

    @Test
    public void should_configure_method_search_mode_and_current_method()
    {
        final IMethod method = mock(org.eclipse.jdt.core.IMethod.class);

        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .withCurrentMethod(method) //
                .methodSearchMode(MethodSearchMode.BY_NAME) //
                .build();

        assertSame(method, request.getCurrentMethod());
        assertEquals(MethodSearchMode.BY_NAME, request.getMethodSearchMode());
    }

    @Test
    public void should_configure_expected_result_type()
    {
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE) //
                .build();

        assertTrue(request.shouldReturn(MemberType.TYPE));
        assertFalse(request.shouldReturn(MemberType.TYPE_OR_METHOD));
    }

    @Test
    public void create_class_if_no_result_should_enable_creation_and_set_prompt_text()
    {
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .createClassIfNoResult("Choose a member") //
                .build();

        assertTrue(request.shouldCreateClassIfNoResult());
        assertEquals("Choose a member", request.getPromptText());
    }

    @Test
    public void prompt_text_should_be_set_without_enabling_class_creation()
    {
        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .promptText("hint") //
                .build();

        assertFalse(request.shouldCreateClassIfNoResult());
        assertEquals("hint", request.getPromptText());
    }
}
