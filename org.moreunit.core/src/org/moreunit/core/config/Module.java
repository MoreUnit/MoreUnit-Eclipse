package org.moreunit.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.moreunit.core.Service;
import org.moreunit.core.log.Logger;
import org.osgi.framework.BundleContext;

public abstract class Module<M extends Module<M>>
{
    private final List<Service> services = new ArrayList<Service>();
    private BundleContext context;

    protected Module(boolean override)
    {
        setInstance(override ? handleReplacement() : thisModule());
    }

    protected abstract M getInstance();

    protected abstract void setInstance(M instance);

    private M handleReplacement()
    {
        BundleContext ctxt = null;
        M possiblyExistingInstance = getInstance();
        if(possiblyExistingInstance != null)
        {
            ctxt = possiblyExistingInstance.context;
            possiblyExistingInstance.stop();
        }

        if(ctxt != null)
        {
            this.start(ctxt);
        }

        return thisModule();
    }

    @SuppressWarnings("unchecked")
    private M thisModule()
    {
        return (M) this;
    }

    public final BundleContext getContext()
    {
        return context;
    }

    public void start(BundleContext context)
    {
        this.context = context;
        doStart();
        startServices();
    }

    protected abstract void doStart();

    private void startServices()
    {
        for (Service s : services)
        {
            try
            {
                s.start();
            }
            catch (Exception e)
            {
                getLogger().error("Could not start service " + s, e);
            }
        }
    }

    public void stop()
    {
        stopServices();
        doStop();
        context = null;
    }

    protected abstract void doStop();

    private void stopServices()
    {
        for (ListIterator<Service> it = services.listIterator(services.size()); it.hasPrevious();)
        {
            Service s = it.previous();
            try
            {
                s.stop();
                it.remove();
            }
            catch (Exception e)
            {
                getLogger().error("Could not stop service " + s, e);
            }
        }
    }

    protected final void registerService(Service s)
    {
        services.add(s);
    }

    public abstract Logger getLogger();
}
