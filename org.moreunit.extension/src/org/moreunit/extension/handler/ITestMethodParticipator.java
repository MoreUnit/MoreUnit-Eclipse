/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Eclipse Public License for more details.
 * 
 * Autor: Andreas Groll
 * Datum: 07.10.2010
 */
package org.moreunit.extension.handler;

import org.moreunit.extensionpoints.IAddTestMethodContext;

/**
 * This interface declares the properties of a participator that modifies a test method.
 * <p>
 * <b>&copy; AG, D-49326 Melle 2010</b>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 07.10.2010
 * @since 1.5
 */
public interface ITestMethodParticipator {

	/**
	 * Run extension code. Package access only.
	 * @param context Extension context.
	 * @throws Exception Error.
	 */
	void modifyTestMethod(final IAddTestMethodContext context) throws Exception;
}
