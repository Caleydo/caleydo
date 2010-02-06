package org.caleydo.core.util.clusterer;

import gleem.linalg.Vec3f;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.selection.SelectionType;

/**
 * Implementation of a node used in the cluster tree. Cluster node contains information needed in the
 * dendrogram and the radial hierarchy view. Additionally cluster node implements {@link Comparable}.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterNode
	implements IHierarchyData<ClusterNode> {

	@XmlAttribute
	private String sNodeName;
	@XmlElement
	private int iClusterNr;
	@XmlElement
	private int iLeaveID;
	@XmlElement
	private float fCoefficient;
	@XmlElement
	private int iHierarchyDepth;
	@XmlElement
	private int iNrElements;
	@XmlElement
	private Vec3f vPos;
	@XmlElement
	private SelectionType SelectionType;
	@XmlElement
	private boolean bIsRootNode;
	@XmlElement
	private float fAverageExpressionValue;
	@XmlElement
	private float fStandardDeviation;

	private boolean bIsPartOfSubTree = false;
	private Vec3f vPosSubTree;

	// @XmlElement
	// private float[] fArRepresentativeElement;

	public ClusterNode() {

	}

	public ClusterNode(String sNodeName, int iClusterNr, float fCoefficient, int iDepth, boolean bIsRootNode, int iLeaveID) {
		this.sNodeName = sNodeName;
		this.iClusterNr = iClusterNr;
		this.iLeaveID = iLeaveID;
		this.fCoefficient = fCoefficient;
		this.iHierarchyDepth = iDepth;
		this.bIsRootNode = bIsRootNode;
		this.SelectionType = SelectionType.NORMAL;
		this.fAverageExpressionValue = 0f;
		this.fStandardDeviation = 0f;
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

	public void setSelectionType(SelectionType SelectionType) {
		this.SelectionType = SelectionType;
	}

	public SelectionType getSelectionType() {
		return SelectionType;
	}

	public void togglSelectionType() {
		this.SelectionType =
			(SelectionType == SelectionType.SELECTION) ? SelectionType.NORMAL : SelectionType.SELECTION;
	}

	public boolean isRootNode() {
		return bIsRootNode;
	}

	public void setAverageExpressionValue(float fAverageExpressionValue) {
		this.fAverageExpressionValue = fAverageExpressionValue;
	}

	public float getAverageExpressionValue() {
		return fAverageExpressionValue;
	}

	public void setStandardDeviation(float fStandardDeviation) {
		this.fStandardDeviation = fStandardDeviation;
	}

	public float getStandardDeviation() {
		return fStandardDeviation;
	}

	public void setIsPartOfSubTree(boolean bIsPartOfSubTree) {
		this.bIsPartOfSubTree = bIsPartOfSubTree;
	}

	public boolean isPartOfSubTree() {
		return bIsPartOfSubTree;
	}

	public void setPosSubTree(Vec3f vPosSubTree) {
		this.vPosSubTree = vPosSubTree;
	}

	public Vec3f getPosSubTree() {
		return vPosSubTree;
	}
	
	public int getLeaveID() {
		return iLeaveID;
	}

	@Override
	public String getLabel() {
		return sNodeName;
	}

	@Override
	public float getSize() {
		return iNrElements;
	}

	@Override
	public int getComparableValue() {
		return iClusterNr;
	}

	@Override
	public int getID() {
		return iClusterNr;
	}

	@Override
	public int compareTo(ClusterNode o) {
		return iClusterNr - o.iClusterNr;
	}

	// public void setRepresentativeElement(float[] fArRepresentativeElement) {
	// this.fArRepresentativeElement = fArRepresentativeElement;
	// }
	//
	// public float[] getRepresentativeElement() {
	// return fArRepresentativeElement;
	// }
}
