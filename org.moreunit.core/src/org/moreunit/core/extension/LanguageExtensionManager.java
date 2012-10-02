package org.moreunit.core.extension;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.languages.Language;
import org.moreunit.core.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

// TODO Nicolas: handle extension addition/removal
public class LanguageExtensionManager
{
    private static final String DEPENDENCY_COND_TYPE = "dependency";
    private static final String CLASS_ATTR = "class";
    private static final String CONDITION_EL = "condition";
    private static final String FILE_EXTENSION_ATTR = "fileExtension";
    private static final String JUMPER_EL = "jumper";
    private static final String NAME_ATTR = "name";
    private static final String TYPE_ATTR = "type";
    private static final String VALUE_ATTR = "value";

    private final BundleContext bundleContext;
    private final Logger logger;

    public LanguageExtensionManager(BundleContext bundleContext, Logger logger)
    {
        this.bundleContext = bundleContext;
        this.logger = logger;
    }

    public boolean extensionExistsForLanguage(String langId)
    {
        return getLanguages().contains(new Language(langId));
    }

    private Collection<Language> getLanguages()
    {
        Collection<Language> languages = new HashSet<Language>();

        for (IConfigurationElement cfg : Platform.getExtensionRegistry().getConfigurationElementsFor(ExtensionPoints.LANGUAGES))
        {
            try
            {
                if(conditionsAreMet(cfg.getChildren(CONDITION_EL)))
                {
                    languages.add(new Language(cfg.getAttribute(FILE_EXTENSION_ATTR), cfg.getAttribute(NAME_ATTR)));
                }
            }
            catch (Exception e)
            {
                logger.warn("Could not load extension from plug-in \"" + cfg.getContributor().getName() + "\" for point \"" + ExtensionPoints.LANGUAGES + "\": " + e.getMessage());
                continue;
            }
        }

        return languages;
    }

    private boolean conditionsAreMet(IConfigurationElement[] conditionElements)
    {
        if(conditionElements == null)
        {
            return true;
        }

        for (IConfigurationElement condition : conditionElements)
        {
            if(! DEPENDENCY_COND_TYPE.equals(condition.getAttribute(TYPE_ATTR)))
            {
                return false; // condition type is unknown
            }
            if(! isDependencyPresent(condition.getAttribute(VALUE_ATTR)))
            {
                return false;
            }
        }

        return true;
    }

