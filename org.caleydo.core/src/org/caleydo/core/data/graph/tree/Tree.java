/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.graph.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDType;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A implementation of a sorted tree, based on the JGraphT library.
 *
 * @author Alexander Lex
 * @param <NodeType>
 */
public class Tree<NodeType extends AHierarchyElement<NodeType>> {

	private IDType nodeIDType;
	private IDType leafIDType;

	private NodeType rootNode;

	DirectedGraph<NodeType, DefaultEdge> graph;

	private HashMap<Integer, NodeType> hashNodes;

	HashMap<Integer, ArrayList<Integer>> hashLeafIDToNodeIDs;

	private int iDepth;

	private boolean isDirty;

	private ESortingStrategy sortingStrategy = ESortingStrategy.DEFAULT;

	/**
	 * Constructor that should only be used for de-serialization or for trees synchronized with a previously existing
	 * tree. For other cases use {@link #Tree(IDType)} instead.
	 */
	public Tree() {
		init(100);
	}

	/**
	 * Constructor for the tree, specifying the ID type, which should be used whenever creating a new, independent tree.
	 *
	 * @param leaveIDType
	 * @param expectedSize
	 *            An estimate for the expected size - good estimates improve performance
	 */
	public Tree(IDType leaveIDType, int expectedSize) {
		init(expectedSize);
		initializeIDTypes(leaveIDType);
	}

	public void destroy() {
		graph = null;
		rootNode = null;
	}

	/**
	 * Sets the id type of the leaves and creates a new node id type. This should only be used when the constructor
	 * without arguments was used.
	 *
	 * @param leafIDType
	 */
	public void initializeIDTypes(IDType leafIDType) {
		this.leafIDType = leafIDType;
		nodeIDType = IDType.registerInternalType("tree_" + this.hashCode(), leafIDType.getIDCategory(),
				EDataType.INTEGER);
	}

	/**
	 * Sets a node id type. This should only be done when this tree is a copy of another tree (for example with
	 * different nodes types), and not for a new tree. For a new tree use {@link #initializeIDTypes(IDType)}
	 *
	 * @param nodeIDType
	 */
	public void setNodeIDType(IDType nodeIDType) {
		this.nodeIDType = nodeIDType;
	}

	/**
	 * Returns the id type of the nodes. The node ID Type is dynamically generated on construction.
	 *
	 * @return
	 */
	public IDType getNodeIDType() {
		return nodeIDType;
	}

	/**
	 * Sets a leaf id type. This should only be done when this tree is a copy of another tree (for example with
	 * different nodes types), and not for a new tree. For a new tree use {@link #initializeIDTypes(IDType)}
	 *
	 * @param leafIDType
	 */
	public void setLeafIDType(IDType leafIDType) {
		this.leafIDType = leafIDType;
	}

	/**
	 * Returns the id type of the leaves
	 *
	 * @return
	 */
	public IDType getLeaveIDType() {
		return leafIDType;
	}

	private void init(int expectedSize) {
		graph = new DefaultDirectedGraph<NodeType, DefaultEdge>(DefaultEdge.class);
		hashNodes = new HashMap<Integer, NodeType>(expectedSize * 2);
		hashLeafIDToNodeIDs = new HashMap<Integer, ArrayList<Integer>>((int) (expectedSize * 1.5f));
	}

	public void setHashMap(HashMap<Integer, NodeType> hashNodes) {
		this.hashNodes = hashNodes;
	}

	public void setRootNode(NodeType rootNode) {
		this.rootNode = rootNode;
		graph.addVertex(rootNode);
		setDirty();

		// TODO: this should be removed later on, only for testing purposes
		if (rootNode instanceof ClusterNode) {
			ClusterNode clusterNode = (ClusterNode) rootNode;
			hashNodes.put(clusterNode.getID(), rootNode);
			if (hashLeafIDToNodeIDs.containsKey(clusterNode.getLeafID())) {
				ArrayList<Integer> alNodeIDs = hashLeafIDToNodeIDs.get(clusterNode.getLeafID());
				alNodeIDs.add(clusterNode.getID());
			} else {
				ArrayList<Integer> alNodeIDs = new ArrayList<Integer>();
				alNodeIDs.add(clusterNode.getID());
				hashLeafIDToNodeIDs.put(clusterNode.getLeafID(), alNodeIDs);
			}
		}
		// if (rootNode instanceof IDrawAbleNode)
		// hashNodes.put(((IDrawAbleNode) rootNode).getID(), rootNode);
	}

	/**
	 * Returns the root of the tree or null if there is no root
	 *
	 * @return the root node or null
	 */
	public NodeType getRoot() {
		return rootNode;
	}

