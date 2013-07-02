/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.algorithm.ALinearClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

/**
 * @author Samuel Gratzl
 *
 */
public class KMeansWEKAClustererer extends ALinearClusterer {

	private final SimpleKMeans clusterer = new SimpleKMeans();
	private final int numberOfCluster;

	public KMeansWEKAClustererer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);
		KMeansClusterConfiguration kMeansClusterConfiguration = (KMeansClusterConfiguration) config
				.getClusterAlgorithmConfiguration();
		this.numberOfCluster = kMeansClusterConfiguration.getNumberOfClusters();
	}

	@Override
	protected PerspectiveInitializationData cluster() {
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
		DistanceFunction distanceMeasure = convert(config.getDistanceMeasure());

		try {
			clusterer.setNumClusters(numberOfCluster);
			clusterer.setMaxIterations(1000);
			if (distanceMeasure != null)
				clusterer.setDistanceFunction(distanceMeasure);
		} catch (Exception e) {
			return error(e);
		}

		StringBuilder buffer = new StringBuilder();

		buffer.append("@relation test\n\n");

		int percentage = 1;

		rename("Determine Similarities for " + getPerspectiveLabel() + " clustering");

		int iNrElements = va.size();

		if (numberOfCluster >= iNrElements)
			return null;

		for (int nr = 0; nr < oppositeVA.size(); nr++) {
			buffer.append("@attribute Attr" + nr + " real\n");
		}

		buffer.append("@data\n");

		int icnt = 0;
		for (Integer vaID : va) {
			if (isClusteringCanceled)
				return canceled();

			int tempPercentage = (int) ((float) icnt / va.size() * 100);
			if (percentage == tempPercentage) {
				progress(percentage, false);
				percentage++;
			}

			for (Integer oppositeID : oppositeVA) {
				buffer.append(table.getDataDomain().getNormalizedValue(va.getIdType(), vaID, oppositeVA.getIdType(),
						oppositeID));
			}
			buffer.append("\n");
			icnt++;

			eventListeners.processEvents();
		}
		progressScaled(25);

		rename("KMeans clustering of " + getPerspectiveLabel() + " in progress");

		Instances data = null;

		// System.out.println(buffer.toString());

		try {
			data = new Instances(new StringReader(buffer.toString()));
		} catch (IOException e) {
			return error(e);
		}

		progress(10, false);

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		} catch (Exception e) {
			return error(e);
		}

		eventListeners.processEvents();
		if (isClusteringCanceled) {
			return canceled();
		}
		progress(45, false);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		} catch (Exception e) {
			return error(e);
		}
		eventListeners.processEvents();
		if (isClusteringCanceled) {
			return canceled();
		}
		progress(60, false);

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
		eventListeners.processEvents();
		if (isClusteringCanceled) {
			return canceled();
		}
		progress(80, false);

		int[] assignments = new int[data.numInstances()];
		for (int i = 0; i < data.numInstances(); i++) {
			assignments[i] = sampleElements.get((int)ClusterAssignments[i]);
		}

		return postProcess(assignments, sampleElements);
	}

	/**
	 * @param distanceMeasure
	 * @return
	 */
	private static DistanceFunction convert(EDistanceMeasure measure) {
		switch (measure) {
		case MANHATTAN_DISTANCE:
			return new ManhattanDistance();
		case EUCLIDEAN_DISTANCE:
			return new EuclideanDistance();
		default:
			throw new IllegalStateException("Unsupported Distance Measure for K-Means: " + measure);
		}
	}
}

