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
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;

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

	private int numberOfCluster = 5;

	public KMeansClusterer() {
		clusterer = new SimpleKMeans();
	}

	private PerspectiveInitializationData cluster(DataTable table, ClusterConfiguration clusterState) {

		// Arraylist holding clustered indicess
		ArrayList<Integer> indices = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();

		DistanceFunction distanceMeasure;

		// SimpleKMeans only supports Eudlidean and Manhattan at that time
		// if (clusterState.getDistanceMeasure() == EDistanceMeasure.CHEBYSHEV_DISTANCE)
		// distanceMeasure = new ChebyshevDistance();
		if (clusterState.getDistanceMeasure() == EDistanceMeasure.MANHATTAHN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else
			distanceMeasure = new EuclideanDistance();

		try {
			clusterer.setNumClusters(numberOfCluster);
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

		int percentage = 1;

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING) {

			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			int iNrElements = recordVA.size();

			if (numberOfCluster >= iNrElements)
				return null;

			for (int nr = 0; nr < dimensionVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer recordIndex : recordVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt / recordVA.size() * 100);
					if (percentage == tempPercentage) {
						GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(percentage, false));
						percentage++;
					}

					for (Integer iDimensionIndex : dimensionVA) {
						buffer.append(table.getFloat(DataRepresentation.NORMALIZED, recordIndex,
							iDimensionIndex) + ", ");

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

			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			int iNrElements = dimensionVA.size();

			if (numberOfCluster >= iNrElements)
				return null;

			for (int nr = 0; nr < recordVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int isto = 0;
			for (Integer iDimensionIndex : dimensionVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) isto / dimensionVA.size() * 100);
					if (percentage == tempPercentage) {
						GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(percentage, false));
						percentage++;
					}

					for (Integer recordIndex : recordVA) {
						buffer.append(table.getFloat(DataRepresentation.NORMALIZED, recordIndex,
							iDimensionIndex) + ", ");

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
		GeneralManager
			.get()
			.getEventPublisher()
			.triggerEvent(
				new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING)
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("KMeans clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("KMeans clustering of experiments in progress"));

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

		for (int i = 0; i < numberOfCluster; i++) {
			clusterSizes.add(0);
		}

		// System.out.println(eval.getNumClusters());
		// System.out.println(data.numAttributes());
		// System.out.println(data.numInstances());

		for (int cluster = 0; cluster < numberOfCluster; cluster++) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (ClusterAssignments[i] == cluster) {
					sampleElements.add(i);
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

		HashMap<Integer, Integer> hashExamples = new HashMap<Integer, Integer>();

		int cnt = 0;
		for (int example : sampleElements) {
			hashExamples.put(example, cnt);
			cnt++;
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(table, recordVA, dimensionVA, sampleElements,
			clusterState.getClustererType());

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING) {
			for (int cluster : sampleElements) {
				for (int i = 0; i < data.numInstances(); i++) {
					if (ClusterAssignments[i] == hashExamples.get(cluster)) {
						indices.add(recordVA.get(i));
						clusterSizes.set(hashExamples.get(cluster),
							clusterSizes.get(hashExamples.get(cluster)) + 1);
					}
				}
			}
		}
		else {

			for (int cluster : sampleElements) {
				for (int i = 0; i < data.numInstances(); i++) {
					if (ClusterAssignments[i] == hashExamples.get(cluster)) {
						indices.add(dimensionVA.get(i));
						clusterSizes.set(hashExamples.get(cluster),
							clusterSizes.get(hashExamples.get(cluster)) + 1);
					}
				}
			}
		}

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(indices, clusterSizes, sampleElements);

		GeneralManager
			.get()
			.getEventPublisher()
			.triggerEvent(
				new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		return tempResult;
	}

	@Override
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
		ClusterConfiguration clusterState, int iProgressBarOffsetValue, int iProgressBarMultiplier) {

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING)
			numberOfCluster = clusterState.getkMeansNumberOfClustersForRecords();
		else
			numberOfCluster = clusterState.getkMeansNumberOfClustersForDimensions();

		return cluster(dataDomain.getTable(), clusterState);
	}
}
