package org.caleydo.core.data.graph.tree;

import java.io.FileNotFoundException;
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

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Class responsible for exporting and importing (clustered) {@link Tree}
 * 
 * @author Bernhard Schlegl
 */
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

	/**
	 * Imports a tree with the aid of {@link JAXBContext}.
	 * 
	 * @param fileName
	 *            Full file name of the serialized tree
	 * @return the imported tree
	 * @throws JAXBException
	 *             in case of a XML-serialization error
	 */
	public Tree<ClusterNode> importTree(String fileName) throws JAXBException, FileNotFoundException {

		Tree<ClusterNode> tree = new Tree<ClusterNode>();
		ClusterNode rootNode = null;

		DirectedGraph<ClusterNode, DefaultEdge> graph =
			new DefaultDirectedGraph<ClusterNode, DefaultEdge>(DefaultEdge.class);

		JAXBContext jaxbContext = null;
		TreePorter treePorter = null;
		Unmarshaller unmarshaller;

		HashMap<Integer, ClusterNode> hashClusterNr = new HashMap<Integer, ClusterNode>();
		HashMap<String, ClusterNode> hashClusterNodes = new HashMap<String, ClusterNode>();

		jaxbContext = JAXBContext.newInstance(TreePorter.class);
		unmarshaller = jaxbContext.createUnmarshaller();
		treePorter =
			(TreePorter) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(fileName));

		for (ClusterNode node : treePorter.nodeSet) {
			graph.addVertex(node);
			hashClusterNodes.put(node.toString(), node);
			hashClusterNr.put(node.getID(), node);
			if (node.isRootNode())
				rootNode = node;
		}

		for (String[] edge : treePorter.edges) {
			graph.addEdge(hashClusterNodes.get(edge[0]), hashClusterNodes.get(edge[1]));
		}

		tree.setHashMap(hashClusterNr);
		tree.setRootNode(rootNode);
		tree.setGraph(graph);

		return tree;
	}

	/**
	 * Export function uses {@link JAXBContext} to export a given tree into an XML file.
	 * 
	 * @param fileName
	 *            name of the file where the exported tree should be saved
	 * @param tree
	 *            the tree wanted to export
	 * @throws JAXBException
	 *             in case of a XML-serialization error
	 * @throws IOException
	 *             in case of an error while writing to the stream
	 */
	public void exportTree(String fileName, Tree<ClusterNode> tree) throws JAXBException, IOException {
		FileWriter writer = new FileWriter(fileName);
		try {
			exportTree(writer, tree);
			writer.close();
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException ex) {
					// nothing to do here, assuming the writer is closed
				}
				writer = null;
			}
		}
	}

	/**
	 * Export function uses {@link JAXBContext} to export a given tree to a {@link Writer}
	 * 
	 * @param writer
	 *            {@link Writer} to write the serialized tree to.
	 * @param tree
	 *            the tree wanted to export
	 * @throws JAXBException
	 *             in case of a XML-serialization error
	 * @throws IOException
	 *             in case of an error while writing to the stream
	 */
	public void exportTree(Writer writer, Tree<ClusterNode> tree) throws JAXBException, IOException {

		Set<DefaultEdge> edgeSet = tree.graph.edgeSet();

		for (DefaultEdge edge : edgeSet) {
			String temp[] = new String[2];
			temp[0] = tree.graph.getEdgeSource(edge).getNodeName();
			temp[1] = tree.graph.getEdgeTarget(edge).getNodeName();
			edges.add(temp);
		}

		nodeSet = tree.graph.vertexSet();

		JAXBContext jaxbContext = JAXBContext.newInstance(TreePorter.class, DefaultEdge.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.marshal(this, writer);
	}
}
