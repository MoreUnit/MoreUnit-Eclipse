package org.moreunit.core.expressions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;

// white-box test: uses internal types on purpose
@SuppressWarnings("restriction")
public class FileTesterTest
{
    private final Workspace workspace = mock(Workspace.class);
    private final FileTester tester = new FileTester(workspace);

    @Test
    public void should_return_false_when_receiver_is_not_a_file()
    {
        assertFalse(tester.test("not a file", "hasDefaultSupport", new Object[0], null));
    }

    @Test
    public void should_return_false_when_method_is_not_supported()
    {
        final IFile file = mock(IFile.class);

        assertFalse(tester.test(file, "unknownMethod", new Object[0], null));
    }

    @Test
    public void has_default_support_should_return_true_when_expected_value_is_false()
    {
        final IFile file = mock(IFile.class);
        when(workspace.toSrcFile(file)).thenReturn(mock(SrcFile.class));

        assertTrue(tester.test(file, "hasDefaultSupport", new Object[0], "false"));
    }

    @Test
    public void has_default_support_should_delegate_to_file_when_expected_value_is_not_false()
    {
        final IFile file = mock(IFile.class);
        final SrcFile srcFile = mock(SrcFile.class);
        when(workspace.toSrcFile(file)).thenReturn(srcFile);

        when(srcFile.hasDefaultSupport()).thenReturn(true);
        assertTrue(tester.test(file, "hasDefaultSupport", new Object[0], null));

        when(srcFile.hasDefaultSupport()).thenReturn(false);
        assertFalse(tester.test(file, "hasDefaultSupport", new Object[0], null));
    }
}
