package org.moreunit.core.preferences;

import static org.moreunit.core.preferences.Preferences.orDefault;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.core.log.Logger;

public class ProjectPreferences implements WriteablePreferences, ReadablePreferences
{
    private static final String BASE = Preferences.BASE;
    private static final String LANGUAGES = BASE + "languages";
    private static final String PROPERTIES_ACTIVE = ".active";

    private final Map<String, LanguagePreferencesReader> languagePrefReaders = new HashMap<String, LanguagePreferencesReader>();
    private final Map<String, LanguagePreferencesWriter> languagePrefWriters = new HashMap<String, LanguagePreferencesWriter>();
    private final IProject project;
    private final ScopedPreferenceStore store;
    private final Preferences wsPrefs;
    private final LanguagePreferencesReader anyLanguagePrefReader;
    private final Logger logger;

    public ProjectPreferences(IProject project, ScopedPreferenceStore store, Preferences wsPrefs, Logger logger)
    {
        this.project = project;
        this.store = store;
        this.wsPrefs = wsPrefs;
        anyLanguagePrefReader = new LanguagePreferencesReader(LanguagePreferences.ANY_LANGUAGE, wsPrefs.readerForAnyLanguage(), this);
        this.logger = logger;
    }

    public LanguagePreferencesWriter writerForAnyLanguage()
    {
        return writerForLanguage(LanguagePreferences.ANY_LANGUAGE);
    }

    public LanguagePreferencesWriter writerForLanguage(String language)
    {
        if(languagePrefWriters.containsKey(language))
        {
            return languagePrefWriters.get(language);
        }

        LanguagePreferencesWriter writer = new LanguagePreferencesWriter(language, this);
        languagePrefWriters.put(language, writer);

        return writer;
    }

    public LanguagePreferencesReader readerForLanguage(String language)
    {
        if(languagePrefReaders.containsKey(language))
        {
            return languagePrefReaders.get(language);
        }

        final LanguagePreferencesReader defaults;
        if(wsPrefs.hasPreferencesForLanguage(language))
        {
            defaults = wsPrefs.readerForLanguage(language);
        }
        else
        {
            defaults = anyLanguagePrefReader;
        }

        if(hasPreferencesForLanguage(language))
        {
            LanguagePreferencesReader reader = new LanguagePreferencesReader(language, defaults, this);
            languagePrefReaders.put(language, reader);
            return reader;
        }

        return defaults;
    }

    public boolean hasPreferencesForLanguage(String language)
    {
        if(LanguagePreferences.ANY_LANGUAGE.equals(language))
        {
            return store.getBoolean(BASE + LanguagePreferences.ANY_LANGUAGE + PROPERTIES_ACTIVE);
        }
        return orDefault(store.getString(LANGUAGES), "").matches(String.format(".*\\b%s\\b.*", language));
    }

    public void activatePreferencesForLanguage(String language, boolean active)
    {
        if(hasPreferencesForLanguage(language) == active)
        {
            return;
        }

        if(LanguagePreferences.ANY_LANGUAGE.equals(language))
        {
            store.setValue(BASE + LanguagePreferences.ANY_LANGUAGE + PROPERTIES_ACTIVE, active);
        }
        else
        {
            String activeLanguages = orDefault(store.getString(LANGUAGES), "");
            if(active)
            {
                activeLanguages = activeLanguages + "," + language;
            }
            else
            {
                activeLanguages = activeLanguages.replaceFirst(String.format(",?\\b%s\\b,?", language), "");
            }
            store.setValue(LANGUAGES, activeLanguages);
        }
    }

    public void clearCache()
    {
        languagePrefReaders.clear();
    }

    public ScopedPreferenceStore getStore()
    {
        return store;
    }

    public void save()
    {
        try
        {
            store.save();
        }
        catch (IOException e)
        {
            logger.error("Could not save preferences for project: " + project.getName(), e);
        }
    }
}
