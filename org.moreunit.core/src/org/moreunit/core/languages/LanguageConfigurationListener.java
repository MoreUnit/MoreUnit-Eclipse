package org.moreunit.core.languages;

public interface LanguageConfigurationListener
{
    void languageConfigurationAdded(Language lang);

    void languageConfigurationRemoved(Language lang);
}
