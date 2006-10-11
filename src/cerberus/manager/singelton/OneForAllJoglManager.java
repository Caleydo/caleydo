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

import cerberus.data.collection.IStorage;
import cerberus.data.xml.IMementoXML;


//import cerberus.data.collection.SelectionType; 
//import cerberus.data.collection.SetType;
//import cerberus.data.collection.StorageType;

//import cerberus.data.collection.Selection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.view.IViewCanvas;

import cerberus.manager.ICommandManager;
import cerberus.manager.IDistComponentManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMementoManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ISingelton;
import cerberus.manager.canvas.ViewCanvasManager;
import cerberus.manager.command.CommandManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.data.ISelectionManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.data.pathway.PathwayElementManager;
import cerberus.manager.data.pathway.PathwayManager;
import cerberus.manager.data.selection.SelectionManager;
import cerberus.manager.data.set.SetManager;
import cerberus.manager.data.storage.StorageManager;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;
import cerberus.manager.memento.MementoManager;
import cerberus.manager.menu.swing.SwingMenuManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;

//import cerberus.manager.ViewCanvasManager;
//import cerberus.net.dwt.swing.DHistogramCanvas;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 * @deprecated use OneForAllManager
 */
public class OneForAllJoglManager 
implements IGeneralManagerSingelton {

	protected SingeltonManager refSingeltonManager;
	
	protected ISetManager refSetManager;
	
	protected IStorageManager refStorageManager;
	
	protected ISelectionManager refSelectionManager;
	
	protected IMementoManager refMementoManager;
	
	protected IMenuManager refMenuManager;
	
	protected IDistComponentManager refDComponentManager;
	
	protected CanvasSwingJoglManager refViewManager;
	
	// protected IViewCanvasManager refViewCanvasManager;
	
	protected ICommandManager refCommandManager;
	
	/**
	 * Used to create a new item by a Fabik.
	 * used by cerberus.data.manager.OneForAllManager#createNewId(ManagerObjectType)
	 * 
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewId(ManagerObjectType)
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
	public OneForAllJoglManager( final SingeltonManager sef_SingeltonManager ) {
		
		if ( refSingeltonManager == null ) {
			refSingeltonManager = new SingeltonManager( this );
		} else {
			refSingeltonManager = sef_SingeltonManager;			
		}
		
		initAll();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getSingelton()
	 */
	public final ISingelton getSingelton() {
		return refSingeltonManager;
	}
	
	protected void initAll() {
		
		refMenuManager = new SwingMenuManager( this );
		refSetManager = new SetManager(this,4);
		refStorageManager = new StorageManager(this,4);
		refSelectionManager = new SelectionManager(this,4);
		refMementoManager = new MementoManager(this);
		refDComponentManager = new DComponentSwingFactoryManager(this);
//		refViewCanvasManager = new ViewCanvasManagerSimple( this );
		refViewManager = new CanvasSwingJoglManager( this );
		refCommandManager = new CommandManager( this );
		refMenuManager = new SwingMenuManager( this );
		
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
		
		//refSingeltonManager.setPathwayElementManager( PathwayElementManager.getInstance() );
		//refSingeltonManager.setPathwayManager( PathwayManager.getInstance() );
		
		
		
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
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
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
	public ICommandManager getCommandManager() {
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
	public IGeneralManager getGeneralManager() {
		return this;
	}


	/* (non-Javadoc)
	 * @see cerberus.data.manager.singelton.SingeltonManager#getCurrentType()
	 */
	public final ManagerObjectType getCurrentType() {
		return this.setCurrentType;
	}
	
	/**
	 * ISet the current type used to create the next Id using
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId()
	 * Does not influence 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
	 * or 
	 * cerberus.data.manager.singelton.OneForAllManager#createNewItem(ManagerObjectType, String)
	 * .
	 * 
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewId(ManagerObjectType)
	 * @see cerberus.manager.singelton.OneForAllJoglManager#createNewItem(ManagerObjectType, String)
	 * @see cerberus.manager.singelton.OneForAllJoglManager#getCurrentType()
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
				throw new RuntimeException("Error VIEWS create thier own Id's!");
				//return refViewManager.createNewId();
				//return refViewCanvasManager.createNewId(setNewBaseType);
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
				return this.refViewManager.unregisterItem( iItemId, type );
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
			
			case COMMAND:
				throw new CerberusRuntimeException("Error in OneForAllManager.registerItem() type " +
						type.name() + " can niot register!");	
				
			case VIEW:
//				return refViewCanvasManager.registerItem( 
//						registerItem, iItemId, type );
				
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
				StringTokenizer tokenizer = new StringTokenizer(sNewTypeDetails, 
						CommandFactory.sDelimiter_Parser_DataType );
				
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
				
//				if ( createNewType == ManagerObjectType.VIEW_NEW_FRAME ) {
//					
//					//return refViewManager.createNewJFrame( sNewTypeDetails );
//				}
//				else if ( createNewType == ManagerObjectType.VIEW_NEW_IFRAME ) {
//					String[] details_array = sNewTypeDetails.split(";");
//										
//					return refViewManager.createNewJInternalFrame( 
//							details_array[0],
//							Integer.getInteger( details_array[1] ));
//				}
//				
//				assert false: "not implemented";
				
//				return refViewCanvasManager.createCanvas( createNewType, sNewTypeDetails );
				
//				if ( createNewType == ManagerObjectType.VIEW_NEW_FRAME ) {
//					return refViewCanvasManager.createWorkspace( createNewType, sNewTypeDetails );
//				}
//				return refViewCanvasManager.createCanvas( createNewType, sNewTypeDetails );
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
			final ISaxParserHandler refSaxHandler) {
		
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
			IMementoXML selectionBuffer = 
				refSelectionManager.createSelection( type );
			
			selectionBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case SET: {
			ISet setBuffer = 
				this.refSetManager.createSet( type );
			
			//setBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
		}
		case STORAGE:
			IStorage storageBuffer = refStorageManager.createStorage( type );
			
			storageBuffer.setMementoXML_usingHandler( refSaxHandler );
			return;
			
		case VIEW:
			
			createNewItem( type, details );
			return;
			
//			if ( type == ManagerObjectType.VIEW_NEW_FRAME ) {
//				Object setFrame = 
//					refViewManager.createNewJFrame( details );
//					//refViewCanvasManager.createCanvas( type, details );
//			
//				//setFrame.setMementoXML_usingHandler( refSaxHandler );
//				return;
//			}
//			else if ( type == ManagerObjectType.VIEW_NEW_FRAME ) {
//				String[] detailsArray = details.split( ";" );
//				Object setFrame = 
//					refViewManager.createNewJInternalFrame( detailsArray[0],
//							Integer.valueOf( detailsArray[1] ).intValue()  );
//			}
//			else {
////				IViewCanvas setCanvas = (IViewCanvas)
////					refViewCanvasManager.createCanvas( type, details );
////				
////				setCanvas.setMementoXML_usingHandler( refSaxHandler );
//				return;
//			}
		
		default:
			throw new CerberusRuntimeException("Error in OneForAllManager.createNewId() unknown type " +
					type.name() );			
		} // end switch ( type.getGroupType() )
		
	}

	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		
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
			throw new CerberusRuntimeException("Error in OneForAllManager.getManagerByBaseType() unsupported type " +
					managerType.name() );			
		} // end switch ( type.getGroupType() )
	}
	
	
//	public IViewCanvasManager getViewCanvasManager() {		
//		return null;
//		//return refViewManager;
//	}
	
	public void setErrorMessage( final String sErrorMsg ) {
		System.err.println("ERROR: " + sErrorMsg);
	}
}
