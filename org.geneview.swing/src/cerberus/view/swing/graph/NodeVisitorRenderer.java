/**
 * 
 */
package cerberus.view.swing.graph;

import cerberus.view.swing.graph.NodeInterface;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class NodeVisitorRenderer {

	
	protected NodeInterface rootNode;
	
	/**
	 * 
	 */
	public NodeVisitorRenderer( NodeInterface rootNode ) {
		this.rootNode = rootNode;
	}

	public final NodeInterface getRootNode() {
		return rootNode;
	}
	
	public final void setRootNode( NodeInterface rootNode ) {
		this.rootNode = rootNode;
	}

}
