package org.moreunit.handler;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.extensionpoints.IAddTestMethodContext;

public class AddTestMethodContext implements IAddTestMethodContext
{
    
    private final ICompilationUnit testClassCompilationUnit;
    private final IMethod newTestMethod;

    public AddTestMethodContext(ICompilationUnit testClassCompilationUnit, IMethod newTestMethod){
        this.testClassCompilationUnit = testClassCompilationUnit;
        this.newTestMethod = newTestMethod;
        
    }

    public ICompilationUnit getTestClass()
    {
        return testClassCompilationUnit;
    }

    public IMethod getTestMethod()
    {
        return newTestMethod;
    }

}
