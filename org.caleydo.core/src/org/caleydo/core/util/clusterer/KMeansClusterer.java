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

public class KMeansClusterer
	implements IClusterer {

	private SimpleKMeans clusterer = null;

	private int iNrCluster = 10;

	public KMeansClusterer(int iNrElements) {
		clusterer = new SimpleKMeans();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdStorage) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();

		try {
			clusterer.setNumClusters(iNrCluster);
			clusterer.setMaxIterations(1000);
		}
		catch (Exception e2) {
			e2.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		for (int nr = 0; nr < set.size(); nr++) {
			buffer.append("@attribute Patient" + nr + " real\n");
		}

		buffer.append("@data\n");

		IVirtualArray contentVA = set.getVA(iVAIdOriginal);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		for (Integer iContentIndex : contentVA) {
			for (Integer iStorageIndex : storageVA) {
				buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex)
					+ ", ");

			}
			buffer.append("\n");
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

		double[] ClusterAssignments = eval.getClusterAssignments();

		for (int i = 0; i < iNrCluster; i++) {
			count.add(0);
			alExamples.add(0);
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
		set.setAlClusterSizes(count);
		set.setAlExamples(alExamples);

		return clusteredVAId;
	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage) {

		Integer VAId = 0;

		VAId = cluster(set, idContent, idStorage);

		return VAId;
	}
}
