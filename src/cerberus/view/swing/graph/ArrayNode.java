/**
 * 
 */
package cerberus.view.swing.graph;

import cerberus.view.swing.graph.AbstractMiddleNode;
import cerberus.view.swing.graph.NodeInterface;

/**
 * @author Michael Kalkusch
 *
 */
public class ArrayNode extends AbstractMiddleNode implements NodeInterface {

	protected NodeInterface[] children;
	
	private int iFillIndex = 0;
	
	private int iIteratorIndex = 0;
	
	private final int iSize;
	
	/**
	 * 
	 */
	public ArrayNode() {
		iSize = 2;
		children = new NodeInterface[iSize];
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#startChildIteration()
	 */
	public void startChildIteration() {
		iIteratorIndex = 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#getNextChild()
	 */
	public NodeInterface getNextChild() {
		if ( iIteratorIndex < iSize) {
			return children[iIteratorIndex++];
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#hasChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean hasChild(NodeInterface testChild) {
		for (int i=0; i < this.iSize; i++ ) {
			if ( children[i].equals( testChild ) ) {
				return true;
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#addChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean addChild(NodeInterface addChild) {
		if ( iFillIndex < (iSize-1) ) {
			children[iFillIndex] = addChild;
			iFillIndex++;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeChild(cerberus.view.swing.graph.NodeInterface)
	 */
	public boolean removeChild(NodeInterface removeChild) {
		for (int i=0; i < iSize; i++ ) {
			if ( children[i].equals( removeChild ) ) {
				
				if ( i == iSize-1) {
					/* last item! */
					children[i] = null;
				}
				else {
					for( int j=i; j < iFillIndex-1;j++) {
						children[j] = children[j+1];					
					}
					children[iFillIndex] = null;
				}
				iFillIndex--;
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#removeAllChildren()
	 */
	public boolean removeAllChildren() {
		for (int i=0; i < iSize; i++ ) {
			children[i] = null;			
		}
		iFillIndex = 0;
		return true;
	}
	
	public final int size() {
		return this.iSize;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeIndexDataInterface#getIndexDataRecursive()
	 */
	public int[] getIndexDataRecursive() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public final String toStringRecursively(String indent) {
		String result = "N:";
		
		if ( attributes != null ) {
			result += attributes.toString();
		}
		
		for ( int i=0; i < children.length; i++ ) {
			if ( children[i] == null ) {
				result += i + ":-";
			}
		}
		
		for ( int i=0; i < children.length; i++ ) {
			if ( children[i] != null ) {
				result += i + ":->\n";
				result += children[i].toStringRecursively(null);
			}
		}
				
		return result;
	}

}
