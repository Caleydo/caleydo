package org.caleydo.core.data.graph.tree;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Abstract base class for hierarchy elements that shall be stored in a tree. It provides several methods
 * dealing with the hierarchy.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @param <Node>
 *            Concrete type that is stored in the hierarchy.
 */
public abstract class AHierarchyElement<Node extends AHierarchyElement<Node>>
	implements Comparable<Node> {

	protected Integer id;

	/**
	 * Instance of the tree that represents the hierarchy.
	 */
	protected Tree<Node> tree;
	/**
	 * Specifies the depth of the sub-hierarchy with the current element as root node.
	 */
	protected int hierarchyDepth = -1;
	/**
	 * Instance of the concrete type that is stored in the hierarchy. This element has to be set, otherwise
	 * the methods provided by AHierarchyElement will not work.
	 */
	protected Node node;
	/**
	 * Specifies the level of the hierarchy of the current element. To be clear: If the root node is on level
	 * 0, its children are on level 1 and so on.
	 */
	protected int hierarchyLevel = -1;

	protected int numberOfLeaves = -1;

	protected String label;

	/** if this node is a leaf, this id is >= 0 */
	private int leafID = -1;

	private ArrayList<Integer> leaveIDs;

	protected boolean useDefaultComparator = true;

	public AHierarchyElement() {
	}

	/**
	 * Constructor.
	 * 
	 * @param tree
	 *            Tree that represents the hierarchy.
	 */
	@SuppressWarnings("unchecked")
	public AHierarchyElement(Tree<Node> tree) {
		setNode((Node) this);
		this.tree = tree;
	}

	/**
	 * Sets the instance of the concrete type that is stored in the hierarchy. If the instance is not set, the
	 * methods of AHierarchyElement will not work.
	 * 
	 * @param node
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	public void setLeafID(int leafID) {
		this.leafID = leafID;
	}

	public int getLeafID() {
		return leafID;
	}

	@XmlTransient
	public void setTree(Tree<Node> tree) {
		this.tree = tree;
	}

	public Tree<Node> getTree() {
		return tree;
	}

	public void setID(Integer id) {
		this.id = id;
	}

	public Integer getID() {
		return id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Gets the parent of the current element.
	 * 
	 * @return Parent of the current element, null if the current element is the root node.
	 */

	public Node getParent() {
		return tree.getParent(node);

	}

	/**
	 * Returns whether the current element has children or not.
	 * 
	 * @return True if the current element has children, false otherwise.
	 */
	public boolean hasChildren() {
		return tree.hasChildren(node);
	}

	/**
	 * Returns the children of the node or null if the node is a leaf
	 * 
	 * @return a list of nodes which are the children or null
	 */
	public ArrayList<Node> getChildren() {
		return tree.getChildren(node);
	}

	/**
	 * Returns the number of leaves that are children of this node (i.e. all leaves of this sub-tree)
	 * 
	 * @return
	 */
	public int getNrLeaves() {
		return getLeaveIds().size();
	}

	/**
	 * Returns the size of the hierarchical data object. The size by default is the number of leaves, however,
	 * a sub-view could substitute this for another value (e.g. sum of file sizes)
	 * 
	 * @return
	 */
	public float getSize() {
		return getNrLeaves();
	}

	/**
	 * A node is compared by the value returned by {@link #getComparableValue()}. By default (as long as
	 * getComparableValue is not overriden) this is the ID.
	 */
	@Override
	public int compareTo(Node node) {
		return getComparableValue() - node.getComparableValue();
	}

	/**
	 * Recursively searches upwards the hierarchy and returns a parent with the specified hierarchy level if
	 * it exists.
	 * 
	 * @param iHierarchyLevel
	 *            The hierarchy level the parent should have.
	 * @return The parent element with the specified hierarchy level or null if such an element does not
	 *         exist.
	 */
	public Node getParentWithLevel(int iHierarchyLevel) {
		Node parent = tree.getParent(node);

		if (parent == null) {
			return null;
		}
		if (parent.getHierarchyLevel() == iHierarchyLevel) {
			return parent;
		}
		return parent.getParentWithLevel(iHierarchyLevel);
	}

	/**
	 * Recursively searches upwards the hierarchy and returns if the specified parent is found.
	 * 
	 * @param parent
	 *            The parent element that should be searched for.
	 * @param iDepth
	 *            The maximum number of levels that should be searched upwards.
	 * @return True if the specified element is found within the specified depth, false otherwise.
	 */
	public boolean hasParent(Node parent, int iDepth) {
		Node currentParent = tree.getParent(node);

		if (currentParent == null || iDepth <= 0) {
			return false;
		}
		if (currentParent == parent) {
			return true;
		}
		return currentParent.hasParent(parent, iDepth - 1);
	}

	/**
	 * Gets the path of elements that is taken upwards the hierarchy to come from the current element to the
	 * specified target parent. Note that the current element and the target parent are not included in the
	 * path.
	 * 
	 * @param parent
	 *            Target element of the path.
	 * @return Path from the current element to the target parent if it exists, null otherwise.
	 */
	public ArrayList<Node> getParentPath(Node parent) {
		ArrayList<Node> alParentPath = new ArrayList<Node>();
		return getParentPath(parent, alParentPath);
	}

	/**
	 * Recursively generates the path of elements that is taken upwards the hierarchy to come from the current
	 * element to the specified target parent.
	 * 
	 * @param parent
	 *            Target element of the path.
	 * @param alParentPath
	 *            Current path, where at each recursion step one element is added.
	 * @return Path from the current element to the target parent if it exists, null otherwise.
	 */
	private ArrayList<Node> getParentPath(Node parent, ArrayList<Node> alParentPath) {

		Node currentParent = tree.getParent(node);

		if (currentParent == null) {
			return null;
		}

		alParentPath.add(currentParent);

		if (currentParent == parent) {
			return alParentPath;
		}

		return currentParent.getParentPath(parent, alParentPath);
	}

	/**
	 * Gets the length of the path of elements that is taken upwards the hierarchy to come from the current
	 * element to the specified target parent. Note that the current element is not counted in the path
	 * length.
	 * 
	 * @param parent
	 *            Target element of the path.
	 * @return Length of the path from the current element to the target parent if it exists, -1 otherwise.
	 */
	public int getParentPathLength(Node parent) {
		return getParentPathLength(parent, 0);
	}

	/**
	 * Recursively calculates the length of the path of elements that is taken upwards the hierarchy to come
	 * from the current element to the specified target parent.
	 * 
	 * @param parent
	 *            Target element of the path.
	 * @param iLength
	 *            Current length in the recursion.
	 * @return Length of the path from the current element to the target parent if it exists, -1 otherwise.
	 */
	private int getParentPathLength(Node parent, int iLength) {
		Node currentParent = tree.getParent(node);

		if (currentParent == null) {
			return -1;
		}

		iLength++;

		if (currentParent == parent) {
			return iLength;
		}

		return currentParent.getParentPathLength(parent, iLength);
	}

	/**
	 * Gets the hierarchy level of the current element. Note that calculateHierarchyLevels must have been
	 * called first to get the proper value.
	 * 
	 * @return The hierarchy level of the current element.
	 */
	public int getHierarchyLevel() {
		if (hierarchyLevel == -1 || tree.isDirty())
			tree.makeClean();

		return hierarchyLevel;
	}

	/**
	 * Gets the hierarchy depth of the current element.
	 * 
	 * @return Hierarchy depth of the current element.
	 */
	public int getDepth() {
		if (hierarchyDepth == -1 || tree.isDirty())
			tree.makeClean();

		return hierarchyDepth;
	}

	/**
	 * Returns an array list with the indexes of the elements (gene/experiment) in the tree.
	 * 
	 * @param tree
	 * @param node
	 * @return array list with ordered indexes of the clustered elements in the tree.
	 */
	@XmlTransient
	public ArrayList<Integer> getLeaveIds() {
		if (leaveIDs == null || tree.isDirty())
			tree.makeClean();

		return leaveIDs;
	}

	/**
	 * Returns the same as getID but may be overridden in subclasses.
	 * 
	 * @return a comparable ID
	 */
	public int getComparableValue() {
		return getID();
	}

	ArrayList<Integer> calculateLeaveIDs() {
		leaveIDs = new ArrayList<Integer>();
		ArrayList<Node> children = tree.getChildren(node);
		if (children == null) {
			leaveIDs.add(leafID);
			return leaveIDs;
		}
		else {
			for (Node child : children) {
				leaveIDs.addAll(child.calculateLeaveIDs());
			}
			return leaveIDs;
		}
	}

	/**
	 * Recursively calculates the hierarchy depths of the elements of the sub-hierarchy using the current
	 * element as root node.
	 * 
	 * @return HierarchyDepth of the current element.
	 */
	int calculateHierarchyDepth() {

		ArrayList<Node> alChildren = tree.getChildren(node);
		hierarchyDepth = 1;

		if (alChildren == null) {
			return 1;
		}

		for (Node child : alChildren) {
			int childDepth = child.calculateHierarchyDepth();
			hierarchyDepth = (childDepth >= hierarchyDepth) ? childDepth + 1 : hierarchyDepth;
		}
		return hierarchyDepth;
	}

	/**
	 * Recursively calculates the hierarchy levels of the elements of the sub-hierarchy using the current
	 * element as root node.
	 * 
	 * @param hierarchyLevel
	 *            Specifies the level of the root node.
	 */
	protected void calculateHierarchyLevels(int hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
		ArrayList<Node> alChildren = tree.getChildren(node);

		if (alChildren == null) {
			return;
		}

		for (Node child : alChildren) {
			child.calculateHierarchyLevels(hierarchyLevel + 1);
		}
	}

	/**
	 * Choose whether comparisons should be based on the default comparator (the ID) or some custom comparator
	 * defined in a sub-class
	 * 
	 * @param compareAverageExpressionValues
	 *            if true expression values are used, else IDs
	 */
	public void setUseDefaultComparator(boolean useDefaultComparator) {
		this.useDefaultComparator = useDefaultComparator;
	}

}
