package org.caleydo.core.manager.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.bridge.gui.standalone.SWTStandaloneBridge;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.id.IDManager;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.memento.MementoManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayItemManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.util.wii.WiiRemote;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * General manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GeneralManager
	implements IGeneralManager {

	/**
	 * General manager as a singleton
	 */
	private static IGeneralManager generalManager;

	/**
	 * Preferences store enables storing and restoring of application specific preference data.
	 */
	private PreferenceStore preferenceStore;

	/**
	 * Determines whether Caleydo runs as standalone test GUI or in RCP mode.
	 */
	private boolean bIsStandalone = true;

	private boolean bAllManagersInitialized = false;

	private IStorageManager storageManager;
	private IMementoManager mementoManager;
	// protected IVirtualArrayManager virtualArrayManager;
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
	private IDManager IDManager;

	private Logger logger;

	private IGUIBridge guiBridge;

	private ResourceLoader resourceLoader;

	private WiiRemote wiiRemote;

	private boolean bIsWiiMode = false;

	@Override
	public void init(boolean bIsStandalone, IGUIBridge externalGUIBridge) {
		this.init(bIsStandalone);

		this.guiBridge = externalGUIBridge;
	}

	@Override
	public void init(boolean bIsStandalone) {
		this.bIsStandalone = bIsStandalone;

		if (bAllManagersInitialized)
			throw new IllegalStateException("Tried to initialize managers multiple times. Abort.");

		bAllManagersInitialized = true;

		storageManager = new StorageManager();
		// virtualArrayManager = new VirtualArrayManager(this, 4);
		setManager = new SetManager();
		// connectedElementRepManager = new SelectionManager();
		mementoManager = new MementoManager();
		commandManager = new CommandManager();
		viewGLCanvasManager = new ViewManager();
		sWTGUIManager = new SWTGUIManager();
		eventPublisher = new EventPublisher();
		genomeIdManager = new IDMappingManager();
		pathwayManager = new PathwayManager();
		// serializationInputTest();
		pathwayItemManager = new PathwayItemManager();
		xmlParserManager = new XmlParserManager();
		glyphManager = new GlyphManager();
		IDManager = new IDManager();

		xmlParserManager.initHandlers();

		initLogger();
		initPreferences();

		resourceLoader = new ResourceLoader();

		// Init Standalone GUI Bridge if in standalone mode
		if (bIsStandalone) {
			guiBridge = new SWTStandaloneBridge();
		}

		wiiRemote = new WiiRemote();
		if (GeneralManager.get().isWiiModeActive()) {
			wiiRemote.connect();
		}
	}

	/**
	 * Returns the general method as a singleton object. 
	 * When first called the general manager is created (lazy).
	 * @return singleton GeneralManager instance
	 */
	public static IGeneralManager get() {
		if (generalManager == null) {
			generalManager = new GeneralManager();
		}
		return generalManager;
	}

	private void initPreferences() {
		preferenceStore = new PreferenceStore(IGeneralManager.CALEYDO_HOME_PATH + PREFERENCE_FILE_NAME);

		try {
			preferenceStore.load();
		}
		catch (IOException e) {
			// Create .caleydo folder
			if (!new File(IGeneralManager.CALEYDO_HOME_PATH).exists()) {
				if (!new File(IGeneralManager.CALEYDO_HOME_PATH).mkdir())
					throw new IllegalStateException(
						"Unable to create home folder .caleydo. Check user permissions!");
			}

			// Create log folder in .caleydo
			if (!new File(IGeneralManager.CALEYDO_HOME_PATH + "logs").mkdirs())
				throw new IllegalStateException(
					"Unable to create log folder .caleydo/log. Check user permissions!");

			logger.log(Level.INFO, "Create new preference store at " + IGeneralManager.CALEYDO_HOME_PATH
				+ PREFERENCE_FILE_NAME);

			try {
				preferenceStore.setValue(PreferenceConstants.FIRST_START, true);
				preferenceStore.setValue(PreferenceConstants.PATHWAY_DATA_OK, false);
				preferenceStore.setValue(PreferenceConstants.LOAD_PATHWAY_DATA, true);
				preferenceStore.setValue(PreferenceConstants.USE_PROXY, false);
				preferenceStore.save();
			}
			catch (IOException e1) {
				throw new IllegalStateException("Unable to save preference file.");
			}
		}

		if (preferenceStore.getBoolean(PreferenceConstants.USE_PROXY)) {
			System.setProperty("network.proxy_host", preferenceStore
				.getString(PreferenceConstants.PROXY_SERVER));
			System.setProperty("network.proxy_port", preferenceStore
				.getString(PreferenceConstants.PROXY_PORT));
		}
	}

	/**
	 * Initialize the Java internal logger
	 */
	private void initLogger() {
		logger = Logger.getLogger("Caleydo Log");
	}

	@Override
	public final Logger getLogger() {
		return logger;
	}

	@Override
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	@Override
	public IMementoManager getMementoManager() {
		return mementoManager;
	}

	@Override
	public IStorageManager getStorageManager() {
		return storageManager;
	}

	// public IVirtualArrayManager getVirtualArrayManager() {
	// return virtualArrayManager;
	// }
	// public ISelectionManager getSelectionManager()
	// {
	// return connectedElementRepManager;
	// }

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
		return preferenceStore;
	}

	@Override
	public IDManager getIDManager() {
		return IDManager;
	}

	@Override
	public boolean isStandalone() {
		return bIsStandalone;
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
}
