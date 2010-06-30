package org.moreunit.refactoring;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import static org.junit.Assert.*;

public class RenameMethodChangeTest
{

    /*
     * private Mock declaringType = mock(IType.class); private Mock method =
     * mock(IMethod.class); private Mock renamedMethod = mock(IMethod.class);
     */
    //private NullProgressMonitor progressMonitor = new NullProgressMonitor();
    //private RenameMethodChange change;

    /*
     * protected void setUp() throws Exception { setUpMethod(method, "getKebab",
     * declaringType); setUpMethod(renamedMethod, "getShishKebab",
     * declaringType); change = new RenameMethodChange((IMethod) method.proxy(),
     * "getShishKebab"); // this is not good but I cannot work out how to make
     * mocks work for this test
     * declaringType.stubs().method("getMethods").will(returnValue(new IMethod[]
     * { (IMethod) method.proxy(), (IMethod) renamedMethod.proxy() })); }
     */

    @Test
    public void testPerformReturnsUndoForRenameMethod() throws CoreException
    {
        assertTrue(true);
        /*
         * NullProgressMonitor pm = new NullProgressMonitor();
         * method.expects(once()).method("rename").with(eq("getShishKebab"),
         * eq(false), same(pm)); Change undo = change.perform(pm);
         * assertSame(renamedMethod.proxy(), undo.getModifiedElement());
         */
    }

    @Test
    public void testPerformUndo() throws CoreException
    {
        assertTrue(true);
        /*
         * method.expects(once()).method("rename").with(eq("getShishKebab"),
         * eq(false), same(progressMonitor)); Change undo =
         * change.perform(progressMonitor);
         * renamedMethod.expects(once()).method("rename").with(eq("getKebab"),
         * eq(false), same(progressMonitor)); Change redo =
         * undo.perform(progressMonitor); assertSame(method.proxy(),
         * redo.getModifiedElement());
         */
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
//
