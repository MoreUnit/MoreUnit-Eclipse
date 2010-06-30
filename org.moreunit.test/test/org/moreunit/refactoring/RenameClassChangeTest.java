package org.moreunit.refactoring;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RenameClassChangeTest
{

    private static final String ELEMENT_NAME_NEW = "Anything";
    private static final String ELEMENT_NAME_OLD = "Something";
    /*
     * private Mock typeToRename = mock(IType.class); private Mock renamedType =
     * mock(IType.class); private Mock parentPackage =
     * mock(IPackageFragment.class); private Mock compilationUnitToRename =
     * mock(ICompilationUnit.class); private Mock renamedCompilationUnit =
     * mock(ICompilationUnit.class);
     */
    // private NullProgressMonitor progressMonitor = new NullProgressMonitor();
    //private RenameClassChange change;

    private static final IProgressMonitor PROGRESS_MONITOR = new NullProgressMonitor();

    @Before
    public void setUp() throws Exception
    {
        /*
         * setUpType(typeToRename, "KebabHouse", compilationUnitToRename,
         * parentPackage); setUpType(renamedType, "KebabEmporium",
         * renamedCompilationUnit, parentPackage); change = new
         * RenameClassChange((IType) typeToRename.proxy(), "KebabEmporium");
         */
    }

    @Test
    public void testPerformReturnsUndoForRenameType() throws CoreException
    {
        /*
         * NullProgressMonitor pm = new NullProgressMonitor();
         * typeToRename.expects
         * (once()).method("rename").with(eq("KebabEmporium"), eq(false),
         * same(pm)); Change undo = change.perform(pm);
         * assertSame(renamedType.proxy(), undo.getModifiedElement());
         */
    }

    @Test
    public void testPerformUndo() throws CoreException
    {
        /*
         * typeToRename.expects(once()).method("rename").with(eq("KebabEmporium")
         * , eq(false), same(progressMonitor)); Change undo =
         * change.perform(progressMonitor);
         * renamedType.expects(once()).method("rename").with(eq("KebabHouse"),
         * eq(false), same(progressMonitor)); Change redo =
         * undo.perform(progressMonitor); assertSame(typeToRename.proxy(),
         * redo.getModifiedElement());
         */
    }

    @Test
    public void testGetModifiedElement()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        assertEquals(typeMock, change.getModifiedElement());
    }

    private IType createTypeRenameMockWithOldElementName()
    {
        IType createMock = EasyMock.createNiceMock(IType.class);
        EasyMock.expect(createMock.getElementName()).andReturn(ELEMENT_NAME_OLD).anyTimes();
        EasyMock.expect(createMock.getParent()).andReturn(createFirstParentMockForTypeToRename()).anyTimes();

        EasyMock.replay(createMock);

        return createMock;
    }

    private IJavaElement createFirstParentMockForTypeToRename()
    {
        IJavaElement parentMock = EasyMock.createNiceMock(IJavaElement.class);
        EasyMock.expect(parentMock.getElementType()).andReturn(IJavaElement.COMPILATION_UNIT).anyTimes();
        EasyMock.expect(parentMock.getParent()).andReturn(createPackageFragmentMockWhichContainsTypeNewElementName()).anyTimes();
        EasyMock.replay(parentMock);
        return parentMock;
    }

    private IPackageFragment createPackageFragmentMockWhichContainsTypeNewElementName()
    {
        IPackageFragment mock = EasyMock.createNiceMock(IPackageFragment.class);
        EasyMock.expect(mock.getCompilationUnit(ELEMENT_NAME_NEW + ".java")).andReturn(createCompilationUnitMockWithTypeNewElementName()).anyTimes();
        EasyMock.replay(mock);

        return mock;
    }

    private ICompilationUnit createCompilationUnitMockWithTypeNewElementName()
    {
        ICompilationUnit compilationUnitMock = EasyMock.createNiceMock(ICompilationUnit.class);
        EasyMock.expect(compilationUnitMock.getType(ELEMENT_NAME_NEW)).andReturn(createTypeMockWithNewElementName()).anyTimes();
        EasyMock.replay(compilationUnitMock);

        return compilationUnitMock;
    }

    private IType createTypeMockWithNewElementName()
    {
        IType newTypeMock = EasyMock.createNiceMock(IType.class);
        EasyMock.expect(newTypeMock.getElementName()).andReturn(ELEMENT_NAME_NEW).anyTimes();
        EasyMock.replay(newTypeMock);

        return newTypeMock;
    }

    @Test
    public void testGetName()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        String expected = String.format("Rename %s to %s", ELEMENT_NAME_OLD, ELEMENT_NAME_NEW);
        assertEquals(expected, change.getName());
    }

    @Test
    public void testIsValid()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        assertNotNull(change.isValid(PROGRESS_MONITOR));
    }

    @Test
    public void testPerform() throws CoreException
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        Change perform = change.perform(PROGRESS_MONITOR);

        assertNotNull(perform);
        assertTrue(perform instanceof RenameClassChange);
        String expected = String.format("Rename %s to %s", ELEMENT_NAME_NEW, ELEMENT_NAME_OLD);
        assertEquals(expected, perform.getName());
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.5  2009/06/17 19:04:46  gianasista
// Switched tests to junit4
//
// Revision 1.4  2009/04/05 19:15:31  gianasista
// code formatter
//
// Revision 1.3 2009/01/25 20:11:32 gianasista
// Test refactoring
//
// Revision 1.2 2008/12/17 18:42:13 gianasista
// Test refactoring
//
// Revision 1.1 2008/02/04 20:41:11 gianasista
// Initital
//
// Revision 1.1.1.1 2006/08/13 14:30:55 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:21:44 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:11:29 gianasista
// CVS Refactoring
//
// Revision 1.1 2006/06/01 21:00:49 channingwalton
// made rename methods support undo, it would be nice to figure out how to show
// a preview too...
//
// Revision 1.1 2006/05/17 19:16:00 channingwalton
// enhanced rename refactoring to support undo and so that it is included in the
// preview with other changes.
//
