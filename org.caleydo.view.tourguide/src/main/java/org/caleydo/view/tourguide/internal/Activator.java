/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	private static final String ID = "org.caleydo.view.tourguide";

	// The shared instance
	private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	/**
	 * returns the {@link IResourceLocator} for this view
	 *
	 * @return
	 */
	public static IResourceLocator getResourceLocator() {
		return ResourceLocators.chain(ResourceLocators.classLoader(plugin.getClass().getClassLoader()),
				ResourceLocators.DATA_CLASSLOADER, ResourceLocators.FILE);
	}

	/**
	 * see {@link #getResourceLocator()} wrapped as a {@link ResourceLoader}
	 *
	 * @return
	 */
	public static ResourceLoader getResourceLoader() {
		return new ResourceLoader(getResourceLocator());
	}
}
