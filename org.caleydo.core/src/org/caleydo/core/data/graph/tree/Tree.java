package org.caleydo.core.data.graph.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.data.graph.tree.NodeInfo;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A implementation of a sorted tree, based on the JGraphT library.
 * 
 * @author Alexander Lex
 * @param <NodeType>
 */
public class Tree<NodeType extends Comparable<NodeType>> {

	private NodeType rootNode;

	DirectedGraph<NodeType, DefaultEdge> graph;

	private HashMap<Integer, NodeType> hashNodes;

	private int iDepth;

	private boolean bDepthFlag;

	private HashMap<NodeType, NodeInfo> mNodeMap;

	private HashMap<Integer, Integer> mLayerMap;

	public Tree() {

		graph = new DefaultDirectedGraph<NodeType, DefaultEdge>(DefaultEdge.class);
		hashNodes = new HashMap<Integer, NodeType>();
		mNodeMap = new HashMap<NodeType, NodeInfo>();
		mLayerMap = new HashMap<Integer, Integer>();

	}

	public void setHashMap(HashMap<Integer, NodeType> hashNodes) {
		this.hashNodes = hashNodes;
	}

	public void setRootNode(NodeType rootNode) {
		this.rootNode = rootNode;
		graph.addVertex(rootNode);

		NodeInfo info = new NodeInfo("root", true, 1);
		mNodeMap.put(this.rootNode, info);

		increaseNumberOfElementsInLayer(1);
		setDepthFlag();

		// TODO: this should be removed later on, only for testing purposes
		if (rootNode instanceof ClusterNode)
			hashNodes.put(((ClusterNode) rootNode).getClusterNr(), rootNode);
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
	 * Adds a child to the specified parent node. The order of the children is specified by their compare
	 * method
	 * 
	 * @param parentNode
	 *            the parent where the child will be added
	 * @param childNode
	 *            the child to be added
	 */
	public void addChild(NodeType parentNode, NodeType childNode) {
		graph.addVertex(childNode);
		graph.addEdge(parentNode, childNode);

		NodeInfo parentInfo = mNodeMap.get(parentNode);
		int currentLayer = parentInfo.getLayer() + 1;
		increaseNumberOfElementsInLayer(currentLayer);

		NodeInfo info = new NodeInfo("child", false, currentLayer);

		mNodeMap.put(childNode, info);
		parentInfo.increaseNumberOfKids();

		for (NodeType tmpChild : getChildren(parentNode)) {
			NodeInfo tmpInfo = mNodeMap.get(tmpChild);
			 tmpInfo.increaseNumberOfSiblings();
		}
		setDepthFlag();

		// TODO: this should be removed later on, only for testing purposes
		if (childNode instanceof ClusterNode)
			hashNodes.put(((ClusterNode) childNode).getClusterNr(), childNode);
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
			// TODO: this should be removed later on, only for testing purposes
			if (child instanceof ClusterNode)
				hashNodes.put(((ClusterNode) child).getClusterNr(), child);
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
			edge = (DefaultEdge) tempEdge;
		}
		if (edge == null) {
			// this is the root node
			return null;
		}

		NodeType parentNode = graph.getEdgeSource(edge);
		return parentNode;
	}

	/**
	 * Returns a list of children of parentNode. The returned list is sorted based on the compare method of
	 * the children.
	 * 
	 * @param parentNode
	 *            the node of which the childs are requested
	 * @return the sorted list of children
	 */
	public ArrayList<NodeType> getChildren(NodeType parentNode) {
		Set<DefaultEdge> setEdges = graph.outgoingEdgesOf(parentNode);

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
	 * Returns the graph structure on which the implementation of the tree is based, for advanced manual
	 * manipulations. For example shortest path etc. can be calculated.
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

	/**
	 * Each key in the mLayerMap holds the number of the elements in 
	 * the particular layer. This function increases the elements - number 
	 * of the given layer.
	 * 
	 * @param layer
	 *            Its is the key of the layerMap, representing the layer
	 */
	public void increaseNumberOfElementsInLayer(int layer) {
		if (mLayerMap.containsKey(layer))
			mLayerMap.put(layer, mLayerMap.get(layer) + 1);
		else
			mLayerMap.put(layer, 1);
	}

	/**
	 * Returns the number of elements in the given layer
	 * 
	 * @param layer 
	 * 			The value of this key gets returned 
	 * @return the number of elements
	 */
	public int getNumberOfElementsInLayer(int layer) {

		return mLayerMap.get(layer);

	}

	/**
	 * Returns the depth of the tree, using a recursive function, starting
	 * at the root node
	 * 
	 * @return the depth of the tree
	 */
	public int getDepth() {

		if (isDepthFlagDirty()) {
			resetDepthFlag();

			iDepth = determineDepth(rootNode);

		}
		return iDepth;
	}

	private int determineDepth(NodeType node) {

		NodeInfo info = mNodeMap.get(node);
		if (hasChildren(node)) {
			int tmpDepth = 0;
			for (NodeType currentNode : getChildren(node)) {
				int iChildDepth = determineDepth(currentNode);
				if (tmpDepth <= iChildDepth)
					tmpDepth = iChildDepth;
			}
			return tmpDepth;
		}
		else
			return info.getLayer();
	}

	/**
	 * Returns the number of all nodes in the tree
	 * 
	 * @return number of nodes
	 */
	public int getNumberOfNodes() {
		return mNodeMap.size();
	}

	/**
	 * The depth flag is a performance tool to avoid the
	 * recursive calculating of the getDepth() function when
	 * depth is unmodified
	 */
	public void setDepthFlag() {
		this.bDepthFlag = true;
	}

	public void resetDepthFlag() {
		this.bDepthFlag = false;
	}

	public boolean isDepthFlagDirty() {
		return bDepthFlag;
	}

}
