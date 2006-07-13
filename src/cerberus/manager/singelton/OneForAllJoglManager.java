/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import java.util.StringTokenizer;

import cerberus.data.collection.Storage;
import cerberus.data.xml.MementoXML;


//import cerberus.data.collection.SelectionType; 
//import cerberus.data.collection.SetType;
//import cerberus.data.collection.StorageType;

//import cerberus.data.collection.Selection;
import cerberus.data.collection.Set;
import cerberus.data.collection.view.ViewCanvas;

import cerberus.manager.CommandManager;
import cerberus.manager.DComponentManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.MementoManager;
import cerberus.manager.MenuManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.canvas.ViewCanvasManagerSimple;
import cerberus.manager.command.CommandManagerSimple;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
import cerberus.manager.memento.MementoManagerSimple;
import cerberus.manager.menu.MenuManagerSimple;
import cerberus.manager.selection.SelectionManagerSimple;
import cerberus.manager.set.SetManagerSimple;
import cerberus.manager.storage.StorageManagerSimple;
import cerberus.manager.type.BaseManagerType;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;

//import cerberus.manager.ViewCanvasManager;
//import cerberus.net.dwt.swing.DHistogramCanvas;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class OneForAllJoglManager 
implements GeneralManagerSingelton {

	protected SingeltonManager refSingeltonManager;
	
	protected SetManager refSetManager;
	
	protected StorageManager refStorageManager;
	
	protected SelectionManager refSelectionManager;
	
	protected MementoManager refMementoManager;
	
	protected MenuManager refMenuManager;
	
	protected DComponentManager refDComponentManager;
	
	protected CanvasSwingJoglManager refViewManager;
	
	// protected ViewCanvasManager refViewCanvasManager;
	
	protected CommandManager refCommandManager;
	
	/**
	 * Used to create a new item by a Fabik.
	 * used by cerberus.data.manager.OneForAllManager#createNewId(BaseManagerType)
	 * 
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewId(BaseManagerType)
	 */
	protected BaseManagerType setCurrentType = BaseManagerType.ALL_IN_ONE;
	
//	protected SelectionType initSelectionType;
//	
//	protected SetType 		initSetType;
//	
//	protected StorageType 	initStorageType;
	
	/**
	 * 
	 */
	public OneForAllJoglManager( final SingeltonManager sef_SingeltonManager ) {
		
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
	public final SingeltonManager getSingelton() {
		return refSingeltonManager;
	}
	
	protected void initAll() {
		
		refMenuManager = new MenuManagerSimple( this );
		refSetManager = new SetManagerSimple(this,4);
		refStorageManager = new StorageManagerSimple(this,4);
		refSelectionManager = new SelectionManagerSimple(this,4);
		refMementoManager = new MementoManagerSimple(this);
		refDComponentManager = new DComponentSwingFactoryManager(this);
//		refViewCanvasManager = new ViewCanvasManagerSimple( this );
		refViewManager = new CanvasSwingJoglManager( this );
		refCommandManager = new CommandManagerSimple( this );
		refMenuManager = new MenuManagerSimple( this );
		
		/**
		 * Register managers to singelton ...
		 */
		refSingeltonManager.setCommandManager( refCommandManager );
		refSingeltonManager.setDComponentManager( refDComponentManager );
//		refSingeltonManager.setViewCanvasManager( refViewCanvasManager );
		refSingeltonManager.setSelectionManager( refSelectionManager );
		refSingeltonManager.setSetManager( refSetManager );
		refSingeltonManager.setStorageManager( refStorageManager );
		refSingeltonManager.setMenuManager( refMenuManager );
		
		/**
		 * start threads..
		 */
		
		String[] args = new String[0];
		
		refViewManager.run(args);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem( final int iItemId) {
		
		if ( refSetManager.hasItem( iItemId ) ) return true;
		if ( refSelectionManager.hasItem( iItemId ) ) return true;
		if ( refStorageManager.hasItem( iItemId ) ) return true;
		if ( refDComponentManager.hasItem( iItemId ) ) return true;
		if ( refMementoManager.hasItem( iItemId ) ) return true;
		if ( refViewManager.hasItem( iItemId ) ) return true;
		
		//FIXME Is next line useless?
		if ( refCommandManager.hasItem( iItemId ) ) return true;
		
		return false;
	}
	
	/**
	 * @see cerberus.manager.GeneralManager#hasItem(int)
	 * 
	 * @param iItemId unique Id used for lookup
	 * @return Object bound to Id or null, if id was not found.
	 */
	public Object getItem( final int iItemId) {
		
		if ( refSetManager.hasItem( iItemId ) ) {
			return refSetManager.getItemSet( iItemId );
		}
		if ( refSelectionManager.hasItem( iItemId ) ) {
			return refSelectionManager.getItemSelection( iItemId );
		}
		if ( refStorageManager.hasItem( iItemId ) ) {
			return refStorageManager.getItemStorage( iItemId );
		}
		if ( refDComponentManager.hasItem( iItemId ) ) {
			return refDComponentManager.getItemSet( iItemId );
		}
		if ( refMementoManager.hasItem( iItemId ) ) {
			return refMementoManager.getMemento( iItemId );
		}		
		if ( refViewManager.hasItem( iItemId )) {
			return refViewManager.getItem( iItemId );
		}
		if ( refCommandManager.hasItem( iItemId )) {
			return refCommandManager.getItem( iItemId );
		}
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
		refDComponentManager.size() );
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
	public BaseManagerType getManagerType() {
		return BaseManagerType.ALL_IN_ONE;
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
	public final BaseManagerType getCurrentType() {
		return this.setCurrentType;
	}
	
	/**
	 * Set the current type used to create the next Id using
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId()
	 * Does not influence 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId(BaseManagerType)
	 * or 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewItem(BaseManagerType, String)
	 * .
	 * 
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewId(BaseManagerType)
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewItem(BaseManagerType, String)
	 * @see cerberus.manager.singelton.OneForAllJoglManager#getCurrentType()
	 * 
	 * @param setCurrentType
	 */
	protected final void setCurrentType( BaseManagerType setCurrentType) {
		this.setCurrentType = setCurrentType;
	}
	
//	/**
//	 * Create a new Id using the BaseManagerType set with 
//	 */
//	public final int createNewId() {
//		return this.createNewId( setCurrentType );
//	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public final int createNewId(final BaseManagerType setNewBaseType) {
	
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
				throw new RuntimeException("Error VIEWS create thier own Id's!");
				//return refViewManager.createNewId();
				//return refViewCanvasManager.createNewId(setNewBaseType);
			case COMMAND:
				return refCommandManager.createNewId(setNewBaseType);
				
			default:
				throw new PrometheusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
						setNewBaseType.toString() );			
		}
	
	}

	public boolean unregisterItem( final int iItemId,
			final BaseManagerType type  ) {
		
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
				return this.refViewManager.unregisterItem( iItemId, type );
			case COMMAND:
				throw new PrometheusRuntimeException("Error in OneForAllManager.unregisterItem() type " +
						type.name() + " can not unregister!");
			/**
			 * Note: refCommandManager can not unregister items.
			 */
				
			default:
				throw new PrometheusRuntimeException("Error in OneForAllManager.unregisterItem() unknown type " +
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
			final BaseManagerType type ) {
		
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
			
			case COMMAND:
				throw new PrometheusRuntimeException("Error in OneForAllManager.registerItem() type " +
						type.name() + " can niot register!");	
				
			case VIEW:
//				return refViewCanvasManager.registerItem( 
//						registerItem, iItemId, type );
				
			default:
				throw new PrometheusRuntimeException("Error in OneForAllManager.registerItem() unknown type " +
						type.name() );			
			} // end switch
		}
		
		assert false: "must use type!";
		
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#createNewItem(cerberus.data.manager.BaseManagerType, java.lang.String)
	 */
	public Object createNewItem( final BaseManagerType createNewType,
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
				StringTokenizer tokenizer = new StringTokenizer(sNewTypeDetails, sXMLDelimiter);
				
				FrameBaseType frameType;
				int iUniqueId = -1;
				int iUniqueParentId = -1;
				
				int iCountTokens = tokenizer.countTokens();
				
				if ( iCountTokens > 1 ) {
					frameType = FrameBaseType.valueOf( tokenizer.nextToken() );
					iUniqueId = Integer.parseInt( tokenizer.nextToken() );
					
					if ( iCountTokens == 3) {
						iUniqueParentId = Integer.parseInt( tokenizer.nextToken() );
					} 
					else if ( iCountTokens > 3) {
						throw new RuntimeException("Can not create new new Frame, " +
								"because parameters do not match [" +
								sNewTypeDetails + "]" );
					}
				} 
				else {
					throw new RuntimeException("Can not create new new Frame, " +
							"because parameters do not match [" +
							sNewTypeDetails + "]" );
				}
				
				return refViewManager.addWindow( frameType, iUniqueId, iUniqueParentId );
				
//				if ( createNewType == BaseManagerType.VIEW_NEW_FRAME ) {
//					
//					//return refViewManager.createNewJFrame( sNewTypeDetails );
//				}
//				else if ( createNewType == BaseManagerType.VIEW_NEW_IFRAME ) {
//					String[] details_array = sNewTypeDetails.split(";");
//										
//					return refViewManager.createNewJInternalFrame( 
//							details_array[0],
//							Integer.getInteger( details_array[1] ));
//				}
//				
//				assert false: "not implemented";
				
//				return refViewCanvasManager.createCanvas( createNewType, sNewTypeDetails );
				
//				if ( createNewType == BaseManagerType.VIEW_NEW_FRAME ) {
//					return refViewCanvasManager.createWorkspace( createNewType, sNewTypeDetails );
//				}
//				return refViewCanvasManager.createCanvas( createNewType, sNewTypeDetails );
			case COMMAND:
				return refCommandManager.createCommand( sNewTypeDetails );
				
			default:
				throw new PrometheusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
						createNewType.toString() );			
		} // end switch
	}
	
	public void callbackForParser( final BaseManagerType type,
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
			
			createNewItem( type, details );
			return;
			
//			if ( type == BaseManagerType.VIEW_NEW_FRAME ) {
//				Object setFrame = 
//					refViewManager.createNewJFrame( details );
//					//refViewCanvasManager.createCanvas( type, details );
//			
//				//setFrame.setMementoXML_usingHandler( refSaxHandler );
//				return;
//			}
//			else if ( type == BaseManagerType.VIEW_NEW_FRAME ) {
//				String[] detailsArray = details.split( ";" );
//				Object setFrame = 
//					refViewManager.createNewJInternalFrame( detailsArray[0],
//							Integer.valueOf( detailsArray[1] ).intValue()  );
//			}
//			else {
////				ViewCanvas setCanvas = (ViewCanvas)
////					refViewCanvasManager.createCanvas( type, details );
////				
////				setCanvas.setMementoXML_usingHandler( refSaxHandler );
//				return;
//			}
		
		default:
			throw new PrometheusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
					type.name() );			
		} // end switch ( type.getGroupType() )
		
	}

	public GeneralManager getManagerByBaseType(BaseManagerType managerType) {
		
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
			return null;
			//return refViewManager;
			//return refViewCanvasManager;
		case COMMAND:
			return refCommandManager;
		
		default:
			throw new PrometheusRuntimeException("Error in OneForAllManager.getManagerByBaseType() unsupported type " +
					managerType.name() );			
		} // end switch ( type.getGroupType() )
	}
	
	
//	public ViewCanvasManager getViewCanvasManager() {		
//		return null;
//		//return refViewManager;
//	}
	
	public void setErrorMessage( final String sErrorMsg ) {
		System.err.println("ERROR: " + sErrorMsg);
	}
}
