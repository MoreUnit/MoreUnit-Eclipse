package org.moreunit.test.context;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.moreunit.test.workspace.CompilationUnitHandler;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.Source;
import org.moreunit.test.workspace.SourceFolderHandler;
import org.moreunit.test.workspace.WorkspaceHandler;

/**
 * An AnnotationConfigExtractor that does not interact with the Workspace.
 */
public class ConfigExtractorThatDoesNotInteractWithWorkspace extends AnnotationConfigExtractor
{
    @Override
    protected WorkspaceConfiguration newWorkspaceConfig()
    {
        return new WorkspaceConfigurationThatDoesNotInteractWithWorkspace();
    }

    private static class WorkspaceConfigurationThatDoesNotInteractWithWorkspace extends WorkspaceConfiguration
    {
        @Override
        protected void applyPreferences(WorkspaceHandler wsHandler)
        {
            // void
        }

        @Override
        protected WorkspaceHandler newWorkspaceHandler(Class< ? > loadingClass, String projectPrefix)
        {
            return new WorkspaceHandlerThatDoesNotInteractWithWorkspace(loadingClass);
        }

        @Override
        protected SourceFolderHandler newSourceFolderHandler(ProjectHandler projectHandler, final String folderName)
        {
            return new SourceFolderHandlerThatDoesNotInteractWithWorkspace(projectHandler, folderName, folderName);
        }
    }

    private static class WorkspaceHandlerThatDoesNotInteractWithWorkspace extends WorkspaceHandler
    {
        private WorkspaceHandlerThatDoesNotInteractWithWorkspace(Class< ? > loadingClass)
        {
            super(loadingClass, "");
        }

        @Override
        protected ProjectHandler newProjectHandler(WorkspaceHandler workspaceHandler, String projectName)
        {
            return new ProjectHandlerThatDoesNotInteractWithWorkspace(workspaceHandler, projectName);
        }

        @Override
        public void clearWorkspace()
        {
        }
    }

    private static class ProjectHandlerThatDoesNotInteractWithWorkspace extends ProjectHandler
    {
        private ProjectHandlerThatDoesNotInteractWithWorkspace(WorkspaceHandler workspaceHandler, String projectName)
        {
            super(workspaceHandler, projectName, "");
        }

        @Override
        protected IType findType(String typeName)
        {
            return null;
        }
    }

    private static class SourceFolderHandlerThatDoesNotInteractWithWorkspace extends SourceFolderHandler
    {
        private final String folderName;

        private SourceFolderHandlerThatDoesNotInteractWithWorkspace(ProjectHandler projectHandler, String sourceFolderName, String folderName)
        {
            super(projectHandler, sourceFolderName);
            this.folderName = folderName;
        }

        @Override
        public String getName()
        {
            return folderName;
        }

        @Override
        protected Source newSource(SourceFolderHandler srcFolderHandler, final String sourceLocation, Class< ? > loadingClass)
        {
            return new SourceThatDoesNotInteractWithWorkspace(srcFolderHandler, sourceLocation, loadingClass, sourceLocation);
        }
    }

    private static class SourceThatDoesNotInteractWithWorkspace extends Source
    {
        private SourceThatDoesNotInteractWithWorkspace(SourceFolderHandler sourceFolderHandler, String location, Class< ? > loadingClass, String sourceLocation)
        {
            super(sourceFolderHandler, location, loadingClass);
        }

        @Override
        public CompilationUnitHandler getOrCreateCompilationUnit() throws CoreException
        {
            return new CompilationUnitThatDoesNotInteractWithWorkspace(null, this);
        }
    }

    private static class CompilationUnitThatDoesNotInteractWithWorkspace extends CompilationUnitHandler
    {
        private final Source source;

        private CompilationUnitThatDoesNotInteractWithWorkspace(ICompilationUnit compilationUnit, Source source)
        {
            super(compilationUnit, source);
            this.source = source;
        }

        @Override
        public String getName()
        {
            return source.getLocation().replace(".txt", "");
        }
    }
}
