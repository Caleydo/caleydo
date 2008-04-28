package org.caleydo.core.data.collection;


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
