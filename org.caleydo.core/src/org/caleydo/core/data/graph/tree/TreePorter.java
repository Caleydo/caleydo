package org.caleydo.core.data.graph.tree;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	ArrayList<String[]> edges = new ArrayList<String[]>();

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

		for (ClusterNode node : treePorter.nodeSet) {
			graph.addVertex(node);
			hashClusterNodes.put(node.toString(), node);
			if (node.isRootNode())
				rootNode = node;
		}

		for (String[] edge : treePorter.edges) {
			graph.addEdge(hashClusterNodes.get(edge[0]), hashClusterNodes.get(edge[1]));
		}

		tree.setRootNode(rootNode);
		tree.setGraph(graph);

		return tree;
	}

	public boolean exportTree(String fileName, Tree<ClusterNode> tree) throws JAXBException, IOException {

		Set<DefaultEdge> edgeSet = (Set<DefaultEdge>) tree.graph.edgeSet();

		for (DefaultEdge edge : edgeSet) {
			String temp[] = new String[2];
			temp[0] = tree.graph.getEdgeSource(edge).getNodeName();
			temp[1] = tree.graph.getEdgeTarget(edge).getNodeName();
			edges.add(temp);
		}

		nodeSet = tree.graph.vertexSet();

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
