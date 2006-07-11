/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;


/*
 * Methodes for meta data handling.
 */
public interface MetaDataSetInterface {

	/**
	 * Get the meta data from a prometheus.data.collection.selection.Selection
	 */
	public CollectionMetaData getMetaDataAny();
	
	/**
	 * Set the meta data for a selection.
	 */
	public void setMetaDataAny( CollectionMetaData setMetaData );
	
}
