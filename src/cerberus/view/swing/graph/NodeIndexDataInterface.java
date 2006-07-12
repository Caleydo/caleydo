/**
 * 
 */
package cerberus.view.swing.graph;

/**
 * @author java
 *
 */
public interface NodeIndexDataInterface {

	public int[] getIndexDataOfNode ();
	
	public int[] getIndexDataRecursive();
	
	public int getIndex();
	
	public void setIndexDataOfNode( int[] setData);
	
	public boolean hasNodeIndexData();	
	
}
