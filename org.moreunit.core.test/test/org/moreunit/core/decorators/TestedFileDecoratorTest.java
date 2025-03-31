package org.moreunit.core.decorators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.ui.ImageRegistry;

public class TestedFileDecoratorTest
{
    ImageRegistry imageRegistry = mock(ImageRegistry.class);
    TestedFileDecorator decorator = new TestedFileDecorator(imageRegistry, mock(Logger.class));

    IDecoration decoration = mock(IDecoration.class);
    SrcFile fileToDecorate = mock(SrcFile.class);

    @Before
    public void prepareImageRegistry() throws Exception
    {
        ImageDescriptor imageDescriptor = mock(ImageDescriptor.class);
        when(imageRegistry.getTestedFileIndicator()).thenReturn(imageDescriptor);
    }

    @Test
    public void should_not_decorate_files_which_language_is_handled_by_another_plugin() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(false);

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then
        verifyNoInteractions(decoration);
    }

    @Test
    public void should_not_decorate_test_files() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(true);
        given(fileToDecorate.isTestFile()).willReturn(true);

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then
        verifyNoInteractions(decoration);
    }

    @Test
    public void should_not_decorate_untested_files() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(true);
        given(fileToDecorate.isTestFile()).willReturn(false);
        given(fileToDecorate.hasCorrespondingFiles()).willReturn(false);

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then
        verifyNoInteractions(decoration);
    }

    @Test
    public void should_decorate_tested_files() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(true);
        given(fileToDecorate.isTestFile()).willReturn(false);
        given(fileToDecorate.hasCorrespondingFiles()).willReturn(true);

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then
        verify(decoration).addOverlay(imageRegistry.getTestedFileIndicator(), IDecoration.TOP_RIGHT);
    }

    @Test
    public void should_ignore_configuration_error() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(true);
        given(fileToDecorate.isTestFile()).willReturn(false);
        given(fileToDecorate.hasCorrespondingFiles()).willThrow(new DoesNotMatchConfigurationException(null));

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then no exception is thrown, no window opens, and:
        verifyNoInteractions(decoration);
    }

    @Test
    public void should_ignore_all_exceptions() throws Exception
    {
        // given
        given(fileToDecorate.hasDefaultSupport()).willReturn(true);
        given(fileToDecorate.hasCorrespondingFiles()).willThrow(new RuntimeException("dummy exception"));

        // when
        decorator.decorate(fileToDecorate, decoration);

        // then no exception is thrown, no window opens, and:
        verifyNoInteractions(decoration);
    }
}
