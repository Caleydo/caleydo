package org.caleydo.datadomain.genetic;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.SerializationManager;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = GeneticDataDomain.DATA_DOMAIN_TYPE;

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
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	/**
	 * Creates the plug-in, registeres all the types needed for serialization,
	 * and loads the id mapping.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		SerializationManager.get().registerSerializableType(GeneticDataDomain.class);
		// load ids needed in this datadomain
		GeneralManager.get().getXmlParserManager()
				.parseXmlFileByName("data/bootstrap/bootstrap.xml");
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

}
