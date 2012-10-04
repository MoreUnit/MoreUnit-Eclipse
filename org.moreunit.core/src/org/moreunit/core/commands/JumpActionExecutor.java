package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.extension.JumperExtensionManager;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.FileMatcher;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.FolderCreationException;
import org.moreunit.core.ui.MessageDialogs;
import org.moreunit.core.ui.NewFileWizard;

public class JumpActionExecutor
{
    private final JumperExtensionManager extensionManager;
    private final FileMatcher fileMatcher;
    private final Logger logger;

    public JumpActionExecutor(JumperExtensionManager extensionManager, FileMatcher fileMatcher, Logger logger)
    {
        this.extensionManager = extensionManager;
        this.fileMatcher = fileMatcher;
        this.logger = logger;
    }

    public void execute(ExecutionContext context) throws ExecutionException
    {
        IFile selectedFile = context.getSelection().getUniqueFile();
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
                    NewFileWizard wizard = new NewFileWizard(context.getWorkbench(), match.getSrcFolderToCreate(), match.getFileToCreate(), logger);
                    context.openDialog(wizard);
                }
                catch (FolderCreationException e)
                {
                    MessageDialogs.openError(context.getActiveShell(), "An error occurred while attempting to create folder " + e.getFolder());
                }
            }
            else
            {
                context.openEditor(match.get());
            }
        }
        catch (DoesNotMatchConfigurationException e)
        {
            MessageDialogs.openInformation(context.getActiveShell(), e.getPath() + " does not match your source folder preferences");
        }
    }
}
