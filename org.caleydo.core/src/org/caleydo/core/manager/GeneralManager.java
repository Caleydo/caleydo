/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.manager;

import java.io.File;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.id.object.IDCreator;
import org.caleydo.core.io.parser.xml.XmlParserManager;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.core.view.ViewManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * General manager that contains all module managers.
 * 
 * @author Marc Streit
 */
public class GeneralManager {

	/**
	 * In release mode non-stable or student views are automatically removed
	 * from the workbench.
	 */
	public static final boolean RELEASE_MODE = false;

	/**
	 * This is the current version of Caleydo. The value must be the same as
	 * specified in the plugin/bundle. We need to access the version before the
	 * workbench is started. Therefore we have to set it hardcoded at this
	 * point.
	 */
	public static final String VERSION = "2.1";

	public static final String PLUGIN_ID = "org.caleydo.core";

	/**
	 * The template for the concrete Caleydo folder, ie CALEYDO_FOLDER. This is
	 * used for example in XML files and is then replaced with the concrete
	 * folder
	 */
	public static final String USER_HOME = "user.home";
	public static final String CALEYDO_FOLDER_TEMPLATE = "caleydo.folder";

	/** The major version number determines the name of the Caleydo folder **/
	public static final String CALEYDO_FOLDER = ".caleydo_" + VERSION.substring(0, 3);

	// public static final String CALEYDO_HOME_PATH =
	// Platform.getLocation().toOSString()+ File.separator;
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME)
			+ File.separator + CALEYDO_FOLDER + File.separator;
	public static final String CALEYDO_LOG_PATH = CALEYDO_HOME_PATH + "logs" + File.separator;

	/**
	 * General manager as a singleton
	 */
	private volatile static GeneralManager instance;

	/**
	 * In dry mode Caleydo runs without GUI. However, the core's functionality
	 * can be used without limitations. This is for instance used when Caleydo
	 * project files are generated from XML files.
	 */
	private boolean isDryMode;

	private BasicInformation basicInfo;

	private SWTGUIManager swtGUIManager;
	private ViewManager viewManager;
	private EventPublisher eventPublisher;
	private XmlParserManager xmlParserManager;
	private IDCreator idCreator;
	private ResourceLoader resourceLoader;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	public void init() {

		PreferenceManager preferenceManager = PreferenceManager.get();
		preferenceManager.initialize();

		basicInfo = new BasicInformation();

		eventPublisher = new EventPublisher();
		viewManager = ViewManager.get();
		swtGUIManager = new SWTGUIManager();
		xmlParserManager = new XmlParserManager();
		idCreator = new IDCreator();
		xmlParserManager.initHandlers();
		serializationManager = SerializationManager.get();
		resourceLoader = new ResourceLoader();
	}

	/**
	 * Returns the general method as a singleton object. When first called the
	 * general manager is created (lazy).
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

	public BasicInformation getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInformation basicInfo) {
		this.basicInfo = basicInfo;
	}

	/**
	 * Resource loader that is responsible for loading images, textures and data
	 * files in the Caleydo framework. DO NOT LOAD YOUR FILES ON YOUR OWN!
	 * 
	 * @return resource loader
	 */
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public ViewManager getViewManager() {
		return viewManager;
	}

	public SWTGUIManager getSWTGUIManager() {
		return swtGUIManager;
	}

	public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public XmlParserManager getXmlParserManager() {
		return xmlParserManager;
	}

	/**
	 * Returns the preference store where Caleydo stores its preferences. The
	 * object can store and restore preferences to/from a predefined file.
	 */
	public PreferenceStore getPreferenceStore() {
		return PreferenceManager.get().getPreferenceStore();
	}

	public IDCreator getIDCreator() {
		return idCreator;
	}

	public IStatisticsPerformer getRStatisticsPerformer() {

		if (rStatisticsPerformer == null) {
			// Lazy creation
			IExtensionRegistry reg = Platform.getExtensionRegistry();

			IExtensionPoint ep = reg
					.getExtensionPoint("org.caleydo.util.statistics.StatisticsPerformer");
			IExtension ext = ep.getExtension("org.caleydo.util.r.RStatisticsPerformer");
			IConfigurationElement[] ce = ext.getConfigurationElements();

			try {
				rStatisticsPerformer = (IStatisticsPerformer) ce[0]
						.createExecutableExtension("class");
			}
			catch (Exception ex) {
				throw new RuntimeException("Could not instantiate R Statistics Peformer", ex);
			}
		}

		return rStatisticsPerformer;
	}

	/**
	 * Obtains the {@link SerializationManager} responsible for
	 * xml-serialization related tasks
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
	 * @param isDryMode setter, see {@link #isDryMode}
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
}
