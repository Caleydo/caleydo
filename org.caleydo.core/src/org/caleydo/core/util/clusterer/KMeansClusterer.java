package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClusterer {

	private SimpleKMeans clusterer = null;

	private int iNrCluster = 50;

	public KMeansClusterer() {
		clusterer = new SimpleKMeans();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdClustered, Integer iVAIdStorage) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();

		try {
			clusterer.setNumClusters(iNrCluster);
		}
		catch (Exception e2) {
			e2.printStackTrace();
		}

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

		// System.out.println(dataToCluster.getVA(iContentVAID).size());

		IVirtualArray contentVA = set.getVA(iVAIdOriginal);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		for (Integer iContentIndex : contentVA) {
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

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println(clusterer);

		double[] ClusterAssignments = eval.getClusterAssignments();

		for (int i = 0; i < iNrCluster; i++) {
			count.add(0);
		}

		for (int cluster = 0; cluster < iNrCluster; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == cluster) {
					indexes.add(i);
					count.set(cluster, count.get(cluster) + 1);
				}
			}
		}

		Integer clusteredVAId = set.createStorageVA(indexes);

		// set cluster result in Set
		set.setClusteredGraph(null); // no hierarchical clustering --> no graph
		set.setAlClusterSizes(count);

		return clusteredVAId;
	}
}
