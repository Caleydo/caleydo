package org.caleydo.view.bookmarking;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.bookmarking.creator.ViewCreator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.caleydo.view.bookmarking";

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
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerDataDomains();
		GeneralManager.get().getViewGLCanvasManager()
				.addViewCreator(new ViewCreator(PLUGIN_ID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, PLUGIN_ID);
	}

}
