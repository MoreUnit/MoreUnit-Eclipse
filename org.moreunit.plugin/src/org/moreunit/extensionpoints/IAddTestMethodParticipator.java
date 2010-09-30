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
package org.moreunit.extensionpoints;

/**
 * Interface defines the method that will be called, when clients implement the
 * extension point <code>addTestmethodParticipator</code>. Implementors get the
 * test method context and can so modify the created test method.
 * <p>
 * <dt><b>Changes:</b></dt>
 * <dd>16.06.2010 Gro Commented</dd>
 * 
 * @author vera, andreas
 * @version 16.06.2010
 */
public interface IAddTestMethodParticipator
{
    /**
     * Give the test method context to client, that may modify testmethod.
     * <p>
     * Do not forget to update the context after the test method was replaced
     * {@link IAddTestMethodContext#setTestMethod(org.eclipse.jdt.core.IMethod)}.
     * 
     * @param context Test method context.
     */
    void addTestMethod(IAddTestMethodContext context);
}
