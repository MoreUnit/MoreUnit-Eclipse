package org.moreunit.core.preferences;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.ui.PlatformUI;
import org.moreunit.core.Logger;
import org.moreunit.core.MoreUnitCore;

public class PageManager
{
    private static final String MAIN_PAGE = "org.moreunit.core.preferences.mainPage";
    private static final String PREFERENCE_PAGE_ID_BASE = "org.moreunit.core.preferences.page.";
    private static final String PROPERTY_PAGE_EXTENSION_ID_BASE = "org.moreunit.core.properties.page.extension.";
    private static final String PROPERTY_PAGE_ID_BASE = "org.moreunit.core.properties.page.";

    private final Preferences preferences;
    private final Logger logger;

    public PageManager(Preferences preferences, Logger logger)
    {
        this.preferences = preferences;
        this.logger = logger;
    }

    public void startup()
    {
        for (Language lang : preferences.getConfiguredLanguages())
        {
            addPagesFor(lang);
        }
    }

    public void addPagesFor(Language lang)
    {
        addPreferencePage(lang);
        addPropertyPage(lang);
    }

    private void addPreferencePage(Language lang)
    {
        PreferenceManager preferenceManager = PlatformUI.getWorkbench().getPreferenceManager();

        preferenceManager.addTo(MAIN_PAGE, new LanguagePreferenceNode(lang, preferences.writerForLanguage(lang.getExtension())));
        logger.info("Added preference page for language " + lang);
    }

    private void addPropertyPage(Language lang)
    {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        Object token = ((ExtensionRegistry) extensionRegistry).getTemporaryUserToken();

        IContributor contributor = ContributorFactoryOSGi.createContributor(MoreUnitCore.get().getBundle());

        try
        {
            String xml = asString(getClass().getResourceAsStream("propertyPage.template.xml"), "UTF-8");

            xml = xml.replace("${extensionId}", PROPERTY_PAGE_EXTENSION_ID_BASE + lang.getExtension()) //
            .replace("${pageId}", PROPERTY_PAGE_ID_BASE + lang.getExtension()) //
            .replace("${languageName}", lang.getLabel()) //
            .replace("${languageExtension}", lang.getExtension());

            InputStream is = new ByteArrayInputStream(xml.getBytes());

            boolean result = extensionRegistry.addContribution(is, contributor, false, null, null, token);
            logger.info("Added property page for language " + lang + " with result: " + result);
        }
        catch (IOException e)
        {
            logger.error("Could not add property page for language " + lang, e);
        }
    }

    public static String asString(InputStream is, String encoding) throws IOException
    {
        StringBuilder sb = new StringBuilder(Math.max(16, is.available()));
        char[] buf = new char[4096];

        try
        {
            InputStreamReader reader = new InputStreamReader(is, encoding);
            for (int cnt; (cnt = reader.read(buf)) > 0;)
            {
                sb.append(buf, 0, cnt);
            }
        }
        finally
        {
            is.close();
        }
        return sb.toString();
    }

    public void shutdown()
    {
        for (Language lang : preferences.getConfiguredLanguages())
        {
            removePreferencePage(lang);
            removePropertyPage(lang);
        }
    }

    private void removePreferencePage(Language lang)
    {
        PlatformUI.getWorkbench().getPreferenceManager().remove(PREFERENCE_PAGE_ID_BASE + lang.getExtension());
        logger.info("Removed preference page for language " + lang);
    }

    private void removePropertyPage(Language lang)
    {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        Object token = ((ExtensionRegistry) extensionRegistry).getTemporaryUserToken();

        IExtension extension = extensionRegistry.getExtension(PROPERTY_PAGE_EXTENSION_ID_BASE + lang.getExtension());

        boolean result = extensionRegistry.removeExtension(extension, token);
        logger.info("Removed property page for language " + lang + " with result: " + result);
    }

    private static class LanguagePreferenceNode extends PreferenceNode
    {
        private final Language language;
        private final LanguagePreferencesWriter prefWriter;

        public LanguagePreferenceNode(Language lang, LanguagePreferencesWriter prefWriter)
        {
            super(PREFERENCE_PAGE_ID_BASE + lang.getExtension());
            this.language = lang;
            this.prefWriter = prefWriter;
        }

        @Override
        public String getLabelText()
        {
            return language.getLabel();
        }

        @Override
        public void createPage()
        {
            setPage(new GenericPreferencePage(getLabelText(), prefWriter));
        }
    }
}
