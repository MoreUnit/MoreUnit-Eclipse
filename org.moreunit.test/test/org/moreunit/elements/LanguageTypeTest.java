package org.moreunit.elements;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

public class LanguageTypeTest
{
    @Test
    public void should_return_java() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("java");

        // then
        assertThat(LanguageType.forPath(path)).isEqualTo(LanguageType.JAVA);
    }

    @Test
    public void should_return_groovy() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("groovy");

        // then
        assertThat(LanguageType.forPath(path)).isEqualTo(LanguageType.GROOVY);
    }

    @Test
    public void should_return_unknown_when_unsupported() throws Exception
    {
        // given
        IPath path = mock(IPath.class);
        when(path.getFileExtension()).thenReturn("cpp");

        // then
        assertThat(LanguageType.forPath(path)).isEqualTo(LanguageType.UNKNOWN);
    }
}
