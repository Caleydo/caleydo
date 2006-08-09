/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data;

import cerberus.data.IDataItem;

import java.util.Collection;

public interface IDimensionGroup {

	/// set a label for an index
	void setLabelByIndex( int iIndex, String sSetLabel );
	
	
	/// return the label stored at one index
	String getLabelByIndex( int iIndex );
	
	
	/*
	 * Inserts a data item at the end of the row.
	 */
	void addItemByIndex( int iIndex, IDataItem setItem );
	
	
	/*
	 * Get all IDataItem-items of one row.
	 * row is addressed by iIndex.
	 * Items contained in Collection are IDataItem-items. 
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
	IDataItem getDataItemByGlobalIndex( int iGlobalItemIndex );
	
	
	/*
	 * Data is organized like this:
	 * 
	 * [row 0]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 1]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 * [row 2]:  [colum 0] [colum 1] [colum 2] ... [colom n]
	 *   ..
	 */
	IDataItem getDataItemByRowIndex( int iRowIndex, int iColumIndex );
	

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
