package org.caleydo.core.manager;

import java.util.logging.Logger;
import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.IDManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * Interface for general manager.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public interface IGeneralManager
{
	public static final String PREFERENCE_FILE_NAME = "caleydo.prefs";
	public static final String USER_HOME = "user.home";
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME)
			+ "/.caleydo/";

	public static final String sDelimiter_Paser_DataItemBlock = "@";
	public static final String sDelimiter_Parser_DataItems = " ";
	public static final String sDelimiter_Parser_DataType = ";";
	public static final String sDelimiter_Parser_DataItems_Tab = "\t";

	public void init(boolean bIsStandalone);

	/**
	 * Init method for external GUI embedding (e.g. RCP)
	 */
	public void init(boolean bIsStandalone, IGUIBridge externalGUIBridge);


	public IMementoManager getMementoManager();

	public IStorageManager getStorageManager();

	public ISetManager getSetManager();

	// public  ISelectionManager getSelectionManager();
	public  ICommandManager getCommandManager();

	public  ISWTGUIManager getSWTGUIManager();

	public  IViewGLCanvasManager getViewGLCanvasManager();

	public  IEventPublisher getEventPublisher();

	public  IXmlParserManager getXmlParserManager();

	public  IPathwayManager getPathwayManager();

	public  IPathwayItemManager getPathwayItemManager();

	public  IIDMappingManager getIDMappingManager();

	public  IGlyphManager getGlyphManager();

	public  IDManager getIDManager();

	// public  IVirtualArrayManager getVirtualArrayManager();

	/**
	 * Returns the logger.
	 * 
	 * @return logger
	 */
	public  Logger getLogger();

	/**
	 * Returns the preference store where Caleydo stores its preferences. The
	 * object can store and restore preferences to/from a predefined file.
	 */
	public PreferenceStore getPreferenceStore();

	/**
	 * Returns whether the application runs as standalone test GUI or embedded
	 * in RCP
	 */
	public boolean isStandalone();

	public IGUIBridge getGUIBridge();
}