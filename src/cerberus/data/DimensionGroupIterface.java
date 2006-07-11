/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data;

import cerberus.data.DataItemInterface;

import java.util.Collection;

public interface DimensionGroupIterface {

	/// set a label for an index
	void setLabelByIndex( int iIndex, String sSetLabel );
	
	
	/// return the label stored at one index
	String getLabelByIndex( int iIndex );
	
	
	/*
	 * Inserts a data item at the end of the row.
	 */
	void addItemByIndex( int iIndex, DataItemInterface setItem );
	
	
	/*
	 * Get all DataItemInterface-items of one row.
	 * row is addressed by iIndex.
	 * Items contained in Collection are DataItemInterface-items. 
	 */
	Collection getRowCollectionByIndex( int iIndex );
	
	
	/*
	 * Return one specific data item is available.
	 * Else null is returned.
	 * 
	 * Data is organized like this:
	 * 
	 * [row 0]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 1]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 2]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 *   ..
	 */
	DataItemInterface getDataItemByGlobalIndex( int iGlobalItemIndex );
	
	
	/*
	 * Data is organized like this:
	 * 
	 * [row 0]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 1]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 2]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 *   ..
	 */
	DataItemInterface getDataItemByRowIndex( int iRowIndex, int iColumIndex );
	

	/*  return the nuber of active rows
	 * 
	 * Data is organized like this:
	 * 
	 * [row 0]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 1]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 2]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 *   ..
	 */
	int sizeRow();
	
	
	/*  return the nuber of maximum colums.
	 * Note, that not all rows must have the same amount of colums.
	 * 
	 * Data is organized like this:
	 * 
	 * [row 0]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 1]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 2]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 *   ..
	 */
	int sizeMaxColum();
	
	
	/// debug info for hole group.
	String toString();
	
	
	/* defines the number of rows.
	 * Warning, causes reallocation of hole data set.
	 * All stored values are deleted.
	 */
	void setRowSize( int iSetRowSize );
	
}
