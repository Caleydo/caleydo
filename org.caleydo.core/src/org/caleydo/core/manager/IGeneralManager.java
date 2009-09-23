package org.caleydo.core.manager;

import java.io.File;
import java.util.Collection;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.IDManager;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.genetic.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.util.wii.WiiRemote;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * Interface for general manager.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public interface IGeneralManager {

	/**
	 * This is the current version of Caleydo. The value must be the same as specified in the plugin/bundle.
	 * We need to access the version before the workbench is started. Therefore we have to set it hardcoded at
	 * this point.
	 */
	public static final String VERSION = "1.3";

	public static final String PLUGIN_ID = "org.caleydo.core";

	public static final String PREFERENCE_FILE_NAME = "caleydo.prefs";
	public static final String USER_HOME = "user.home";
	public static final String CALEYDO_HOME_PATH =
		System.getProperty(USER_HOME) + File.separator + ".caleydo" + File.separator;

	public static final String sDelimiter_Paser_DataItemBlock = "@";
	public static final String sDelimiter_Parser_DataItems = " ";
	public static final String sDelimiter_Parser_DataType = ";";
	public static final String sDelimiter_Parser_DataItems_Tab = "\t";

	public void init();

	/**
	 * Init method for external GUI embedding (e.g. RCP)
	 */
	public void init(IGUIBridge externalGUIBridge);

	public IStorageManager getStorageManager();

	public ISetManager getSetManager();

	public ICommandManager getCommandManager();

	public ISWTGUIManager getSWTGUIManager();

	public IViewManager getViewGLCanvasManager();

	public IEventPublisher getEventPublisher();

	public IXmlParserManager getXmlParserManager();

	public IPathwayManager getPathwayManager();

	public IPathwayItemManager getPathwayItemManager();

	public IIDMappingManager getIDMappingManager();

	public GlyphManager getGlyphManager();

	public IDManager getIDManager();

	/**
	 * Returns the logger.
	 * 
	 * @return logger
	 */
	public ILog getLogger();

	/**
	 * Resource loader that is responsible for loading images, textures and data files in the Caleydo
	 * framework. DO NOT LOAD YOUR FILES ON YOUR OWN!
	 * 
	 * @return resource loader
	 */
	public ResourceLoader getResourceLoader();

	/**
	 * Returns the preference store where Caleydo stores its preferences. The object can store and restore
	 * preferences to/from a predefined file.
	 */
	public PreferenceStore getPreferenceStore();

	public IGUIBridge getGUIBridge();

	public boolean isWiiModeActive();

	public WiiRemote getWiiRemote();

	public TrackDataProvider getTrackDataProvider();

	/**
	 * Returns the current use case. The use case determines which views are showing what kind of data and
	 * which data set is currently in use.
	 */
	public IUseCase getUseCase(EDataDomain useCaseType);

	/**
	 * Returns all use cases that are currently registered.
	 * 
	 * @return
	 */
	public Collection<IUseCase> getAllUseCases();

	/**
	 * Set a different use case. The use case changes the behavior of the views and its loaded data.
	 * 
	 * @param useCase
	 */
	public void addUseCase(IUseCase useCase);

	/**
	 * Obtains the {@link IGroupwareManager} responsible for communication purposes with other calyedo
	 * application
	 * 
	 * @return the {@link IGroupwareManager} of this caleydo application
	 */
	public IGroupwareManager getGroupwareManager();

	/**
	 * Sets the {@link IGroupwareManager} responsible for communication purposes with other calyedo
	 * application
	 * 
	 * @param groupwareManager
	 *            the environment specific {@link IGroupwareManager} to use
	 */
	public void setGroupwareManager(IGroupwareManager groupwareManager);

	/**
	 * Obtains the {@link SerializationManager} responsible for xml-serialization related tasks
	 * 
	 * @return the {@link SerializationManager} of this caleydo application
	 */
	public SerializationManager getSerializationManager();

}