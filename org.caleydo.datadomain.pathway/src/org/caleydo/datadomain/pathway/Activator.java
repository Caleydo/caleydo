package org.caleydo.datadomain.pathway;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.datadomain.pathway.manager.PathwayDatabase;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayLoaderThread;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = PathwayDataDomain.DATA_DOMAIN_TYPE;

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
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		SerializationManager.get().registerSerializableType(PathwayDataDomain.class);

		createPathwayDataDomains();
	}

	private void createPathwayDataDomains() {
		String pathwayDataSources = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);

		// created pathway DD
		DataDomainManager.get().createDataDomain("org.caleydo.datadomain.pathway");

		PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(PathwayDatabaseType.BIOCARTA,
				"data/html/", "data/images/", "data/html");
		
		new PathwayLoaderThread(pathwayDatabase).start();
		
		DataDomainManager.get().createDataDomain("org.caleydo.datadomain.pathway");

		pathwayDatabase = PathwayManager.get().createPathwayDatabase(PathwayDatabaseType.KEGG,
				"data/xml/", "data/images/", "");
		
		new PathwayLoaderThread(pathwayDatabase).start();
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
