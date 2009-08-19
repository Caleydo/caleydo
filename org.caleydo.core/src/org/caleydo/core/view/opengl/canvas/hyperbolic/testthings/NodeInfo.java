package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

public class NodeInfo {
	
	private String sNodeName;

	private boolean bIsRootNode;
	
	private int iNumberOfKids;
	
	private int iNumberOfSiblings;
	
	private int iLayer;
	
	public NodeInfo(String sNodename, boolean bIsRootNode, int iLayer ){ 
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

	public void increaseNumberOfKids() {
		this.iNumberOfKids++;
	}

	public int getNumberOfKids() {
		return iNumberOfKids;
	}

	public void increaseiNumberOfSiblings() {
		this.iNumberOfSiblings++;
	}

	public int getNumberOfSiblings() {
		return iNumberOfSiblings;
	}

	public void setLayer(int iLayer) {
		this.iLayer = iLayer;
	}

	public int getLayer() {
		return iLayer;
	}

}
