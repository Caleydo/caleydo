/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.data.collection.ISet;


/**
 * Manges all ISet's.
 * 
 * Note: the SetManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface SetManager
extends GeneralManager
{
	
	public ISet createSet( final ManagerObjectType useSetType );
	
	public boolean deleteSet( ISet deleteSet );
	
	public boolean deleteSet( final int iItemId );
	
	public ISet getItemSet( final int iItemId );
	
	public ISet[] getAllSetItems();
	
	/**
	 * Initialize data structures prior using this manager.
	 *
	 */
	public void initManager();
	
	//public ManagerObjectType getManagerType();
	
}
