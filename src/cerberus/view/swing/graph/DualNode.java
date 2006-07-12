/**
 * 
 */
package cerberus.view.swing.graph;

import cerberus.view.swing.graph.AbstractMiddleNode;
import cerberus.view.swing.graph.NodeInterface;

/**
 * Node with only two child nodes.
 * 
 * @author kalkusch
 *
 */
public class DualNode extends AbstractMiddleNode implements NodeInterface {

	protected NodeInterface left;
	
	protected NodeInterface right;
	
	private boolean bIteratorIndex = false;

	/**
	 * 
	 */
	public DualNode() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#startChildIteration()
	 */
	public void startChildIteration() {
		bIteratorIndex = false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#getNextChild()
	 */
	public NodeInterface getNextChild() {
		if ( bIteratorIndex ) {
			return right;
		}
		bIteratorIndex = true;
		return left;
	}


	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#hasChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean hasChild(NodeInterface testChild) {
		if ( left.equals( testChild )
				||(right.equals( testChild ) )) {
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#addChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean addChild(NodeInterface addChild) {
		if ( left == null ) {
			left = addChild;
			return true;
		}
		if ( right == null ) {
			right = addChild;
			return true;
		}
		return false;
	}
	
	public boolean isLeftNodeSet() {
		if ( this.left == null ) {
			return false;
		}
		return true;
	}
	
	public boolean isRightNodeSet() {
		if ( this.right == null ) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean removeChild(NodeInterface removeChild) {
		
		if ( left.equals( removeChild ) ) {
			left = right;
			right = null;
			return true;
		}
		if ( right.equals( removeChild ) ) {
			right = null;
			return true;
		}		
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeAllChildren()
	 */
	public boolean removeAllChildren() {
		left = null;
		right = null;
		return true;
	}
	
	public final int size() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataRecursive()
	 */
	public int[] getIndexDataRecursive() {
		
		return null;
	}

	public final String toString() {
		String result = "N:";
		
		if ( attributes != null ) {
			result += attributes.toString();
		}
		
		if ( parent == null ) {
			result += "p-";
		} else {
			result += "p+";
		}
		
		if ( left == null ) {
			result +="L:-";
		} else {
			result +="L:+";
		}
		if ( right == null ) {
			result +="R:-";
		} else {
			result +="R:+";
		}
		
		return result;
	}
	
	public final String toStringRecursively(String indent) {
		String result = indent + this.toString();
		
		if ( left != null ) {
//			result += "\n" + indent+ "L:->\n";
			result += "\n" + left.toStringRecursively( indent + INDENT_PER_LEVEL );
		}
		if ( right != null ) {
//			result += "\n" + indent+ "R:->\n";
			result += "\n" + right.toStringRecursively( indent + INDENT_PER_LEVEL );
		}
		result += "\n" + indent + "-";
		
		return result;
	}
}
