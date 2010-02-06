package org.moreunit.extensionpoints;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;

public interface IAddTestMethodContext
{
    ICompilationUnit getTestClass();
    
    IMethod getTestMethod();
}
