package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class LanguageTypeTest
{
    @Test
    public void should_return_java() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("java");

        // then
        assertEquals(LanguageType.forPath(path), LanguageType.JAVA);
    }

    @Test
    public void should_return_groovy() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("groovy");

        // then
        assertEquals(LanguageType.forPath(path), LanguageType.GROOVY);
    }

    @Test
    public void should_return_unknown_when_unsupported() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("cpp");

        // then
        assertEquals(LanguageType.forPath(path), LanguageType.UNKNOWN);
    }
}
