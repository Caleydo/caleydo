/**
 * 
 */
package cerberus.view.swing.graph;

/**
 * @author Michael Kalkusch
 *
 */
public class IndexDataNode extends AbstractNode {

	protected int[] data;
	
	/**
	 * 
	 */
	public IndexDataNode( int iSize) {
		super();
		
		if (( iSize < 3 )&&( iSize > 0 )) {
			data = new int[iSize];
		} else {
			throw new RuntimeException("IndexDataNode size mut be [1..2]");
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#startChildIteration()
	 */
	public void startChildIteration() {
		assert false : "Useless to call this function on a IndexDataNode!";
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#getNextChild()
	 */
	public final NodeInterface getNextChild() {
		assert false : "Useless to call this function on a IndexDataNode!";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#hasChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public final boolean hasChild(NodeInterface testChild) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#addChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public final boolean addChild(NodeInterface addChild) {
		assert false : "Useless to call this function on a IndexDataNode!";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public final boolean removeChild(NodeInterface addChild) {
		assert false : "Useless to call this function on a IndexDataNode!";
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeAllChildren()
	 */
	public final boolean removeAllChildren() {
		assert false : "Useless to call this function on a IndexDataNode!";
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#size()
	 */
	public final int size() {
		return 0;
	}

	
	public void setIndexDataOfNode( int[] setData) {
		if (( setData.length < 3 )&&(setData.length > 0)) {
			if ( setData.length != data.length ) {
				data = new int[setData.length];
				
				for ( int i=0; i< setData.length; i++) {
					data[i] = setData[i];
				}
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataOfNode()
	 */
	public int[] getIndexDataOfNode() {
		return data;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataRecursive()
	 */
	public int[] getIndexDataRecursive() {
		return data;
	}
	

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#hasNodeIndexData()
	 */
	public final boolean hasNodeIndexData() {
		return true;
	}
	
	public final String toStringRecursively(String indent) {
		return data.toString();
	}

	public final int getIndex() {
		assert false : "not tested yet!";
		return this.data[0];
	}
}
