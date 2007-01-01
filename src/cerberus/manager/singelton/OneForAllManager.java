/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import cerberus.data.collection.IStorage;
import cerberus.data.xml.IMementoXML;
import cerberus.manager.ICommandManager;
import cerberus.manager.IDistComponentManager;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.IMementoManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ISingelton;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.IViewCanvasManager;
import cerberus.manager.IViewGLCanvasManager;
//import cerberus.manager.canvas.ViewCanvasManager;
import cerberus.manager.canvas.ViewCanvasManager;
import cerberus.manager.command.CommandManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.data.genome.GenomeIdManager;
import cerberus.manager.data.pathway.PathwayElementManager;
import cerberus.manager.data.pathway.PathwayManager;
import cerberus.manager.data.set.SetManager;
import cerberus.manager.data.storage.StorageManager;
import cerberus.manager.data.virtualarray.VirtualArrayManager;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
//import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.logger.ConsoleLogger;
//import cerberus.manager.logger.ConsoleSimpleLogger;
import cerberus.manager.memento.MementoManager;
import cerberus.manager.menu.swing.SwingMenuManager;
//import cerberus.manager.menu.swing.SwingMenuManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.view.ViewJoglManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.xml.parser.command.CommandQueueSaxType;

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
 * @author Marc Streit
 *
 */
