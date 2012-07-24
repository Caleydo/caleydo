/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.path;

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
	 * 
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

	public void addNode(INode existingNode, INode newNode) {
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

	public static void main(String args[]) {
		// Path path = new Path();
		// path.addNode(new Node("Oans", "Oansaview"));
		// Node node = new Node("Zwoa", "Zwoaview");
		// path.addNode(node);
		// path.addNode(new Node("Drei", "Dreierview"));
		// path.addNode(node, new Node("Via", "Viaraview"));

		// for(Node tempNode : path.getFollowingNodes(node))
		// {
		// System.out.println(tempNode);
		// }
		// System.out.println("");
		//
		// System.out.println(path.getGraph());
	}
}
