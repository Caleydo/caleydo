package org.caleydo.core.data.graph.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.caleydo.core.util.clusterer.ClusterNode;
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

	TreePorter porter = new TreePorter();

	public Tree() {

		graph = new DefaultDirectedGraph<NodeType, DefaultEdge>(DefaultEdge.class);

	}

	public void setRootNode(NodeType rootNode) {
		this.rootNode = rootNode;
		graph.addVertex(rootNode);
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

	public boolean exportTree(String fileName) throws JAXBException, IOException {

		return porter.exportTree(fileName, (DirectedGraph<ClusterNode, DefaultEdge>) this.graph,
			(ClusterNode) this.rootNode);

	}

	public Tree<NodeType> importTree(String file) {

		return (Tree<NodeType>) porter.importTree(file);

	}
}
