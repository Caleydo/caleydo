package org.caleydo.core.util.clusterer;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionType;

/**
 * Implementation of a node used in the cluster tree. Cluster node contains information needed in the
 * dendrogram and the radial hierarchy view. Additionally cluster node implements {@link Comparable}.
 * 
 * @author Bernhard Schlegl
 * @author Christian Partl
 * @author Alexander Lex
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterNode
	extends AHierarchyElement<ClusterNode> implements IHierarchyData<ClusterNode>{

	@XmlAttribute
	private String nodeName;

	@XmlElement
	private float fCoefficient;
	@XmlElement
	private int iHierarchyDepth;
	@XmlElement
	private int iNrElements;
	@XmlElement
	private Vec3f vPos;
	@XmlElement
	private SelectionType selectionType;
	@XmlElement
	private boolean bIsRootNode;
	@XmlElement
	private float fAverageExpressionValue;
	@XmlElement
	private float fStandardDeviation;

	private boolean bIsPartOfSubTree = false;
	private Vec3f vPosSubTree;

	@XmlTransient
	private ISet metaSet;

	// @XmlElement
	// private float[] fArRepresentativeElement;

	public ClusterNode() {
	}

	public ClusterNode(Tree<ClusterNode> tree, String sNodeName, int iClusterNr, float fCoefficient,
		int iDepth, boolean bIsRootNode, int leafID) {
		super(tree);
		this.nodeName = sNodeName;
		this.id = iClusterNr;
		super.setLeafID(leafID);
		this.fCoefficient = fCoefficient;
		this.iHierarchyDepth = iDepth;
		this.bIsRootNode = bIsRootNode;
		this.selectionType = SelectionType.NORMAL;
		this.fAverageExpressionValue = 0f;
		this.fStandardDeviation = 0f;

	}

	public void createMetaSets(ISet set) {
		metaSet = set.getShallowClone();
		metaSet.setLabel("MetaSet at " + nodeName);
		metaSet.setContentTree(set.getContentTree());
//		Tree<ClusterNode> subTree = tree.getSubTree();
		
		metaSet.setStorageTree(tree);
		metaSet.setStorageTreeRoot(this);
		ArrayList<Integer> storageIDs = this.getLeaveIds();
		for (Integer storageID : storageIDs)
			metaSet.addStorage(set.get(storageID));
		
		
		
		ArrayList<ClusterNode> children = tree.getChildren(this);
		if(children != null)
			for(ClusterNode child : children)
			{
				child.createMetaSets(set);
			}
	}

	public ISet getMetaSet()
	{
		return metaSet;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public float getCoefficient() {
		return fCoefficient;
	}

	@Override
	public String toString() {
		return nodeName;
	}

//	public void setNrElements(int iNrElements) {
//		this.iNrElements = iNrElements;
//	}
//
//	public int getNrElements() {
//		return iNrElements;
//	}

	public void setPos(Vec3f vPos) {
		this.vPos = vPos;
	}

	public Vec3f getPos() {
		return vPos;
	}

	public void setSelectionType(SelectionType SelectionType) {
		this.selectionType = SelectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void togglSelectionType() {
		this.selectionType =
			(selectionType == SelectionType.SELECTION) ? SelectionType.NORMAL : SelectionType.SELECTION;
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
	
	@Override
	public int getComparableValue() {
		return id;
	}

	@Override
	public float getSize() {
		return iNrElements;
	}

	// public void setRepresentativeElement(float[] fArRepresentativeElement) {
	// this.fArRepresentativeElement = fArRepresentativeElement;
	// }
	//
	// public float[] getRepresentativeElement() {
	// return fArRepresentativeElement;
	// }
}
