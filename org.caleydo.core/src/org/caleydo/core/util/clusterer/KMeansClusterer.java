package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

/**
 * KMeans clusterer using Weka
 * 
 * @author Bernhard Schlegl
 */
public class KMeansClusterer
	extends AClusterer
	implements IClusterer {

	private SimpleKMeans clusterer = null;

	private int iNrCluster = 5;

	private int iVAIdContent = 0;
	private int iVAIdStorage = 0;

	public KMeansClusterer(int iNrElements) {
		clusterer = new SimpleKMeans();
	}

	private IVirtualArray cluster(ISet set, ClusterState clusterState) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();

		DistanceFunction distanceMeasure;

		// SimpleKMeans only supports Eudlidean and Manhattan at that time
		// if (clusterState.getDistanceMeasure() == EDistanceMeasure.CHEBYSHEV_DISTANCE)
		// distanceMeasure = new ChebyshevDistance();
		if (clusterState.getDistanceMeasure() == EDistanceMeasure.MANHATTAHN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else
			distanceMeasure = new EuclideanDistance();

		try {
			clusterer.setNumClusters(iNrCluster);
			clusterer.setMaxIterations(1000);
			if (distanceMeasure != null)
				clusterer.setDistanceFunction(distanceMeasure);
		}
		catch (Exception e2) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		int iPercentage = 1;

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			int iNrElements = contentVA.size();

			if (iNrCluster >= iNrElements)
				return null;

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
					return null;
				}
			}
		}
		else {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			int iNrElements = storageVA.size();

			if (iNrCluster >= iNrElements)
				return null;

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
					return null;
				}
			}
		}
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
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
			return null;
		}

		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(10, false));

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(45, false));

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(60, false));

		double[] ClusterAssignments = eval.getClusterAssignments();

		for (int i = 0; i < iNrCluster; i++) {
			count.add(0);
		}

		// System.out.println(eval.getNumClusters());
		// System.out.println(data.numAttributes());
		// System.out.println(data.numInstances());

		IVirtualArray currentVA = null;
		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			currentVA = set.getVA(iVAIdContent);
		else
			currentVA = set.getVA(iVAIdStorage);

		for (int cluster = 0; cluster < iNrCluster; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == cluster) {
					alExamples.add(currentVA.get(i));
					break;
				}
			}
		}
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(80, false));

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		// ClusterHelper.sortClusters(set, iVAIdContent, iVAIdStorage, alExamples, clusterState
		// .getClustererType());

		IVirtualArray virualArray;
		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
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

		IVirtualArray virtualArray = null;
		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), indexes);
		else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), indexes);

		// set cluster result in Set
		set.setAlClusterSizes(count);
		set.setAlExamples(alExamples);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	@Override
	public IVirtualArray getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		IVirtualArray virtualArray = null;

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;
		this.iVAIdContent = clusterState.getContentVaId();
		this.iVAIdStorage = clusterState.getStorageVaId();

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			iNrCluster = clusterState.getKMeansClusterCntGenes();
		else
			iNrCluster = clusterState.getKMeansClusterCntExperiments();

		virtualArray = cluster(set, clusterState);

		return virtualArray;
	}
}
