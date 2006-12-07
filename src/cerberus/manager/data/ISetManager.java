/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data;

import java.util.Collection;

import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.data.collection.ISet;


/**
 * Manges all ISet's.
 * 
 * Note: the ISetManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetManager
extends IGeneralManager
{
	
	public ISet createSet( final CommandQueueSaxType useSetType );
	
	public boolean deleteSet( ISet deleteSet );
	
	public boolean deleteSet( final int iItemId );
	
	public ISet getItemSet( final int iItemId );
	
	public Collection<ISet> getAllSetItems();
	
	/**
	 * Initialize data structures prior using this manager.
	 *
	 */
	public void initManager();
	
	//public ManagerObjectType getManagerType();
	
}
