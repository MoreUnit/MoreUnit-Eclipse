package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.extension.JumperExtensionManager;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.FileMatcher;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.FolderCreationException;
import org.moreunit.core.ui.UserInterface;

public class JumpActionExecutor
{
    private final JumperExtensionManager extensionManager;
    private final FileMatcher fileMatcher;

    public JumpActionExecutor(JumperExtensionManager extensionManager, FileMatcher fileMatcher)
    {
        this.extensionManager = extensionManager;
        this.fileMatcher = fileMatcher;
    }

    public void execute(ExecutionContext context) throws ExecutionException
    {
        execute(context.getSelection(), context.getUserInterface(), context);
    }

    private void execute(Selection selection, UserInterface ui, ExecutionContext context)
    {
        IFile selectedFile = selection.getUniqueFile();
        if(selectedFile == null || selectedFile.getFileExtension() == null)
        {
            return;
        }

        JumpResult jumpResult = extensionManager.jump(new JumpContext(context, selectedFile));
        if(jumpResult.isDone())
        {
            return;
        }

        try
        {
            MatchingFile match = fileMatcher.match(selectedFile);
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
