package org.caleydo.core.manager.general;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.xml.IMementoXML;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.manager.data.IPathwayItemManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.data.genome.DynamicGenomeIdManager;
import org.caleydo.core.manager.data.pathway.PathwayItemManager;
import org.caleydo.core.manager.data.pathway.PathwayManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.data.virtualarray.VirtualArrayManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.logger.ConsoleLogger;
import org.caleydo.core.manager.memento.MementoManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.manager.view.ViewGLCanvasManager;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


/**
 * Overall manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class GeneralManager 
implements IGeneralManager
{
	private boolean bAllManagersInitialized = false;

	private ArrayList<IManager> llAllManagerObjects;

	protected IStorageManager refStorageManager;
	
	protected IMementoManager refMementoManager;
	
	protected IVirtualArrayManager refVirtualArrayManager;
	
	protected ISetManager refSetManager;
	
	protected ICommandManager refCommandManager;
	
	protected ILoggerManager generalManager;
	
	protected ISWTGUIManager refSWTGUIManager;
	
	protected IViewGLCanvasManager refViewGLCanvasManager;
	
	protected IPathwayManager refPathwayManager;
	
	protected IPathwayItemManager refPathwayItemManager;
	
	protected IEventPublisher refEventPublisher;
	
	protected IXmlParserManager refXmlParserManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	protected ILoggerManager refLoggerManager;
	
	/**
	 * Unique Id per each application over the network.
	 * Used to identify and create Id's unique for distributed applications. 
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	private int iNetworkApplicationIdPostfix = 0;

	/**
	 * Constructor.
	 */
	public GeneralManager()
	{
		/**
		 * The Network postfix must be unique inside the network for 
		 * distributed Caleydo applications. 
		 * For stand alone Caleydo applications this id must match the XML file.
		 */
		setNetworkPostfix(0);
		
		llAllManagerObjects = new ArrayList<IManager>();
		
		initManager();
	}

	/**
	 * Must be called right after the constructor before using this class.
	 * Initializes all manager objects.
	 *
	 */
	public void initManager()
	{
		if (bAllManagersInitialized)
		{
			throw new CaleydoRuntimeException(
					"initAll() was called at least twice!");
		}
		
		bAllManagersInitialized = true;

		refLoggerManager = new ConsoleLogger(this);
		refLoggerManager.setSystemLogLevel( 
				// ILoggerManager.LoggerType.FULL );
				ILoggerManager.LoggerType.VERBOSE );

		refStorageManager = new StorageManager(this, 4);
		refVirtualArrayManager = new VirtualArrayManager(this, 4);
		refSetManager = new SetManager(this, 4);
		refMementoManager = new MementoManager(this);
		refCommandManager = new CommandManager(this);
		refViewGLCanvasManager = new ViewGLCanvasManager(this);
		refSWTGUIManager = new SWTGUIManager(this);
		refEventPublisher = new EventPublisher(this);
		refGenomeIdManager = new DynamicGenomeIdManager(this);
		refPathwayManager = new PathwayManager(this);
		refPathwayItemManager = new PathwayItemManager(this);
		refXmlParserManager = new XmlParserManager(this);
		
		/**
		 * Insert all Manager objects handling registered objects to 
		 * the LinkedList
		 */
		llAllManagerObjects.add(refSetManager);
		llAllManagerObjects.add(refVirtualArrayManager);
		llAllManagerObjects.add(refStorageManager);
		llAllManagerObjects.add(refPathwayManager);
		llAllManagerObjects.add(refPathwayItemManager);
		llAllManagerObjects.add(refGenomeIdManager);
		llAllManagerObjects.add(refEventPublisher);
		llAllManagerObjects.add(refViewGLCanvasManager);
		llAllManagerObjects.add(refSWTGUIManager);
		llAllManagerObjects.add(refCommandManager);
		llAllManagerObjects.add(refMementoManager);
		llAllManagerObjects.add(refLoggerManager);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(final int iItemId)
	{
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			if ( iter.next().hasItem(iItemId) ) 
				return true;
		} // while ( iter.hasNext() ) 

		return false;
	}

	/**
	 * @see org.caleydo.core.manager.IGeneralManager#hasItem(int)
	 * 
	 * @param iItemId unique Id used for lookup
	 * @return Object bound to Id or null, if id was not found.
	 */
	public Object getItem(final int iItemId)
	{
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IManager buffer = iter.next();
			
			if ( buffer.hasItem(iItemId) ) 
			{
				return buffer.getItem(iItemId);
			}
			
		} // while ( iter.hasNext() ) 

		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#size()
	 */
	public int size()
	{	
		return (refSetManager.size() + refStorageManager.size()
				+ refVirtualArrayManager.size() + refMementoManager.size()
				+ refViewGLCanvasManager.size() + refSWTGUIManager
				.size());
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.manager.singelton.GeneralManagerSingelton#getCommandManager()
	 */
	public ICommandManager getCommandManager()
	{
		return refCommandManager;
	}

	//	/**
	//	 * Create a new Id using the ManagerObjectType set with 
	//	 */
	//	public final int createNewId() {
	//		return this.createNewId( setCurrentType );
	//	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.singelton.SingeltonManager#createNewId(org.caleydo.core.data.manager.BaseManagerType)
	 */
	public final int createId(final ManagerObjectType type)
	{

		assert type != null : "registerItem called with type == null!";
		
		IManager buffer = this.getManagerByObjectType( type );		
		assert buffer != null : "createNewId type does not address manager!";
		
		return buffer.createId(type);
	}

	public boolean unregisterItem(final int iItemId,
			final ManagerObjectType type)
	{
		assert type != null : "registerItem called with type == null!";
		
		IManager buffer = this.getManagerByObjectType( type );
		
		if ( buffer != null ) 
		{
			return buffer.unregisterItem(iItemId, type);
		}
		
		return false;
	}

	public boolean registerItem(final Object registerItem, final int iItemId,
			final ManagerObjectType type)
	{
		assert type != null : "registerItem called with type == null!";
		
		IManager buffer = this.getManagerByObjectType( type );
		
		assert buffer != null : "registerItem type does not address manager!";
		
		return buffer.registerItem( registerItem, iItemId, type);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.singelton.SingeltonManager#createNewItem(org.caleydo.core.data.manager.BaseManagerType, Stringt)
	 */
	public Object createNewItem(final ManagerObjectType createNewType,
			final String sNewTypeDetails)
	{

		switch (createNewType.getGroupType())
		{
		case MEMENTO:
			//return refMementoManager.c();
			assert false : "not implemented";
		case DATA_VIRTUAL_ARRAY:
			return refVirtualArrayManager.createVirtualArray(createNewType);
//		case DATA_SET:
//			return refSetManager.createSet( SetDataType.SET_LINEAR );
		case DATA_STORAGE:
			return refStorageManager.createStorage(createNewType);
//		case VIEW:
//			if (createNewType == ManagerObjectType.VIEW_NEW_FRAME)
//			{
//				return refViewGLCanvasManager.createWorkspace(createNewType,
//						sNewTypeDetails);
//			}
//			return refViewGLCanvasManager.createCanvas(createNewType,
//					sNewTypeDetails);
		case COMMAND:
			assert false : "update to new command structure!";		    
//			return refCommandManager.createCommand(sNewTypeDetails);
			return null;
		case VIEW_DISTRIBUTE_GUI:
			assert false : "removed from package!";
			return null;
			//return refDComponentManager.createSet( DGuiComponentType.valueOf(sNewTypeDetails) );

		default:
			throw new CaleydoRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ createNewType.toString());
		} // end switch
	}

	public void callbackForParser(final ManagerObjectType type,
			final String tag_causes_callback, final String details,
			final ISaxParserHandler refSaxHandler)
	{

		assert type != null : "type is null!";

		System.out.println("OneForAllManager.callbackForParser() callback in OneForAllManager");

		switch (type.getGroupType())
		{
		case MEMENTO:
			//return refMementoManager.c();
			assert false : "not implemented";
		case DATA_VIRTUAL_ARRAY:
		{
			IMementoXML selectionBuffer = refVirtualArrayManager
					.createVirtualArray(type);

			selectionBuffer.setMementoXML_usingHandler(refSaxHandler);
			return;
		}
		case DATA_SET:
		{
//			ISet setBuffer = this.refSetManager.createSet( SetDataType.SET_LINEAR );
//			setBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case DATA_STORAGE:
			IStorage storageBuffer = refStorageManager.createStorage(type);

			storageBuffer.setMementoXML_usingHandler(refSaxHandler);
			return;

//		case VIEW:
//
//			if (type == ManagerObjectType.VIEW_NEW_FRAME)
//			{
//				Object setFrame = refViewCanvasManager.createCanvas(type,
//						details);
//
//				//setFrame.setMementoXML_usingHandler( refSaxHandler );
//				return;
//			} else
//			{
//				IViewCanvas setCanvas = (IViewCanvas) refViewCanvasManager
//						.createCanvas(type, details);
//
//				setCanvas.setMementoXML_usingHandler(refSaxHandler);
//				return;
//			}

		default:
			throw new CaleydoRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ type.name());
		} // end switch ( type.getGroupType() )

	}
	
	public IManager getManagerByObjectType(final ManagerObjectType managerType) 
	{
		return getManagerByType(managerType.getGroupType());
	}
	
	public IManager getManagerByType(final ManagerType managerType)
	{
		assert managerType != null : "type is null!";

		switch (managerType)
		{
		case MEMENTO:
			return refMementoManager;
		case DATA_VIRTUAL_ARRAY:
			return refVirtualArrayManager;
		case DATA_SET:
			return refSetManager;
		case DATA_STORAGE:
			return refStorageManager;
		case VIEW:
			return refViewGLCanvasManager;
		case COMMAND:
			return refCommandManager;
		case VIEW_GUI_SWT:
			return refSWTGUIManager;
		case EVENT_PUBLISHER:
			return refEventPublisher;
		case DATA_GENOME_ID:
			return refGenomeIdManager;
			
		default:
			throw new CaleydoRuntimeException(
					"Error in OneForAllManager.getManagerByObjectType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}
	
	public void destroyOnExit() {
		
		generalManager.logMsg("OneForAllManager.destroyOnExit()", LoggerType.STATUS );
		
		this.refViewGLCanvasManager.destroyOnExit();
		
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IManager buffer = iter.next();
			
			if ( buffer != null ) {
				buffer.destroyOnExit();
			}
			
		} // while ( iter.hasNext() ) 
		
		generalManager.logMsg("OneForAllManager.destroyOnExit()  ...[DONE]", LoggerType.STATUS);
	}

	public boolean setCreateNewId(ManagerType setNewBaseType, int iCurrentId) {

		IManager refSecialManager = getManagerByType( setNewBaseType );
		
		if ( ! refSecialManager.setCreateNewId(setNewBaseType, iCurrentId) ) {
			generalManager.logMsg("setCreateNewId failed!", LoggerType.MINOR_ERROR );
			return false;
		}
		
		return true;
	}

	public int createId(CommandQueueSaxType setNewBaseType) {

		// TODO Auto-generated method stub
		return 0;
	}

	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	public void setNetworkPostfix( int iSetNetworkPrefix ) {
		if (( iSetNetworkPrefix < IGeneralManager.iUniqueId_WorkspaceOffset) && 
				( iSetNetworkPrefix >= 0)) { 
			iNetworkApplicationIdPostfix = iSetNetworkPrefix;
			return;
		}
		throw new RuntimeException("SIngeltonManager.setNetworkPostfix() exceeded range [0.." +
				IGeneralManager.iUniqueId_WorkspaceOffset + "] ");
	}

	public final void logMsg( final String info, final LoggerType logLevel) {
		refLoggerManager.logMsg( info, logLevel );		
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getMementoManager()
	 */
	public IMementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/* (non-Javadoc)	
	 * @see org.caleydo.core.manager.singelton.Singelton#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return refStorageManager;
	}
		
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getVirtualArrayManager()
	 */
	public IVirtualArrayManager getVirtualArrayManager() {
		return refVirtualArrayManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getSetManager()
	 */
	public ISetManager getSetManager() {
		return refSetManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager() {
		return refViewGLCanvasManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISingelton#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager() {
		
		return refPathwayManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISingelton#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager() {
		
		return refPathwayItemManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getEventManager()
	 */
	public IEventPublisher getEventPublisher() {
		return refEventPublisher;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getLoggerManager()
	 */
	public ILoggerManager getLoggerManager() {
		return this.generalManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.singelton.Singelton#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager() {
		return this.refXmlParserManager;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getGenomeIdManager()
	 */
	public IGenomeIdManager getGenomeIdManager() {
		return this.refGenomeIdManager;
	}
}

