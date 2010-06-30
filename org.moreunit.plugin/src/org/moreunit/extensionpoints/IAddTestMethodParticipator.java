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
 * Interface definies the method that will be called, wenn clients implement the
 * extension point <code>addTestmethodParticipator</code>.
 * 
 * Implementors get the test method context and can so modify the created test method.
 * 
 * @author vera, extended andreas 16.06.2010
 */
public interface IAddTestMethodParticipator
{
    /**
     * Give the test method context to client, that may modify testmethod.
     * @param context Test method context.
     */
    void addTestMethod(IAddTestMethodContext context);
}
