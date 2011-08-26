package org.caleydo.core.data.perspective;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

@XmlType
@XmlSeeAlso({ RecordPerspective.class, DimensionPerspective.class, RecordVirtualArray.class,
		DimensionVirtualArray.class })
public abstract class DataPerspective<VA extends VirtualArray<VA, DeltaType, GroupType>, GroupType extends GroupList<GroupType, VA, DeltaType>, DeltaType extends VirtualArrayDelta<DeltaType>, FilterManagerType extends FilterManager<?, DeltaType, ?, VA>> {

	/** The unique ID of the perspective */
	@XmlElement
	protected String perspectiveID;

	/**
	 * Flag determining whether this perspective is private to a certain view. That means that other views
	 * typically should not use this perspective
	 */
	@XmlElement
	protected boolean isPrivate;

	ATableBasedDataDomain dataDomain;

	VA virtualArray;
	/** indices of examples (cluster centers) */
	@XmlTransient
	ArrayList<Integer> sampleElements;
	/** number of elements per cluster */
	@XmlTransient
	ArrayList<Integer> clusterSizes;
	/**
	 * The tree that shows relation between the elements in the {@link VirtualArray}. Always needs to be in
	 * sync with the VAs.
	 */
	@XmlTransient
	ClusterTree tree;
	/**
	 * Flag telling us whether the tree has been automatically generated (i.e. is the default tree), or
	 * whether it has been externally set, e.g., using clustering or importing. It does not make sense to
	 * visualize a default tree for example.
	 */
	@XmlElement
	boolean isTreeDefaultTree = true;
	/**
	 * The root not of the tree from this perspectives view. This enables the perspective to use only part of
	 * the tree
	 */
	@XmlElement
	ClusterNode rootNode;
	/**
	 * The filter manager that manages and holds all filters applied to this prespective.
	 */
	@XmlTransient
	protected FilterManagerType filterManager;

	@XmlElement
	boolean isPartitionallyClustered = false;

	@XmlTransient
	IDType idType;

	/** Only for serialization */
	public DataPerspective() {
	}

	public DataPerspective(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		init();
	}

