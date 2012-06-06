package org.moreunit.mock;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplateLoader;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.ops4j.peaberry.Peaberry.osgiModule;
import static org.ops4j.peaberry.eclipse.EclipseRegistry.eclipseRegistry;

public class MoreUnitMockPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.mock"; //$NON-NLS-1$

    private static MoreUnitMockPlugin plugin;

    private Injector injector;
    private Logger logger;
    private MockingTemplateLoader templateLoader;
    private MockingTemplateStore mockingTemplateStore;

    public static MoreUnitMockPlugin getDefault()
    {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        log("Starting MoreUnit Mock Plugin...");

        plugin = this;

        init(new MockPluginCoreModule());

        log("MoreUnit Mock Plugin started.");
    }

    void init(Module module)
    {
        BundleContext bundleContext = getBundle().getBundleContext();
        Guice.createInjector(osgiModule(bundleContext, eclipseRegistry()), module).injectMembers(this);

        templateLoader.loadTemplates();
    }

    private void log(String message)
    {
        if(logger == null)
        {
            getLog().log(new Status(IStatus.INFO, MoreUnitMockPlugin.PLUGIN_ID, message));
        }
        else
        {
            logger.info(message);
        }
    }

    @Inject
    void initDependencies(Injector injector, Logger logger, MockingTemplateLoader templateLoader, MockingTemplateStore templateStore)
    {
        this.injector = injector;
        this.logger = logger;
        this.templateLoader = templateLoader;
        this.mockingTemplateStore = templateStore;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        log("Stopping MoreUnit Mock Plugin...");

        mockingTemplateStore.clear();
        plugin = null;

        log("MoreUnit Mock Plugin stopped.");
        super.stop(context);
    }

    public Injector getInjector()
    {
        return injector;
    }
}
