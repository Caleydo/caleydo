package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

/**
 * Abstract base class for hierarchy elements that shall be stored in a tree. It provides several methods
 * dealing with the hierarchy.
 * 
 * @author Christian Partl
 * @param <Node>
 *            Concrete type that is stored in the hierarchy.
 */
public abstract class AHierarchyElement<Node extends AHierarchyElement<Node>>
	implements Comparable<Node> {

	/**
	 * Instance of the tree that represents the hierarchy.
	 */
	protected Tree<Node> tree;
	/**
	 * Specifies the depth of the sub-hierarchy with the current element as root node.
	 */
	protected int iHierarchyDepth;
	/**
	 * Instance of the concrete type that is stored in the hierarchy. This element has to be set, otherwise
	 * the methods provided by AHierarchyElement will not work.
	 */
	protected Node node;
	/**
	 * Specifies the level of the hierarchy of the current element. To be clear: If the root node is on level
	 * 0, its children are on level 1 and so on.
	 */
	protected int iHierarchyLevel;

	/**
	 * Constructor.
	 * 
	 * @param tree
	 *            Tree that represents the hierarchy.
	 */
	public AHierarchyElement(Tree<Node> tree) {
		this.tree = tree;
		iHierarchyLevel = 0;

	}

	/**
	 * Sets the instance of the concrete type that is stored in the hierarchy. If the instance is not set, the
	 * methods of AHierarchyElement will not work.
	 * 
	 * @param node
	 */
	protected void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Recursively calculates the hierarchy levels of the elements of the sub-hierarchy using the current
	 * element as root node.
	 * 
	 * @param iLevel
	 *            Specifies the level of the root node.
	 */
	public void calculateHierarchyLevels(int iLevel) {
		iHierarchyLevel = iLevel;
		ArrayList<Node> alChildren = tree.getChildren(node);

		if (alChildren == null) {
			return;
		}

		for (Node child : alChildren) {
			child.calculateHierarchyLevels(iLevel + 1);
		}
	}

	/**
	 * Gets the hierarchy level of the current element. Note that calculateHierarchyLevels must have been
	 * called first to get the proper value.
	 * 
	 * @return The hierarchy level of the current element.
	 */
	public int getHierarchyLevel() {
		return iHierarchyLevel;
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
	 * Recursively calculates the hierarchy depths of the elements of the sub-hierarchy using the current
	 * element as root node.
	 * 
	 * @return HierarchyDepth of the current element.
	 */
	public int calculateHierarchyDepth() {

		ArrayList<Node> alChildren = tree.getChildren(node);
		iHierarchyDepth = 1;

		if (alChildren == null) {
			return 1;
		}

		for (Node child : alChildren) {
			int iChildDepth = child.calculateHierarchyDepth();
			iHierarchyDepth = (iChildDepth >= iHierarchyDepth) ? iChildDepth + 1 : iHierarchyDepth;
		}
		return iHierarchyDepth;
	}

	/**
	 * Gets the hierarchy depth of the current element. Note that calculateHierarchyDepth must have been
	 * called first for getting a proper value.
	 * 
	 * @return Hierarchy depth of the current element.
	 */
	public int getHierarchyDepth() {
		return iHierarchyDepth;
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
}
