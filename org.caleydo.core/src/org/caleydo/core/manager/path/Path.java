package org.caleydo.core.manager.path;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Directed graph used for history path and guidance path.
 * 
 * @author Alexander Lex
 */
public class Path {

	private DefaultDirectedGraph<INode, DefaultEdge> graph;

	private INode lastNode = null;

	public Path() {
		graph = new DefaultDirectedGraph<INode, DefaultEdge>(DefaultEdge.class);
	}

	/**
	 * Append node to the node last added
	 * @param newNode
	 */
	public void addNode(INode newNode) {
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
	
	public void addNode(INode existingNode, INode newNode)
	{
		graph.addVertex(newNode);
		graph.addEdge(existingNode, newNode);
		lastNode = newNode;
	}

	public Set<DefaultEdge> getEdgesOf(INode node) {
		return graph.edgesOf(node);
	}
	
	public INode getLastNode() {
		return lastNode;
	}
	
	public void setLastNode(INode lastNode) {
		this.lastNode = lastNode;
	}

	public ArrayList<INode> getFollowingNodes(INode node) {
		Set<DefaultEdge> edges = graph.outgoingEdgesOf(node);
		ArrayList<INode> nodes = new ArrayList<INode>();
		for (DefaultEdge edge : edges) {
			nodes.add(graph.getEdgeTarget(edge));
		}
		return nodes;
	}

	public ArrayList<INode> getPrecedingNode(INode node) {
		Set<DefaultEdge> edges = graph.incomingEdgesOf(node);
		ArrayList<INode> nodes = new ArrayList<INode>();
		for (DefaultEdge edge : edges) {
			nodes.add(graph.getEdgeSource(edge));
		}
		return nodes;
	}

	public DefaultDirectedGraph<INode, DefaultEdge> getGraph() {
		return graph;
	}
	
	
	public static void main(String args[])
	{
		Path path = new Path();
//		path.addNode(new Node("Oans", "Oansaview"));
//		Node node = new Node("Zwoa", "Zwoaview");
//		path.addNode(node);
//		path.addNode(new Node("Drei", "Dreierview"));
//		path.addNode(node,  new Node("Via", "Viaraview"));
		
		
//		for(Node tempNode : path.getFollowingNodes(node))
//		{
//			System.out.println(tempNode);
//		}
//		System.out.println("");
//		
//		System.out.println(path.getGraph());
	}
}
