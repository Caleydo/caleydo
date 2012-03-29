package org.caleydo.core.manager;

import java.io.File;
import org.caleydo.core.command.CommandManager;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.dimension.ColumnManager;
import org.caleydo.core.data.id.IDCreator;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.parser.xml.XmlParserManager;
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
	 * In release mode non-stable or student views are automatically removed from the workbench.
	 */
	public static final boolean RELEASE_MODE = false;

	/**
	 * This is the current version of Caleydo. The value must be the same as specified in the plugin/bundle.
	 * We need to access the version before the workbench is started. Therefore we have to set it hardcoded at
	 * this point.
	 */
	public static final String VERSION = "2.0";

	public static final String PLUGIN_ID = "org.caleydo.core";

	// public static final String PREFERENCE_FILE_NAME = "caleydo.prefs";

	/**
	 * The template for the concrete caleydo folder, ie CALEYDO_FOLDER. This is used for example in XML files
	 * and is then replaced with the concrete folder
	 */
	public static final String USER_HOME = "user.home";
	public static final String CALEYDO_FOLDER_TEMPLATE = "caleydo.folder";
	public static final String CALEYDO_FOLDER = ".caleydo_" + VERSION;
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME) + File.separator
		+ CALEYDO_FOLDER + File.separator;
	public static final String CALEYDO_LOG_PATH = CALEYDO_HOME_PATH + "logs" + File.separator;

	/**
	 * General manager as a singleton
	 */
	private volatile static GeneralManager instance;

	private BasicInformation basicInfo;

	private ColumnManager dimensionManager;
	private CommandManager commandManager;
	private SWTGUIManager swtGUIManager;
	private ViewManager viewManager;
	private EventPublisher eventPublisher;
	private XmlParserManager xmlParserManager;
	private IDCreator idCreator;
	private ResourceLoader resourceLoader;
	private IGroupwareManager groupwareManager;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	public void init() {

		PreferenceManager preferenceManager = PreferenceManager.get();
		preferenceManager.initialize();

		basicInfo = new BasicInformation();

		dimensionManager = new ColumnManager();
		commandManager = new CommandManager();
		eventPublisher = new EventPublisher();
		viewManager = new ViewManager();
		swtGUIManager = new SWTGUIManager();
		xmlParserManager = new XmlParserManager();
		idCreator = new IDCreator();
		xmlParserManager.initHandlers();

		groupwareManager = null;
		serializationManager = SerializationManager.get();

		resourceLoader = new ResourceLoader();
	}

	/**
	 * Returns the general method as a singleton object. When first called the general manager is created
	 * (lazy).
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
	 * Resource loader that is responsible for loading images, textures and data files in the Caleydo
	 * framework. DO NOT LOAD YOUR FILES ON YOUR OWN!
	 * 
	 * @return resource loader
	 */
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public ColumnManager getColumnManager() {
		return dimensionManager;
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

	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Returns the preference store where Caleydo stores its preferences. The object can store and restore
	 * preferences to/from a predefined file.
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

			IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.util.statistics.StatisticsPerformer");
			IExtension ext = ep.getExtension("org.caleydo.util.r.RStatisticsPerformer");
			IConfigurationElement[] ce = ext.getConfigurationElements();

			try {
				rStatisticsPerformer = (IStatisticsPerformer) ce[0].createExecutableExtension("class");
			}
			catch (Exception ex) {
				throw new RuntimeException("Could not instantiate R Statistics Peformer", ex);
			}
		}

		return rStatisticsPerformer;
	}

	/**
	 * Obtains the {@link IGroupwareManager} responsible for communication purposes with other calyedo
	 * application
	 * 
	 * @return the {@link IGroupwareManager} of this caleydo application
	 */
	public IGroupwareManager getGroupwareManager() {
		return groupwareManager;
	}

	/**
	 * Sets the {@link IGroupwareManager} responsible for communication purposes with other calyedo
	 * application
	 * 
	 * @param groupwareManager
	 *            the environment specific {@link IGroupwareManager} to use
	 */
	public void setGroupwareManager(IGroupwareManager groupwareManager) {
		this.groupwareManager = groupwareManager;
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
}
