package org.moreunit.properties;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.moreunit.core.log.Logger;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;



/**
 * @author gianasista
 */
@Context(SimpleJUnit4Project.class)
public class WorkspaceSourceFolderContentProviderTest extends ContextTestCase
{

    /*
     * Test for Bug 3590427 (Exception was logged on closed projects)
     */
    @Test
    public void getElements_should_not_throw_exception_when_workspace_contains_closed_projects() throws Exception
    {
        closeProjectAndPrepareMockedLoggerToThrowExcpetionWhenErrorGetsLogged();

        ArrayList<SourceFolderMapping> list = new ArrayList<SourceFolderMapping>(0);
        WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(list);
        provider.getElements(null);
    }

    private void closeProjectAndPrepareMockedLoggerToThrowExcpetionWhenErrorGetsLogged() throws CoreException, NoSuchFieldException, IllegalAccessException
    {
        context.getProjectHandler().get().getProject().close(null);

        Field loggerField = LogHandler.getInstance().getClass().getDeclaredField("logger");
        loggerField.setAccessible(true);

        Logger mockedLogger = mock(Logger.class);
        doThrow(new RuntimeException("error must not get thrown on closed projects")).when(mockedLogger).error(notNull(Throwable.class));
        loggerField.set(LogHandler.getInstance(), mockedLogger);
    }
}
