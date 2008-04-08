/**
 * 
 */
package org.geneview.core.data.collection;

/**
 * @author Michael Kalkusch
 *
 */
public interface IGroupedSelection {
	
	public void setSelectionIdArray( int[] iArSelectionId );
	
	public void setGroupArray( int[] iArSelectionGroup );
	
	public void setOptionalDataArray( int[] iArSelectionOptionalData );
	
	public void setAllSelectionDataArrays(  int[] iArSelectionId,
			int[] iArSelectionGroup,
			int[] iArSelectionOptionalData );
}
