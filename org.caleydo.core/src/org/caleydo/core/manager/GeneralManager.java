package org.caleydo.core.manager;

import java.io.File;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.id.IDCreator;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.util.wii.WiiRemote;
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

	public static final String PREFERENCE_FILE_NAME = "caleydo.prefs";
	public static final String USER_HOME_TEMPLATE = "user.home";
	
	/**
	 * The template for the concrete caleydo folder, ie CALEYDO_FOLDER. This is used for example in XML files
	 * and is then replaced with the concrete folder
	 */
	public static final String CALEYDO_FOLDER_TEMPLATE = "caleydo.folder";
	public static final String CALEYDO_FOLDER = ".caleydo_" + VERSION;
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME_TEMPLATE) + File.separator
		+ CALEYDO_FOLDER + File.separator;
	public static final String CALEYDO_LOG_PATH = CALEYDO_HOME_PATH + "logs" + File.separator;
	public static final String USER_HOME = "user.home";

	public static final String sDelimiter_Paser_DataItemBlock = "@";
	public static final String sDelimiter_Parser_DataItems = " ";
	public static final String sDelimiter_Parser_DataType = ";";
	public static final String sDelimiter_Parser_DataItems_Tab = "\t";

	/**
	 * General manager as a singleton
	 */
	private volatile static GeneralManager instance;
	
	private BasicInformation basicInfo;

	private IStorageManager storageManager;
	private CommandManager commandManager;
	private SWTGUIManager sWTGUIManager;
	private ViewManager viewGLCanvasManager;
	private EventPublisher eventPublisher;
	private XmlParserManager xmlParserManager;
	private IDMappingManager genomeIdManager;
	private IDCreator IDManager;
	private IGUIBridge guiBridge;
	private ResourceLoader resourceLoader;
	private WiiRemote wiiRemote;
	private TrackDataProvider trackDataProvider;
	private IGroupwareManager groupwareManager;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	private boolean bIsWiiMode = false;

	public void init(IGUIBridge externalGUIBridge) {

		this.guiBridge = externalGUIBridge;
	}

	public void init() {

		PreferenceManager preferenceManager = PreferenceManager.get();
		preferenceManager.initialize();

		basicInfo = new BasicInformation();
		
		storageManager = new StorageManager();
		commandManager = new CommandManager();
		eventPublisher = new EventPublisher();
		viewGLCanvasManager = new ViewManager();
		sWTGUIManager = new SWTGUIManager();
		genomeIdManager = new IDMappingManager();
		xmlParserManager = new XmlParserManager();
		IDManager = new IDCreator();
		xmlParserManager.initHandlers();

		groupwareManager = null;
		serializationManager = SerializationManager.get();

		resourceLoader = new ResourceLoader();

		wiiRemote = new WiiRemote();
		if (GeneralManager.get().isWiiModeActive()) {
			wiiRemote.connect();
		}

		trackDataProvider = new TrackDataProvider();
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

	public IStorageManager getStorageManager() {
		return storageManager;
	}

	public ViewManager getViewGLCanvasManager() {
		return viewGLCanvasManager;
	}

	public SWTGUIManager getSWTGUIManager() {
		return sWTGUIManager;
	}

	public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public XmlParserManager getXmlParserManager() {
		return xmlParserManager;
	}

	public IDMappingManager getIDMappingManager() {
		return genomeIdManager;
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

	public IDCreator getIDManager() {
		return IDManager;
	}

	public IGUIBridge getGUIBridge() {
		return guiBridge;
	}

	public boolean isWiiModeActive() {
		return bIsWiiMode;
	}

	public WiiRemote getWiiRemote() {
		return wiiRemote;
	}

	public TrackDataProvider getTrackDataProvider() {
		return trackDataProvider;
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
