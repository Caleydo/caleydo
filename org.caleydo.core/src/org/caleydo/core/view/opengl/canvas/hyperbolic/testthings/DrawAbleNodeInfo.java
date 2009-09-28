package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

public class DrawAbleNodeInfo {

	private String sNodeName;

	private int iHierarchyDepth;

	private boolean bIsRootNode;

	public void setNodeName(String sNodeName) {
		this.sNodeName = sNodeName;
	}

	public String getNodeName() {
		return sNodeName;
	}

	public void setHierarchyDepth(int iHierarchyDepth) {
		this.iHierarchyDepth = iHierarchyDepth;
	}

	public int getHierarchyDepth() {
		return iHierarchyDepth;
	}

	public void setIsRootNode(boolean bIsRootNode) {
		this.bIsRootNode = bIsRootNode;
	}

	public boolean isRootNode() {
		return bIsRootNode;
	}

}
