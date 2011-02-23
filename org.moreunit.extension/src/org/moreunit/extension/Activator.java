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
package org.moreunit.extension;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The class <code>Activator</code> activates the instance. This is a Singleton-Class.
 * <p>
 * <b>&copy; AG, D-49326 Melle</b>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * <dd>23.02.2011 Gro Moved to Subversion Repository</dd>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 23.02.2011
 * @since 1.5
 */
public class Activator extends Plugin {

	/**
	 * The plug-in ID, that has to be identical to the instance-id in the manifest.
	 */
	public static final String PLUGIN_ID = "org.moreunit.extension";

	/**
	 * The singleton-instance.
	 */
	private static Activator instance;

	/**
	 * The constructor.
	 */
	public Activator() {

		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {

		super.start(context);
		setInstance(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {

		setInstance(null);
		super.stop(context);
	}

	/**
	 * Returns the singleton-instance.
	 * @return Instance.
	 */
	public static Activator getInstance() {

		return instance;
	}

	/**
	 * Setzt den Wert für die einzige Instanz.
	 * @param instance Plugin-Instanz.
	 */
	private static void setInstance(final Activator instance) {

		Activator.instance = instance;
	}
}