public class OneForAllManager 
implements IGeneralManagerSingelton {

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

	protected SingeltonManager refSingeltonManager;

	protected ISetManager refSetManager;

	protected IStorageManager refStorageManager;

	protected IVirtualArrayManager refVirtualArrayManager;

	protected ICommandManager refCommandManager;

	protected ILoggerManager refLoggerManager;

	protected IViewGLCanvasManager refViewGLManager;

	protected ISWTGUIManager refSWTGUIManager;

	protected IPathwayManager refPathwayManager;

	protected IPathwayElementManager refPathwayElementManager;

	protected IEventPublisher refEventPublisher;

	protected IGenomeIdManager refGenomeIdManager;

	/**
	 * @deprecated
	 */
	protected IDistComponentManager refDComponentManager;
	
	/**
	 * @deprecated
	 */
	protected IViewCanvasManager refViewCanvasManager;

	/**
	 * @deprecated
	 */	
	protected IMementoManager refMementoManager;

	/**
	 * @deprecated
	 */	
	protected IMenuManager refMenuManager;
	
//	/**
//	 * Used to create a new item by a Fabrik.
//	 * used by cerberus.data.manager.OneForAllManager#createNewId(ManagerObjectType)
//	 * 
//	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
//	 */
//	protected ManagerObjectType setCurrentType = ManagerObjectType.ALL_IN_ONE;

	/**
	 * Call initAll() before using this class!
	 * 
	 * @see cerberus.data.manager.singelton.OneForAllManager#initAll()
	 */
	public OneForAllManager(final SingeltonManager sef_SingeltonManager) {

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
		refSingeltonManager.setNetworkPostfix(1);

		//initAll();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingelton()
	 */
	public final ISingelton getSingelton() {

		return refSingeltonManager;
	}

	/**
	 * Must be called right after the constructor before using this class.
	 * Initialzes all Mangeger obejcts.
	 *
	 */
	public void initAll() {

		if (bAllManagersInizailized)
		{
			throw new CerberusRuntimeException(
					"initAll() was called at least twice!");
		}
		bAllManagersInizailized = true;

		/** int logger first! */
		refLoggerManager = new ConsoleLogger(this);
		refLoggerManager.setSystemLogLevel(ILoggerManager.LoggerType.FULL);
		//ILoggerManager.LoggerType.VERBOSE );
		refSingeltonManager.setLoggerManager(refLoggerManager);
		/* end init logger */

		refStorageManager = new StorageManager(this, 4);
		refVirtualArrayManager = new VirtualArrayManager(this, 4);
		refSetManager = new SetManager(this, 4);
		refCommandManager = new CommandManager(this);
		refViewGLManager = new ViewJoglManager(this);
		refSWTGUIManager = new SWTGUIManager(this);
		refPathwayManager = new PathwayManager(this);
		refPathwayElementManager = new PathwayElementManager(this);
		refEventPublisher = new EventPublisher(this);
		refGenomeIdManager = new GenomeIdManager(this);

		refDComponentManager = new DComponentSwingFactoryManager(this);
		refMenuManager = new SwingMenuManager(this);
		refViewCanvasManager = new ViewCanvasManager(this);
		refMementoManager = new MementoManager(this);
		
		refSingeltonManager.setGenomeIdManager(new GenomeIdManager(this));

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
		refSingeltonManager.setVirtualArrayManager(refVirtualArrayManager);
		refSingeltonManager.setSetManager(refSetManager);
		refSingeltonManager.setStorageManager(refStorageManager);
		refSingeltonManager.setMenuManager(refMenuManager);
		refSingeltonManager.setViewGLCanvasManager(refViewGLManager);
		refSingeltonManager.setSWTGUIManager(refSWTGUIManager);
		refSingeltonManager.setPathwayElementManager(refPathwayElementManager);
		refSingeltonManager.setPathwayManager(refPathwayManager);
		refSingeltonManager.setEventPublisher(refEventPublisher);
		refSingeltonManager.setGenomeIdManager(refGenomeIdManager);

		refSingeltonManager.setDComponentManager(refDComponentManager);
		refSingeltonManager.setViewCanvasManager(refViewCanvasManager);
		refSingeltonManager.setMenuManager(refMenuManager);
		refSingeltonManager.setMementoManager(refMementoManager);

		refSetManager.initManager();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(final int iItemId) {

		if (refSetManager.hasItem(iItemId))
			return true;
		else if (refStorageManager.hasItem(iItemId))
			return true;		
		else if (refVirtualArrayManager.hasItem(iItemId))
			return true;
		else if (refCommandManager.hasItem(iItemId))
			return true;
		else if (refLoggerManager.hasItem(iItemId))
			return true;
		else if (refViewGLManager.hasItem(iItemId))
			return true;
		else if (refSWTGUIManager.hasItem(iItemId))
			return true;
		else if (refPathwayElementManager.hasItem(iItemId))
			return true;
		else if (refPathwayManager.hasItem(iItemId))
			return true;
		else if (refEventPublisher.hasItem(iItemId))
			return true;
		else if (refGenomeIdManager.hasItem(iItemId))
			return true;
		else if (refDComponentManager.hasItem(iItemId))
			return true;
		else if (refMementoManager.hasItem(iItemId))
			return true;
		else if (refViewCanvasManager.hasItem(iItemId))
			return true;
		else if (refMenuManager.hasItem(iItemId))
			return true;

		return false;
	}

	/**
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
	 * 
	 * @param iItemId unique Id used for lookup
	 * @return Object bound to Id or null, if id was not found.
	 */
	public Object getItem(final int iItemId) {

		if (refSetManager.hasItem(iItemId))
			return refSetManager.getItemSet(iItemId);
		else if (refStorageManager.hasItem(iItemId))
			return refStorageManager.getItem(iItemId);	
		else if (refVirtualArrayManager.hasItem(iItemId))
			return refVirtualArrayManager.getItem(iItemId);
		else if (refCommandManager.hasItem(iItemId))
			return refCommandManager.getItem(iItemId);
		else if (refViewGLManager.hasItem(iItemId))
			return refViewGLManager.getItem(iItemId);
		else if (refSWTGUIManager.hasItem(iItemId))
			return refSWTGUIManager.getItem(iItemId);
		else if (refPathwayElementManager.hasItem(iItemId))
			return refPathwayElementManager.getItem(iItemId);
		else if (refPathwayManager.hasItem(iItemId))
			return refPathwayManager.getItem(iItemId);
		else if (refEventPublisher.hasItem(iItemId))
			return refEventPublisher.getItem(iItemId);
		else if (refGenomeIdManager.hasItem(iItemId))
			return refGenomeIdManager.getItem(iItemId);
		else if (refDComponentManager.hasItem(iItemId))
			return refDComponentManager.getItem(iItemId);
		else if (refMementoManager.hasItem(iItemId))
			return refMementoManager.getItem(iItemId);
		else if (refViewCanvasManager.hasItem(iItemId))
			return refViewCanvasManager.getItem(iItemId);
		else if (refMenuManager.hasItem(iItemId))
			return refMenuManager.getItem(iItemId);
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.singelton.IGeneralManagerSingelton#getManagerByBaseType(cerberus.manager.type.ManagerObjectType)
	 */
	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {

		assert managerType != null : "type is null!";

		switch (managerType.getGroupType())
		{
		case SET:
			return refSetManager;
		case VIRTUAL_ARRAY:
			return refVirtualArrayManager;
		case STORAGE:
			return refStorageManager;
		case COMMAND:
			return refCommandManager;
		case VIEW_GL_CANVAS:
			return refViewGLManager;
		case VIEW:
			return refViewGLManager;
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
		case MEMENTO:
			return refMementoManager;
		case GUI_COMPONENT:
			return refDComponentManager;
			
		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.getManagerByBaseType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size() {

		return -1;
		//		return (refSetManager.size() + refStorageManager.size()
		//				+ refVirtualArrayManager.size() + refMementoManager.size()
		//				+ refDComponentManager.size() + refViewCanvasManager.size() + refSWTGUIManager
		//				.size());
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {

		return ManagerObjectType.ALL_IN_ONE;
	}

//	/* (non-Javadoc)
//	 * @see cerberus.data.manager.GeneralManager#getSingeltonManager()
//	 */
//	public IGeneralManager getGeneralManager() {
//
//		return this;
//	}

//	/**
//	 * ISet the current type used to create the next Id using
//	 * cerberus.data.manager.singelton.OneForAllManager#createNewId()
//	 * Does not influence 
//	 * cerberus.data.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
//	 * or 
//	 * cerberus.data.manager.singelton.OneForAllManager#createNewItem(ManagerObjectType, String)
//	 * .
//	 * 
//	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
//	 * @see cerberus.manager.singelton.OneForAllManager#createNewItem(ManagerObjectType, String)
//	 * @see cerberus.manager.singelton.OneForAllManager#getCurrentType()
//	 * 
//	 * @param setCurrentType
//	 */
//	protected final void setCurrentType(ManagerObjectType setCurrentType) {
//
//		this.setCurrentType = setCurrentType;
//	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public final int createNewId(final ManagerObjectType newBaseType) {

//		this.setCurrentType = setNewBaseType;

		switch (newBaseType.getGroupType())
		{
//		case SET:
//			return refSetManager.createNewId(newBaseType);
//		case VIRTUAL_ARRAY:
//			return refVirtualArrayManager.createNewId(newBaseType);
//		case STORAGE:
//			return refStorageManager.createNewId(newBaseType);
//		case COMMAND:
//			return refCommandManager.createNewId(newBaseType);
//		case VIEW:
//			return refViewCanvasManager.createNewId(newBaseType);
//		case MEMENTO:
//			return refMementoManager.createNewId(newBaseType);
//		case GUI_COMPONENT:
//			return refDComponentManager.createNewId(newBaseType);

		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ newBaseType.toString());
		}

	}

	public boolean unregisterItem(final int iItemId,
			final ManagerObjectType type) {

		if (type != null)
		{
			switch (type.getGroupType())
			{
//			case MEMENTO:
//				//return refMementoManager.c();
//				assert false : "not implemented";
//			case GUI_COMPONENT:
//				//return refDComponentManager.createNewId();
//				assert false : "not implemented";
//			case VIRTUAL_ARRAY:
//				return refVirtualArrayManager.unregisterItem(iItemId, type);
//			case SET:
//				return refSetManager.unregisterItem(iItemId, type);
//			case STORAGE:
//				return refStorageManager.unregisterItem(iItemId, type);
//			case VIEW:
//				return refViewCanvasManager.unregisterItem(iItemId, type);
//			case COMMAND:
//				throw new CerberusRuntimeException(
//						"Error in OneForAllManager.unregisterItem() type "
//								+ type.name() + " can not unregister!");
			default:
				throw new CerberusRuntimeException(
						"Error in OneForAllManager.unregisterItem() unknown type "
								+ type.name());
			} // end switch
		}

		/**
		 * TEST all sub managers
		 */

		assert false : "must use type!";

		return false;
	}

	public boolean registerItem(final Object registerItem, final int iItemId,
			final ManagerObjectType type) {

		if (type != null)
		{
			switch (type.getGroupType())
			{
//			case MEMENTO:
//				//return refMementoManager.c();
//				assert false : "not implemented";
//
//			case GUI_COMPONENT:
//				//return refDComponentManager.createNewId();
//				assert false : "not implemented";
//
//			case VIRTUAL_ARRAY:
//				return refVirtualArrayManager.registerItem(registerItem,
//						iItemId, type);
//			case SET:
//				return refSetManager.registerItem(registerItem, iItemId, type);
//			case STORAGE:
//				return refStorageManager.registerItem(registerItem, iItemId,
//						type);
//			case VIEW:
//				return refViewCanvasManager.registerItem(registerItem, iItemId,
//						type);
//			case COMMAND:
//				throw new CerberusRuntimeException(
//						"Error in OneForAllManager.registerItem() type "
//								+ type.name() + " can niot register!");

			default:
				throw new CerberusRuntimeException(
						"Error in OneForAllManager.registerItem() unknown type "
								+ type.name());
			} // end switch
		}

		assert false : "must use type!";

		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewItem(cerberus.data.manager.BaseManagerType, java.lang.String)
	 */
	public Object createNewItem(final ManagerObjectType createNewType,
			final String sNewTypeDetails) {

		switch (createNewType.getGroupType())
		{
//		case MEMENTO:
//			//return refMementoManager.c();
//			assert false : "not implemented";
//		case GUI_COMPONENT:
//			//return refDComponentManager.createNewId();
//			assert false : "not implemented";
//		case VIRTUAL_ARRAY:
//			return refVirtualArrayManager.createSelection(createNewType);
//		case SET:
//			return refSetManager.createSet(CommandQueueSaxType.CREATE_SET);
//		case STORAGE:
//			return refStorageManager.createStorage(createNewType);
//		case VIEW:
//			if (createNewType == ManagerObjectType.VIEW_NEW_FRAME)
//			{
//				return refViewCanvasManager.createWorkspace(createNewType,
//						sNewTypeDetails);
//			}
//			return refViewCanvasManager.createCanvas(createNewType,
//					sNewTypeDetails);
//		case COMMAND:
//			return refCommandManager.createCommand(sNewTypeDetails);

		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ createNewType.toString());
		} // end switch
	}

	public void callbackForParser(final ManagerObjectType type,
			final String tag_causes_callback, final String details,
			final ISaxParserHandler refSaxHandler) {

		assert type != null : "type is null!";

		System.out.println("callback in OneForAllManager");

		switch (type.getGroupType())
		{
//		case MEMENTO:
//			//return refMementoManager.c();
//			assert false : "not implemented";
//		case GUI_COMPONENT:
//			//return refDComponentManager.createNewId();
//			assert false : "not implemented";
//		case VIRTUAL_ARRAY:
//		{
//			IMementoXML selectionBuffer = refVirtualArrayManager
//					.createSelection(type);
//
//			selectionBuffer.setMementoXML_usingHandler(refSaxHandler);
//			return;
//		}
//		case SET:
//		{
//			ISet setBuffer = this.refSetManager
//					.createSet(CommandQueueSaxType.CREATE_SET);
//
//			//setBuffer.setMementoXML_usingHandler( refSaxHandler );
//			return;
//		}
//		case STORAGE:
//			IStorage storageBuffer = refStorageManager.createStorage(type);
//
//			storageBuffer.setMementoXML_usingHandler(refSaxHandler);
//			return;
//
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
//
		default:
			throw new CerberusRuntimeException(
					"Error in OneForAllManager.createNewId() unknown type "
							+ type.name());
		} // end switch ( type.getGroupType() )

	}

	public void setErrorMessage(final String sErrorMsg) {

		System.err.println("ERROR: " + sErrorMsg);
	}

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
	public void setStateSWT(final boolean bEnableSWT) {

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
	public boolean getStateSWT() {

		return this.bEnableSWT;
	}

	public void destroyOnExit() {

		refLoggerManager.logMsg("OneForAllManager.destroyOnExit()");

		this.refViewGLManager.destroyOnExit();

		refLoggerManager.logMsg("OneForAllManager.destroyOnExit()  ...[DONE]");
	}
}
