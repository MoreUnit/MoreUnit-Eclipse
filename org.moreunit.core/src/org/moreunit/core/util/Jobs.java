package org.moreunit.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for {@link Job}.
 */
public class Jobs
{

    private Jobs()
    {
    }

    public static <T> void executeAndRunInUI(String jobName, Supplier<T> backgroundJob, Consumer<T> uiJob)
    {
        Job job = new Job(jobName)
        {

            @Override
            protected IStatus run(IProgressMonitor progressmonitor)
            {
                T result = backgroundJob.get();
                if(progressmonitor.isCanceled())
                {
                    return Status.CANCEL_STATUS;
                }
                if(result != null)
                {
                    Display.getDefault().syncExec(() -> uiJob.accept(result));
                }
                return progressmonitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
