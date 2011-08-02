package org.caleydo.view.datagraph;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		registerDataDomains();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
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
		// FIXME: The DataGraph is independent from dataDomains
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLDataGraph.VIEW_TYPE);

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLDataGraph.VIEW_TYPE);
	}
}
