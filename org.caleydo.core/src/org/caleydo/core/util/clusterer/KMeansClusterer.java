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

	private int iNrCluster = 15;

	public KMeansClusterer(int iNrElements) {
		clusterer = new SimpleKMeans();
	}

	public Integer cluster(ISet set, Integer iVAIdContent, Integer iVAIdStorage, EClustererType eClustererType) {

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

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			for (int nr = 0; nr < storageVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			for (Integer iContentIndex : contentVA) {
				for (Integer iStorageIndex : storageVA) {
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
						iContentIndex - 1)
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
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
						iContentIndex - 1)
						+ ", ");

				}
				buffer.append("\n");
			}
		}

		Instances data = null;

		// System.out.println(buffer.toString());

		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

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
		}

		// System.out.println(eval.getNumClusters());
		// System.out.println(data.numAttributes());
		// System.out.println(data.numInstances());

		for (int j = 0; j < iNrCluster; j++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == j) {
					alExamples.add(i - 1);
					break;
				}
			}
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		// ClusterHelper.sortClusters(set, iVAIdContent, iVAIdStorage, alExamples, eClustererType);

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
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, ClusterState clusterState) {

		Integer VAId = 0;

		iNrCluster = clusterState.getKMeansClusterCnt();
		
		VAId = cluster(set, idContent, idStorage, clusterState.getClustererType());

		return VAId;
	}
}
