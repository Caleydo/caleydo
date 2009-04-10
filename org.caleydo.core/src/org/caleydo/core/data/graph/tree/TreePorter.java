package org.caleydo.core.data.graph.tree;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TreePorter {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "root")
	ClusterNode root;

	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	ArrayList<String> edges = new ArrayList<String>();

	@XmlElementWrapper(name = "nodes")
	@XmlElement(name = "node")
	Set<ClusterNode> nodeSet;

	public Tree<ClusterNode> importTree(String fileName) {

		Tree<ClusterNode> tree = new Tree<ClusterNode>();
		ClusterNode rootNode = null;

		DirectedGraph<ClusterNode, DefaultEdge> graph =
			new DefaultDirectedGraph<ClusterNode, DefaultEdge>(DefaultEdge.class);

		JAXBContext jaxbContext = null;
		TreePorter treePorter = null;
		Unmarshaller unmarshaller;

		HashMap<String, ClusterNode> hashClusterNodes = new HashMap<String, ClusterNode>();

		try {
			jaxbContext = JAXBContext.newInstance(TreePorter.class);
			unmarshaller = jaxbContext.createUnmarshaller();
			treePorter = (TreePorter) unmarshaller.unmarshal(new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}

		rootNode = treePorter.root;

		for (ClusterNode node : treePorter.nodeSet) {
			graph.addVertex(node);
			hashClusterNodes.put(node.toString(), node);
		}

		for (String edge : treePorter.edges) {

			StringTokenizer strTokenLine = new StringTokenizer(edge, "(");
			String temp = strTokenLine.nextToken();
			strTokenLine = new StringTokenizer(temp, ")");
			temp = strTokenLine.nextToken();
			strTokenLine = new StringTokenizer(temp, ":");

			String Node1 = strTokenLine.nextToken().trim();
			String Node2 = strTokenLine.nextToken().trim();

			graph.addEdge(hashClusterNodes.get(Node1), hashClusterNodes.get(Node2));

		}
		tree.setRootNode(rootNode);
		tree.setGraph(graph);

		return tree;
	}

	public boolean exportTree(String fileName, DirectedGraph<ClusterNode, DefaultEdge> graph, ClusterNode root)
		throws JAXBException, IOException {

		this.root = root;

		Set<DefaultEdge> edgeSet = (Set<DefaultEdge>) graph.edgeSet();

		for (DefaultEdge edge : edgeSet) {
			edges.add(edge.toString());
		}

		nodeSet = graph.vertexSet();

		JAXBContext jaxbContext = JAXBContext.newInstance(TreePorter.class, DefaultEdge.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		Writer w = null;
		try {
			w = new FileWriter(fileName);
			marshaller.marshal(this, w);
		}
		finally {
			try {
				w.close();
			}
			catch (Exception e) {
				return false;
			}
		}
		return true;
	}
}
