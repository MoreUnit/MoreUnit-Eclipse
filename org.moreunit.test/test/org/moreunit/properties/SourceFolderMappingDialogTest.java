package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.support.DialogHelper;

/**
 * Tests {@link SourceFolderMappingDialog} with its (modal) selection dialog
 * being driven by an automatic closer.
 */
@Project(mainCls = "Foo", testCls = "FooTest")
public class SourceFolderMappingDialogTest extends ContextTestCase
{
    @Test
    public void open_should_notify_block_with_selected_folders_when_dialog_is_confirmed() throws Exception
    {
        final SourceFolderMapping mapping = new SourceFolderMapping(context.getProjectHandler().get(), //
                context.getProjectHandler().getMainSrcFolderHandler().get(), //
                context.getProjectHandler().getTestSrcFolderHandler().get());
        final UnitSourceFolderBlock block = mock(UnitSourceFolderBlock.class);

        final Display display = Display.getDefault();
        final Shell dialogParent = new Shell(display);
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, shell -> DialogHelper.confirmOkButton(shell), 2000));
        try
        {
            SourceFolderMappingDialog.open(block, dialogParent, mapping);

            verify(block).handleSourceDialogMappingFinished(eq(mapping), anyList());
        }
        finally
        {
            dialogParent.dispose();
        }
    }

    @Test
    public void open_should_not_notify_block_when_dialog_is_cancelled() throws Exception
    {
        final SourceFolderMapping mapping = new SourceFolderMapping(context.getProjectHandler().get(), //
                context.getProjectHandler().getMainSrcFolderHandler().get(), //
                context.getProjectHandler().getTestSrcFolderHandler().get());
        final UnitSourceFolderBlock block = mock(UnitSourceFolderBlock.class);

        final Display display = Display.getDefault();
        final Shell dialogParent = new Shell(display);
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, Shell::close, 2000));
        try
        {
            SourceFolderMappingDialog.open(block, dialogParent, mapping);

            verify(block, never()).handleSourceDialogMappingFinished(any(), anyList());
        }
        finally
        {
            dialogParent.dispose();
        }
    }
}
