package org.caleydo.core.util.clusterer;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.MetaSet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
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
	extends AHierarchyElement<ClusterNode>
	implements IHierarchyData<ClusterNode>, Comparable<ClusterNode> {

	// @XmlAttribute
	// private String nodeName;

	// @XmlElement
	// private int iNrElements;
	@XmlElement
	private Vec3f vPos;
	@XmlElement
	private SelectionType selectionType;
	@XmlElement
	private boolean isRootNode;
	@XmlElement
	private float fAverageExpressionValue;
	@XmlElement
	private float fStandardDeviation;
	@XmlTransient
	private boolean bIsPartOfSubTree = false;
	private Vec3f vPosSubTree;
	@XmlTransient
	private MetaSet metaSet;

	// @XmlElement
	// private float[] fArRepresentativeElement;

	public ClusterNode() {
	}

	/**
	 * Constructor for a cluster node.
	 * 
	 * @param tree
	 *            The tree the clusternode belongs to
	 * @param nodeName
	 *            the name of the node
	 * @param clusterNr
	 *            the id, the cluster number
	 * @param isRootNode
	 *            true if this is a root node
	 * @param leafID
	 *            the id of the leaf, or -1 if this is not a leaf
	 */
	public ClusterNode(Tree<ClusterNode> tree, String label, int clusterNr, boolean isRootNode, int leafID) {

		super(tree);
		this.label = label;
		this.id = clusterNr;
		super.setLeafID(leafID);
		this.isRootNode = isRootNode;
		this.selectionType = SelectionType.NORMAL;
		this.fAverageExpressionValue = 0f;
		this.fStandardDeviation = 0f;

	}

	/**
	 * Creates a meta-set for this node
	 * 
	 * @param set
	 */
	public <SetType extends Set> void createMetaSet(SetType set) {
		metaSet = new MetaSet(set, tree, this);
		metaSet.setLabel(label);
		// metaSet.setContentTree(set.getContentTree());
		// Tree<ClusterNode> subTree = tree.getSubTree();

		ArrayList<Integer> storageIDs = this.getLeaveIds();
		SetUtils.setStorages(metaSet, storageIDs);
	}

	/**
	 * Creates meta-sets recursively for the whole sub-tree of this node
	 * 
	 * @param set
	 */
	public <SetType extends Set> void createMetaSets(SetType set) {
		createMetaSet(set);
		ArrayList<ClusterNode> children = tree.getChildren(this);
		if (children != null)
			for (ClusterNode child : children) {
				child.createMetaSets(set);
			}
	}

	public ISet getMetaSet() {
		return metaSet;
	}

	/**
	 * Returns a metaset if this node or any of its sub-nodes contain the MetaSet specified by the ID
	 * 
	 * @param setID
	 * @return
	 */
	public ISet getMetaSetFromSubTree(int setID) {

		if (metaSet.getID() == setID)
			return metaSet;
		else if (!this.hasChildren())
			return null;
		else {
			for (ClusterNode child : getChildren()) {
				ISet tempSet = child.getMetaSetFromSubTree(setID);
				if (tempSet != null)
					return tempSet;
			}
			return null;
		}
	}

	public ArrayList<ISet> getAllMetaSetsFromSubTree() {

		ArrayList<ISet> allMetaSets = new ArrayList<ISet>();

		allMetaSets.add(metaSet);

		if (this.hasChildren()) {
			for (ClusterNode child : getChildren()) {
				allMetaSets.addAll(child.getAllMetaSetsFromSubTree());
			}
		}

		return allMetaSets;
	}

	@Override
	public String toString() {
		return label;
	}

	// public void setNrElements(int iNrElements) {
	// this.iNrElements = iNrElements;
	// }
	//
	// public int getNrElements() {
	// return iNrElements;
	// }

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
		return isRootNode;
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

	// @Override
	// public float getSize() {
	// return iNrElements;
	// }

	// public void setRepresentativeElement(float[] fArRepresentativeElement) {
	// this.fArRepresentativeElement = fArRepresentativeElement;
	// }
	//
	// public float[] getRepresentativeElement() {
	// return fArRepresentativeElement;
	// }

	// public void sortByGeneExpression()
	// {
	// ArrayList<ClusterNode> children = getChildren();
	//
	//
	// }

	@Override
	public int compareTo(ClusterNode node) {
		if (tree.isUseDefaultComparator())
			return super.compareTo(node);

		if (fAverageExpressionValue < node.fAverageExpressionValue)
			return 1;
		else
			return -1;
	}
}
