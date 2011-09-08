package org.caleydo.core.data.perspective;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.container.ADataContainer;
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

/**
 * <p>
 * A {@link ADataPerspective} holds all relevant meta data for either records through the
 * {@link RecordPerspective} or dimensions through the {@link DimensionPerspective}. For many uses both, a
 * RecordPerspective and a DimsenionPerspective are necessary. {@link ADataContainer} is designed to hold
 * combinations of Record- and DimensionPerspectives.
 * </p>
 * <p>
 * Among the information the DataPerspectives holds are:
 * <ol>
 * <li>The {@link VirtualArray}, which determines which elements of a Dimension or Record should be accessed
 * in which order.</li>
 * <li>The {@link GroupList} (currently as part of the VirtualArray), which holds information on which
 * elements in the VirtualArray are grouped (e.g., because of a clustering).</li>
 * <li>The {@link ClusterTree}, which defines a hierarchy on how similar elements are to each other.
 * <li>The {@link FilterManager}, which holds and manages all filters defined on this perspective.
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 * @param <VA>
 * @param <GroupType>
 * @param <DeltaType>
 * @param <FilterManagerType>
 */
@XmlType
@XmlSeeAlso({ RecordPerspective.class, DimensionPerspective.class, RecordVirtualArray.class,
		DimensionVirtualArray.class })
public abstract class ADataPerspective<VA extends VirtualArray<VA, DeltaType, GroupType>, GroupType extends GroupList<GroupType, VA, DeltaType>, DeltaType extends VirtualArrayDelta<DeltaType>, FilterManagerType extends FilterManager<?, DeltaType, ?, VA>> {

	/** The unique ID of the perspective */
	@XmlElement
	protected String perspectiveID;

	/**
	 * Flag determining whether this perspective is private to a certain view. That means that other views
	 * typically should not use this perspective
	 */
	@XmlElement
	protected boolean isPrivate;

	/**
	 * Flag telling us whether the tree has been automatically generated (i.e., is the default tree), or
	 * whether it has been externally set, e.g., using clustering or importing. It does not make sense to
	 * visualize a default tree for example.
	 */
	@XmlElement
	boolean isTreeDefaultTree = true;

	/** The dataDomain this perspective belongs to */
	@XmlElement
	protected ATableBasedDataDomain dataDomain;

	/** The {@link VirtualArray} of this DataPerspective. */
	protected VA virtualArray;

	/**
	 * The root not of the tree from this perspectives view. This enables the perspective to use only part of
	 * the tree
	 */
	@XmlElement
	protected ClusterNode rootNode;
	/**
	 * The filter manager that manages and holds all filters applied to this prespective.
	 */
	@XmlTransient
	protected FilterManagerType filterManager;

	@XmlTransient
	protected IDType idType;

	/**
	 * Indices of elements that represent a cluster (cluster centers). Used for initialization to create a
	 * sample element for every group.
	 */
	@XmlTransient
	private ArrayList<Integer> sampleElements;
	/**
	 * The sizes of the clusters in a list sorted so that combined with the {@link VirtualArray} the clusters
	 * are uniquely identified. Used for initialization.
	 */
	@XmlTransient
	private ArrayList<Integer> clusterSizes;
	/**
	 * The tree that shows relation between the elements in the {@link VirtualArray}. Always needs to be in
	 * sync with the VAs.
	 */
	@XmlTransient
	private ClusterTree tree;

	/** Only for serialization */
	public ADataPerspective() {

	}

	public ADataPerspective(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		init();
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		init();
	}

	/**
	 * @param isPrivate
	 *            setter, see {@link #isPrivate}
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	/**
	 * @return the isPrivate, see {@link #isPrivate}
	 */
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

	/**
	 * @param idType
	 *            setter, see {@link #idType}
	 */
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
	public void createVA(List<Integer> indices) {
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
		// Create groupList and tree based on cluster sizes. The t
		if (virtualArray != null && clusterSizes != null && sampleElements != null) {
			GroupType groupList = createGroupList();

			int groupCounter = 0;
			isTreeDefaultTree = true;
			tree = new ClusterTree(idType);
			int clusterNr = 0;
			ClusterNode root = new ClusterNode(tree, "Root", clusterNr++, true, -1);
			tree.setRootNode(root);
			ClusterNode node;
			int from = 0;
			int to = 0;
			for (Integer clusterSize : clusterSizes) {
				node = new ClusterNode(tree, "Group: " + clusterNr, clusterNr++, true, -1);
				Group temp = new Group(clusterSize, sampleElements.get(groupCounter), node);
				tree.addChild(root, node);
				groupList.append(temp);
				groupCounter++;
				to += clusterSize;
				ClusterNode leaf;
				for (int vaIndex = from; vaIndex < to; vaIndex++) {
					Integer id = virtualArray.get(vaIndex);
					leaf = new ClusterNode(tree, "Leaf: " + id, clusterNr++, true, id);
					tree.addChild(node, leaf);
				}
				from = to;
			}

			virtualArray.setGroupList(groupList);
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

	/**
	 * Returns the {@link FilterManager} associated with this perspective. Every perspective has it's own
	 * unique filter manager.
	 * 
	 * @return
	 */
	public FilterManagerType getFilterManager() {
		if (filterManager == null)
			createFilterManager();
		return filterManager;
	}

	/** Create a concrete {@link GroupList} in the derived classes. */
	protected abstract GroupType createGroupList();

	/** Create a concrete {@link FilterManager} in derived classes */
	protected abstract void createFilterManager();

	/** Create a concrete {@link VirtualArray} in the derived classes */
	protected abstract VA newConcreteVirtualArray(List<Integer> indexList);

	/**
	 * @return the isTreeDefaultTree, see {@link #isTreeDefaultTree}
	 */
	public boolean isTreeDefaultTree() {
		return isTreeDefaultTree;
	}
}
