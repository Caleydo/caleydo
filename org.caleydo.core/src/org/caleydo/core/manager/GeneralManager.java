/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.manager;

import static org.caleydo.data.loader.ResourceLocators.DATA_CLASSLOADER;
import static org.caleydo.data.loader.ResourceLocators.FILE;
import static org.caleydo.data.loader.ResourceLocators.URL;
import static org.caleydo.data.loader.ResourceLocators.chain;

import java.io.File;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.object.IDCreator;
import org.caleydo.core.internal.ConsoleFlags;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.core.view.ViewManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;

/**
 * General manager that contains all module managers.
 *
 * @author Marc Streit
 */
public class GeneralManager {

	/**
	 * In release mode non-stable or student views are automatically removed from the workbench.
	 */
	public static final boolean RELEASE_MODE = !ConsoleFlags.EXPERIMENTAL_MODE;

	/**
	 * This is the current version of Caleydo. The value must be the same as specified in the plugin/bundle. We need to
	 * access the version before the workbench is started. Therefore we have to set it hardcoded at this point.
	 */
	public static final String VERSION = "3.0";

	public static final String PLUGIN_ID = "org.caleydo.core";

	/**
	 * The template for the concrete Caleydo folder, ie CALEYDO_FOLDER. This is used for example in XML files and is
	 * then replaced with the concrete folder
	 */
	public static final String USER_HOME = "user.home";
	public static final String CALEYDO_FOLDER_TEMPLATE = "caleydo.folder";

	/** The major version number determines the name of the Caleydo folder **/
	public static final String CALEYDO_FOLDER = ".caleydo_" + VERSION.substring(0, 3);

	// public static final String CALEYDO_HOME_PATH =
	// Platform.getLocation().toOSString()+ File.separator;
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME) + File.separator + CALEYDO_FOLDER
			+ File.separator;
	public static final String CALEYDO_LOG_PATH = CALEYDO_HOME_PATH + "logs" + File.separator;

	/**
	 * General manager as a singleton
	 */
	private volatile static GeneralManager instance;

	/**
	 * In dry mode Caleydo runs without GUI. However, the core's functionality can be used without limitations. This is
	 * for instance used when Caleydo project files are generated from XML files.
	 */
	private boolean isDryMode;

	/**
	 * Progress monitor of the splash. Only valid during startup.
	 **/
	private SubMonitor progressMonitor;

	private ViewManager viewManager;
	private EventPublisher eventPublisher;
	private IDCreator idCreator;
	private ResourceLoader resourceLoader;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	private ProjectMetaData metaData = ProjectMetaData.createDefault();

	private Logger logger = Logger.create(GeneralManager.class);


	public void init() {
		eventPublisher = EventPublisher.INSTANCE;
		viewManager = ViewManager.get();
		idCreator = new IDCreator();
		serializationManager = SerializationManager.get();
		resourceLoader = new ResourceLoader(chain(DATA_CLASSLOADER, FILE, URL));
	}

	/**
	 * Returns the general method as a singleton object. When first called the general manager is created (lazy).
	 *
	 * @return singleton GeneralManager instance
	 */
	public static GeneralManager get() {
		if (instance == null) {
			synchronized (GeneralManager.class) {
				if (instance == null) {
					instance = new GeneralManager();
					instance.init();
				}
			}
		}
		return instance;
	}

	/**
	 * @return the metaData, see {@link #metaData}
	 */
	public ProjectMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            setter, see {@link metaData}
	 */
	public void setMetaData(ProjectMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Resource loader that is responsible for loading images, textures and data files in the Caleydo framework. DO NOT
	 * LOAD YOUR FILES ON YOUR OWN!
	 *
	 * @return resource loader
	 */
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public ViewManager getViewManager() {
		return viewManager;
	}

	public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public IDCreator getIDCreator() {
		return idCreator;
	}

	public IStatisticsPerformer getRStatisticsPerformer() {

		if (rStatisticsPerformer == null) {
			// Lazy creation
			IExtensionRegistry reg = Platform.getExtensionRegistry();

			IConfigurationElement[] ce = reg
					.getConfigurationElementsFor("org.caleydo.util.statistics.StatisticsPerformer");
			try {
				rStatisticsPerformer = (IStatisticsPerformer) ce[0].createExecutableExtension("class");
			} catch (Exception ex) {
				throw new RuntimeException("Could not instantiate R Statistics Peformer", ex);
			}
		}

		return rStatisticsPerformer;
	}

	/**
	 * Obtains the {@link SerializationManager} responsible for xml-serialization related tasks
	 *
	 * @return the {@link SerializationManager} of this caleydo application
	 */
	public SerializationManager getSerializationManager() {
		return serializationManager;
	}

	public static DataDomainManager getDataDomainManagerInstance() {
		return DataDomainManager.get();
	}

	/**
	 * @param isDryMode
	 *            setter, see {@link #isDryMode}
	 */
	public void setDryMode(boolean isDryMode) {
		this.isDryMode = isDryMode;
	}

	/**
	 * @return the isDryMode, see {@link #isDryMode}
	 */
	public boolean isDryMode() {
		return isDryMode;
	}

	/**
	 * checks whether data generated for the given caleydo version can be loaded with this one
	 *
	 * @param caleydoVersion
	 * @return
	 */
	public static boolean canLoadDataCreatedFor(String caleydoVersion) {
		if (caleydoVersion == null)
			return false;
		return VERSION.equalsIgnoreCase(caleydoVersion);
	}

	public void setSplashProgressMonitor(IProgressMonitor splashProgressMonitor) {
		this.progressMonitor = SubMonitor.convert(splashProgressMonitor, "Loading Caleydo", 1500);
	}

	public SubMonitor createSubProgressMonitor() {
		if (progressMonitor == null)
			return SubMonitor.convert(new NullProgressMonitor() {
				@Override
				public void setTaskName(String name) {
					logger.info("progress, set task: " + name);
				}

				@Override
				public void beginTask(String name, int totalWork) {
					logger.info("begin task: " + name + " (" + totalWork + ")");
				}
			});
		return progressMonitor.newChild(100, SubMonitor.SUPPRESS_SUBTASK);
	}
}
