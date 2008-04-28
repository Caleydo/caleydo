package org.caleydo.core.data.collection;


/**
 * Methods for meta data handling.
 */
public interface IMetaDataHandler {

	/**
	 * Get the meta data from a caleydo.data.collection.selection.Selection
	 */
	public IMetaData getMetaData();
	
	/**
	 * ISet the meta data for a selection.
	 */
	public void setMetaData( IMetaData setMetaData );
	
}
