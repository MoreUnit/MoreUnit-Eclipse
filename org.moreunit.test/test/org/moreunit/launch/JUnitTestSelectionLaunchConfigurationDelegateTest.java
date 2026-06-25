package org.moreunit.launch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IMember;
import org.junit.jupiter.api.Test;

public class JUnitTestSelectionLaunchConfigurationDelegateTest
{
    @Test
    public void evaluateTests_should_return_constructor_members_in_insertion_order() throws Exception
    {
        IMember member1 = mock(IMember.class);
        IMember member2 = mock(IMember.class);
        IMember member3 = mock(IMember.class);

        Collection<IMember> input = new LinkedHashSet<>(Arrays.asList(member1, member2, member3));

        JUnitTestSelectionLaunchConfigurationDelegate delegate = new JUnitTestSelectionLaunchConfigurationDelegate(input);

        ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
        IProgressMonitor monitor = mock(IProgressMonitor.class);

        IMember[] result = delegate.evaluateTests(configuration, monitor);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertArrayEquals(new IMember[] { member1, member2, member3 }, result);
    }

    @Test
    public void evaluateTests_should_return_empty_array_when_no_members_were_given() throws Exception
    {
        Collection<IMember> input = new LinkedHashSet<>();

        JUnitTestSelectionLaunchConfigurationDelegate delegate = new JUnitTestSelectionLaunchConfigurationDelegate(input);

        ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
        IProgressMonitor monitor = mock(IProgressMonitor.class);

        IMember[] result = delegate.evaluateTests(configuration, monitor);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void evaluateTests_should_preserve_single_member() throws Exception
    {
        IMember single = mock(IMember.class);
        Collection<IMember> input = new LinkedHashSet<>(Arrays.asList(single));

        JUnitTestSelectionLaunchConfigurationDelegate delegate = new JUnitTestSelectionLaunchConfigurationDelegate(input);

        ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
        IProgressMonitor monitor = mock(IProgressMonitor.class);

        IMember[] result = delegate.evaluateTests(configuration, monitor);

        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(single, result[0]);
    }

    @Test
    public void evaluateTests_should_deduplicate_when_collection_has_duplicates() throws Exception
    {
        IMember member = mock(IMember.class);
        // LinkedHashSet treats the second addition as a no-op
        Collection<IMember> input = new LinkedHashSet<>(Arrays.asList(member, member));

        JUnitTestSelectionLaunchConfigurationDelegate delegate = new JUnitTestSelectionLaunchConfigurationDelegate(input);

        ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
        IProgressMonitor monitor = mock(IProgressMonitor.class);

        IMember[] result = delegate.evaluateTests(configuration, monitor);

        assertNotNull(result);
        assertEquals(1, result.length);
    }
}