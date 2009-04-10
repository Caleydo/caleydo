package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterNode
	implements Comparable<ClusterNode> {

	@XmlAttribute
	private String sNodeName;
	@XmlElement
	private int iClusterNr;
	@XmlElement
	private float fCoefficient;
	@XmlElement
	private int iHierarchyDepth;
	
	public ClusterNode() {

	}
	
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
		return sNodeName + "_" + iClusterNr;
	}

	public void setDepth(int iDepth) {
		this.iHierarchyDepth = iDepth;
	}

	public int getDepth() {
		return iHierarchyDepth;
	}
}
