package org.moreunit.dependencies.guice;

import static org.eclipse.core.runtime.ContributorFactoryOSGi.resolve;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Status;

import com.google.inject.Injector;

/**
 * This class is inspired by
 * {@link org.ops4j.peaberry.eclipse.GuiceExtensionFactory} and
 * {@link org.eclipse.springframework.util.SpringExtensionFactory}. It provides
 * a convenient way to let Guice create extensions defined in plugin.xml and
 * inject their dependencies. The difference with GuiceExtensionFactory is that
 * instead of searching for the declaration of a
 * {@link com.google.inject.Module} in plugin.xml to create an
 * {@link com.google.inject.Injector}, this abstract factory requires to be
 * subclassed in order to specify an injector to be used. That way you can use
 * an already existing injector if you will.
 * <p>
 * This is especially handy when the extensions defined in plugin.xml are not
 * the only entry point to your plugin, and when you want to use the same module
 * defining singletons to inject the members of both your extensions and the
 * other entry point (GuiceExtensionFactory would re-instantiate the singletons
 * for your extensions).
 * </p>
 * <p>
 * To use your concrete factory put it in front of your class name in the
 * extension XML. Or replace your class with the factory and put your class in
 * the {@code id} attribute instead. Because the implementation will be injected
 * based on the bindings you could even replace your class name with one of its
 * interfaces, and that interface will then be used to lookup the correct
 * implementation.
 * </p>
 * <p>
 * Here's a more detailed walkthrough, based on the example RCP Mail Template:
 * 
 * <pre>
 * {@literal <}extension point="org.eclipse.ui.views"{@literal >}
 * {@literal <}view name="Message"
 * allowMultiple="true"
 * icon="icons/sample2.gif"
 * class="example.ViewImpl"
 * id="example.view" /{@literal >}
 * {@literal <}/extension{@literal >}
 * </pre>
 * 
 * becomes:
 * 
 * <pre>
 * {@literal <}extension point="org.eclipse.ui.views"{@literal >}
 * {@literal <}view name="Message"
 * allowMultiple="true"
 * icon="icons/sample2.gif"
 * class="com.example.MyConcreteGuiceInjectorExtensionFactory:example.ViewImpl"
 * id="example.view" /{@literal >}
 * {@literal <}/extension{@literal >}
 * </pre>
 * 
 * Here's the same example with the class in the {@code id} attribute:
 * 
 * <pre>
 * {@literal <}extension point="org.eclipse.ui.views"{@literal >}
 * {@literal <}view name="Message"
 * allowMultiple="true"
 * icon="icons/sample2.gif"
 * class="com.example.MyConcreteGuiceInjectorExtensionFactory"
 * id="example.ViewImpl" /{@literal >}
 * {@literal <}/extension{@literal >}
 * </pre>
 * 
 * and again, this time using an interface instead of the implementation:
 * 
 * <pre>
 * {@literal <}extension point="org.eclipse.ui.views"{@literal >}
 * {@literal <}view name="Message"
 * allowMultiple="true"
 * icon="icons/sample2.gif"
 * class="com.example.MyConcreteGuiceInjectorExtensionFactory:org.eclipse.ui.IViewPart"
 * id="example.view" /{@literal >}
 * {@literal <}/extension{@literal >}
 * </pre>
 * 
 * </p>
 */
public abstract class AbstractGuiceInjectorExtensionFactory implements IExecutableExtension, IExecutableExtensionFactory
{
    private IConfigurationElement configuration;
    private IContributor contributor;
    private String className;

    public void setInitializationData(final IConfigurationElement config, final String name, final Object data)
    {
        configuration = config;

        // find contributor while still valid
        contributor = config.getContributor();

        // if there's no (string-based) adapter data then the class name must be
        // under "id"
        className = data instanceof String ? (String) data : config.getAttribute("id");
    }

    public Object create() throws CoreException
    {
        if(null == className)
        {
            throw newCoreException("Configuration is missing class information");
        }

        final Class< ? > clazz;
        try
        {
            clazz = resolve(contributor).loadClass(className);
        }
        catch (final InvalidRegistryObjectException e)
        {
            throw newCoreException(e);
        }
        catch (final ClassNotFoundException e)
        {
            throw newCoreException(e);
        }

        final Object o = getInjector().getInstance(clazz);
        if(o instanceof IExecutableExtension)
        {
            ((IExecutableExtension) o).setInitializationData(configuration, null, null);
        }
        return o;
    }

    abstract protected Injector getInjector();

    private CoreException newCoreException(final Throwable e)
    {
        return new CoreException(new Status(IStatus.ERROR, contributor.getName(), e.getMessage(), e));
    }

    private CoreException newCoreException(final String message)
    {
        return new CoreException(new Status(IStatus.ERROR, contributor.getName(), message));
    }
}
