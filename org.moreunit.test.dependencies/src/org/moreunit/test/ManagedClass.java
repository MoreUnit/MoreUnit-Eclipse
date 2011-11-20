package org.moreunit.test;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ManagedClass
{
    static final Pattern PACKAGE_PATTERN = Pattern.compile(".*package\\s+([^;]+);.*", Pattern.DOTALL);
    static final Pattern CLASSNAME_PATTERN = Pattern.compile(".*public\\s+class\\s+(\\w+).*", Pattern.DOTALL);

    private final SourceFolderConfiguration sourceFolderConfig;
    private final String location;
    private final Class< ? > loadingClass;
    private String initialSource;
    private IType type;

    public ManagedClass(SourceFolderConfiguration sourceFolderConfig, String location, Class< ? > loadingClass)
    {
        this.sourceFolderConfig = sourceFolderConfig;
        this.loadingClass = loadingClass;
        this.location = location;
    }

    public String getInitialSource()
    {
        if(initialSource == null)
        {
            initialSource = loadClassDefinition(loadingClass, location);
        }
        return initialSource;
    }

    private String loadClassDefinition(Class< ? > loadingClass, String definitionLocation)
    {
        try
        {
            URL definitionUrl = loadingClass.getResource(definitionLocation.trim());
            if(definitionUrl == null)
            {
                throw new RuntimeException(String.format("Resource not found: '%s'", definitionLocation));
            }
            return Resources.toString(definitionUrl, Charsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Could not load class definition '%s'", definitionLocation), e);
        }
    }

    protected ManagedClass create() throws JavaModelException, CoreException
    {
        String src = getInitialSource();

        String packageName = extract(src, PACKAGE_PATTERN);
        String className = extract(src, CLASSNAME_PATTERN);

        IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(sourceFolderConfig.getPackageFragmentRoot(), packageName);
        type = WorkspaceHelper.createJavaClass(packageFragment, className);

        setSource(type, src);

        return this;
    }

    String extract(String src, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(src);
        if(! matcher.matches())
        {
            throw new RuntimeException(String.format("Could not match pattern <%s> in file <%s>", pattern.pattern(), location));
        }
        return matcher.group(1).trim();
    }

    private void setSource(IType type, String source) throws JavaModelException
    {
        ICompilationUnit cu = type.getCompilationUnit();
        NullProgressMonitor progressMonitor = new NullProgressMonitor();
        if(! cu.isOpen())
        {
            cu.open(progressMonitor);
        }
        ICompilationUnit wc = cu.getWorkingCopy(progressMonitor);

        wc.getBuffer().setContents(source);
        wc.commitWorkingCopy(false, progressMonitor);
    }

    public ICompilationUnit getCompilationUnit()
    {
        return type.getCompilationUnit();
    }
}
