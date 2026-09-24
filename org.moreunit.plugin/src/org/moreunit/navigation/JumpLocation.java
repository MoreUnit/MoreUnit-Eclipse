package org.moreunit.navigation;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

/**
 * A position in a compilation unit the user was located at when jumping
 * between a class under test and its test case (and vice versa). Locations are
 * stored into the {@link JumpNavigationHistory}, in order to allow the user to
 * jump back to where (s)he was before jumping.
 */
public class JumpLocation
{
    /**
     * Offset used when the exact position inside the compilation unit is not
     * known (eg. when jumping from the package explorer).
     */
    public static final int UNKNOWN_OFFSET = -1;

    private final ICompilationUnit compilationUnit;
    private final int offset;

    public JumpLocation(ICompilationUnit compilationUnit, int offset)
    {
        this.compilationUnit = compilationUnit;
        this.offset = offset;
    }

    /**
     * Returns the location of the name of the given member, or null if it
     * cannot be determined.
     */
    public static JumpLocation of(IMember member)
    {
        final ICompilationUnit compilationUnit = member == null ? null : member.getCompilationUnit();
        if(compilationUnit == null)
        {
            return null;
        }

        try
        {
            final ISourceRange nameRange = member.getNameRange();
            return new JumpLocation(compilationUnit, nameRange == null ? UNKNOWN_OFFSET : nameRange.getOffset());
        }
        catch (final JavaModelException e)
        {
            return new JumpLocation(compilationUnit, UNKNOWN_OFFSET);
        }
    }

    /**
     * Returns a location in the given compilation unit (its exact position
     * being unknown), or null if it cannot be determined.
     */
    public static JumpLocation of(ICompilationUnit compilationUnit)
    {
        return compilationUnit == null ? null : new JumpLocation(compilationUnit, UNKNOWN_OFFSET);
    }

    public ICompilationUnit getCompilationUnit()
    {
        return compilationUnit;
    }

    /**
     * Returns the offset of this location inside its compilation unit, or
     * {@link #UNKNOWN_OFFSET} if the exact position is not known.
     */
    public int getOffset()
    {
        return offset;
    }

    @Override
    public int hashCode()
    {
        final int compilationUnitHash = compilationUnit == null ? 0 : compilationUnit.hashCode();
        return 31 * compilationUnitHash + offset;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(! (obj instanceof final JumpLocation other))
        {
            return false;
        }
        return offset == other.offset && (compilationUnit == null ? other.compilationUnit == null : compilationUnit.equals(other.compilationUnit));
    }

    @Override
    public String toString()
    {
        return "JumpLocation[" + compilationUnit + " @" + offset + "]";
    }
}
