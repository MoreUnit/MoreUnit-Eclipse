/**
 *
 */
package org.moreunit.properties;

import java.util.Arrays;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.util.PluginTools;

public class SourceFolderMappingDialog
{

    private SourceFolderMappingDialog()
    {
        // utility class
    }

    public static void open(UnitSourceFolderBlock unitSourceFolderBlock, Shell parentShell, SourceFolderMapping sourceFolderMapping)
    {
        final var input = PluginTools.getAllSourceFolderFromProject(sourceFolderMapping.getJavaProject()).toArray();
        final var selected = sourceFolderMapping.getSourceFolderList().toArray();
        final var title = "Mapped folders";
        final var message = "Select mapped source folders";
        final var dialog = ListSelectionDialog.of(input).preselect(selected).title(title).message(message).labelProvider(new JavaElementLabelProvider()).create(parentShell);
        if(dialog.open() != Window.OK || dialog.getResult() == null)
        {
            return;
        }

        final var result = Arrays.stream(dialog.getResult()).filter(IPackageFragmentRoot.class::isInstance).map(IPackageFragmentRoot.class::cast).toList();
        unitSourceFolderBlock.handleSourceDialogMappingFinished(sourceFolderMapping, result);
    }

}
