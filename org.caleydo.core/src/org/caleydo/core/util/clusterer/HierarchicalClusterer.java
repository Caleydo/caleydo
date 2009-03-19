package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer {
	private Cobweb clusterer = new Cobweb();

	public HierarchicalClusterer() {
		clusterer = new Cobweb();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdClustered, Integer iVAIdStorage) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();

		// System.out.println("iVAIdOriginal" + iVAIdOriginal);
		// System.out.println("iVAIdStorage" + iVAIdStorage);

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

		for (Integer iter : temp) {
			if (iter > 0) {
				count.add(iter);
			}
		}

		Integer clusteredVAId = set.createStorageVA(indexes);

		// set cluster result in Set
		set.setAlClusterSizes(count);
		set.setClusteredGraph(clusterer.getGraph());

		return clusteredVAId;
	}
}
