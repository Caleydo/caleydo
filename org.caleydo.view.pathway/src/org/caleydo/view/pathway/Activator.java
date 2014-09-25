/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The shared instance
	private static Activator plugin;

	/**
	 * the resource locator of this plugin to find icons,...
	 */
	private static IResourceLocator resourceLocator = ResourceLocators.chain(
			ResourceLocators.classLoader(Activator.class.getClassLoader()), ResourceLocators.DATA_CLASSLOADER,
			ResourceLocators.FILE);

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

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLPathway.VIEW_TYPE);
	}

	public static ResourceLoader getResourceLoader() {
		return new ResourceLoader(resourceLocator);
	}

	/**
	 * @return the resourceLocator, see {@link #resourceLocator}
	 */
	public static IResourceLocator getResourceLocator() {
		return resourceLocator;
	}
}
