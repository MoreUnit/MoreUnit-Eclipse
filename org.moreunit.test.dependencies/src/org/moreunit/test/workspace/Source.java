package org.moreunit.test.workspace;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Source
{
    static final Pattern PACKAGE_PATTERN = Pattern.compile(".*package\\s+([^;]+);.*", Pattern.DOTALL);
    static final Pattern CLASSNAME_PATTERN = Pattern.compile(".*public\\s+class\\s+(\\w+).*", Pattern.DOTALL);

    private final SourceFolderHandler sourceFolderHandler;
    private final String location;
    private final Class< ? > loadingClass;
    private String source;
    private ICompilationUnit compilationUnit;

    public Source(SourceFolderHandler sourceFolderHandler, String location, Class< ? > loadingClass)
    {
        this.sourceFolderHandler = sourceFolderHandler;
        this.location = location;
        this.loadingClass = loadingClass;
    }

    public String getSource()
    {
        if(source == null)
        {
            source = loadSource(loadingClass, location);
        }
        return source;
    }

    public String getLocation()
    {
        return location;
    }

    private String loadSource(Class< ? > loadingClass, String sourceLocation)
    {
        try
        {
            URL definitionUrl = loadingClass.getResource(sourceLocation.trim());
            if(definitionUrl == null)
            {
                throw new RuntimeException(String.format("Resource not found: '%s'", sourceLocation));
            }
            return Resources.toString(definitionUrl, Charsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Could not load class definition '%s'", sourceLocation), e);
        }
    }

    public CompilationUnitHandler getOrCreateCompilationUnit() throws CoreException
    {
        if(compilationUnit == null)
        {
            String src = getSource();

            String packageName = extract(src, PACKAGE_PATTERN);
            String className = extract(src, CLASSNAME_PATTERN);

            IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(sourceFolderHandler.get(), packageName);
            compilationUnit = WorkspaceHelper.createJavaClass(packageFragment, className).getCompilationUnit();

            setSource(compilationUnit, src);
        }

        return new CompilationUnitHandler(compilationUnit, this);
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

    private void setSource(ICompilationUnit cu, String source) throws JavaModelException
    {
        NullProgressMonitor progressMonitor = new NullProgressMonitor();
        if(! cu.isOpen())
        {
            cu.open(progressMonitor);
        }
        ICompilationUnit wc = cu.getWorkingCopy(progressMonitor);

        wc.getBuffer().setContents(source);
        wc.commitWorkingCopy(false, progressMonitor);
    }

    public SourceFolderHandler getSourceFolderHandler()
    {
        return sourceFolderHandler;
    }
}
