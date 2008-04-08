/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection;


/*
 * Methodes for meta data handling.
 */
public interface IMetaDataHandler {

	/**
	 * Get the meta data from a prometheus.data.collection.selection.Selection
	 */
	public IMetaData getMetaData();
	
	/**
	 * ISet the meta data for a selection.
	 */
	public void setMetaData( IMetaData setMetaData );
	
}
