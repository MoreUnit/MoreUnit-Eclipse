package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionException;
import org.moreunit.core.extension.JumperExtensionManager;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.FolderCreationException;
import org.moreunit.core.ui.UserInterface;

public class JumpActionExecutor
{
    private final JumperExtensionManager extensionManager;

    public JumpActionExecutor(JumperExtensionManager extensionManager)
    {
        this.extensionManager = extensionManager;
    }

    public void execute(ExecutionContext context) throws ExecutionException
    {
        execute(context.getSelection(), context.getUserInterface());
    }

    private void execute(Selection selection, UserInterface ui)
    {
        SelectedSrcFile selectedFile = selection.getUniqueSrcFile();
        if(! selectedFile.isSupported())
        {
            return;
        }

        JumpResult jumpResult = extensionManager.jump(selectedFile.createJumpContext());
        if(jumpResult.isDone())
        {
            return;
        }

        try
        {
            // TODO Nicolas refactor this part (listeners?)
            MatchingFile match = selectedFile.getSrcFile().findUniqueMatch();
            if(match.isSearchCancelled())
            {
                return;
            }

            if(! match.isFound())
            {
                try
                {
                    ui.createNewFileWizard(match.getSrcFolderToCreate(), match.getFileToCreate()).open();
                }
                catch (FolderCreationException e)
                {
                    ui.showError("An error occurred while attempting to create folder " + e.getFolder());
                }
            }
            else
            {
                ui.openEditor(match.get());
            }
        }
        catch (DoesNotMatchConfigurationException e)
        {
            ui.showInfo(e.getPath() + " does not match your source folder preferences");
        }
    }
}
