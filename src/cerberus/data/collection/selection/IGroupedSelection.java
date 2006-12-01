/**
 * 
 */
package cerberus.data.collection.selection;


/**
 * @author kalkusch
 *
 */
public interface IGroupedSelection {

	public int[] getSelectionIdArray();
	
	public int[] getGroupArray();
	
	public int[] getOptionalDataArray();
	
	public void setSelectionIdArray( int[] iArSelectionId );
	
	public void setGroupArray( int[] iArSelectionGroup );
	
	public void setOptionalDataArray( int[] iArSelectionOptionalData );
	
	public int getParentContainerId();
	
	public void setParentContainerId( final int iParentContainerId );
	
}
