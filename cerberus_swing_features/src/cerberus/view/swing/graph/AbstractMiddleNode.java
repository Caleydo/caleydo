/**
 * 
 */
package cerberus.view.swing.graph;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AbstractMiddleNode extends AbstractNode {

	/**
	 * 
	 */
	public AbstractMiddleNode() {
		super();
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataOfNode()
	 */
	public final int[] getIndexDataOfNode() {
		throw new RuntimeException("Can not get data to a non-data node");
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#hasNodeIndexData()
	 */
	public final boolean hasNodeIndexData() {
		return false;
	}

	public final void setIndexDataOfNode( int[] setData) {
		throw new RuntimeException("Can not set data to a non-data node");
	}

	public final int getIndex() {
		assert false : "do not call this method";
		return -1;
	}
}
