package org.caleydo.core.manager;

import java.util.logging.Logger;

import org.caleydo.core.manager.data.ISelectionManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;

/**
 * Interface for general manager.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public interface IGeneralManager
{

	/**
	 * Used to create a unique networkwide identification numbers. This defines
	 * the lowest numbers to address applications over the network. Schema:
	 * [enumeration of components][type][unique network id] regular ranges: [
	 * >0][01..99][0..9] examples: [12][03][9]
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_Increment
	 * @see org.caleydo.core.manager.IGeneralManager#createNewId(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_WorkspaceOffset = 10;

	/**
	 * Used to create a unique networkwide identification numbers. This defines
	 * the lowest numbers to address applications over the network. Schema:
	 * [enumeration of components][type][unique network id] regular ranges: [
	 * >0][01..99][0..9] examples: [12][03][9]
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_Increment
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 * @see org.caleydo.core.manager.IGeneralManager#createNewId(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_TypeOffsetMultiplyer = 10;

	/**
	 * Increment from one Id to the next unique id.
	 * 
	 * @see org.caleydo.core.manager.GeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.GeneralManager#iUniqueId_Workspace
	 * @see org.caleydo.core.manager.GeneralManager#createNewId(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_Increment = 1000;

	/*
	 * List of types for all managed objects which are addressable via an id.
	 */

	// FIXME: put ID range to EManagerObjectType in enum parameter list
	// Also look at EManagerType IDs - don't know where we should hold ID
	public static final int iUniqueId_TypeOffset_Logger = 10;

	public static final int iUniqueId_TypeOffset_GUI_AWT = 31;

	public static final int iUniqueId_TypeOffset_GUI_SWT = 32;

	public static final int iUniqueId_TypeOffset_GUI_SWT_Window = 33;

	public static final int iUniqeuId_TypeOffset_GUI_SWT_Container = 34;

	public static final int iUniqueId_TypeOffset_View = 40;

	public static final int iUniqueID_TypeOffset_PickingID = 43;

	public static final int iUniqueID_TypeOffset_Selection = 44;

	public static final int iUniqueId_TypeOffset_Set = 51;

	public static final int iUniqueId_TypeOffset_VirtualArray = 52;

	public static final int iUniqueId_TypeOffset_Storage = 53;

	public static final int iUniqueId_TypeOffset_InteractiveSelection = 54;

	public static final int iUniqueId_TypeOffset_Pathways_Pathway = 60;

	public static final int iUniqueId_TypeOffset_Pathways_Object = 61;

	public static final int iUniqueId_TypeOffset_Pathways_Edge = 62;

	public static final int iUniqueId_TypeOffset_Pathways_Vertex = 63;

	public static final int iUniqueId_TypeOffset_GenomeId = 70;

	public static final int iUniqueId_TypeOffset_GenomeId_PATHWAY = 75;

	public static final int iUniqueId_TypeOffset_EventPublisher = 90;

	public static final int iUniqueId_TypeOffset_Memento = 95;

	public static final int iUniqueId_TypeOffset_Command_Queue = 98;

	public static final int iUniqueId_TypeOffset_Command = 99;

	/*
	 * Derived static attributes:
	 */
	public static final int iUniqueId_View = iUniqueId_Increment + iUniqueId_TypeOffset_View
			* iUniqueId_TypeOffsetMultiplyer;

	public static final String sDelimiter_Paser_DataItemBlock = "@";

	public static final String sDelimiter_Parser_DataItems = " ";

	public static final String sDelimiter_Parser_DataType = ";";

	public static final String sDelimiter_Parser_DataItems_Tab = "\t";

	/**
	 * Initialize the singleton. Call this method before using the singleton.
	 */
	public void initManager();

	public abstract IMementoManager getMementoManager();

	public abstract IStorageManager getStorageManager();

	// public abstract IVirtualArrayManager getVirtualArrayManager();

	public abstract ISetManager getSetManager();

	public abstract ISelectionManager getSelectionManager();

	public abstract ICommandManager getCommandManager();

	public abstract ISWTGUIManager getSWTGUIManager();

	public abstract IViewGLCanvasManager getViewGLCanvasManager();

	public abstract IEventPublisher getEventPublisher();

	public abstract IXmlParserManager getXmlParserManager();

	public abstract IPathwayManager getPathwayManager();

	public abstract IPathwayItemManager getPathwayItemManager();

	public abstract IGenomeIdManager getGenomeIdManager();

	public abstract IGlyphManager getGlyphManager();

	/**
	 * Returns the logger.
	 * 
	 * @return logger
	 */
	public abstract Logger getLogger();
}