package org.moreunit.core.languages;

public interface LanguageRepository
{
    boolean contains(String langId);

    void add(Language lang);

    void remove(Language lang);
}
