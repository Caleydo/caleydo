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

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Class responsible for exporting and importing (clustered) {@link Tree}
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
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
	String leaveIDTypeString;

	private ASetBasedDataDomain dataDomain;

	@XmlElement
	private boolean useDefaultComparator = true;

	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Imports a tree with the aid of {@link JAXBContext}.
	 * 
	 * @param fileName
	 *            Full file name of the serialized tree
	 * @return the imported tree
	 * @throws JAXBException
	 *             in case of a XML-serialization error
	 */
	public ClusterTree importTree(String fileName, IDType leafIDType) throws JAXBException,
		FileNotFoundException {

		ClusterTree tree = new ClusterTree(leafIDType);
		// tree.initializeIDTypes(IDType.getIDType(leaveIDTypeString));
		ClusterNode rootNode = null;

		DirectedGraph<ClusterNode, DefaultEdge> graph =
			new DefaultDirectedGraph<ClusterNode, DefaultEdge>(DefaultEdge.class);

		JAXBContext jaxbContext = null;
		TreePorter treePorter = null;
		Unmarshaller unmarshaller;

		jaxbContext = JAXBContext.newInstance(TreePorter.class);
		unmarshaller = jaxbContext.createUnmarshaller();
		treePorter =
			(TreePorter) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(fileName));

		tree.setUseDefaultComparator(treePorter.useDefaultComparator);

		int size = (int) (treePorter.nodeSet.size() * 1.5);
		HashMap<Integer, ClusterNode> hashClusterNr = new HashMap<Integer, ClusterNode>(size);
		HashMap<String, ClusterNode> hashClusterNodes = new HashMap<String, ClusterNode>(size);
		HashMap<Integer, ArrayList<Integer>> hashLeafIDToNodeIDs =
			new HashMap<Integer, ArrayList<Integer>>(size);

		for (ClusterNode node : treePorter.nodeSet) {
			graph.addVertex(node);
			hashClusterNodes.put(node.toString(), node);
			hashClusterNr.put(node.getID(), node);
			if (node.isRootNode())
				rootNode = node;
			node.setTree(tree);
			node.setNode(node);

			// take care of hashing leaf ids to node ids
			if (node.getLeafID() >= 0) {
				if (hashLeafIDToNodeIDs.containsKey(node.getLeafID())) {
					ArrayList<Integer> alNodeIDs = hashLeafIDToNodeIDs.get(node.getLeafID());
					alNodeIDs.add(node.getID());
				}
				else {

					ArrayList<Integer> alNodeIDs = new ArrayList<Integer>();
					alNodeIDs.add(node.getID());
					hashLeafIDToNodeIDs.put(node.getLeafID(), alNodeIDs);
				}
			}
		}

		for (String[] edge : treePorter.edges) {
			graph.addEdge(hashClusterNodes.get(edge[0]), hashClusterNodes.get(edge[1]));
		}

		tree.setHashMap(hashClusterNr);
		tree.setRootNode(rootNode);
		tree.setGraph(graph);
		tree.hashLeafIDToNodeIDs = hashLeafIDToNodeIDs;

		return tree;
	}

	public ClusterTree importStorageTree(String fileName) throws JAXBException, FileNotFoundException {
		ClusterTree tree = importTree(fileName, dataDomain.getStorageIDType());

		org.caleydo.core.data.collection.set.Set set =
			(org.caleydo.core.data.collection.set.Set) dataDomain.getSet();
		tree.createMetaSets(set);
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
			temp[0] = tree.graph.getEdgeSource(edge).getLabel();
			temp[1] = tree.graph.getEdgeTarget(edge).getLabel();
			edges.add(temp);
		}

		nodeSet = tree.graph.vertexSet();
		leaveIDTypeString = tree.getLeaveIDType().getTypeName();
		useDefaultComparator = tree.useDefaultComparator;

		JAXBContext jaxbContext = JAXBContext.newInstance(TreePorter.class, DefaultEdge.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.marshal(this, writer);
	}
}
