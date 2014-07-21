/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.caleydo.core";

	// The shared instance
	private static Activator plugin;

	public static Version version;

	private final ServiceTracker<BundleContext, IProxyService> proxyTracker;

	/**
	 * The constructor
	 */
	public Activator() {
		proxyTracker = new ServiceTracker<>(FrameworkUtil.getBundle(this.getClass()).getBundleContext(),
				IProxyService.class.getName(), null);
		proxyTracker.open();
	}

	public IProxyService getProxyService() {
		return proxyTracker.getService();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		version = Version.parseVersion(getBundle().getHeaders().get(Constants.BUNDLE_VERSION));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		proxyTracker.close();
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

	public static void updateProxySettings(URL url) {
		try {
			updateProxySettings(url.toURI());
		} catch (URISyntaxException e) {
			Logger.create(Activator.class).error("can't convert url to uri", e);
		}
	}
	public static void updateProxySettings(URI uri) {
		if (plugin == null)
			return;
		IProxyService proxyService = plugin.getProxyService();
		IProxyData[] proxyDataForHost = proxyService.select(uri);

		for (IProxyData data : proxyDataForHost) {
			if (data.getHost() != null) {
				System.setProperty("http.proxySet", "true");
				System.setProperty("http.proxyHost", data.getHost());
			}
			if (data.getHost() != null) {
				System.setProperty("http.proxyPort", String.valueOf(data.getPort()));
			}
		}
		// Close the service and close the service tracker
		proxyService = null;
	}
}
