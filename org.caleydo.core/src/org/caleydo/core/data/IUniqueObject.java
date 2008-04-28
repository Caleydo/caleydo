package org.caleydo.core.data;

/**
 * Interface to access prometheus.data.manager.CollectionManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IUniqueObject {
	
	/**
	 * Resets the selectionId.
	 * @param iSetCollectionId new unique collection Id
	 */
	public void setId( int iSetCollectionId );
	
	/**
	 * Get a unique Id
	 * 
	 * @return unique Id
	 */
	public int getId();
}
