/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import cerberus.manager.CommandManager;
import cerberus.manager.DComponentManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.LoggerManager;
import cerberus.manager.MementoManager;
import cerberus.manager.MenuManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.Singelton;
import cerberus.manager.StorageManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.ViewCanvasManager;
import cerberus.manager.canvas.ViewCanvasManagerSimple;
import cerberus.manager.command.CommandManagerSimple;
import cerberus.manager.data.selection.SelectionManagerSimple;
import cerberus.manager.data.set.SetManagerSimple;
import cerberus.manager.data.storage.StorageManagerSimple;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
import cerberus.manager.logger.ConsoleSimpleLogger;
import cerberus.manager.memento.MementoManagerSimple;
import cerberus.manager.menu.MenuManagerSimple;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.gui.SWTGUIManagerSimple;
import cerberus.data.collection.Storage;
import cerberus.data.xml.MementoXML;


//import prometheus.data.collection.SelectionType; 
//import prometheus.data.collection.SetType;
//import prometheus.data.collection.StorageType;

//import prometheus.data.collection.Selection;
import cerberus.data.collection.Set;

import cerberus.data.collection.view.ViewCanvas;


//import prometheus.net.dwt.swing.DHistogramCanvas;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class OneForAllManager 
implements GeneralManagerSingelton {

	protected SingeltonManager refSingeltonManager;
	
	protected SetManager refSetManager;
	
	protected StorageManager refStorageManager;
	
	protected SelectionManager refSelectionManager;
	
	protected MementoManager refMementoManager;
	
	protected MenuManager refMenuManager;
	
	protected DComponentManager refDComponentManager;
	
	protected ViewCanvasManager refViewCanvasManager;
	
	protected CommandManager refCommandManager;
	
	protected LoggerManager refLoggerManager;
	
	protected SWTGUIManager refSWTGUIManager;
	
	/**
	 * Used to create a new item by a Fabrik.
	 * used by cerberus.data.manager.OneForAllManager#createNewId(ManagerObjectType)
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
	 */
	protected ManagerObjectType setCurrentType = ManagerObjectType.ALL_IN_ONE;
	
//	protected SelectionType initSelectionType;
//	
//	protected SetType 		initSetType;
//	
//	protected StorageType 	initStorageType;
	
	/**
	 * 
	 */
	public OneForAllManager( final SingeltonManager sef_SingeltonManager ) {
		
		if ( refSingeltonManager == null ) {
			refSingeltonManager = new SingeltonManager();
		} else {
			refSingeltonManager = sef_SingeltonManager;			
		}
		
		initAll();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingelton()
	 */
	public final Singelton getSingelton() {
		return refSingeltonManager;
	}
	
	protected void initAll() {
		
		refSetManager = new SetManagerSimple(this, 4);
		refStorageManager = new StorageManagerSimple(this, 4);
		refSelectionManager = new SelectionManagerSimple(this, 4);
		refMementoManager = new MementoManagerSimple(this);
		refDComponentManager = new DComponentSwingFactoryManager(this);
		refViewCanvasManager = new ViewCanvasManagerSimple( this );
		refCommandManager = new CommandManagerSimple( this );
		refMenuManager = new MenuManagerSimple( this );		
		refLoggerManager = new ConsoleSimpleLogger( this );
		refSWTGUIManager = new SWTGUIManagerSimple( this );
		
		/**
		 * Register managers to singelton ...
		 */
		refSingeltonManager.setCommandManager( refCommandManager );
		refSingeltonManager.setDComponentManager( refDComponentManager );
		refSingeltonManager.setViewCanvasManager( refViewCanvasManager );
		refSingeltonManager.setSelectionManager( refSelectionManager );
		refSingeltonManager.setSetManager( refSetManager );
		refSingeltonManager.setStorageManager( refStorageManager );
		refSingeltonManager.setMenuManager( refMenuManager );
		refSingeltonManager.setLoggerManager( refLoggerManager );
		refSingeltonManager.setSWTGUIManager (refSWTGUIManager );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem( final int iItemId) {
		
		if ( refSetManager.hasItem( iItemId ) ) 
			return true;
		if ( refSelectionManager.hasItem( iItemId ) ) 
			return true;
		if ( refStorageManager.hasItem( iItemId ) ) 
			return true;
		if ( refDComponentManager.hasItem( iItemId ) ) 
			return true;
		if ( refMementoManager.hasItem( iItemId ) )
			return true;
		if ( refViewCanvasManager.hasItem( iItemId ) ) 
			return true;
		
		//FIXME Is next line useless?
		if ( refCommandManager.hasItem( iItemId ) )
			return true;
		
		return false;
	}
	
	/**
	 * @see cerberus.manager.GeneralManager#hasItem(int)
	 * 
	 * @param iItemId unique Id used for lookup
	 * @return Object bound to Id or null, if id was not found.
	 */
	public Object getItem( final int iItemId) {
		
		if ( refSetManager.hasItem( iItemId ) )
			return refSetManager.getItemSet( iItemId );

		if ( refSelectionManager.hasItem( iItemId ) )
			return refSelectionManager.getItemSelection( iItemId );

		if ( refStorageManager.hasItem( iItemId ) )
			return refStorageManager.getItemStorage( iItemId );

		if ( refDComponentManager.hasItem( iItemId ) )
			return refDComponentManager.getItemSet( iItemId );

		if ( refMementoManager.hasItem( iItemId ) )
			return refMementoManager.getMemento( iItemId );
		
		if ( refViewCanvasManager.hasItem( iItemId ))
			return refViewCanvasManager.getItemCanvas( iItemId );

		if ( refCommandManager.hasItem( iItemId ))
			return refCommandManager.getItem( iItemId );
		
//		if ( refSWTGUIManager.hasItem( iItemId ))
//			return refSWTGUIManager.getItem( iItemId );

		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size() {
		
		return (refSetManager.size() +
		refStorageManager.size() +
		refSelectionManager.size() +
		refMementoManager.size() + 
		refDComponentManager.size() +
		refViewCanvasManager.size() +
		refSWTGUIManager.size());
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.singelton.GeneralManagerSingelton#getCommandManager()
	 */
	public CommandManager getCommandManager() {
		return refCommandManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.ALL_IN_ONE;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingeltonManager()
	 */
	public GeneralManager getGeneralManager() {
		return this;
	}


	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#getCurrentType()
	 */
	public final ManagerObjectType getCurrentType() {
		return this.setCurrentType;
	}
	
	/**
	 * Set the current type used to create the next Id using
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId()
	 * Does not influence 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
	 * or 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewItem(ManagerObjectType, String)
	 * .
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
	 * @see cerberus.manager.singelton.OneForAllManager#createNewItem(ManagerObjectType, String)
	 * @see cerberus.manager.singelton.OneForAllManager#getCurrentType()
	 * 
	 * @param setCurrentType
	 */
	protected final void setCurrentType( ManagerObjectType setCurrentType) {
		this.setCurrentType = setCurrentType;
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
	public final int createNewId(final ManagerObjectType setNewBaseType) {
	
		this.setCurrentType = setNewBaseType;
		
		switch ( setNewBaseType.getGroupType() ) {
			case MEMENTO:
				return refMementoManager.createNewId(setNewBaseType);
			case GUI_COMPONENT:
				return refDComponentManager.createNewId(setNewBaseType);
			case SELECTION:
				return refSelectionManager.createNewId(setNewBaseType);
			case SET:
				return refSetManager.createNewId(setNewBaseType);
			case STORAGE:
				return refStorageManager.createNewId(setNewBaseType);
			case VIEW:
				return refViewCanvasManager.createNewId(setNewBaseType);
			case COMMAND:
				return refCommandManager.createNewId(setNewBaseType);
				
			default:
				throw new CerberusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
						setNewBaseType.toString() );			
		}
	
	}

	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( type != null ) {
			switch ( type.getGroupType() ) {
			case MEMENTO:
				//return refMementoManager.c();
				assert false: "not implemented";
			case GUI_COMPONENT:
				//return refDComponentManager.createNewId();
				assert false: "not implemented";
			case SELECTION:
				return refSelectionManager.unregisterItem( iItemId, type );
			case SET:
				return refSetManager.unregisterItem( iItemId, type );
			case STORAGE:
				return refStorageManager.unregisterItem( iItemId, type );
			case VIEW:
				return refViewCanvasManager.unregisterItem( iItemId, type );
			case COMMAND:
				throw new CerberusRuntimeException("Error in OneForAllManager.unregisterItem() type " +
						type.name() + " can not unregister!");
			/**
			 * Note: refCommandManager can not unregister items.
			 */
				
			default:
				throw new CerberusRuntimeException("Error in OneForAllManager.unregisterItem() unknown type " +
						type.name() );			
			} // end switch
		}
		
		/**
		 * TEST all sub managers
		 */
		
		assert false: "must use type!";
		
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type ) {
		
		if ( type != null ) {
			switch ( type.getGroupType() ) {
			case MEMENTO:
				//return refMementoManager.c();
				assert false: "not implemented";
			
			case GUI_COMPONENT:
				//return refDComponentManager.createNewId();
				assert false: "not implemented";
			
			case SELECTION:
				return refSelectionManager.registerItem( 
						registerItem, iItemId, type );
			case SET:
				return refSetManager.registerItem( 
						registerItem, iItemId, type );
			case STORAGE:
				return refStorageManager.registerItem( 
						registerItem, iItemId, type );
			case VIEW:
				return refViewCanvasManager.registerItem( 
						registerItem, iItemId, type );
			case COMMAND:
				throw new CerberusRuntimeException("Error in OneForAllManager.registerItem() type " +
						type.name() + " can niot register!");	
				
			default:
				throw new CerberusRuntimeException("Error in OneForAllManager.registerItem() unknown type " +
						type.name() );			
			} // end switch
		}
		
		assert false: "must use type!";
		
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewItem(cerberus.data.manager.BaseManagerType, java.lang.String)
	 */
	public Object createNewItem( final ManagerObjectType createNewType,
			final String sNewTypeDetails ) {
		
		switch ( createNewType.getGroupType() ) {
			case MEMENTO:
				//return refMementoManager.c();
				assert false: "not implemented";
			case GUI_COMPONENT:
				//return refDComponentManager.createNewId();
				assert false: "not implemented";
			case SELECTION:
				return refSelectionManager.createSelection( createNewType );
			case SET:
				return refSetManager.createSet( createNewType );
			case STORAGE:
				return refStorageManager.createStorage( createNewType );
			case VIEW:
				if ( createNewType == ManagerObjectType.VIEW_NEW_FRAME ) {
					return refViewCanvasManager.createWorkspace( createNewType, sNewTypeDetails );
				}
				return refViewCanvasManager.createCanvas( createNewType, sNewTypeDetails );
			case COMMAND:
				return refCommandManager.createCommand( sNewTypeDetails );
				
			default:
				throw new CerberusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
						createNewType.toString() );			
		} // end switch
	}
	
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final DParseSaxHandler refSaxHandler) {
		
		assert type!=null: "type is null!";
		
		System.out.println("callback in OneForAllManager");
		
		switch ( type.getGroupType() ) {
		case MEMENTO:
			//return refMementoManager.c();
			assert false: "not implemented";
		case GUI_COMPONENT:
			//return refDComponentManager.createNewId();
			assert false: "not implemented";
		case SELECTION: {
			MementoXML selectionBuffer = 
				refSelectionManager.createSelection( type );
			
			selectionBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case SET: {
			Set setBuffer = 
				this.refSetManager.createSet( type );
			
			//setBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case STORAGE:
			Storage storageBuffer = refStorageManager.createStorage( type );
			
			storageBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
			
		case VIEW:
			
			if ( type == ManagerObjectType.VIEW_NEW_FRAME ) {
				Object setFrame = 
					refViewCanvasManager.createCanvas( type, details );
			
				//setFrame.setMementoXML_usingHandler( refSaxHandler );
				return;
			}
			else {
				ViewCanvas setCanvas = (ViewCanvas)
					refViewCanvasManager.createCanvas( type, details );
				
				setCanvas.setMementoXML_usingHandler( refSaxHandler );
				return;
			}
		
		default:
			throw new CerberusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
					type.name() );			
		} // end switch ( type.getGroupType() )
		
	}

	public GeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		
		assert managerType!=null: "type is null!";
		
		
		switch ( managerType.getGroupType() ) {
		case MEMENTO:
			return refMementoManager;
		case GUI_COMPONENT:
			return refDComponentManager;
		case SELECTION: 
			return refSelectionManager;
		case SET: 
			return refSetManager;
		case STORAGE:
			return refStorageManager;
		case VIEW:
			return refViewCanvasManager;
		case COMMAND:
			return refCommandManager;
		case SWT_GUI:
			return refSWTGUIManager;
		
		default:
			throw new CerberusRuntimeException("Error in OneForAllManager.getManagerByBaseType() unsupported type " +
					managerType.name() );			
		} // end switch ( type.getGroupType() )
	}
	
	
	public ViewCanvasManager getViewCanvasManager() {
		return refViewCanvasManager;
	}
	
	public void setErrorMessage( final String sErrorMsg ) {
		System.err.println("ERROR: " + sErrorMsg);
	}
}
