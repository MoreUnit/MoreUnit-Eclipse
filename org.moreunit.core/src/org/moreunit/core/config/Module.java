package org.moreunit.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.moreunit.core.log.Logger;
import org.osgi.framework.BundleContext;

/**
 * A module contributes configuration information, such as instances to use when
 * a dependency of some type is required, or system properties. Simply extend
 * this class to create a module.
 * <p>
 * A module also takes care of starting and stopping any {@link Service} that is
 * {@link #registerService(Service) registered} into it.
 */
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

    /**
     * Starts this module:
     * <ul>
     * <li>{@link #prepare() prepares} it
     * <li>then starts services {@link #registerService(Service) registered}
     * into it
     * </ul>
     */
    public final void start(BundleContext context)
    {
        this.context = context;
        prepare();
        startServices();
    }

    /**
     * Prepares this module when it is {@link #start(BundleContext) started}.
     */
    protected abstract void prepare();

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

    /**
     * Stops this module:
     * <ul>
     * <li>stops services {@link #registerService(Service) registered} into it
     * <li>then {@link #clean() cleans} it
     * </ul>
     */
    public final void stop()
    {
        stopServices();
        clean();
        context = null;
    }

    /**
     * Cleans this module when it is {@link #stop() stopped}.
     */
    protected abstract void clean();

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

    /**
     * Registers a service into this module, allowing it for being started and
     * stopped together with the module.
     */
    protected final void registerService(Service s)
    {
        services.add(s);
    }

    public abstract Logger getLogger();
}
