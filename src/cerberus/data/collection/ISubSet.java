/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import cerberus.data.collection.ISet;

/**
 * @author Michael Kalkusch
 *
 */
public interface ISubSet {

	public ISet[] getSubSets();
	
	public boolean hasSubSets();
	
	public boolean addSubSet( final ISet addSet );
	
	public boolean swapSubSet( ISet fromSet, ISet toSet );
	
	public boolean removeSubSet( final ISet addSet );
	
}
