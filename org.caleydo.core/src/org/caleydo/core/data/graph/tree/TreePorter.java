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
package org.caleydo.core.data.graph.tree;

import java.io.BufferedReader;
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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
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
	ArrayList<Integer[]> edges = new ArrayList<Integer[]>();

	@XmlElementWrapper(name = "nodes")
	@XmlElement(name = "node")
	Set<ClusterNode> nodeSet;
	String leaveIDTypeString;

	private ATableBasedDataDomain dataDomain;

	@XmlElement
	private ESortingStrategy sortingStrategy = ESortingStrategy.DEFAULT;

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public ATableBasedDataDomain getDataDomain() {
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
	public ClusterTree importTree(String fileName, IDType leafIDType)
			throws JAXBException, FileNotFoundException {

		JAXBContext jaxbContext = null;
		TreePorter treePorter = null;
		Unmarshaller unmarshaller;

		jaxbContext = JAXBContext.newInstance(TreePorter.class);
		unmarshaller = jaxbContext.createUnmarshaller();
		BufferedReader treeFileReader;
		try {
			treeFileReader = GeneralManager.get().getResourceLoader()
					.getResource(fileName);
		} catch (IllegalStateException fnfe) {
//			Logger.log(new Status(IStatus.INFO, "TreePorter", "No tree available for "
//					+ fileName));
			return null;
		}
		treePorter = (TreePorter) unmarshaller.unmarshal(treeFileReader);

		ClusterTree tree = new ClusterTree(leafIDType, treePorter.nodeSet.size());
		// tree.initializeIDTypes(IDType.getIDType(leaveIDTypeString));
		ClusterNode rootNode = null;

		DirectedGraph<ClusterNode, DefaultEdge> graph = new DefaultDirectedGraph<ClusterNode, DefaultEdge>(
				DefaultEdge.class);

		tree.setSortingStrategy(treePorter.sortingStrategy);

		int size = (int) (treePorter.nodeSet.size() * 1.5);
		HashMap<Integer, ClusterNode> hashClusterNr = new HashMap<Integer, ClusterNode>(
				size);
		// HashMap<String, ClusterNode> hashClusterNodes = new HashMap<String,
		// ClusterNode>(size);
		HashMap<Integer, ArrayList<Integer>> hashLeafIDToNodeIDs = new HashMap<Integer, ArrayList<Integer>>(
				size);

		for (ClusterNode node : treePorter.nodeSet) {
			graph.addVertex(node);
			// hashClusterNodes.put(node.toString(), node);
			hashClusterNr.put(node.getID(), node);
			if (node.isRootNode())
				rootNode = node;
			node.setTree(tree);
			node.setNode(node);

			// take care of hashing leaf ids to node ids
			if (node.getLeafID() >= 0) {
				if (hashLeafIDToNodeIDs.containsKey(node.getLeafID())) {
					ArrayList<Integer> alNodeIDs = hashLeafIDToNodeIDs.get(node
							.getLeafID());
					alNodeIDs.add(node.getID());
				} else {

					ArrayList<Integer> alNodeIDs = new ArrayList<Integer>();
					alNodeIDs.add(node.getID());
					hashLeafIDToNodeIDs.put(node.getLeafID(), alNodeIDs);
				}
			}
		}

		for (Integer[] edge : treePorter.edges) {
			graph.addEdge(hashClusterNr.get(edge[0]), hashClusterNr.get(edge[1]));
		}

		tree.setHashMap(hashClusterNr);
		tree.setRootNode(rootNode);
		tree.setGraph(graph);
		tree.hashLeafIDToNodeIDs = hashLeafIDToNodeIDs;

		return tree;
	}

	public ClusterTree importDimensionTree(String fileName) throws JAXBException,
			FileNotFoundException {
		ClusterTree tree = importTree(fileName, dataDomain.getDimensionIDType());
		return tree;
	}

	/**
	 * Export function uses {@link JAXBContext} to export a given tree into an
	 * XML file.
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
	public void exportTree(String fileName, Tree<ClusterNode> tree) throws JAXBException,
			IOException {
		FileWriter writer = new FileWriter(fileName);
		try {
			exportTree(writer, tree);
			writer.close();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {
					// nothing to do here, assuming the writer is closed
				}
				writer = null;
			}
		}
	}

	/**
	 * Export function uses {@link JAXBContext} to export a given tree to a
	 * {@link Writer}
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
	public void exportTree(Writer writer, Tree<ClusterNode> tree) throws JAXBException,
			IOException {

		Set<DefaultEdge> edgeSet = tree.graph.edgeSet();

		for (DefaultEdge edge : edgeSet) {
			Integer temp[] = new Integer[2];
			temp[0] = tree.graph.getEdgeSource(edge).getID();
			temp[1] = tree.graph.getEdgeTarget(edge).getID();
			edges.add(temp);
		}

		nodeSet = tree.graph.vertexSet();
		leaveIDTypeString = tree.getLeaveIDType().getTypeName();
		sortingStrategy = tree.getSortingStrategy();

		JAXBContext jaxbContext = JAXBContext.newInstance(TreePorter.class,
				DefaultEdge.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.marshal(this, writer);
	}
}
