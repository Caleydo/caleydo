package org.caleydo.core.util.clusterer;

public class ClusterNode
	implements Comparable<ClusterNode> {

	private String sNodeName;
	private int iClusterNr;
	private float fCoefficient;
	private int iHierarchyDepth;
	

	public ClusterNode(String sNodeName, int iClusterNr, float fCoefficient, int iDepth) {
		this.sNodeName = sNodeName;
		this.iClusterNr = iClusterNr;
		this.fCoefficient = fCoefficient;
		this.iHierarchyDepth = iDepth;
	}

	@Override
	public int compareTo(ClusterNode node) {
		return iClusterNr - node.iClusterNr;
	}

	public String getNodeName() {
		return sNodeName;
	}

	public float getCoefficient() {
		return fCoefficient;
	}

	public int getClusterNr() {
		return iClusterNr;
	}
		
	@Override
	public String toString() {
		return sNodeName + " " + iClusterNr;
	}

	public void setDepth(int iDepth) {
		this.iHierarchyDepth = iDepth;
	}

	public int getDepth() {
		return iHierarchyDepth;
	}
}
