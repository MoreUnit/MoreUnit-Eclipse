package org.moreunit.core.preferences;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.languages.Language;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.matching.TestFolderPathPattern;

public class Preferences implements WriteablePreferences, ReadablePreferences, LanguageRepository
{
    public static final Defaults DEFAULTS = new Defaults();
    static final String BASE = "org.moreunit.core.";
    private static final String LANGUAGES = BASE + "languages";

    private final Map<IProject, ScopedPreferenceStore> projectStores = new HashMap<IProject, ScopedPreferenceStore>();
    private final Map<String, LanguagePreferencesReader> languagePrefReaders = new HashMap<String, LanguagePreferencesReader>();
    private final Map<String, LanguagePreferencesWriter> languagePrefWriters = new HashMap<String, LanguagePreferencesWriter>();
    private final IPreferenceStore store;
    private final LanguagePreferencesReader anyLanguagePrefReader;
    private final LanguagePreferencesWriter anyLanguagePrefWriter;
    private final Logger logger;

    public Preferences(IPreferenceStore store, Logger logger)
    {
        this.store = store;
        anyLanguagePrefReader = new LanguagePreferencesReader(LanguagePreferences.ANY_LANGUAGE, DEFAULTS, this);
        anyLanguagePrefWriter = new LanguagePreferencesWriter(LanguagePreferences.ANY_LANGUAGE, this);
        this.logger = logger;
    }

    public ProjectPreferences get(IProject project)
    {
        if(project == null)
        {
            throw new NullPointerException("project");
        }
        return new ProjectPreferences(project, getStore(project), this, logger);
    }

    public LanguagePreferencesWriter writerForAnyLanguage()
    {
        return anyLanguagePrefWriter;
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

    public LanguagePreferencesReader readerForAnyLanguage()
    {
        return anyLanguagePrefReader;
    }

    public LanguagePreferencesReader readerForLanguage(String language)
    {
        if(! hasPreferencesForLanguage(language))
        {
            return anyLanguagePrefReader;
        }

        if(languagePrefReaders.containsKey(language))
        {
            return languagePrefReaders.get(language);
        }

        LanguagePreferencesReader reader = new LanguagePreferencesReader(language, anyLanguagePrefReader, this);
        languagePrefReaders.put(language, reader);

        return reader;
    }

    public boolean contains(String language)
    {
        return hasPreferencesForLanguage(language);
    }

    public boolean hasPreferencesForLanguage(String language)
    {
        return getLanguages().contains(new Language(language));
    }

    public void activatePreferencesForLanguage(String language, boolean active)
    {
        // nothing to do
    }

    public List<Language> getLanguages()
    {
        String str = orDefault(store.getString(LANGUAGES), "");

        List<Language> result = new ArrayList<Language>();
        for (String s : str.split(","))
        {
            if(s.length() > 2)
            {
                String[] lang = s.split(":");
                if(lang.length == 2)
                {
                    result.add(new Language(lang[0], lang[1]));
                }
            }
        }

        return result;
    }

    public void add(Language language)
    {
        List<Language> languages = getLanguages();
        languages.add(language);
        sort(languages);

        setLanguages(languages);
    }

    private void setLanguages(List<Language> languages)
    {
        StringBuilder sb = new StringBuilder();
        for (Language lang : languages)
        {
            if(sb.length() != 0)
            {
                sb.append(",");
            }
            sb.append(lang.getExtension()).append(":").append(lang.getLabel());
        }
        store.setValue(LANGUAGES, sb.toString());
    }

    public void remove(Language language)
    {
        List<Language> languages = getLanguages();
        languages.remove(language);
        setLanguages(languages);
    }

    private ScopedPreferenceStore getStore(IProject project)
    {
        if(projectStores.containsKey(project))
        {
            return projectStores.get(project);
        }

        ProjectScope projectScope = new ProjectScope(project);
        ScopedPreferenceStore store = new ScopedPreferenceStore(projectScope, MoreUnitCore.PLUGIN_ID);
        store.setSearchContexts(new IScopeContext[] { projectScope });
        projectStores.put(project, store);

        return store;
    }

    public IPreferenceStore getStore()
    {
        return store;
    }

    public void save()
    {
        // nothing to do
    }

    static String orDefault(String value, String defaultValue)
    {
        return value != null && value.length() != 0 ? value : defaultValue;
    }

    public static class Defaults extends LanguagePreferencesReader
    {
        public Defaults()
        {
            super(null, null, null);
        }

        @Override
        public String getFileWordSeparator()
        {
            return "";
        }

        @Override
        public String getSrcFolderPathTemplate()
        {
            return TestFolderPathPattern.SRC_PROJECT_VARIABLE;
        }

        @Override
        public String getTestFileNameTemplate()
        {
            return TestFileNamePattern.SRC_FILE_VARIABLE + "Test";
        }

        @Override
        public String getTestFolderPathTemplate()
        {
            return TestFolderPathPattern.SRC_PROJECT_VARIABLE;
        }
    }
}
