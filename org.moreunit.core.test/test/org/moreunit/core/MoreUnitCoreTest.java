package org.moreunit.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class MoreUnitCoreTest extends Plugin
{
    public static MoreUnitCoreTest instance;

    public static Plugin get()
    {
        return instance;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);
    }
}