    private boolean isDependencyPresent(String bundleId)
    {
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++)
        {
            Bundle bundle = bundles[i];
            if(bundleId.equals(bundle.getSymbolicName()))
            {
                return true;
            }
        }
        return false;
    }

    public Iterable<IJumper> getJumpersFor(String extension)
    {
        // "Never join already wrapped lines"
        return new JumperIterable( //
        /**/new JumperIterator( //
        /* * */new FilteredActiveLanguageExtensionIterator( //
        /* * * */new ActiveLanguageExtensionIterator( //
        /* * * * */new LanguageExtensionIterator() //
        /* * * */, this) //
        /* * */, extension) //
        /**/, this) //
        );
    }

    private static class JumperIterable implements Iterable<IJumper>
    {
        private final Iterator<IJumper> it;

        public JumperIterable(Iterator<IJumper> it)
        {
            this.it = it;
        }

        public Iterator<IJumper> iterator()
        {
            return it;
        }
    }

    /**
     * Iterates on the jumpers provided by the given language extensions.
     */
    private static class JumperIterator extends AbstractIterator<IJumper>
    {
        private final LanguageExtensionManager mgr;
        private final Iterator<IConfigurationElement> languageExtensions;
        private InternalJumperIterator jumpersOfCurrentExtension;

        public JumperIterator(Iterator<IConfigurationElement> languageExtensions, LanguageExtensionManager mgr)
        {
            this.languageExtensions = languageExtensions;
            this.mgr = mgr;
        }

        @Override
        protected boolean doHasNext()
        {
            if(jumpersOfCurrentExtension != null && jumpersOfCurrentExtension.hasNext())
            {
                setCurrent(jumpersOfCurrentExtension.next());
                return true;
            }

            while (languageExtensions.hasNext())
            {
                IConfigurationElement currentExtension = languageExtensions.next();

                jumpersOfCurrentExtension = new InternalJumperIterator(currentExtension.getChildren(JUMPER_EL), mgr);

                if(jumpersOfCurrentExtension.hasNext())
                {
                    setCurrent(jumpersOfCurrentExtension.next());
                    return true;
                }
            }

            return false;
        }
    }

    private static class InternalJumperIterator extends AbstractIterator<IJumper>
    {
        private final Iterator<IConfigurationElement> possibleJumpers;
        private final LanguageExtensionManager mgr;

        public InternalJumperIterator(IConfigurationElement[] possibleJumpers, LanguageExtensionManager mgr)
        {
            this.mgr = mgr;
            if(possibleJumpers == null)
            {
                this.possibleJumpers = Collections.<IConfigurationElement> emptyList().iterator();
            }
            else
            {
                this.possibleJumpers = asList(possibleJumpers).iterator();
            }
        }

        @Override
        protected boolean doHasNext()
        {
            while (possibleJumpers.hasNext())
            {
                IConfigurationElement el = possibleJumpers.next();
                try
                {
                    Object jumper = el.createExecutableExtension(CLASS_ATTR);
                    if(! (jumper instanceof IJumper))
                    {
                        mgr.logger.warn("Element " + JUMPER_EL + " of point " + ExtensionPoints.LANGUAGES + " does not support class: " + el.getClass());
                        continue;
                    }
                    setCurrent((IJumper) jumper);
                    return true;
                }
                catch (CoreException e)
                {
                    mgr.logger.warn("Could not create instance of class defined by attribute " + CLASS_ATTR + " of element " + el.getName() + " from plug-in \"" + el.getContributor().getName() + "\" for point \"" + ExtensionPoints.LANGUAGES + "\": " + e.getMessage());
                    continue;
                }
            }

            return false;
        }
    }

    /**
     * Only returns language extensions matching the given file extension from
     * the given iterator.
     */
    private static class FilteredActiveLanguageExtensionIterator extends AbstractIterator<IConfigurationElement>
    {
        private final Iterator<IConfigurationElement> languageExtensions;
        private final String fileExtensionToConsider;

        public FilteredActiveLanguageExtensionIterator(Iterator<IConfigurationElement> languageExtensions, String fileExtensionToConsider)
        {
            this.languageExtensions = languageExtensions;
            this.fileExtensionToConsider = fileExtensionToConsider;
        }

        @Override
        protected boolean doHasNext()
        {
            while (languageExtensions.hasNext())
            {
                setCurrent(languageExtensions.next());
                if(fileExtensionToConsider.equalsIgnoreCase(current.getAttribute(FILE_EXTENSION_ATTR)))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Only returns active language extensions from the given iterator.
     */
    private static class ActiveLanguageExtensionIterator extends AbstractIterator<IConfigurationElement>
    {
        private final LanguageExtensionManager mgr;
        private final Iterator<IConfigurationElement> languageExtensions;

        public ActiveLanguageExtensionIterator(Iterator<IConfigurationElement> languageExtensions, LanguageExtensionManager mgr)
        {
            this.languageExtensions = languageExtensions;
            this.mgr = mgr;
        }

        @Override
        protected boolean doHasNext()
        {
            while (languageExtensions.hasNext())
            {
                setCurrent(languageExtensions.next());
                if(mgr.conditionsAreMet(current.getChildren(CONDITION_EL)))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Starting point: asks the platform for extensions on point
     * {@link ExtensionPoints#LANGUAGES}.
     */
    private static class LanguageExtensionIterator extends AbstractIterator<IConfigurationElement>
    {
        private IConfigurationElement[] languageCfgElements;
        private int currentIdx;

        @Override
        protected boolean doHasNext()
        {
            if(languageCfgElements == null)
            {
                languageCfgElements = Platform.getExtensionRegistry().getConfigurationElementsFor(ExtensionPoints.LANGUAGES);
                if(languageCfgElements == null)
                {
                    return false;
                }
            }
            return currentIdx + 1 <= languageCfgElements.length;
        }

        @Override
        protected IConfigurationElement doNext()
        {
            return languageCfgElements[currentIdx++];
        }
    }

    /**
     * Implements {@link #remove()}, caches the result of {@link #hasNext()}
     * between two calls to {@link #next()}, and caches the current item when
     * retrieved by {@link #hasNext()} so that it is returned when calling
     * {@link #next()} .
     */
    private static abstract class AbstractIterator<T> implements Iterator<T>
    {
        protected T current;
        private Boolean hasNext;

        public final boolean hasNext()
        {
            if(hasNext != null)
            {
                return hasNext;
            }
            hasNext = doHasNext();
            return hasNext;
        }

        protected abstract boolean doHasNext();

        protected void setCurrent(T current)
        {
            this.current = current;
        }

        public final T next()
        {
            if(! hasNext())
            {
                throw new NoSuchElementException();
            }
            hasNext = null;
            return doNext();
        }

        protected T doNext()
        {
            return current;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
