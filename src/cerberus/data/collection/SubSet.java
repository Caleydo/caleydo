/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import cerberus.data.collection.Set;

/**
 * @author Michael Kalkusch
 *
 */
public interface SubSet {

	public Set[] getSubSets();
	
	public boolean hasSubSets();
	
	public boolean addSubSet( final Set addSet );
	
	public boolean swapSubSet( Set fromSet, Set toSet );
	
	public boolean removeSubSet( final Set addSet );
	
}
