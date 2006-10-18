package org.moreunit.refactoring;

import org.moreunit.AbstractMoreUnitTest;

public class RenameMethodParticipantTest extends AbstractMoreUnitTest {

	private RenameMethodParticipant	renameMethodParticipant	= new RenameMethodParticipant();

	public void testGetNewTestMethodNameWhenThereIsNoSuffix() {
		assertEquals("testGetBar", renameMethodParticipant.getNewTestMethodName("testGetFoo", "getFoo", "getBar"));
	}

	public void testGetNewTestMethodNameWhenThereIsASuffix() {
		assertEquals("testGetBarDoesSomething", renameMethodParticipant.getNewTestMethodName("testGetFooDoesSomething", "getFoo", "getBar"));
	}

}
