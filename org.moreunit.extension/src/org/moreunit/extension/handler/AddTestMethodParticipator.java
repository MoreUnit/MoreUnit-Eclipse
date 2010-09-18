/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.extension.handler;

import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.extensionpoints.IAddTestMethodParticipator;
import org.moreunit.log.LogHandler;

/**
 * The class <code>AddTestMethodParticipator</code> implements a participator, that
 * changes test methods created by moreunit.
 * <p>
 * <b>&copy; AG, D-49326 Melle 2010</b>
 * <dd>09.08.2010 Gro Catch all exceptions</dd>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 09.08.2010
 * @since 1.5
 */
public class AddTestMethodParticipator implements IAddTestMethodParticipator {

	/**
	 * Counter.
	 */
	//private static volatile int counter = 0;

	/**
	 * Constructor for AddTestMethodParticipator.
	 */
	public AddTestMethodParticipator() {

		// Default-Contructor
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTestMethod(final IAddTestMethodContext context) {

		// Wenn die Testmethode null ist, ist die Methode bereits vorhanden
		if (context.getTestMethod() == null && !context.isNewTestClassCreated()) {
			LogHandler.getInstance().handleInfoLog("TestMethod already exists");
			return;
		}

		// Partizipator instanzieren und ausführen
		try {

			// Replace testmethod with a new one
			//new ReplaceTestMethodParticipator().replaceTestMethod(context);

			// Modify testmethod with ast manipulation
			new ModifyTestMethodParticipator().modifyTestMethod(context);

		} catch (Exception e) {
			LogHandler.getInstance().handleExceptionLog(
				"Error executing extension: " + this.getClass().getName(), e);
		}
	}
}
