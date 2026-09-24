package org.moreunit.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;

public class JumpLocationTest
{

    private final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);

    @Test
    public void of_member_should_return_location_at_member_name() throws Exception
    {
        final IMember member = mock(IMember.class);
        final ISourceRange nameRange = mock(ISourceRange.class);
        when(member.getCompilationUnit()).thenReturn(compilationUnit);
        when(member.getNameRange()).thenReturn(nameRange);
        when(nameRange.getOffset()).thenReturn(42);

        final JumpLocation location = JumpLocation.of(member);

        assertEquals(compilationUnit, location.getCompilationUnit());
        assertEquals(42, location.getOffset());
    }

    @Test
    public void of_member_should_return_location_with_unknown_offset_when_name_range_is_missing()
    {
        final IMember member = mock(IMember.class);
        when(member.getCompilationUnit()).thenReturn(compilationUnit);

        final JumpLocation location = JumpLocation.of(member);

        assertEquals(JumpLocation.UNKNOWN_OFFSET, location.getOffset());
    }

    @Test
    public void of_member_should_return_location_with_unknown_offset_when_source_is_not_available() throws Exception
    {
        final IMember member = mock(IMember.class);
        when(member.getCompilationUnit()).thenReturn(compilationUnit);
        when(member.getNameRange()).thenThrow(new JavaModelException(new Status(IStatus.ERROR, "org.moreunit.test", "not available")));

        final JumpLocation location = JumpLocation.of(member);

        assertEquals(JumpLocation.UNKNOWN_OFFSET, location.getOffset());
    }

    @Test
    public void of_member_should_return_null_when_location_cannot_be_determined()
    {
        assertNull(JumpLocation.of((IMember) null));
        assertNull(JumpLocation.of(mock(IMember.class)));
    }

    @Test
    public void of_compilation_unit_should_return_location_with_unknown_offset()
    {
        final JumpLocation location = JumpLocation.of(compilationUnit);

        assertEquals(compilationUnit, location.getCompilationUnit());
        assertEquals(JumpLocation.UNKNOWN_OFFSET, location.getOffset());
        assertNull(JumpLocation.of((ICompilationUnit) null));
    }

    @Test
    public void equals_should_compare_compilation_unit_and_offset()
    {
        final JumpLocation location = new JumpLocation(compilationUnit, 12);
        final ICompilationUnit otherCompilationUnit = mock(ICompilationUnit.class);

        assertSame(location, location);
        assertEquals(location, new JumpLocation(compilationUnit, 12));
        assertEquals(location.hashCode(), new JumpLocation(compilationUnit, 12).hashCode());
        assertNotEquals(location, new JumpLocation(compilationUnit, 13));
        assertNotEquals(location, new JumpLocation(otherCompilationUnit, 12));
        assertFalse(location.equals(null));
        assertFalse(location.equals("not a location"));
    }
}
