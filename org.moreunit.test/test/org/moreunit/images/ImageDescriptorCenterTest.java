package org.moreunit.images;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.Test;

public class ImageDescriptorCenterTest
{
    @Test
    public void getTestCaseLabelImageDescriptor_should_return_descriptor_and_cache_it()
    {
        ImageDescriptor descriptor = ImageDescriptorCenter.getTestCaseLabelImageDescriptor();

        assertNotNull(descriptor);
        assertSame(descriptor, ImageDescriptorCenter.getTestCaseLabelImageDescriptor());
    }
}
