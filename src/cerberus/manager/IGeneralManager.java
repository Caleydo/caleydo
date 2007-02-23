package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
//import prometheus.net.dwt.swing.mdi.DDesktopPane;

public interface IGeneralManager {

	public static final boolean bEnableMultipelThreads = false;
	
	/**
	 * Used to create a unique networkwide identification numbers.
	 * This defines the lowest numbers to address applications over the network.
	 * 
	 * Schema: [enumeration of components][type][unique network id]
	 * regular ranges: [ >0][01..99][0..9]
	 * 
	 * exampels: [12][03][9]
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see cerberus.manager.IGeneralManager#iUniqueId_Increment
	 * @see cerberus.manager.IGeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_WorkspaceOffset = 10;
	
	/**
	 * Used to create a unique networkwide identification numbers.
	 * This defines the lowest numbers to address applications over the network.
	 * 
	 * Schema: [enumeration of components][type][unique network id]
	 * regular ranges: [ >0][01..99][0..9]
	 * 
	 * exampels: [12][03][9]
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_Increment
	 * @see cerberus.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 * @see cerberus.manager.IGeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
	 */
	public static final int iUniqueId_TypeOffsetMultiplyer = 10;
	
	/**
	 * Increment from one Id to the next unique id.
	 * 
	 * @see cerberus.manager.GeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see cerberus.manager.GeneralManager#iUniqueId_Workspace
	 * @see cerberus.manager.GeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
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

	/** 
	 * @deprecated use Set, Virtual Array or Storage 
	 */
	public static final int iUniqueId_TypeOffset_Collection = 50;

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
	
	public static final int iUniqueId_TypeOffset_EventPublisher = 90;
	
	public static final int iUniqueId_TypeOffset_Memento = 95;
	
	public static final int iUniqueId_TypeOffset_Command_Queue = 98;
	
	public static final int iUniqueId_TypeOffset_Command = 99;
	
	
	/**
	 * Deprecated ID ranges
	 */
	
	public static final int iUniqueId_TypeOffset_GuiComponent = 30;
	
	public static final int iUniqueId_TypeOffset_GUI_AWT_Menu = 39;
	
	
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
	 * Tests, if a cairtain iItemId is handled by the manager.
	 * 
	 * @param iItemId to identify an item that is tested
	 * @return TRUE if iItemId exists
	 */
	public boolean hasItem(final int iItemId);

	/**
	 * Return the item bound to the iItemId or null if the id is not 
	 * bound to an item.
	 * 
	 * @param iItemId uniqu id used for lookup
	 * @return object bound to iItemId
	 */
	public Object getItem( final int iItemId);
	
	/**
	 * Get the number of current handled items.
	 * 
	 * @return number of items
	 */
	public int size();

	/**
	 * Type of the manager
	 * 
	 * @return type of the manager
	 */
	public ManagerType getManagerType();
	
	/**
	 * Returns the reference to the prometheus.app.SingeltonManager.
	 * 
	 * Note: Do not forget to set the reference to the SingeltonManager inside the constructor.
	 * 
	 * @return reference to SingeltonManager
	 */
	public IGeneralManager getGeneralManager();
	
	/**
	 * Get the Singleton obejct.
	 * 
	 * @return ISingelton object
	 */
	public ISingelton getSingelton();
	
	/**
	 * Registers one Id and links it to the reference.
	 * 
	 * @param registerItem Object to be registered
	 * @param iItemId unique Id
	 * @param type defines type, can also be null if type is not known
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type );
	
	
	/**
	 * Unregisters an item using it's Id.
	 * 
	 * @param iItemId unique Id
	 * @param type defines type, can also be null if type is not known
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean unregisterItem( final int iItemId, 
			final ManagerObjectType type  );
	
	/**
	 * Create a new unique Id.
	 * 
	 * @return new unique Id
	 * @param setNewBaseType type of object the id shall be created for
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see cerberus.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	public int createNewId( final ManagerObjectType setNewBaseType );

	/**
	 * Set the current Id, what is incremented once the next time createNewId() is called.
	 * 
	 * Attention: this methode must be called from a synchronized block on the actual manager!
	 * 
	 * @param setNewBaseType test if manager may create such an id
	 * @param iCurrentId set the new current Id
	 * @return ture if the new current Id was valid, which is the case if it is larger than the current NewId!
	 */
	public boolean setCreateNewId(ManagerType setNewBaseType, final int iCurrentId );
	
	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType);
		
	/**
	 * Remove all data and stop all threads.
	 *
	 */
	public void destroyOnExit();
	
}