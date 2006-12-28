/**
 * 
 */
package cerberus.view.swing.graph;

/**
 * @author Michael Kalkusch
 *
 */
public interface NodeIndexDataInterface {

	public int[] getIndexDataOfNode ();
	
	public int[] getIndexDataRecursive();
	
	public int getIndex();
	
	public void setIndexDataOfNode( int[] setData);
	
	public boolean hasNodeIndexData();	
	
}
