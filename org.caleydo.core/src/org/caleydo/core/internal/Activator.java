/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.internal.net.ProxySelector;
import org.eclipse.core.internal.net.ProxyType;
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

	@SuppressWarnings("restriction")
	public static void updateProxySettings(URI uri) {
		if (plugin == null)
			return;
		IProxyService proxyService = plugin.getProxyService();
		IProxyData[] proxyDataForHost = proxyService.select(uri);

		boolean proxiesEnabled = !ProxySelector.getDefaultProvider().equalsIgnoreCase("DIRECT");
		for (IProxyData data : proxyDataForHost) {
			if (!data.getType().equals(IProxyData.HTTP_PROXY_TYPE))
				continue;
			Properties sysProps = System.getProperties();
			if (!proxiesEnabled || data.getHost() == null || data.getHost().equals("")) { //$NON-NLS-1$
				sysProps.remove("http.proxySet"); //$NON-NLS-1$
				sysProps.remove("http.proxyHost"); //$NON-NLS-1$
				sysProps.remove("http.proxyPort"); //$NON-NLS-1$
				sysProps.remove("http.nonProxyHosts"); //$NON-NLS-1$
				sysProps.remove("http.proxyUser"); //$NON-NLS-1$
				sysProps.remove("http.proxyUserName"); //$NON-NLS-1$
				sysProps.remove("http.proxyPassword"); //$NON-NLS-1$
			} else {
				sysProps.put("http.proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				sysProps.put("http.proxyHost", data.getHost()); //$NON-NLS-1$
				int port = data.getPort();
				if (port == -1) {
					sysProps.remove("http.proxyPort"); //$NON-NLS-1$
				} else {
					sysProps.put("http.proxyPort", String.valueOf(port)); //$NON-NLS-1$
				}
				sysProps.put("http.nonProxyHosts", //$NON-NLS-1$
						ProxyType.convertHostsToPropertyString(ProxySelector.getBypassHosts(ProxySelector
								.getDefaultProvider())));

				String userid = data.getUserId();
				String password = data.getPassword();
				if (userid == null || password == null || userid.length() == 0 || password.length() == 0) {
					sysProps.remove("http.proxyUser"); //$NON-NLS-1$
					sysProps.remove("http.proxyUserName"); //$NON-NLS-1$
					sysProps.remove("http.proxyPassword"); //$NON-NLS-1$
				} else {
					sysProps.put("http.proxyUser", userid); //$NON-NLS-1$
					sysProps.put("http.proxyUserName", userid); //$NON-NLS-1$
					sysProps.put("http.proxyPassword", password); //$NON-NLS-1$
				}
			}
		}
		// Close the service and close the service tracker
		proxyService = null;
	}
}
