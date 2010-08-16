package org.caleydo.core.manager.general;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.id.IDCreator;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.specialized.Organism;
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
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * General manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GeneralManager
	implements IGeneralManager {

	public static final boolean IS_IN_RELEASE_MODE = true;

	/**
	 * General manager as a singleton
	 */
	private static GeneralManager generalManager;

	private IStorageManager storageManager;
	private ICommandManager commandManager;
	private ISWTGUIManager sWTGUIManager;
	private IViewManager viewGLCanvasManager;
	private IEventPublisher eventPublisher;
	private IXmlParserManager xmlParserManager;
	private IIDMappingManager genomeIdManager;
	private IDCreator IDManager;
	private ILog logger;
	private IGUIBridge guiBridge;
	private ResourceLoader resourceLoader;
	private WiiRemote wiiRemote;
	private TrackDataProvider trackDataProvider;
	private IGroupwareManager groupwareManager;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	private Organism organism = Organism.HOMO_SAPIENS;

	private boolean bIsWiiMode = false;

	@Override
	public void init(IGUIBridge externalGUIBridge) {
		// this.init();
		this.guiBridge = externalGUIBridge;
	}

	@Override
	public void init() {

		PreferenceManager preferenceManager = PreferenceManager.get();
		preferenceManager.initialize();

		initLogger();

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
		serializationManager = new SerializationManager();

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
		if (generalManager == null) {
			generalManager = new GeneralManager();
			generalManager.init();
		}
		return generalManager;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	/**
	 * Initialize the Java internal logger
	 */
	private void initLogger() {
		logger = Platform.getLog(Platform.getBundle("org.caleydo.rcp"));
	}

	@Override
	public final ILog getLogger() {
		return logger;
	}

	@Override
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	@Override
	public IStorageManager getStorageManager() {
		return storageManager;
	}

	@Override
	public IViewManager getViewGLCanvasManager() {
		return viewGLCanvasManager;
	}
	
	@Override
	public ISWTGUIManager getSWTGUIManager() {
		return sWTGUIManager;
	}

	@Override
	public IEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	@Override
	public IXmlParserManager getXmlParserManager() {
		return this.xmlParserManager;
	}

	@Override
	public IIDMappingManager getIDMappingManager() {
		return this.genomeIdManager;
	}

	@Override
	public ICommandManager getCommandManager() {
		return commandManager;
	}

	@Override
	public PreferenceStore getPreferenceStore() {
		return PreferenceManager.get().getPreferenceStore();
	}

	@Override
	public IDCreator getIDManager() {
		return IDManager;
	}

	@Override
	public IGUIBridge getGUIBridge() {
		return guiBridge;
	}

	@Override
	public boolean isWiiModeActive() {
		return bIsWiiMode;
	}

	@Override
	public WiiRemote getWiiRemote() {
		return wiiRemote;
	}

	@Override
	public TrackDataProvider getTrackDataProvider() {
		return trackDataProvider;
	}

	@Override
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

	@Override
	public IGroupwareManager getGroupwareManager() {
		return groupwareManager;
	}

	@Override
	public void setGroupwareManager(IGroupwareManager groupwareManager) {
		this.groupwareManager = groupwareManager;
	}

	@Override
	public SerializationManager getSerializationManager() {
		return serializationManager;
	}

	public static DataDomainManager getDataDomainManagerInstance() {
		return DataDomainManager.getInstance();
	}
}
