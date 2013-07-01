/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	private static final String PLUGIN_ID = "org.caleydo.view.info.selection";
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


	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String imageFilePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}
}
