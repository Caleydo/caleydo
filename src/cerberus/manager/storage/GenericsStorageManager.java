/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.storage;

import java.util.Vector;

import cerberus.manager.singelton.SingeltonManager;
import cerberus.manager.type.BaseManagerType;


/**
 * @author Michael Kalkusch
 *
 */
public class GenericsStorageManager< T > 
{

	protected SingeltonManager refSingeltonManager = null;
	
	protected BaseManagerType refBaseManagerType = BaseManagerType.FABRIK;
	
	protected Vector<T> vecItems;
	
	public GenericsStorageManager( SingeltonManager setSingeltonManager,
			final int iInitSizeContainer,
			final BaseManagerType initBaseManagerType) {
		
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
		
	public final BaseManagerType getManagerType() {		
		return refBaseManagerType;
	}
	
} // end class
