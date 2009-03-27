package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.IGraph;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer
	implements IClusterer {
	private Cobweb clusterer;

	public HierarchicalClusterer(int iNrElements) {
		clusterer = new Cobweb();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdStorage) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();

//		 System.out.println("iVAIdOriginal" + iVAIdOriginal);
//		 System.out.println("iVAIdStorage" + iVAIdStorage);

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");
		// optional
		// buffer.append("@attribute Instance_name { A, B, C, D}\n");

		for (int nr = 0; nr < set.size(); nr++) {
			buffer.append("@attribute Patient" + nr + " real\n");
		}

		buffer.append("@data\n");

		// System.out.println(set.getVA(iVAIdOriginal).size());

		IVirtualArray contentVA = set.getVA(iVAIdOriginal);

		for (Integer iContentIndex : contentVA) {
			IVirtualArray storageVA = set.getVA(iVAIdStorage);
			for (Integer iStorageIndex : storageVA) {
				buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex) + ", ");
			}
			buffer.append("\n");
		}

		// System.out.println(buffer.toString());

		Instances data = null;
		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		// unsupervised learning --> no class given
		data.setClassIndex(-1);

		// System.out.println(data.toString());

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println(clusterer);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		double[] test = eval.getClusterAssignments();
		int nrclusters = eval.getNumClusters();
		
		System.out.println(nrclusters);
		System.out.println(data.numAttributes());
		System.out.println(data.numInstances());

		ArrayList<Integer> temp = new ArrayList<Integer>();

		for (int i = 0; i < nrclusters; i++) {
			temp.add(0);
		}

		for (int cluster = 0; cluster < nrclusters; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (test[i] == cluster) {
					indexes.add(i);
					temp.set(cluster, temp.get(cluster) + 1);
				}
			}
		}

		Integer clusteredVAId = set.createStorageVA(indexes);

		// set cluster result in Set
		HierarchyGraph graph = new HierarchyGraph();
		
		CNode node = clusterer.m_cobwebTree;
		
		graph = matchTree(graph, node);
		
		System.out.println("cnt: " + cnt);
		
		set.setClusteredGraph(graph);

		return clusteredVAId;
	}

	private int cnt = 0;
	
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
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage) {

		Integer VAId = 0;

		VAId = cluster(set, idContent, idStorage);

		return VAId;
	}
}
