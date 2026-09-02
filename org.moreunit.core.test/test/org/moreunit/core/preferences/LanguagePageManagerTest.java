package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.moreunit.core.languages.Language;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.log.Logger;

public class LanguagePageManagerTest
{
    @Test
    public void should_read_stream_content_as_string() throws Exception
    {
        InputStream is = new ByteArrayInputStream("some content éà".getBytes(StandardCharsets.UTF_8));

        assertEquals("some content éà", LanguagePageManager.asString(is, "UTF-8"));
    }

    @Test
    public void should_return_empty_string_when_stream_is_empty() throws Exception
    {
        InputStream is = new ByteArrayInputStream(new byte[0]);

        assertEquals("", LanguagePageManager.asString(is, "UTF-8"));
    }

    @Test
    public void should_close_stream_after_reading_it() throws Exception
    {
        final boolean[] closed = new boolean[1];
        InputStream is = new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8))
        {
            @Override
            public void close() throws IOException
            {
                closed[0] = true;
                super.close();
            }
        };

        LanguagePageManager.asString(is, "UTF-8");

        assertTrue(closed[0]);
    }

    @Test
    public void should_propagate_exception_when_encoding_is_unknown()
    {
        InputStream is = new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8));

        assertThrows(IOException.class, () -> LanguagePageManager.asString(is, "no-such-encoding"));
    }

    @Test
    public void should_add_and_remove_pages_when_started_and_stopped()
    {
        assumeTrue(PlatformUI.isWorkbenchRunning(), "Workbench is not running");

        Language lang = new Language("lmo", "Lmo");
        List<Language> languages = new ArrayList<>();
        languages.add(lang);

        LanguageRepository languageRepository = mock(LanguageRepository.class);
        Preferences preferences = mock(Preferences.class);
        when(preferences.getLanguages()).thenReturn(languages);
        Logger logger = mock(Logger.class);

        LanguagePageManager pageManager = new LanguagePageManager(languageRepository, preferences, logger);

        PreferenceManager preferenceManager = PlatformUI.getWorkbench().getPreferenceManager();
        IPreferenceNode mainNode = preferenceManager.find(PreferencePages.FEATURED_LANGUAGES);
        IPreferenceNode otherLanguagesNode = mainNode.findSubNode(PreferencePages.OTHER_LANGUAGES);

        pageManager.start();

        IPreferenceNode languageNode = otherLanguagesNode.findSubNode("org.moreunit.core.preferences.page.lmo");
        assertTrue(languageNode != null, "Preference page for language should have been added");
        assertEquals("Lmo", languageNode.getLabelText());
        assertTrue(Platform.getExtensionRegistry().getExtension("org.moreunit.core.properties.page.extension.lmo") != null, //
        "Property page extension for language should have been contributed");
        verify(logger).debug(Mockito.contains("Added preference page for language"));

        pageManager.stop();

        assertNull(otherLanguagesNode.findSubNode("org.moreunit.core.preferences.page.lmo"), "Preference page for language should have been removed");
        assertNull(Platform.getExtensionRegistry().getExtension("org.moreunit.core.properties.page.extension.lmo"), //
        "Property page extension for language should have been removed");
    }
}
