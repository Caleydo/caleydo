package org.caleydo.core.manager.data.set;

import java.util.Collection;
import java.util.Hashtable;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ACollectionManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;


/**
 * @author Michael Kalkusch
 *
 */
public class SetManager 
extends ACollectionManager
implements ISetManager {
	
	protected Hashtable <Integer, ISet > hashId2Set;
	
	/**
	 * Constructor.
	 */
	public SetManager( IGeneralManager setSingelton,
			final int iInitSizeContainer ) {
		super( setSingelton , 
				IGeneralManager.iUniqueId_TypeOffset_Set,
				ManagerType.DATA_SET );

		assert setSingelton != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
		
		hashId2Set = new Hashtable <Integer, ISet > ();		
	}
	
	/**
	 * Create a test ISet.
	 */
	public void initManager() {

	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SetManager#createSet()
	 */
	public ISet createSet( final ESetType setType ) 
	{
		return new Set(4, generalManager);
		// Check if requested set is a selection set
		/*
		if (setType.equals(SetType.SET_SELECTION))
		{
			return new SetSelection(4, generalManager);
		}
		else
		{
			switch ( setType.getDataType() ) 
			{
			case SET_LINEAR:
				return new SetFlatThreadSimple(4, 
						generalManager,
						null,
						setType);
				
			case SET_PLANAR: 
				return new SetPlanarSimple(4, generalManager);
			
			case SET_MULTI_DIM:  
				return new SetMultiDim(4, 
						generalManager,
						null,
						3,
						setType);
			
			case SET_VIEWCAMERA:
				return new SetViewData(4, 
						generalManager,
						null,
						setType);
				
				// Sets not implemented yet.. 
			case SET_MULTI_DIM_VARIABLE:  
			case SET_CUBIC:
				
			
			default:
				throw new RuntimeException("SetManagerSimple.createSet() failed due to unhandled type [" +
						setType.toString() + "]");
		
			}
	
		}
		*/
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SetManager#deleteSet(org.caleydo.core.data.collection.ISet)
	 */
	public boolean deleteSet(ISet deleteSet ) {
		
		throw new RuntimeException("not impelemtned!");
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SetManager#deleteSet(org.caleydo.core.data.collection.ISet)
	 */
	public boolean deleteSet( final int iItemId ) {
		
		ISet removedObj = hashId2Set.remove( iItemId );
		
		if ( removedObj == null ) {
//			generalManager.logMsg( 
//					"deleteSet(" + 
//					iItemId + ") falied, because Set was not registered!",
//					LoggerType.STATUS );
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SetManager#getItemSet(int)
	 */
	public ISet getSet( final int iItemId) {
		return hashId2Set.get( iItemId );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getSet(iItemId);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SetManager#getAllSetItems()
	 */
	public Collection<ISet> getAllSets() {
		
		return hashId2Set.values();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {
		return hashId2Set.containsKey( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {		
		return hashId2Set.size();
	}
	
	public boolean unregisterItem(final int iItemId) 
	{
		
		ISet buffer = hashId2Set.remove(iItemId);
		
		if  ( buffer == null ) {
//			this.generalManager.logMsg(
//					"unregisterItem(" + 
//					iItemId + ") failed because Set was not registered!",
//					LoggerType.STATUS );
			return false;
		}
		return true;
	}

	public boolean registerItem(final Object registerItem, final int iItemId) 
	{
		try 
		{
			ISet addItem = (ISet) registerItem;
			
			if ( this.hashId2Set.containsKey( iItemId ) ) {
				
				return false;
			}
			
			hashId2Set.put( iItemId, addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false : "cast of object ot storage falied";
			return false;
		}
	
	}
}
