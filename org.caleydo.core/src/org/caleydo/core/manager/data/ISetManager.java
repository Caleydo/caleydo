package org.caleydo.core.manager.data;

import java.util.Collection;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.manager.IManager;


/**
 * Manages all ISet's.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetManager
extends IManager
{
	public ISet createSet( final SetType setType );
	
	public boolean deleteSet( ISet deleteSet );
	
	public boolean deleteSet( final int iItemId );
	
	public ISet getItemSet( final int iItemId );
	
	public Collection<ISet> getAllSetItems();
	
	/**
	 * Initialize data structures prior using this manager.
	 *
	 */
	public void initManager();	
}
