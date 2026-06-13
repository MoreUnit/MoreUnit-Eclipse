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

    private final Map<String, LanguagePreferencesReader> languagePrefReaders = new HashMap<>();
    private final Map<String, LanguagePreferencesWriter> languagePrefWriters = new HashMap<>();
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

    @Override
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

    @Override
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

    @Override
    public boolean hasPreferencesForLanguage(String language)
    {
        if(LanguagePreferences.ANY_LANGUAGE.equals(language))
        {
            return store.getBoolean(BASE + LanguagePreferences.ANY_LANGUAGE + PROPERTIES_ACTIVE);
        }
        return orDefault(store.getString(LANGUAGES), "").matches(".*\\b%s\\b.*".formatted(language));
    }

    @Override
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
                activeLanguages = removeLanguage(activeLanguages, language);
            }
            store.setValue(LANGUAGES, activeLanguages);
        }
    }

    public static String removeLanguage(String languages, String language)
    {
        int idx = languages.indexOf(language);
        while (idx != -1)
        {
            boolean startBoundary = (idx == 0 || languages.charAt(idx - 1) == ',');
            boolean endBoundary = (idx + language.length() == languages.length() || languages.charAt(idx + language.length()) == ',');

            if (startBoundary && endBoundary)
            {
                int startRemove = idx;
                int endRemove = idx + language.length();

                if (startRemove > 0 && languages.charAt(startRemove - 1) == ',')
                {
                    startRemove--;
                }
                else if (endRemove < languages.length() && languages.charAt(endRemove) == ',')
                {
                    endRemove++;
                }

                return languages.substring(0, startRemove) + languages.substring(endRemove);
            }
            idx = languages.indexOf(language, idx + 1);
        }
        return languages;
    }

    public void clearCache()
    {
        languagePrefReaders.clear();
    }

    @Override
    public ScopedPreferenceStore getStore()
    {
        return store;
    }

    @Override
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
