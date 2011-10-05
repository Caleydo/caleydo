package org.caleydo.core.data.graph.tree;

/**
 * The info object of the corresponding node
 * 
 * @author Helmut Pichlhoefer
 */

class NodeInfo {

	private String sNodeName;

	private boolean bIsRootNode;

	private int iLayer;

	private int iDepth;

	public NodeInfo(String sNodename, boolean bIsRootNode, int iLayer) {
		this.sNodeName = sNodename;
		this.bIsRootNode = bIsRootNode;
		this.iLayer = iLayer;

	}

	public void setNodeName(String sNodeName) {
		this.sNodeName = sNodeName;
	}

	public String getNodeName() {
		return sNodeName;
	}

	public void setIsRootNode(boolean bIsRootNode) {
		this.bIsRootNode = bIsRootNode;
	}

	public boolean isRootNode() {
		return bIsRootNode;
	}

	public void setLayer(int iLayer) {
		this.iLayer = iLayer;
	}

	public int getLayer() {
		return iLayer;
	}

	public void setDepth(int iDepth) {
		this.iDepth = iDepth;
	}

	public int getDepth() {
		return iDepth;
	}

}
