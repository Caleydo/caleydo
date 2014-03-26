/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.DataDomainManager;
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
	private static final String ID = "org.caleydo.view.parcoords";
	// The shared instance
	private static Activator plugin;


	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		registerDataDomains();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	private void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager.get().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, GLParallelCoordinates.VIEW_TYPE);
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	public static ResourceLoader getResourceLoader() {
		return new ResourceLoader(getResourceLocator());
	}

	public static IResourceLocator getResourceLocator() {
		return ResourceLocators.chain(
ResourceLocators.classLoader(Activator.class.getClassLoader()),
				ResourceLocators.DATA_CLASSLOADER,
				ResourceLocators.FILE);
	}
}
