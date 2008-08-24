package org.caleydo.core.manager;

import java.util.logging.Logger;
import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.IDManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
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
	public static final String CALEYDO_HOME_PATH = System.getProperty(USER_HOME) + "/.caleydo/";
	
	public static final String sDelimiter_Paser_DataItemBlock = "@";
	public static final String sDelimiter_Parser_DataItems = " ";
	public static final String sDelimiter_Parser_DataType = ";";
	public static final String sDelimiter_Parser_DataItems_Tab = "\t";

	public void init(boolean bIsStandalone);
	
	/**
	 * Init method for external GUI embedding (e.g. RCP)
	 */
	public void init(boolean bIsStandalone, IGUIBridge externalGUIBridge);

	public abstract IMementoManager getMementoManager();
	public abstract IStorageManager getStorageManager();
	public abstract ISetManager getSetManager();
//	public abstract ISelectionManager getSelectionManager();
	public abstract ICommandManager getCommandManager();
	public abstract ISWTGUIManager getSWTGUIManager();
	public abstract IViewGLCanvasManager getViewGLCanvasManager();
	public abstract IEventPublisher getEventPublisher();
	public abstract IXmlParserManager getXmlParserManager();
	public abstract IPathwayManager getPathwayManager();
	public abstract IPathwayItemManager getPathwayItemManager();
	public abstract IGenomeIdManager getGenomeIdManager();
	public abstract IGlyphManager getGlyphManager();
	public abstract IDManager getIDManager();
	// public abstract IVirtualArrayManager getVirtualArrayManager();

	/**
	 * Returns the logger.
	 * 
	 * @return logger
	 */
	public abstract Logger getLogger();
	
	/**
	 * Returns the preference store where Caleydo stores its preferences.
	 * The object can store and restore preferences to/from a predefined file.
	 */
	public PreferenceStore getPreferenceStore();
	
	/**
	 * Returns whether the application runs as standalone test GUI or embedded in RCP
	 */
	public boolean isStandalone();
	
	public IGUIBridge getGUIBridge();
}