	/**
	 * Adds a child to the specified parent node. The order of the children is specified by their compare method
	 *
	 * @param parentNode
	 *            the parent where the child will be added
	 * @param childNode
	 *            the child to be added
	 */
	public void addChild(NodeType parentNode, NodeType childNode) {
		graph.addVertex(childNode);
		graph.addEdge(parentNode, childNode);

		setDirty();

		hashNodes.put(childNode.getID(), childNode);
		if (childNode.getLeafID() >= 0) {
			if (hashLeafIDToNodeIDs.containsKey(childNode.getLeafID())) {
				ArrayList<Integer> alNodeIDs = hashLeafIDToNodeIDs.get(childNode.getLeafID());
				alNodeIDs.add(childNode.getID());
			} else {
				ArrayList<Integer> alNodeIDs = new ArrayList<Integer>();
				alNodeIDs.add(childNode.getID());
				hashLeafIDToNodeIDs.put(childNode.getLeafID(), alNodeIDs);
			}
		}

		// childNode.setUseDefaultComparator(useDefaultComparator);
	}

	/**
	 * Add a list of children to the specified parent node. Uses {@link #addChildren(ANode, List)} internally
	 *
	 * @param parentNode
	 *            the node to which the children are added
	 * @param children
	 *            the list of children
	 */
	public void addChildren(NodeType parentNode, List<NodeType> children) {
		for (NodeType child : children) {
			addChild(parentNode, child);
		}
	}

	/**
	 * Returns the parent of a specified node. If the node is root, then null is returned.
	 *
	 * @param childNode
	 *            the node of which the parent is of interest
	 * @return the parent node of childNode or null if childNode is root
	 */
	public NodeType getParent(NodeType childNode) {
		Set<DefaultEdge> setEdges = graph.incomingEdgesOf(childNode);
		DefaultEdge edge = null;

		for (DefaultEdge tempEdge : setEdges) {
			edge = tempEdge;
		}
		if (edge == null) {
			// this is the root node
			return null;
		}

		NodeType parentNode = graph.getEdgeSource(edge);
		return parentNode;
	}

	/**
	 * Returns a list of children of parentNode. The returned list is sorted based on the compare method of the
	 * children.
	 *
	 * @param parentNode
	 *            the node of which the children are requested
	 * @return the sorted list of children
	 */
	public ArrayList<NodeType> getChildren(NodeType parentNode) {
		Set<DefaultEdge> setEdges = null;
		try {
			setEdges = graph.outgoingEdgesOf(parentNode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<NodeType> alNodes = new ArrayList<NodeType>();
		for (DefaultEdge tempEdge : setEdges) {
			alNodes.add(graph.getEdgeTarget(tempEdge));
		}

		Collections.sort(alNodes);

		if (alNodes.isEmpty())
			return null;
		else
			return alNodes;
	}

	/**
	 * Returns true, when parentNode has children, else false
	 *
	 * @param parentNode
	 *            the node to be checked
	 * @return true if node has children, else false
	 */
	public boolean hasChildren(NodeType parentNode) {
		Set<DefaultEdge> setEdges = graph.outgoingEdgesOf(parentNode);
		if (setEdges.size() == 0)
			return false;
		return true;
	}

	/**
	 * Returns the graph structure on which the implementation of the tree is based, for advanced manual manipulations.
	 * For example shortest path etc. can be calculated.
	 *
	 * @return the complete graph
	 */
	public DirectedGraph<NodeType, DefaultEdge> getGraph() {
		return graph;
	}

	public void setGraph(DirectedGraph<NodeType, DefaultEdge> graph) {
		this.graph = graph;
	}

	public NodeType getNodeByNumber(int iClusterNr) {
		return hashNodes.get(iClusterNr);
	}

	public int getNumberOfNodes() {
		return hashNodes.size();
	}

	public ArrayList<Integer> getNodeIDsFromLeafID(int iLeafID) {
		return hashLeafIDToNodeIDs.get(iLeafID);
	}

	/**
	 * Returns the depth of the tree
	 *
	 * @return the depth of the tree
	 */
	public int getDepth() {
		// Update iDepth if tree has changed
		if (isDirty()) {
			makeClean();
			iDepth = rootNode.getDepth();
		}
		return iDepth;
	}

	private void reCalculateMetaInfo() {
		rootNode.calculateHierarchyDepth();
		rootNode.calculateLeaveIDs();
		rootNode.calculateHierarchyLevels(0);
	}

	/**
	 * The depth flag is a performance tool to avoid the recursive calculating of the getDepth() function when depth is
	 * unmodified
	 */
	public void setDirty() {
		this.isDirty = true;
	}

	public void makeClean() {
		reCalculateMetaInfo();
		this.isDirty = false;
	}

	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * Set the sorting strategy to be used for this tree. The default sorting (based on the cluster ID, which is
	 * generated on the fly) needs not be table. If a non-default strategy is used the nodes in the tree must implement
	 * the {@link Comparable#compareTo(Object)} method with options for the sorting strategies
	 */
	public void setSortingStrategy(ESortingStrategy sortingStrategy) {
		this.sortingStrategy = sortingStrategy;

	}

	/**
	 * Get the sorting strategy used in this tree.
	 *
	 * @return
	 */
	public ESortingStrategy getSortingStrategy() {
		return sortingStrategy;
	}

}
