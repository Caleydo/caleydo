/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The shared instance
	private static Activator plugin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		registerDataDomains();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
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

	private void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLHeatMap.VIEW_TYPE);

	}
}
