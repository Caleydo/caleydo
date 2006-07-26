/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.dcomponent;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.DComponentManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.data.CollectionManager;
import cerberus.manager.type.ManagerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.singelton.SingeltonManager;

import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.base.DGuiComponentType;

import cerberus.net.dwt.swing.component.DButton;
import cerberus.net.dwt.swing.component.DPanel;

import cerberus.util.exception.CerberusRuntimeException;

//import cerberus.net.dwt.swing.DViewHistogram;

/**
 * @author Michael Kalkusch
 *
 */
public class DComponentSwingFactoryManager 
 extends AbstractManagerImpl
 implements DComponentManager {
	
	protected Vector<DNetEventComponentInterface> vecGuiComponents;
	
	protected Hashtable<Integer,DNetEventComponentInterface> hashGuiIndexLookup;
	
	private int iCurrentUniqueComponentId;
	
	protected GeneralManager refGeneralManager = null;
	
	/**
	 * 
	 */
	public DComponentSwingFactoryManager(GeneralManager setGeneralManager) {
		
		super( setGeneralManager);
		
		assert setGeneralManager != null: "DComponentSwingFactoryManager.DComponentSwingFactoryManager() init with null-pointer.";
		
		refGeneralManager = setGeneralManager;
		
		vecGuiComponents = new Vector<DNetEventComponentInterface> ();
		hashGuiIndexLookup = new Hashtable<Integer,DNetEventComponentInterface> ();
		
		iCurrentUniqueComponentId = 
			CollectionManager.calculateId( 
					GeneralManager.iUniqueId_TypeOffset_GuiComponent, 
					setGeneralManager );
		
		refGeneralManager.getSingelton().setDComponentManager( this );
	}


	/* (non-Javadoc)
	 * @see cerberus.data.manager.DComponentManager#createSet(cerberus.net.dwt.DNetEventType)
	 */
	public DNetEventComponentInterface createSet( final DGuiComponentType useSetType) {
		
		DNetEventComponentInterface refResult = null;

		
		switch ( useSetType ) {
			case BUTTON:
				refResult = new DButton();
				break;
			case PANEL:
				refResult = new DPanel();
				break;
			default:
				throw new CerberusRuntimeException("The type " + useSetType.toString() + " is not supported yet.");
				//return null;
		}
		
		final int iNewUniqueId = this.createNewId( ManagerObjectType.MEMENTO );
		refResult.setId( iNewUniqueId );
		
		vecGuiComponents.add( refResult );
		hashGuiIndexLookup.put( iNewUniqueId, refResult );
		
		return refResult;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.MementoManager#createMementoId()
	 */
	 public int createNewId( final ManagerObjectType setNewBaseType ) {
		 if ( setNewBaseType.getGroupType() == ManagerType.GUI_COMPONENT ) {
			 iCurrentUniqueComponentId += GeneralManager.iUniqueId_Increment;
			 return iCurrentUniqueComponentId;
		 }
		 throw new CerberusRuntimeException("createNewId() called with non GUI_COMPONENT type.");
	 }

	/* (non-Javadoc)
	 * @see cerberus.data.manager.DComponentManager#deleteSet(int)
	 */
	public boolean deleteSet(int iNetEventId) {
		try {
			DNetEventComponentInterface removeItem = 
				hashGuiIndexLookup.get( new Integer(iNetEventId) );
			vecGuiComponents.remove( removeItem );
			
			hashGuiIndexLookup.remove( new Integer(iNetEventId) );
		}
		catch (NullPointerException ne) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.DComponentManager#getItemSet(int)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public DNetEventComponentInterface getItemSet( final int iNetEventId) {
		try {
			return hashGuiIndexLookup.get( new Integer(iNetEventId) );
		}
		catch (NullPointerException ne) {
			return null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getItemSet(iItemId);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.DComponentManager#getAllSetItems()
	 */
	public Iterator<DNetEventComponentInterface> getIteratorComponents() {
		return this.vecGuiComponents.iterator();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem( final int iItemId) {
		return hashGuiIndexLookup.containsKey( new Integer( iItemId) );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size() {
		return this.vecGuiComponents.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.GUI_COMPONENT;
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		assert false:"not done yet";
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type ) {
		
		
		assert false:"not done yet";
		return false;
	}

}
