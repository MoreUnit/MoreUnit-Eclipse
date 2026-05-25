package org.moreunit.core.languages;

import java.util.ArrayList;
import java.util.List;

import org.moreunit.core.config.Service;
import org.moreunit.core.extension.LanguageExtensionManager;

public class MainLanguageRepository implements LanguageRepository, Service
{
    private final List<LanguageConfigurationListener> listeners = new ArrayList<>();
    private final LanguageRepository userDefinedLangRepo;
    private final LanguageExtensionManager extensionManager;

    public MainLanguageRepository(LanguageRepository userDefinedLangRepo, LanguageExtensionManager extensionManager)
    {
        this.userDefinedLangRepo = userDefinedLangRepo;
        this.extensionManager = extensionManager;
    }

    @Override
    public boolean contains(String langId)
    {
        return userDefinedLangRepo.contains(langId) || extensionManager.extensionExistsForLanguage(langId);
    }

    @Override
    public void add(Language lang)
    {
        userDefinedLangRepo.add(lang);
        notifyListenersOfAddition(lang);
    }

    private void notifyListenersOfAddition(Language lang)
    {
        for (LanguageConfigurationListener l : listeners)
        {
            l.languageConfigurationAdded(lang);
        }
    }

    @Override
    public void remove(Language lang)
    {
        userDefinedLangRepo.remove(lang);
        notifyListenersOfRemoval(lang);
    }

    private void notifyListenersOfRemoval(Language lang)
    {
        for (LanguageConfigurationListener l : listeners)
        {
            l.languageConfigurationRemoved(lang);
        }
    }

    public MainLanguageRepository addListener(LanguageConfigurationListener l)
    {
        listeners.add(l);
        return this;
    }

    public MainLanguageRepository removeListener(LanguageConfigurationListener l)
    {
        listeners.remove(l);
        return this;
    }

    @Override
    public void start()
    {
        // nothing to do
    }

    @Override
    public void stop()
    {
        listeners.clear();
    }
}
