package org.caleydo.core.manager.path;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;;

/**
 * Directed graph used for history path and guidance path.
 * 
 * @author Alexander Lex
 */
public class Path {

	private DefaultDirectedGraph<Node, DefaultEdge> graph;

	private Node lastNode = null;

	public Path() {
		graph = new DefaultDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);
		// dataDomainGraph = new SimpleGraph<Node, Edge>(edgeFactory);

	}

	/**
	 * Append node to the node last added
	 * @param newNode
	 */
	public void addNode(Node newNode) {
		graph.addVertex(newNode);
		if (lastNode == null) {
			lastNode = newNode;
			return;
		}
		else {
			graph.addEdge(lastNode, newNode);
			lastNode = newNode;
		}
	}
	
	public void addNode(Node existingNode, Node newNode)
	{
		graph.addVertex(newNode);
		graph.addEdge(existingNode, newNode);
		lastNode = newNode;
	}

	public Set<DefaultEdge> getEdgesOf(Node node) {
		return graph.edgesOf(node);
	}

	public ArrayList<Node> getFollowingNodes(Node node) {
		Set<DefaultEdge> edges = graph.outgoingEdgesOf(node);
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (DefaultEdge edge : edges) {
			nodes.add(graph.getEdgeTarget(edge));
		}
		return nodes;
	}

	public ArrayList<Node> getPrecedingNode(Node node) {
		Set<DefaultEdge> edges = graph.incomingEdgesOf(node);
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (DefaultEdge edge : edges) {
			nodes.add(graph.getEdgeSource(edge));
		}
		return nodes;
	}

	public DefaultDirectedGraph<Node, DefaultEdge> getGraph() {
		return graph;
	}
	
	
	public static void main(String args[])
	{
		Path path = new Path();
		path.addNode(new Node("Oans", "Oansaview"));
		Node node = new Node("Zwoa", "Zwoaview");
		path.addNode(node);
		path.addNode(new Node("Drei", "Dreierview"));
		path.addNode(node,  new Node("Via", "Viaraview"));
		
		
		for(Node tempNode : path.getFollowingNodes(node))
		{
			System.out.println(tempNode);
		}
		System.out.println("");
		
		System.out.println(path.getGraph());
	}
	

}
