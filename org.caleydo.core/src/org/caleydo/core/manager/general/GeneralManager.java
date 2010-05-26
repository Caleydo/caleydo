package org.caleydo.core.manager.general;

import java.util.Collection;
import java.util.EnumMap;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.id.IDCreator;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.genetic.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.manager.specialized.genetic.pathway.PathwayItemManager;
import org.caleydo.core.manager.specialized.genetic.pathway.PathwayManager;
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
	private static IGeneralManager generalManager;

	private IStorageManager storageManager;
	private ISetManager setManager;
	private ICommandManager commandManager;
	private ISWTGUIManager sWTGUIManager;
	private IViewManager viewGLCanvasManager;
	private IPathwayManager pathwayManager;
	private IPathwayItemManager pathwayItemManager;
	private IEventPublisher eventPublisher;
	private IXmlParserManager xmlParserManager;
	private IIDMappingManager genomeIdManager;
	private GlyphManager glyphManager;
	private IDCreator IDManager;
	private ILog logger;
	private IGUIBridge guiBridge;
	private ResourceLoader resourceLoader;
	private WiiRemote wiiRemote;
	private TrackDataProvider trackDataProvider;
	private IGroupwareManager groupwareManager;
	private SerializationManager serializationManager;
	private IStatisticsPerformer rStatisticsPerformer;

	/**
	 * The use case determines which kind of data is loaded in the views.
	 */
	private EnumMap<EDataDomain, IDataDomain> useCaseMap;

	/**
	 * The master use case of this analysis session.
	 * 
	 * @deprecated Will be replaced by new use case management / handling.
	 */
	@Deprecated
	private IDataDomain masterUseCase;

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
		setManager = new SetManager();
		// connectedElementRepManager = new SelectionManager();
		commandManager = new CommandManager();
		eventPublisher = new EventPublisher();
		viewGLCanvasManager = new ViewManager();
		sWTGUIManager = new SWTGUIManager();
		genomeIdManager = new IDMappingManager();
		pathwayManager = new PathwayManager();
		pathwayItemManager = new PathwayItemManager();
		xmlParserManager = new XmlParserManager();
		glyphManager = new GlyphManager();
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
		useCaseMap = new EnumMap<EDataDomain, IDataDomain>(EDataDomain.class);
	}

	/**
	 * Returns the general method as a singleton object. When first called the general manager is created
	 * (lazy).
	 * 
	 * @return singleton GeneralManager instance
	 */
	public static IGeneralManager get() {
		if (generalManager == null) {
			generalManager = new GeneralManager();
			generalManager.init();
		}
		return generalManager;
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
	public ISetManager getSetManager() {
		return setManager;
	}

	@Override
	public IViewManager getViewGLCanvasManager() {
		return viewGLCanvasManager;
	}

	@Override
	public IPathwayManager getPathwayManager() {
		return pathwayManager;
	}

	@Override
	public IPathwayItemManager getPathwayItemManager() {
		return pathwayItemManager;
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
	public GlyphManager getGlyphManager() {
		return glyphManager;
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
	public void addUseCase(IDataDomain useCase) {
		useCaseMap.put(useCase.getDataDomain(), useCase);
	}

	@Override
	public IDataDomain getUseCase(EDataDomain useCaseType) {
		return useCaseMap.get(useCaseType);
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

	@Override
	public Collection<IDataDomain> getAllUseCases() {
		return useCaseMap.values();
	}

	@Override
	public IDataDomain getMasterUseCase() {
		return masterUseCase;
	}

	@Override
	public void setMasterUseCase(EDataDomain dataDomain) {
		this.masterUseCase = getUseCase(dataDomain);
	}

	@Override
	public void setMasterUseCase(IDataDomain useCase) {
		this.masterUseCase = useCase;
	}
}
