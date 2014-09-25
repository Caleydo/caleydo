/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.imageviewer.internal;

import java.net.URL;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

/**
 * resource definition
 *
 * @author Samuel Gratzl
 *
 */
public final class Resources {
	private Resources() {
	}
	/**
	 * the resource locator of this plugin to find icons,...
	 */
	private static IResourceLocator resourceLocator = ResourceLocators.chain(
			ResourceLocators.classLoader(Activator.class.getClassLoader()), ResourceLocators.DATA_CLASSLOADER,
			ResourceLocators.FILE);

	/**
	 * @return
	 */
	public static ResourceLoader getResourceLoader() {
		return new ResourceLoader(resourceLocator);
	}

	/**
	 * @return the resourceLocator, see {@link #resourceLocator}
	 */
	public static IResourceLocator getResourceLocator() {
		return resourceLocator;
	}

	public static final URL ICON = icon("icon.png");

	/**
	 * assumes icons are in the package org.caleydo.view.imageviewer.internal.icons
	 *
	 * @param icon
	 * @return
	 */
	private static URL icon(String icon) {
		return Resources.class.getResource("icons/" + icon);
	}
}
