package org.caleydo.core.data.perspective;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.container.DataContainer;
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
 * RecordPerspective and a DimsenionPerspective are necessary. {@link DataContainer} is designed to hold
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

	/** A human-readable description of the perspective */
	private String label;

	/**
	 * Flag determining whether this perspective is private to a certain view. That means that other views
	 * typically should not use this perspective
	 */
	@XmlElement
	protected boolean isPrivate;
	
	/**
	 * Flag determining whether this perspective is the default perspective that for instance will be displayed by the support views.
	 */
	@XmlElement	
	protected boolean isDefault;

	/**
	 * Flag telling us whether the tree has been automatically generated (i.e., is the default tree), or
	 * whether it has been externally set, e.g., using clustering or importing. It does not make sense to
	 * visualize a default tree for example.
	 */
	@XmlElement
	boolean isTreeDefaultTree = true;

	/** The dataDomain this perspective belongs to */
	@XmlTransient
	protected ATableBasedDataDomain dataDomain;

	@XmlTransient
	protected IDType idType;

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

	// @XmlTransient
	// private ArrayList<Integer> sampleElements;

	// @XmlTransient
	// private ArrayList<Integer> clusterSizes;

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
	 * @param label
	 *            setter, see {@link #label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}, or if the label is null, the perspectiveID is returned.
	 */
	public String getLabel() {
		if (label == null || label.isEmpty())
			return perspectiveID;
		return label;
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
	 * @param isDefault setter, see {@link #isDefault}
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	/**
	 * @return the isDefault, see {@link #isDefault}
	 */
	public boolean isDefault() {
		return isDefault;
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
	public String getID() {
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
	private void createVA(List<Integer> indices) {
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
		// this should only happen when we de-serialize with a default tree.
		if (tree == null)
			createDefaultTree();
		return tree;
	}

	/**
	 * Set a cluster tree for the virtual array.
	 * 
	 * @param tree
	 */
	@Deprecated
	public void setTree(ClusterTree tree) {
		isTreeDefaultTree = false;
		this.tree = tree;
	}

	/**
	 * Sets a new virtual array to this perspective.
	 * 
	 * @param virtualArray
	 */
	@Deprecated
	public void setVirtualArray(VA virtualArray) {
		if (virtualArray == null) {
			Logger.log(new Status(Status.ERROR, "org.caleydo.core", "Virtual array to be set was null"));
			return;
		}
		this.virtualArray = virtualArray;
	}

	/**
	 * @return the isTreeDefaultTree, see {@link #isTreeDefaultTree}
	 */
	public boolean isTreeDefaultTree() {
		return isTreeDefaultTree;
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

	/**
	 * Sets the delta to the virtual array and resets other related data (groups, trees) accordingly.
	 * 
	 * @param delta
	 */
	public void setVADelta(DeltaType delta) {
		virtualArray.setDelta(delta);
		createDefaultTree();
		virtualArray.setGroupList(null);
	}

	/**
	 * <p>
	 * {@link ADataPerspective}s are initialized by providing a {@link PerspectiveInitializationData} object,
	 * which can contain a number of different combinations of information to initialize the perspective.
	 * </p>
	 * <p>
	 * If null is passed, an empty perspective is created.
	 * </p>
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void init(PerspectiveInitializationData data) {

		// Case 1: we want an empty perspective
		if (data == null) {
			createVA(null);
		}
		// Case 2: we only have a virtual array
		else if (data.getIndices() != null && data.getClusterSizes() == null && data.getTree() == null) {
			createVA(data.getIndices());
			createDefaultTree();
		}
		// Case 3: we have a virtual array and grouping
		else if (data.getIndices() != null && data.getClusterSizes() != null) {
			createVA(data.getIndices());
			createGroupListAndDefaultTreeFromClusterSizes(data);

		}
		// Case 4: we have a tree and nothing else, with either the default root or a specific node as root
		else if (data.getTree() != null && data.getIndices() == null) {
			createEverythingFromTree(data);
		}
		// Case 5: we initialize from a given virtual array
		else if(data.getVirtualArray() != null)
		{
			virtualArray = (VA)data.getVirtualArray();
			// FIXME we need to create a tree with groups here
			createDefaultTree();
		}

		else {
			throw new IllegalStateException("Cannot initialize from " + data
				+ " either redundant or to little information.");
		}
	}

	public void reset() {
		tree = null;
		rootNode = null;
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(getIDList());
		init(data);
	}

	// ------------------ Abstract Methods that need the concrete data types ---------------

	/** Get the human readable label of the element specified by the ID */
	protected abstract String getElementLabel(Integer id);

	/** Create a concrete {@link GroupList} in the derived classes. */
	protected abstract GroupType createGroupList();

	/** Create a concrete {@link FilterManager} in derived classes */
	protected abstract void createFilterManager();

	/** Create a concrete {@link VirtualArray} in the derived classes */
	protected abstract VA newConcreteVirtualArray(List<Integer> indexList);

	/** Get a complete list of indices of all elements in the data table for the perspective type */
	protected abstract List<Integer> getIDList();

	// -------------------------- Initialization Methods -------------------------------------

	/** Creates a default tree of depth 1, assuming only one group */
	private void createDefaultTree() {
		isTreeDefaultTree = true;
		tree = new ClusterTree(idType, virtualArray.size());
		ClusterNode root = new ClusterNode(tree, "root", 0, true, -1);
		tree.setRootNode(root);
		for (Integer id : virtualArray) {
			tree.addChild(root, new ClusterNode(tree, getElementLabel(id), id, false, id));
		}
	}

	private void createGroupListAndDefaultTreeFromClusterSizes(PerspectiveInitializationData data) {
		GroupType groupList = createGroupList();
		int groupCounter = 0;
		isTreeDefaultTree = true;
		tree = new ClusterTree(idType, data.getIndices().size());
		int clusterNr = 0;
		ClusterNode root = new ClusterNode(tree, "Root", clusterNr++, true, -1);
		tree.setRootNode(root);
		ClusterNode node;
		int from = 0;
		int to = 0;
		for (Integer clusterSize : data.getClusterSizes()) {
			node = new ClusterNode(tree, "Group: " + clusterNr, clusterNr++, true, -1);
			Group temp = new Group(clusterSize, data.getSampleElements().get(groupCounter), node);
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

	/**
	 * Creates a virtual array for this perspective by extracting the leaves from the tree. Respects the
	 * artificial {@link #rootNode}, or uses the trees default root if no artificial root is set.
	 */
	private void createEverythingFromTree(PerspectiveInitializationData data) {
		tree = data.getTree();

		if (tree.getLeaveIDType() == null)
			tree.initializeIDTypes(idType);
		if (tree.getLeaveIDType() != idType)
			throw new IllegalStateException("IDType of tree and perspective do not match. Tree:  "
				+ tree.getLeaveIDType() + " perspective: " + idType);

		isTreeDefaultTree = false;
		if (data.getRootNode() == null)
			rootNode = tree.getRoot();
		else
			rootNode = data.getRootNode();

		virtualArray = newConcreteVirtualArray(rootNode.getLeaveIds());
		ArrayList<ClusterNode> listOfNodesForGroups = new ArrayList<ClusterNode>(1);
		listOfNodesForGroups.add(rootNode);
		virtualArray.buildNewGroupList(createGroupList(), listOfNodesForGroups);

	}

	@Override
	public String toString() {
		return perspectiveID + ", size: " + virtualArray.size();
	}

}