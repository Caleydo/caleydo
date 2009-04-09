package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.IGraph;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer
	implements IClusterer {

	private Cobweb clusterer;

	Tree<ClusterNode> tree = new Tree<ClusterNode>();

	public HierarchicalClusterer(int iNrElements) {
		clusterer = new Cobweb();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdStorage,
		EClustererType eClustererType) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		IVirtualArray contentVA = set.getVA(iVAIdOriginal);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			for (int nr = 0; nr < storageVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			for (Integer iContentIndex : contentVA) {
				for (Integer iStorageIndex : storageVA) {
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex - 1)
						+ ", ");

				}
				buffer.append("\n");
			}
		}
		else {
			for (int nr = 0; nr < contentVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			for (Integer iStorageIndex : storageVA) {
				for (Integer iContentIndex : contentVA) {
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex - 1)
						+ ", ");

				}
				buffer.append("\n");
			}
		}

		Instances data = null;
		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		// unsupervised learning --> no class given
		data.setClassIndex(-1);

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		double[] clusterAssignments = eval.getClusterAssignments();
		int nrclusters = eval.getNumClusters();

		System.out.println(nrclusters);
		System.out.println(data.numAttributes());
		System.out.println(data.numInstances());

		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<Integer> alExamples = new ArrayList<Integer>();
		for (int i = 0; i < nrclusters; i++) {
			temp.add(0);
			alExamples.add(0);
		}

		for (int cluster = 0; cluster < nrclusters; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (clusterAssignments[i] == cluster) {
					indexes.add(i);
					temp.set(cluster, temp.get(cluster) + 1);
				}
			}
		}

		Integer clusteredVAId = set.createStorageVA(indexes);

		// set cluster result Set
		HierarchyGraph graph = new HierarchyGraph();

		CNode node = clusterer.m_cobwebTree;

		ClusterNode clusterNode = new ClusterNode("Root", 1, 0f, 0);
		tree.setRootNode(clusterNode);

		CNodeToTree(clusterNode, node);

		set.setClusteredTree(tree);

		// graph = matchTree(graph, node);
		// set.setClusteredGraph(graph);

		set.setAlClusterSizes(temp);
		set.setAlExamples(alExamples);

		return clusteredVAId;
	}

	private int cnt = 0;

	private void CNodeToTree(ClusterNode clusterNode, CNode node) {

		if (node.getChilds() != null) {
			int iNrChildsNode = node.getChilds().size();

			for (int i = 0; i < iNrChildsNode; i++) {

				CNode currentNode = (CNode) node.getChilds().elementAt(i);
				ClusterNode currentGraph =
					new ClusterNode("Node_" + currentNode.getClusterNum(), currentNode.getClusterNum(), 0f, 0);
				tree.addChild(clusterNode, currentGraph);
				// temp.addGraph(matchTree(currentGraph, currentNode), EGraphItemHierarchy.GRAPH_CHILDREN);
				CNodeToTree(currentGraph, currentNode);
			}
		}

	}

	private HierarchyGraph matchTree(IGraph graph, CNode Node) {
		HierarchyGraph temp = new HierarchyGraph("Node" + Node.getClusterNum(), Node.getClusterNum(), 0);
		cnt++;
		graph = temp;

		if (Node.getChilds() != null) {
			int iNrChildsNode = Node.getChilds().size();

			for (int i = 0; i < iNrChildsNode; i++) {

				CNode currentNode = (CNode) Node.getChilds().elementAt(i);
				HierarchyGraph currentGraph = new HierarchyGraph();
				temp.addGraph(matchTree(currentGraph, currentNode), EGraphItemHierarchy.GRAPH_CHILDREN);
			}
		}

		return temp;
	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, EClustererType eClustererType) {

		Integer VAId = 0;

		VAId = cluster(set, idContent, idStorage, eClustererType);

		return VAId;
	}
}
