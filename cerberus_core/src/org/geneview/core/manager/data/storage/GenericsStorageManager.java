/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager.data.storage;

import java.util.Vector;

import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.type.ManagerObjectType;


/**
 * @author Michael Kalkusch
 *
 */
public class GenericsStorageManager< T > 
{

	protected ISingelton refSingeltonManager = null;
	
	protected ManagerObjectType refBaseManagerType = ManagerObjectType.STORAGE;
	
	protected Vector<T> vecItems;
	
	public GenericsStorageManager( ISingelton setSingeltonManager,
			final int iInitSizeContainer,
			final ManagerObjectType initBaseManagerType) {
		
		refBaseManagerType = initBaseManagerType;
		
		vecItems = new Vector< T >(iInitSizeContainer);
		
		refSingeltonManager = setSingeltonManager;
	}
	
	public boolean add( T newItem ) {

		if ( vecItems.contains( newItem ) ) {
			return false;
		}
		
		vecItems.add( newItem );
		
		return true;
	}
	
	public boolean delete( T deleteItem ) {
		
		return vecItems.remove( deleteItem );		
	}
	
	public T getItem( int iItemId ) {
		try {
			return vecItems.get( iItemId );
		} catch( ArrayIndexOutOfBoundsException ae) {
			throw new RuntimeException("ERROR: wrong index " + ae.toString());
		}
	}
	
	public <T> T[] getAllItems() {
		//T[] returnArray = (T[]) new Object[ vecItems.size() ];
		
		try {
			return (T[]) vecItems.toArray( );
		} 
		catch (NullPointerException ne) {
			assert false: "GenericsStorageManager.getAllItems() NullPointerException because vector is empty.";
			return null;
		}
	}
	
	public boolean hasItem( int iItemId ) {
		if ( vecItems.isEmpty() ) {
			return false;
		}
		
		if ( iItemId < vecItems.size() ) {
			return true;
		}
		
		return false;
	}
		
	public final ManagerObjectType getManagerType() {		
		return refBaseManagerType;
	}
	
} // end class
