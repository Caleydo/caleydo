package org.caleydo.core.manager.data;

import java.util.Collection;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.manager.IManager;


/**
 * Manages all ISets.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetManager
extends IManager
{
	public ISet createSet(final ESetType setType);
	
	public boolean removeSet(ISet deleteSet);
	
	public boolean removeSet(final int iItemId);
	
	public ISet getSet(final int iItemId);
	
	public Collection<ISet> getAllSets();
}
