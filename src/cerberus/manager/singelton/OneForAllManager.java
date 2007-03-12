/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import java.util.Iterator;
import java.util.LinkedList;

import cerberus.command.CommandQueueSaxType;
import cerberus.data.collection.IStorage;
import cerberus.data.xml.IMementoXML;
import cerberus.manager.ICommandManager;
import cerberus.manager.IDistComponentManager;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.IMementoManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ISingelton;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.IViewCanvasManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.command.CommandManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.data.genome.DynamicGenomeIdManager;
import cerberus.manager.data.pathway.PathwayElementManager;
import cerberus.manager.data.pathway.PathwayManager;
import cerberus.manager.data.set.SetManager;
import cerberus.manager.data.storage.StorageManager;
import cerberus.manager.data.virtualarray.VirtualArrayManager;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.logger.ConsoleLogger;
//import cerberus.manager.logger.ConsoleSimpleLogger;
import cerberus.manager.memento.MementoManager;
import cerberus.manager.menu.swing.SwingMenuManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.manager.view.ViewJoglManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.net.dwt.base.DGuiComponentType;

//import prometheus.data.collection.SelectionType; 
//import prometheus.data.collection.SetType;
//import prometheus.data.collection.StorageType;

//import prometheus.data.collection.Selection;
import cerberus.data.collection.ISet;

import cerberus.data.collection.view.IViewCanvas;

