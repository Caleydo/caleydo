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
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
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
public class KMeansClusterer extends AClusterer implements IClusterer {

	private final SimpleKMeans clusterer;

	public KMeansClusterer() {
		clusterer = new SimpleKMeans();
	}

	private PerspectiveInitializationData cluster(DataTable table, ClusterConfiguration clusterState,
			int numberOfCluster) {

		// Arraylist holding clustered indicess
		List<Integer> indices = new ArrayList<>();
		// Arraylist holding # of elements per cluster
		List<Integer> clusterSizes = new ArrayList<>();
		// Arraylist holding indices of examples (cluster centers)
		List<Integer> sampleElements = new ArrayList<>();


		// SimpleKMeans only supports Euclidean and Manhattan at that time
		// if (clusterState.getDistanceMeasure() ==
		// EDistanceMeasure.CHEBYSHEV_DISTANCE)
		// distanceMeasure = new ChebyshevDistance();
		DistanceFunction distanceMeasure = convert(clusterState.getDistanceMeasure());

		try {
			clusterer.setNumClusters(numberOfCluster);
			clusterer.setMaxIterations(1000);
			if (distanceMeasure != null)
				clusterer.setDistanceFunction(distanceMeasure);
		} catch (Exception e) {
			return error(e);
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		int percentage = 1;

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {

			triggerRename("Determine Similarities for gene clustering");

			int iNrElements = recordVA.size();

			if (numberOfCluster >= iNrElements)
				return null;

			for (int nr = 0; nr < dimensionVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer recordIndex : recordVA) {
				if (bClusteringCanceled)
					return canceled();

				int tempPercentage = (int) ((float) icnt / recordVA.size() * 100);
				if (percentage == tempPercentage) {
					triggerProgress(percentage, false);
					percentage++;
				}

				for (Integer iDimensionIndex : dimensionVA) {
					buffer.append(table.getNormalizedValue(recordIndex, iDimensionIndex) + ", ");

				}
				buffer.append("\n");
				icnt++;

				processEvents();
			}
		} else {

			triggerRename("Determine Similarities for experiment clustering");

			int iNrElements = dimensionVA.size();

			if (numberOfCluster >= iNrElements)
				return null;

			for (int nr = 0; nr < recordVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int isto = 0;
			for (Integer iDimensionIndex : dimensionVA) {
				if (bClusteringCanceled)
					return canceled();

				int tempPercentage = (int) ((float) isto / dimensionVA.size() * 100);
				if (percentage == tempPercentage) {
					triggerProgress(percentage, false);
					percentage++;
				}

				for (Integer recordIndex : recordVA) {
					buffer.append(table.getNormalizedValue(recordIndex, iDimensionIndex) + ", ");
				}
				buffer.append("\n");
				isto++;
				processEvents();
			}
		}
		triggerProgress(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true);

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING)
			triggerRename("KMeans clustering of genes in progress");
		else
			triggerRename("KMeans clustering of experiments in progress");

		Instances data = null;

		// System.out.println(buffer.toString());

		try {
			data = new Instances(new StringReader(buffer.toString()));
		} catch (IOException e) {
			return error(e);
		}

		triggerProgress(10, false);

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		} catch (Exception e) {
			return error(e);
		}

		processEvents();
		if (bClusteringCanceled) {
			return canceled();
		}
		triggerProgress(45, false);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		} catch (Exception e) {
			return error(e);
		}
		processEvents();
		if (bClusteringCanceled) {
			return canceled();
		}
		triggerProgress(60, false);

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
			return canceled();
		}
		triggerProgress(80, false);

		HashMap<Integer, Integer> hashExamples = new HashMap<Integer, Integer>();

		int cnt = 0;
		for (int example : sampleElements) {
			hashExamples.put(example, cnt);
			cnt++;
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(table, recordVA, dimensionVA, sampleElements,
				clusterState.getClusterTarget());

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
			for (int cluster : sampleElements) {
				for (int i = 0; i < data.numInstances(); i++) {
					if (ClusterAssignments[i] == hashExamples.get(cluster)) {
						indices.add(recordVA.get(i));
						clusterSizes.set(hashExamples.get(cluster),
								clusterSizes.get(hashExamples.get(cluster)) + 1);
					}
				}
			}
		} else {

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

		triggerProgress(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true);

		return tempResult;
	}

	/**
	 * @param distanceMeasure
	 * @return
	 */
	private DistanceFunction convert(EDistanceMeasure distanceMeasure) {
		switch (clusterState.getDistanceMeasure()) {
		case MANHATTAN_DISTANCE:
			return new ManhattanDistance();
		case EUCLIDEAN_DISTANCE:
			return new EuclideanDistance();
		default:
			throw new IllegalStateException("Unsupported Distance Measure for K-Means: "
					+ clusterState.getDistanceMeasure());
		}
	}

	protected PerspectiveInitializationData error(Exception e1) {
		triggerProgress(100, true);
		return null;
	}

	protected PerspectiveInitializationData canceled() {
		triggerProgress(100, true);
		return null;
	}

	private static void triggerProgress(int percentCompleted, boolean forSimilaritiesBar) {
		GeneralManager.get().getEventPublisher()
				.triggerEvent(new ClusterProgressEvent(percentCompleted, forSimilaritiesBar));
	}

	private static void triggerRename(String text) {
		GeneralManager.get().getEventPublisher().triggerEvent(new RenameProgressBarEvent(text));
	}

	@Override
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
			ClusterConfiguration clusterConfiguration, int iProgressBarOffsetValue,
			int iProgressBarMultiplier) {

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		KMeansClusterConfiguration kMeansClusterConfiguration = (KMeansClusterConfiguration) clusterConfiguration
				.getClusterAlgorithmConfiguration();

		int numberOfCluster = kMeansClusterConfiguration.getNumberOfClusters();
		if (numberOfCluster < 1) {
			throw new IllegalStateException("Illegal Number of clusters: " + numberOfCluster);
		}

		return cluster(dataDomain.getTable(), clusterConfiguration, numberOfCluster);
	}

}
