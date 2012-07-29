package org.moreunit.core.languages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.core.extension.LanguageExtensionManager;

@RunWith(MockitoJUnitRunner.class)
public class MainLanguageRepositoryTest
{
    @Mock
    private LanguageExtensionManager extensionManager;
    @Mock
    private LanguageRepository userDefinedLanguagesRepo;
    @InjectMocks
    private MainLanguageRepository mainRepo;

    @Mock
    private LanguageConfigurationListener listener1;
    @Mock
    private LanguageConfigurationListener listener2;

    @Test
    public void should_contain_language_when_defined_by_user() throws Exception
    {
        // given
        when(userDefinedLanguagesRepo.contains("vb")).thenReturn(true);
        when(extensionManager.extensionExistsForLanguage("vb")).thenReturn(false);

        // then
        assertTrue(mainRepo.contains("vb"));
    }

    @Test
    public void should_contain_language_when_defined_by_extension() throws Exception
    {
        // given
        when(userDefinedLanguagesRepo.contains("pl")).thenReturn(false);
        when(extensionManager.extensionExistsForLanguage("pl")).thenReturn(true);

        // then
        assertTrue(mainRepo.contains("pl"));
    }

    @Test
    public void should_not_contain_language_otherwise() throws Exception
    {
        // given
        when(userDefinedLanguagesRepo.contains("cpp")).thenReturn(false);
        when(extensionManager.extensionExistsForLanguage("cpp")).thenReturn(false);

        // then
        assertFalse(mainRepo.contains("cpp"));
    }

    @Test
    public void should_add_language_to_user_languages() throws Exception
    {
        // given
        Language lang = new Language("rb");

        // when
        mainRepo.add(lang);

        verify(userDefinedLanguagesRepo).add(lang);
    }

    @Test
    public void should_remove_language_from_user_languages() throws Exception
    {
        // given
        Language lang = new Language("js");
        mainRepo.add(lang);

        // when
        mainRepo.remove(lang);

        verify(userDefinedLanguagesRepo).remove(lang);
    }

    @Test
    public void should_notify_listeners_when_language_is_added() throws Exception
    {
        // given
        mainRepo.addListener(listener1);
        mainRepo.addListener(listener2);

        Language lang = new Language("rb");

        // when
        mainRepo.add(lang);

        // then
        verify(listener1).languageConfigurationAdded(lang);
        verify(listener2).languageConfigurationAdded(lang);
    }

    @Test
    public void should_notify_listeners_when_language_is_removed() throws Exception
    {
        // given
        mainRepo.addListener(listener1);
        mainRepo.addListener(listener2);

        Language lang = new Language("clj");
        mainRepo.add(lang);

        // when
        mainRepo.remove(lang);

        // then
        verify(listener1).languageConfigurationRemoved(lang);
        verify(listener2).languageConfigurationRemoved(lang);
    }
}
