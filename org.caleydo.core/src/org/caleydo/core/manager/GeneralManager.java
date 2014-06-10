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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.internal.Activator;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ViewManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.framework.Version;

/**
 * General manager that contains all module managers.
 *
 * @author Marc Streit
 */
public class GeneralManager {

	/**
	 * This is the current version of Caleydo. The value must be the same as specified in the plugin/bundle. We need to
	 * access the version before the workbench is started. Therefore we have to set it hard-coded at this point.
	 */
	public static final Version VERSION = readVersion();

	public static final String PLUGIN_ID = "org.caleydo.core";

	/** The major version number determines the name of the Caleydo folder **/
	private static final String CALEYDO_FOLDER = ".caleydo_" + VERSION.getMajor() + "." + VERSION.getMinor();

	public static final String CALEYDO_HOME_PATH = System.getProperty("user.home") + File.separator + CALEYDO_FOLDER
			+ File.separator;
	public static final String CALEYDO_LOG_PATH = CALEYDO_HOME_PATH + "logs" + File.separator;

	public static final String DATA_URL_PREFIX = "http://data.icg.tugraz.at/caleydo/download/" + VERSION.getMajor()
			+ "." + VERSION.getMinor() + "/";

	public static final String HELP_URL = "http://help.caleydo.org/" + VERSION.getMajor() + "." + VERSION.getMinor()
			+ "/index.html#!";

	/**
	 * General manager as a singleton
	 */
	private volatile static GeneralManager instance;

	private final ResourceLoader resourceLoader;
	private final Logger logger = Logger.create(GeneralManager.class);

	/**
	 * In dry mode Caleydo runs without GUI. However, the core's functionality can be used without limitations. This is
	 * for instance used when Caleydo project files are generated from XML files.
	 */
	private boolean isDryMode;

	/**
	 * Progress monitor of the splash. Only valid during startup.
	 **/
	private SubMonitor progressMonitor;

	private ProjectMetaData metaData = ProjectMetaData.createDefault();

	/**
	 *
	 */
	private GeneralManager() {
		resourceLoader = new ResourceLoader(chain(DATA_CLASSLOADER, FILE, URL));
	}


	/**
	 * @return
	 */
	private static Version readVersion() {
		if (Activator.version != null)
			return Activator.version;
		Properties p = new Properties();
		try (InputStream in = GeneralManager.class.getResourceAsStream("/version.properties")) {
			if (in != null)
				p.load(in);
		} catch (IOException e) {
			Logger.log(new Status(IStatus.ERROR, "org.caleydo.core", "can't parse: version.properties"));
		}
		String v = p.getProperty("caleydo.version", "1.0.0.unknown");
		return Version.parseVersion(v);
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
		Version tocheck = Version.parseVersion(caleydoVersion);
		if (VERSION.getMajor() != tocheck.getMajor())
			return false;
		return VERSION.getMinor() >= tocheck.getMinor();
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
