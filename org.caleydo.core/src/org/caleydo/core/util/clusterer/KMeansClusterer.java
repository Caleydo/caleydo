package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClusterer
	extends AClusterer
	implements IClusterer {

	private SimpleKMeans clusterer = null;

	private int iNrCluster = 5;

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
			return -1;
			// e2.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		int iPercentage = 1;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			int iNrElements = contentVA.size();

			if (iNrCluster >= iNrElements)
				return -1;

			for (int nr = 0; nr < storageVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer iContentIndex : contentVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt / contentVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iStorageIndex : storageVA) {
						buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
							iContentIndex)
							+ ", ");

					}
					buffer.append("\n");
					icnt++;

					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		}
		else {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			int iNrElements = storageVA.size();

			if (iNrCluster >= iNrElements)
				return -1;

			for (int nr = 0; nr < contentVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int isto = 0;
			for (Integer iStorageIndex : storageVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) isto / contentVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iContentIndex : contentVA) {
						buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
							iContentIndex)
							+ ", ");

					}
					buffer.append("\n");
					isto++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		}
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("KMeans clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("KMeans clustering of experiments in progress"));

		Instances data = null;

		// System.out.println(buffer.toString());

		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -1;
			// e1.printStackTrace();
		}

		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(10, false));

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -1;
			// e.printStackTrace();
		}

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -2;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(45, false));

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -1;
			// e.printStackTrace();
		}
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -2;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(60, false));

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
					alExamples.add(i);
					break;
				}
			}
		}
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -2;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(80, false));

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(set, iVAIdContent, iVAIdStorage, alExamples, eClustererType);

		IVirtualArray virualArray;
		if (eClustererType == EClustererType.GENE_CLUSTERING)
			virualArray = set.getVA(iVAIdContent);
		else
			virualArray = set.getVA(iVAIdStorage);

		for (int cluster = 0; cluster < iNrCluster; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == cluster) {
					indexes.add(virualArray.get(i));
					count.set(cluster, count.get(cluster) + 1);
				}
			}
		}

		Integer clusteredVAId = set.createStorageVA(indexes);

		// set cluster result in Set
		set.setAlClusterSizes(count);
		set.setAlExamples(alExamples);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		return clusteredVAId;
	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, ClusterState clusterState,
		int iProgressBarOffsetValue, int iProgressBarMultiplier) {

		Integer VAId = 0;

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			iNrCluster = clusterState.getKMeansClusterCntGenes();
		else
			iNrCluster = clusterState.getKMeansClusterCntExperiments();

		VAId = cluster(set, idContent, idStorage, clusterState.getClustererType());

		return VAId;
	}
}
