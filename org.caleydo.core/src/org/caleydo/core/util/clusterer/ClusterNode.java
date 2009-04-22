package org.caleydo.core.util.clusterer;

import gleem.linalg.Vec3f;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.selection.ESelectionType;

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
	@XmlElement
	private int iNrElements;

	private Vec3f vPos;
	private ESelectionType eSelectionType;
	
	public ClusterNode() {

	}
	
	public ClusterNode(String sNodeName, int iClusterNr, float fCoefficient, int iDepth) {
		this.sNodeName = sNodeName;
		this.iClusterNr = iClusterNr;
		this.fCoefficient = fCoefficient;
		this.iHierarchyDepth = iDepth;
		this.setSelectionType(ESelectionType.NORMAL);
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
		return sNodeName;
	}

	public void setDepth(int iDepth) {
		this.iHierarchyDepth = iDepth;
	}

	public int getDepth() {
		return iHierarchyDepth;
	}

	public void setNrElements(int iNrElements) {
		this.iNrElements = iNrElements;
	}

	public int getNrElements() {
		return iNrElements;
	}

	public void setPos(Vec3f vPos) {
		this.vPos = vPos;
	}

	public Vec3f getPos() {
		return vPos;
	}

	public void setSelectionType(ESelectionType eSelectionType) {
		this.eSelectionType = eSelectionType;
	}

	public ESelectionType getSelectionType() {
		return eSelectionType;
	}
}
