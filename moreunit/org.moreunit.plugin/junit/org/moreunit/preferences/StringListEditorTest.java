package org.moreunit.preferences;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.moreunit.preferences.StringListEditor;

/**
 * @author vera
 *
 * 10.06.2006 20:58:37
 */
public class StringListEditorTest extends TestCase {
	
	StringListEditor editor;
	
	public void testNothing() {
		assertTrue(true);
	}

//	protected void setUp() throws Exception {
//		super.setUp();
//		Mock compositeMock = mock(Composite.class);
//		Composite composite = (Composite) compositeMock.proxy();
//		editor = new StringListEditor("name", "labeltext", composite);
//	}
//	
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		editor = null;
//	}
//	
//	public void testCreateList() {
//		String[] items = {};
//		assertEquals("", editor.createList(items));
//		
//		String[] items2 = { "Test" };
//		assertEquals("Test", editor.createList(items2));
//		
//		String[] items3 = { "Test1", "Test2" };
//		assertEquals("Test1,Test2", editor.createList(items3));
//	}
}