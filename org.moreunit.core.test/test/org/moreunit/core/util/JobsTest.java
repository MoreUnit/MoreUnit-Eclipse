package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.Test;

public class JobsTest
{
    @Test
    public void should_run_background_job_then_run_ui_job_with_its_result() throws Exception
    {
        final Display display = tryGetDisplay();
        assumeTrue(display != null, "No SWT display available");

        final List<String> uiResults = new CopyOnWriteArrayList<>();

        Jobs.waitForIndexExecuteAndRunInUI( //
        "MoreUnit test job", //
        () -> "computed in background", //
        result -> uiResults.add(result));

        waitForUiResult(display, uiResults);

        assertEquals(1, uiResults.size());
        assertEquals("computed in background", uiResults.get(0));
    }

    @Test
    public void should_not_run_ui_job_when_background_job_returns_null() throws Exception
    {
        final Display display = tryGetDisplay();
        assumeTrue(display != null, "No SWT display available");

        final AtomicBoolean uiJobRan = new AtomicBoolean(false);

        Jobs.waitForIndexExecuteAndRunInUI( //
        "MoreUnit test job (null result)", //
        () -> null, //
        result -> uiJobRan.set(true));

        // give the job time to complete without ever dispatching a UI result
        final long deadline = System.currentTimeMillis() + 2_000;
        while(System.currentTimeMillis() < deadline)
        {
            display.readAndDispatch();
            Thread.sleep(20);
        }

        assertTrue(! uiJobRan.get());
    }

    private Display tryGetDisplay()
    {
        try
        {
            return Display.getDefault();
        }
        catch (final Throwable t)
        {
            return null;
        }
    }

    private void waitForUiResult(Display display, List<String> uiResults) throws InterruptedException
    {
        final long deadline = System.currentTimeMillis() + 30_000;
        while(uiResults.isEmpty() && System.currentTimeMillis() < deadline)
        {
            // readAndDispatch allows the syncExec of the job to run, even when
            // the test itself runs on the UI thread
            display.readAndDispatch();
            Thread.sleep(20);
        }
    }
}
