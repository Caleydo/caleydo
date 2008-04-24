package org.caleydo.core.manager.singleton;

import java.util.Iterator;
import java.util.LinkedList;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.xml.IMementoXML;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.ISingleton;
import org.caleydo.core.manager.IViewGLCanvasManager;
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
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.manager.view.ViewJoglManager;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


/**
 * Overall manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class OneForAllManager 
implements IGeneralManagerSingleton
{

	/**
	 * Defines, if initAll() was called.
	 * 
	 * @see org.caleydo.core.manager.singleton.OneForAllManager#initAll()
	 */
	private boolean bAllManagersInitialized = false;

	private LinkedList <IGeneralManager> llAllManagerObjects;
	
	protected SingletonManager refSingeltonManager;

	protected ISetManager refSetManager;

	protected IStorageManager refStorageManager;

	protected IVirtualArrayManager refVirtualArrayManager;

	protected IMementoManager refMementoManager;

	protected ICommandManager refCommandManager;

	protected ILoggerManager refLoggerManager;
	
	protected IGenomeIdManager refGenomeIdManager;

	protected IViewGLCanvasManager refViewGLCanvasManager;

	protected ISWTGUIManager refSWTGUIManager;

	protected IPathwayManager refPathwayManager;
	
	protected IPathwayItemManager refPathwayItemManager;
	
	protected IEventPublisher refEventPublisher;

	/**
	 * Used to create a new item by a Fabrik.
	 * used by org.caleydo.core.data.manager.OneForAllManager#createNewId(ManagerObjectType)
	 * 
	 * @see org.caleydo.core.manager.singleton.OneForAllManager#createNewId(ManagerObjectType)
	 */
	protected ManagerObjectType setCurrentType = ManagerObjectType.ALL_IN_ONE;

	/**
	 * Call initAll() before using this class!
	 * 
	 * @see org.caleydo.core.data.manager.singelton.OneForAllManager#initAll()
	 */
	public OneForAllManager(final SingletonManager sef_SingeltonManager)
	{

		if (refSingeltonManager == null)
		{
			refSingeltonManager = new SingletonManager();
			refSingeltonManager.initManager();
		} else
		{
			refSingeltonManager = sef_SingeltonManager;
		}

		/**
		 * The Network psotfix must be unique inside the network for 
		 * destributed Caleydo applications. 
		 * For stand alone Caleydo applications this id must match the XML file.
		 */
		refSingeltonManager.setNetworkPostfix( 0 );
		
		llAllManagerObjects = new LinkedList <IGeneralManager> ();
		
		//initAll();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getSingelton()
	 */
	public final ISingleton getSingleton()
	{
		return refSingeltonManager;
	}

	/**
	 * Must be called right after the constructor before using this class.
	 * Initialzes all Mangeger obejcts.
	 *
	 */
	public void initAll()
	{

		if (bAllManagersInitialized)
		{
			throw new CaleydoRuntimeException(
					"initAll() was called at least twice!");
		}
		bAllManagersInitialized = true;

		/** int logger first! */
		refLoggerManager = new ConsoleLogger(this);
		refLoggerManager.setSystemLogLevel( 
				// ILoggerManager.LoggerType.FULL );
				ILoggerManager.LoggerType.VERBOSE );
		refSingeltonManager.setLoggerManager(refLoggerManager);
		/* end init logger */
		
		refStorageManager = new StorageManager(this, 4);
		refVirtualArrayManager = new VirtualArrayManager(this, 4);
		refSetManager = new SetManager(this, 4);
		refMementoManager = new MementoManager(this);
		refCommandManager = new CommandManager(this);
		refViewGLCanvasManager = new ViewJoglManager(this);
		refSWTGUIManager = new SWTGUIManager(this);
		refEventPublisher = new EventPublisher(this);
		refGenomeIdManager = new DynamicGenomeIdManager(this);
		refPathwayManager = new PathwayManager(this);
		refPathwayItemManager = new PathwayItemManager(this);
		
		/**
		 * Insert all Manager objects handling registered objects to 
		 * the LinkedList
		 */
		llAllManagerObjects.add( refSetManager );
		llAllManagerObjects.add( refVirtualArrayManager );
		llAllManagerObjects.add( refStorageManager );
		
		llAllManagerObjects.add( refPathwayManager );
		llAllManagerObjects.add( refPathwayItemManager );
		llAllManagerObjects.add( refGenomeIdManager );
		llAllManagerObjects.add( refEventPublisher );
		llAllManagerObjects.add( refViewGLCanvasManager );
		llAllManagerObjects.add( refSWTGUIManager );
		llAllManagerObjects.add( refCommandManager );
		llAllManagerObjects.add( refMementoManager );
		
		/**
		 * Register managers to singleton ...
		 */	
		refSingeltonManager.setCommandManager(refCommandManager);
		refSingeltonManager.setVirtualArrayManager(refVirtualArrayManager);
		refSingeltonManager.setSetManager(refSetManager);
		refSingeltonManager.setStorageManager(refStorageManager);
		refSingeltonManager.setViewGLCanvasManager(refViewGLCanvasManager);
		refSingeltonManager.setSWTGUIManager(refSWTGUIManager);
		refSingeltonManager.setPathwayManager(refPathwayManager);
		refSingeltonManager.setPathwayItemManager(refPathwayItemManager);
		refSingeltonManager.setEventPublisher(refEventPublisher);
		refSingeltonManager.setGenomeIdManager( refGenomeIdManager );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(final int iItemId)
	{

		Iterator <IGeneralManager> iter = llAllManagerObjects.iterator();
		
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

		Iterator <IGeneralManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IGeneralManager buffer = iter.next();
			
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

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerType getManagerType()
	{
		return ManagerType.SINGELTON;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getSingeltonManager()
	 */
	public IGeneralManager getGeneralManager()
	{
		return this;
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
		
		IGeneralManager buffer = this.getManagerByBaseType( type );		
		assert buffer != null : "createNewId type does not address manager!";
		
		return buffer.createId(type);
	}

	public boolean unregisterItem(final int iItemId,
			final ManagerObjectType type)
	{
		assert type != null : "registerItem called with type == null!";
		
		IGeneralManager buffer = this.getManagerByBaseType( type );
		
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
		
		IGeneralManager buffer = this.getManagerByBaseType( type );
		
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
	
	
	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType) 
	{
		return getManagerByType(managerType.getGroupType());
	}
	
	public IGeneralManager getManagerByType(ManagerType managerType)
	{

		assert managerType != null : "type is null!";

		switch (managerType)
		{
		case MEMENTO:
			return refMementoManager;
//		case VIEW_DISTRIBUTE_GUI:
//			return refDComponentManager;
		case DATA_VIRTUAL_ARRAY:
			return refVirtualArrayManager;
		case DATA_SET:
			return refSetManager;
		case DATA_STORAGE:
			return refStorageManager;
		case SINGELTON:
			return this;
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
					"Error in OneForAllManager.getManagerByBaseType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}

//	public IViewCanvasManager getViewCanvasManager()
//	{
//		return refViewCanvasManager;
//	}
	
	public void destroyOnExit() {
		
		refLoggerManager.logMsg("OneForAllManager.destroyOnExit()", LoggerType.STATUS );
		
		this.refViewGLCanvasManager.destroyOnExit();
		
		Iterator <IGeneralManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IGeneralManager buffer = iter.next();
			
			if ( buffer != null ) {
				buffer.destroyOnExit();
			}
			
		} // while ( iter.hasNext() ) 
		
		refLoggerManager.logMsg("OneForAllManager.destroyOnExit()  ...[DONE]", LoggerType.STATUS);
	}

	public boolean setCreateNewId(ManagerType setNewBaseType, int iCurrentId) {

		IGeneralManager refSecialManager = getManagerByType( setNewBaseType );
		
		if ( ! refSecialManager.setCreateNewId(setNewBaseType, iCurrentId) ) {
			refLoggerManager.logMsg("setCreateNewId failed!", LoggerType.MINOR_ERROR );
			return false;
		}
		
		return true;
	}

	public int createId(CommandQueueSaxType setNewBaseType) {

		// TODO Auto-generated method stub
		return 0;
	}
}