//import prometheus.net.dwt.swing.DHistogramCanvas;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class OneForAllManager 
implements IGeneralManagerSingelton
{

	/**
	 * Defines, if initAll() was called.
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#initAll()
	 */
	private boolean bAllManagersInizailized = false;

	/**
	 * Define if SWT is used.
	 */
	private boolean bEnableSWT = false;

	private LinkedList <IGeneralManager> llAllManagerObjects;
	
	protected SingeltonManager refSingeltonManager;

	protected ISetManager refSetManager;

	protected IStorageManager refStorageManager;

	protected IVirtualArrayManager refVirtualArrayManager;

	protected IMementoManager refMementoManager;

	protected IMenuManager refMenuManager;

	protected IDistComponentManager refDComponentManager;

//	protected IViewCanvasManager refViewCanvasManager;

	protected ICommandManager refCommandManager;

	protected ILoggerManager refLoggerManager;
	
	protected IGenomeIdManager refGenomeIdManager;

	protected IViewGLCanvasManager refViewGLCanvasManager;

	protected ISWTGUIManager refSWTGUIManager;

	protected IPathwayManager refPathwayManager;

	protected IPathwayElementManager refPathwayElementManager;

	protected IEventPublisher refEventPublisher;

	/**
	 * Used to create a new item by a Fabrik.
	 * used by cerberus.data.manager.OneForAllManager#createNewId(ManagerObjectType)
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
	 */
	protected ManagerObjectType setCurrentType = ManagerObjectType.ALL_IN_ONE;

	//	protected VirtualArrayType initSelectionType;
	//	
	//	protected SetType 		initSetType;
	//	
	//	protected StorageType 	initStorageType;

	/**
	 * Call initAll() before using this class!
	 * 
	 * @see cerberus.data.manager.singelton.OneForAllManager#initAll()
	 */
	public OneForAllManager(final SingeltonManager sef_SingeltonManager)
	{

		if (refSingeltonManager == null)
		{
			refSingeltonManager = new SingeltonManager(this);
			refSingeltonManager.initManager();
		} else
		{
			refSingeltonManager = sef_SingeltonManager;
		}

		/**
		 * The Network psotfix must be unique inside the network for 
		 * destributed cerberus applications. 
		 * For stand alone cerberus applications this id must match the XML file.
		 */
		refSingeltonManager.setNetworkPostfix( 1 );
		
		llAllManagerObjects = new LinkedList <IGeneralManager> ();
		
		//initAll();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingelton()
	 */
	public final ISingelton getSingelton()
	{
		return refSingeltonManager;
	}

	/**
	 * Must be called right after teh constructor before using this class.
	 * Initialzes all Mangeger obejcts.
	 *
	 */
	public void initAll()
	{

		if (bAllManagersInizailized)
		{
			throw new CerberusRuntimeException(
					"initAll() was called at least twice!");
		}
		bAllManagersInizailized = true;

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
		
		refDComponentManager = new DComponentSwingFactoryManager(this);
		//refViewCanvasManager = new ViewCanvasManager(this);
		refCommandManager = new CommandManager(this);
		refMenuManager = new SwingMenuManager(this);
		
		refViewGLCanvasManager = new ViewJoglManager(this);
		refSWTGUIManager = new SWTGUIManager(this);
		refPathwayManager = new PathwayManager(this);
		refPathwayElementManager = new PathwayElementManager(this);
		refEventPublisher = new EventPublisher(this);
		refGenomeIdManager = new DynamicGenomeIdManager(this);
		
		/**
		 * Insert all Manager objects handling registered objects to 
		 * the LinkedList
		 */
		llAllManagerObjects.add( refSetManager );
		llAllManagerObjects.add( refVirtualArrayManager );
		llAllManagerObjects.add( refStorageManager );
		
		llAllManagerObjects.add( refPathwayManager );
		llAllManagerObjects.add( refPathwayElementManager );
		llAllManagerObjects.add( refGenomeIdManager );
		
		llAllManagerObjects.add( refEventPublisher );
		llAllManagerObjects.add( refViewGLCanvasManager );
		llAllManagerObjects.add( refSWTGUIManager );
		
		llAllManagerObjects.add( refMenuManager );
		llAllManagerObjects.add( refCommandManager );
		llAllManagerObjects.add( refMementoManager );
		llAllManagerObjects.add( refDComponentManager );
		
		/**
		 * Make sure SWT is only used, when needed!
		 */
		//		if ( bEnableSWT ) {
		//			refSWTGUIManager.createApplicationWindow();		
		//		}
		/**
		 * Register managers to singelton ...
		 */
		
		refSingeltonManager.setCommandManager(refCommandManager);
		refSingeltonManager.setDComponentManager(refDComponentManager);
		refSingeltonManager.setVirtualArrayManager(refVirtualArrayManager);
		refSingeltonManager.setSetManager(refSetManager);
		refSingeltonManager.setStorageManager(refStorageManager);
		refSingeltonManager.setMenuManager(refMenuManager);
		refSingeltonManager.setViewGLCanvasManager(refViewGLCanvasManager);
		refSingeltonManager.setSWTGUIManager(refSWTGUIManager);
		refSingeltonManager.setPathwayElementManager(refPathwayElementManager);
		refSingeltonManager.setPathwayManager(refPathwayManager);
		refSingeltonManager.setEventPublisher(refEventPublisher);
		refSingeltonManager.setGenomeIdManager( refGenomeIdManager );
		

		refSetManager.initManager();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
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
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
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
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size()
	{

		return (refSetManager.size() + refStorageManager.size()
				+ refVirtualArrayManager.size() + refMementoManager.size()
				+ refDComponentManager.size() + refViewGLCanvasManager.size() + refSWTGUIManager
				.size());
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.singelton.GeneralManagerSingelton#getCommandManager()
	 */
	public ICommandManager getCommandManager()
	{
		return refCommandManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerType getManagerType()
	{
		return ManagerType.SINGELTON;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingeltonManager()
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
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public final int createNewId(final ManagerObjectType type)
	{

		assert type != null : "registerItem called with type == null!";
		
		IGeneralManager buffer = this.getManagerByBaseType( type );		
		assert buffer != null : "createNewId type does not address manager!";
		
		return buffer.createNewId(type);
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
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewItem(cerberus.data.manager.BaseManagerType, java.lang.String)
	 */
	public Object createNewItem(final ManagerObjectType createNewType,
			final String sNewTypeDetails)
	{

		switch (createNewType.getGroupType())
		{
		case MEMENTO:
			//return refMementoManager.c();
			assert false : "not implemented";
		case VIRTUAL_ARRAY:
			return refVirtualArrayManager.createVirtualArray(createNewType);
		case SET:
			return refSetManager.createSet( CommandQueueSaxType.CREATE_SET );
		case STORAGE:
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
		case D_GUI:
			return refDComponentManager.createSet( DGuiComponentType.valueOf(sNewTypeDetails) );

		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ createNewType.toString());
		} // end switch
	}

	public void callbackForParser(final ManagerObjectType type,
			final String tag_causes_callback, final String details,
			final ISaxParserHandler refSaxHandler)
	{

		assert type != null : "type is null!";

		System.out.println("callback in OneForAllManager");

		switch (type.getGroupType())
		{
		case MEMENTO:
			//return refMementoManager.c();
			assert false : "not implemented";
		case VIRTUAL_ARRAY:
		{
			IMementoXML selectionBuffer = refVirtualArrayManager
					.createVirtualArray(type);

			selectionBuffer.setMementoXML_usingHandler(refSaxHandler);
			return;
		}
		case SET:
		{
			ISet setBuffer = this.refSetManager.createSet( CommandQueueSaxType.CREATE_SET );

			//setBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case STORAGE:
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
			throw new CerberusRuntimeException(
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
		case D_GUI:
			return refDComponentManager;
		case VIRTUAL_ARRAY:
			return refVirtualArrayManager;
		case SET:
			return refSetManager;
		case STORAGE:
			return refStorageManager;
		case SINGELTON:
			return this;
		case VIEW:
		case VIEW_GL_CANVAS:
			return refViewGLCanvasManager;
		case COMMAND:
			return refCommandManager;
		case GUI_SWT:
			return refSWTGUIManager;
		case PATHWAY:
			return refPathwayManager;
		case PATHWAY_ELEMENT:
			return refPathwayElementManager;
		case EVENT_PUBLISHER:
			return refEventPublisher;
		case GENOME_ID:
			return refGenomeIdManager;
			
		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.getManagerByBaseType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}

//	public IViewCanvasManager getViewCanvasManager()
//	{
//		return refViewCanvasManager;
//	}

	/**
	 * ISet curretn state of SWT.
	 * Must be set before initAll() is called!
	 * Default is FALSE.
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#initAll()
	 * @see cerberus.manager.singelton.OneForAllManager#getStateSWT()
	 * 
	 * @param bEnableSWT TRUE to enable SWT
	 */
	public void setStateSWT(final boolean bEnableSWT)
	{
		if (bAllManagersInizailized)
		{
			throw new CerberusRuntimeException(
					"setStateSWT() was called after initAll() was called, which has no influence!");
		}
		this.bEnableSWT = bEnableSWT;
	}

	/**
	 * Get current state of SWT. 
	 * TURE indicates that SWT is used.
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#setStateSWT(boolean)
	 * 
	 * @return TRUE is SWT is enabled.
	 */
	public boolean getStateSWT()
	{
		return this.bEnableSWT;
	}
	
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
}
