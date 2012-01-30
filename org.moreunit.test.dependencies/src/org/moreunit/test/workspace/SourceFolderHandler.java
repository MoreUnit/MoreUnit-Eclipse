package org.moreunit.test.workspace;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.test.workspace.SourceFolderHandler.SourceFolderAssertions;

public class SourceFolderHandler implements ElementHandler<IPackageFragmentRoot, SourceFolderAssertions>
{
    private final ProjectHandler projectHandler;
    private final String sourceFolderName;
    private final Map<String, CompilationUnitHandler> cuHandlers = newHashMap();
    private IPackageFragmentRoot sourceFolder;

    public SourceFolderHandler(ProjectHandler projectHandler, String sourceFolderName)
    {
        this.projectHandler = projectHandler;
        this.sourceFolderName = sourceFolderName;
    }

    public ProjectHandler getProjectHandler()
    {
        return projectHandler;
    }

    public WorkspaceHandler getWorkspaceHandler()
    {
        return projectHandler.getWorkspaceHandler();
    }

    public IPackageFragmentRoot get()
    {
        if(sourceFolder == null)
        {
            try
            {
                sourceFolder = WorkspaceHelper.createSourceFolderInProject(projectHandler.get(), sourceFolderName);
            }
            catch (CoreException e)
            {
                throw new RuntimeException(e);
            }
        }
        return sourceFolder;
    }

    public SourceFolderAssertions assertThat()
    {
        return new SourceFolderAssertions();
    }

    public TypeHandler createType(JavaType javaType)
    {
        final IType type;
        try
        {
            IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(get(), javaType.packageName);

            if(javaType.typeKind == JavaTypeKind.CLASS)
            {
                type = WorkspaceHelper.createJavaClass(packageFragment, javaType.typeName);
            }
            else
            {
                type = WorkspaceHelper.createJavaEnum(packageFragment, javaType.typeName);
            }
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }

        return new TypeHandler(new CompilationUnitHandler(this, type.getCompilationUnit()), type);
    }

    public TypeHandler createClass(String fullyQualifiedTypeName)
    {
        return createType(JavaTypeKind.CLASS, fullyQualifiedTypeName);
    }

    public TypeHandler createEnum(String fullyQualifiedTypeName)
    {
        return createType(JavaTypeKind.ENUM, fullyQualifiedTypeName);
    }

    private TypeHandler createType(JavaTypeKind typeKind, String fullyQualifiedTypeName)
    {
        TypeName name = new TypeName(fullyQualifiedTypeName);

        if(typeKind == JavaTypeKind.ENUM)
        {
            return createType(JavaType.newEnum(name.packageName, name.typeName));
        }
        return createType(JavaType.newClass(name.packageName, name.typeName));
    }

    public TypeHandler extendClass(TypeHandler typeHandler, String fullyQualifiedSubclassName)
    {
        TypeName name = new TypeName(fullyQualifiedSubclassName);
        try
        {
            IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(get(), name.packageName);
            IType type = WorkspaceHelper.createJavaClassExtending(packageFragment, name.typeName, typeHandler.getName());

            return new TypeHandler(new CompilationUnitHandler(this, type.getCompilationUnit()), type);
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void createElementsFromSources(Collection<String> sourceLocations)
    {
        if(sourceLocations.isEmpty())
        {
            return;
        }

        for (String sourceLocation : sourceLocations)
        {
            CompilationUnitHandler cuHandler = createCompilationUnit(this, sourceLocation);
            cuHandlers.put(cuHandler.getName(), cuHandler);
        }
    }

    protected CompilationUnitHandler createCompilationUnit(SourceFolderHandler sourceFolder, String sourceLocation)
    {
        try
        {
            return createSource(sourceLocation).getOrCreateCompilationUnit();
        }
        catch (CoreException e)
        {
            throw new RuntimeException("Could not create compilation unit defined at " + sourceLocation, e);
        }
    }

    Source createSource(String sourceLocation)
    {
        return newSource(this, sourceLocation, getWorkspaceHandler().getLoadingClass());
    }

    protected Source newSource(SourceFolderHandler srcFolderHandler, String sourceLocation, Class< ? > loadingClass)
    {
        return new Source(srcFolderHandler, sourceLocation, loadingClass);
    }

    public void createElements(Collection<JavaType> types)
    {
        for (JavaType type : types)
        {
            CompilationUnitHandler cuHandler = createType(type).getCompilationUnitHandler();
            cuHandlers.put(cuHandler.getName(), cuHandler);
        }
    }

    public String getName()
    {
        return get().getElementName();
    }

    public CompilationUnitHandler createHandlerFor(ICompilationUnit cu)
    {
        CompilationUnitHandler cuHandler = new CompilationUnitHandler(this, cu);
        cuHandlers.put(cuHandler.getName(), cuHandler);
        return cuHandler;
    }

    public CompilationUnitHandler findCompilationUnit(String cuName)
    {
        return cuHandlers.get(cuName);
    }

    private static class TypeName
    {
        final String packageName;
        final String typeName;

        TypeName(String fullyQualifiedName)
        {
            int dotIndex = fullyQualifiedName.lastIndexOf(".");
            if(dotIndex != - 1)
            {
                packageName = fullyQualifiedName.substring(0, dotIndex);
                typeName = fullyQualifiedName.substring(dotIndex + 1);
            }
            else
            {
                packageName = "";
                typeName = fullyQualifiedName;
            }
        }
    }

    public static class SourceFolderAssertions
    {
        public void noAssertionsImplementedYet()
        {
            throw new UnsupportedOperationException("no assertions implemented yet");
        }
    }
}