	/** Only for de-serialization */
	// public void setPerspectiveID(String perspectiveID) {
	// if (this.perspectiveID != null)
	// throw new IllegalStateException(
	// "This method is only for de-serialization. In other cases the perspectiveID is set automatically");
	// this.perspectiveID = perspectiveID;
	// }

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		init();
	}

	public void setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * Initialization that needs to be called as soon as dataDomain is set. Is called by super-class.
	 */
	protected abstract void init();

	/**
	 * Returns the automatically generated globally unique {@link #perspectiveID} of this perspective
	 * 
	 * @return
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	public void setIDType(IDType idType) {
		this.idType = idType;
	}

	/**
	 * Creates a new virtual array for this perspective and resets all other associated parameters
	 * (clusterSizes, contentTree, etc.)
	 * 
	 * @param indices
	 *            can be a filled ArrayList, an empty ArrayList for empty initialization or null for empty
	 *            initialization
	 */
	public void createVA(ArrayList<Integer> indices) {
		reset();
		if (indices == null)
			indices = new ArrayList<Integer>();
		virtualArray = newConcreteVirtualArray(indices);
	}

	/**
	 * Returns a reference to the virtual array of this perspective
	 * 
	 * @return
	 */
	@XmlElementRef
	public VA getVirtualArray() {
		return virtualArray;
	}

	/**
	 * Sets a new virtual array to this perspective.
	 * 
	 * @param virtualArray
	 */
	public void setVirtualArray(VA virtualArray) {
		if (virtualArray == null) {
			Logger.log(new Status(Status.ERROR, "org.caleydo.core", "Virtual array to be set was null"));
			return;
		}
		reset();
		// if (virtualArray.getVaType() != perspectiveID)
		// throw new IllegalArgumentException("VA's ID (" + virtualArray.getVaType()
		// + ") does not match data perspectives ID (" + perspectiveID + ")");
		this.virtualArray = virtualArray;
	}

	/**
	 * <p>
	 * Returns the cluster tree showing the relations between the elements indexed by the virtual array.
	 * </p>
	 * <p>
	 * Depending on whether or not a cluster tree has been set, this method either returns a tree with real
	 * relations, or a tree with one root and all leafs as children of the root (the default tree). You can
	 * check whether the tree is the default tree using {@link #isTreeDefaultTree()}.
	 * </p>
	 * 
	 * @return
	 */
	@XmlTransient
	public ClusterTree getTree() {
		if (tree == null)
			finish();
		return tree;
	}

	/**
	 * Set a cluster tree for the virtual array.
	 * 
	 * @param tree
	 */
	public void setTree(ClusterTree tree) {
		isTreeDefaultTree = false;
		this.tree = tree;
	}

	/**
	 * Set an artificial {@link #rootNode} for the tree.
	 * 
	 * @param rootNode
	 */
	public void setTreeRoot(ClusterNode rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Returns the {@link #rootNode} of the tree, which can be different from {@link ClusterTree#getRoot()} -
	 * it can be a sub-tree only.
	 * 
	 * @return
	 */
	public ClusterNode getTreeRoot() {
		if (rootNode != null)
			return rootNode;
		else if (tree != null)
			return tree.getRoot();
		else
			return null;
	}

	private void reset() {
		clusterSizes = null;
		sampleElements = null;
		tree = null;
	}

	/**
	 * Sets the delta to the virtual array and resets other related data (groups, trees) accordingly.
	 * 
	 * @param delta
	 */
	public void setVADelta(DeltaType delta) {
		virtualArray.setDelta(delta);
		reset();
		virtualArray.setGroupList(null);
	}

	public void setSampleElements(ArrayList<Integer> contentSampleElements) {
		this.sampleElements = contentSampleElements;
	}

	public void setClusterSizes(ArrayList<Integer> contentClusterSizes) {
		this.clusterSizes = contentClusterSizes;
	}

	public void finish() {
		// calculate the group list based on contentClusterSizes (for example for affinity propagation
		if (virtualArray != null && clusterSizes != null && sampleElements != null) {
			isPartitionallyClustered = true;
			GroupType contentGroupList = createGroupList();

			int cnt = 0;
			// int iOffset = 0;
			tree = new ClusterTree(idType);
			int clusterNr = 0;
			ClusterNode root = new ClusterNode(tree, "Root", clusterNr++, true, -1);
			tree.setRootNode(root);
			ClusterNode node;
			int from = 0;
			int to = 0;
			for (Integer clusterSize : clusterSizes) {
				node = new ClusterNode(tree, "Group: " + clusterNr, clusterNr++, true, -1);
				Group temp = new Group(clusterSize, sampleElements.get(cnt), node);
				tree.addChild(root, node);
				contentGroupList.append(temp);
				cnt++;
				// iOffset += iter;
				to += clusterSize;
				ClusterNode leaf;
				for (int vaIndex = from; vaIndex < to; vaIndex++) {
					Integer recordID = virtualArray.get(vaIndex);
					leaf = new ClusterNode(tree, "Leaf: " + recordID, clusterNr++, true, recordID);
					tree.addChild(node, leaf);
				}
				from = to;

			}

			virtualArray.setGroupList(contentGroupList);
		}
		// calculate the group list based on the tree's first level
		else if (virtualArray != null && tree != null) {
			virtualArray.buildNewGroupList(createGroupList(), tree.getRoot().getChildren());
		}
		else if (virtualArray != null && tree == null) {
			isTreeDefaultTree = true;
			tree = new ClusterTree(idType);
			ClusterNode root = new ClusterNode(tree, "root", 0, true, -1);
			tree.setRootNode(root);
			for (Integer id : virtualArray) {
				tree.addChild(root, new ClusterNode(tree, getLabel(id), id, false, id));
			}
		}
	}

	protected abstract String getLabel(Integer id);

	/**
	 * Creates a virtual array for this perspective by extracting the leaves from the tree. Respects the
	 * artificial {@link #rootNode}, or uses the trees default root if no artificial root is set.
	 */
	public void createVABasedOnTree() {
		// ContentGroupList groupList = recordVA.getGroupList();
		if (rootNode == null)
			rootNode = tree.getRoot();
		virtualArray = newConcreteVirtualArray(rootNode.getLeaveIds());
		virtualArray.buildNewGroupList(createGroupList(), tree.getRoot().getChildren());
		// recordVA.setGroupList(groupList);
	}

	public boolean isPartitionallyClustered() {
		return isPartitionallyClustered;
	}

	public FilterManagerType getFilterManager() {
		return filterManager;
	}

	protected abstract GroupType createGroupList();

	protected abstract VA newConcreteVirtualArray(ArrayList<Integer> indexList);

	public boolean isTreeDefaultTree() {
		return isTreeDefaultTree;
	}
}
