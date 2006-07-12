/**
 * 
 */
package cerberus.view.swing.graph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;

/**
 * @author java
 *
 */
public class SingleIndexDataNode extends AbstractNode {

	protected int data;
	
	/**
	 * 
	 */
	public SingleIndexDataNode( int iSize) {
		super();
		
		if (iSize > 0 ) {
			data = iSize;
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

	
	public void setIndexData( int setData) {
		data = setData;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataOfNode()
	 */
	public int getIndexData() {
		return data;
	}
	
	public int[] getIndexDataOfNode() {
		int[] resultArray = new int[1];
		resultArray[0] = data;
		return resultArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataRecursive()
	 */
	public int[] getIndexDataRecursive() {
		return getIndexDataOfNode();
	}
	
	public final void setIndexDataOfNode( int[] setData) {
		throw new RuntimeException("Can not set data to a non-data node");
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#hasNodeIndexData()
	 */
	public final boolean hasNodeIndexData() {
		return true;
	}
	
	public final String toStringRecursively(String indent) {
		return indent + Integer.toString( data );
	}
	
	public int getIndex() {
		return data;
	}

}
