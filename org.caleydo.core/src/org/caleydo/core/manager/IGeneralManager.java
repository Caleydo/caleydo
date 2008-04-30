package org.caleydo.core.manager;

import java.util.logging.Logger;

import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.manager.data.IPathwayItemManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;

/**
 * Interface for general manager.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public interface IGeneralManager {

	public static final boolean bEnableMultipelThreads = false;
	
	/**
	 * Used to create a unique networkwide identification numbers.
	 * This defines the lowest numbers to address applications over the network.
	 * 
	 * Schema: [enumeration of components][type][unique network id]
	 * regular ranges: [ >0][01..99][0..9]
	 * 
	 * examples: [12][03][9]
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_Increment
	 * @see org.caleydo.core.manager.IGeneralManager#createNewId(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_WorkspaceOffset = 10;
	
	/**
	 * Used to create a unique networkwide identification numbers.
	 * This defines the lowest numbers to address applications over the network.
	 * 
	 * Schema: [enumeration of components][type][unique network id]
	 * regular ranges: [ >0][01..99][0..9]
	 * 
	 * examples: [12][03][9]
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
		
	public static final int iUniqueId_Menu_Inc = 10000;

	
	/*
	 * List of types for all managed objects
	 * which are addressable via an id.
	 */
	public static final int iUniqueId_TypeOffset_Logger = 10;
	
	public static final int iUniqueId_TypeOffset_GUI_AWT = 31;
	
	public static final int iUniqueId_TypeOffset_GUI_SWT = 32;
	
	public static final int iUniqueId_TypeOffset_GUI_SWT_Window = 33;
	
	public static final int iUniqeuId_TypeOffset_GUI_SWT_Container = 34;
	
	public static final int iUniqueId_TypeOffset_View = 40;
	
	public static final int iUniqueId_TypeOffset_Workspace = 41;
	
	public static final int iUniqueId_TypeOffset_MenuItem = 42;
	
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
	
	public static final int iUniqueId_TypeOffset_GenomeId_ACCESSION = 71;
	
	public static final int iUniqueId_TypeOffset_GenomeId_ENZYME = 72;
	
	public static final int iUniqueId_TypeOffset_GenomeId_METHABOLIT = 73;
	
	public static final int iUniqueId_TypeOffset_GenomeId_NCBI_GENE = 74;
	
	public static final int iUniqueId_TypeOffset_GenomeId_PATHWAY = 75;
	
	public static final int iUniqueId_TypeOffset_GenomeId_MICROARRAY = 76;	
	
	public static final int iUniqueId_TypeOffset_GenomeId_MICROARRAY_EXPRESSION = 77;
	
	public static final int iUniqueId_TypeOffset_EventPublisher = 90;
	
	public static final int iUniqueId_TypeOffset_Memento = 95;
	
	public static final int iUniqueId_TypeOffset_Command_Queue = 98;
	
	public static final int iUniqueId_TypeOffset_Command = 99;
	
	/*
	 * Derived static attributes:
	 */
	public static final int iUniqueId_View = iUniqueId_Increment + 
		iUniqueId_TypeOffset_View * iUniqueId_TypeOffsetMultiplyer;
	
	public static final int iUniqueId_Workspace = iUniqueId_Increment + 
		iUniqueId_TypeOffset_Workspace * iUniqueId_TypeOffsetMultiplyer;
	
	public static final int iUniqueId_MenuItem = iUniqueId_Increment + 
		iUniqueId_TypeOffset_MenuItem * iUniqueId_TypeOffsetMultiplyer;;

	
	public static final String sDelimiter_Paser_DataItemBlock 	= "@";	
	
	public static final String sDelimiter_Parser_DataItems 		= " ";
	
	public static final String sDelimiter_Parser_DataType 		= ";";
	
	public static final String sDelimiter_Parser_DataItems_Tab	= "\t";
	
	/**
	 * Initialize the singleton. 
	 * Call this method before using the singleton.
	 *
	 */
	public void initManager();
	
	public abstract IManager getManagerByObjectType(final ManagerObjectType managerType);
	
	public abstract IManager getManagerByType(final ManagerType managerType);
	
	public abstract IMementoManager getMementoManager();

	public abstract IStorageManager getStorageManager();

	public abstract IVirtualArrayManager getVirtualArrayManager();

	public abstract ISetManager getSetManager();

	public abstract ICommandManager getCommandManager();

	public abstract ISWTGUIManager getSWTGUIManager();
	
	public abstract IViewGLCanvasManager getViewGLCanvasManager();
	
	public abstract IEventPublisher getEventPublisher();
	
	public abstract IXmlParserManager getXmlParserManager();
	
	public abstract IPathwayManager getPathwayManager();
	
	public abstract IPathwayItemManager getPathwayItemManager();

	public abstract IGenomeIdManager getGenomeIdManager();
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 * 
	 * @return unique networkHostId of this host.
	 */
	public abstract int getNetworkPostfix();
	
	/**
	 * Returns the logger.
	 * @return logger
	 */
	public abstract Logger getLogger();
}