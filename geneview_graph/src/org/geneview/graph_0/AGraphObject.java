/**
 * 
 */
package org.geneview.graph;


import org.geneview.graph.IGraphData;

/**
 * @author java
 *
 */
public abstract class AGraphObject implements IGraphItem {

	private boolean bIsNodeNotEdge;
	
	/**
	 * 
	 */
	protected AGraphObject() {
		
	}

	public final boolean isNode() {
		return bIsNodeNotEdge;
	}

	public final void setIsNode(boolean enable) {
		this.bIsNodeNotEdge = enable;		
	}
	
//	protected abstract void addGraphNode();
//	
//	protected abstract void addGraphEdge();
//	
//	protected abstract void removeGraphNode();
//	
//	protected abstract void removeGraphEdge();


	
}
