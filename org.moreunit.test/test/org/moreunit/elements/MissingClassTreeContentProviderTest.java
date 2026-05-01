package org.moreunit.elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.moreunit.log.LogHandler;
import org.moreunit.core.log.Logger;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

public class MissingClassTreeContentProviderTest
{
    @Test
    public void should_not_throw_exception_but_return_null_when_java_model_exception_occurs() throws JavaModelException
    {
        MissingClassTreeContentProvider provider = new MissingClassTreeContentProvider();
        IPackageFragment mockFragment = mock(IPackageFragment.class);
        when(mockFragment.getCompilationUnits()).thenThrow(new JavaModelException(new RuntimeException("Test exception"), 1));

        Object[] result = null;
        try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class)) {
            LogHandler mockHandler = mock(LogHandler.class);
            logHandlerMock.when(LogHandler::getInstance).thenReturn(mockHandler);
            result = provider.getChildren(mockFragment);
        }

        assertThat(result).isNull();
    }
}
