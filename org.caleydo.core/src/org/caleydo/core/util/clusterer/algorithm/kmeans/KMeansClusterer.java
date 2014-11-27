/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.algorithm.ALinearClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.logging.Logger;

/**
 * KMeans clusterer similar to WEKA ones
 *
 * @author Samuel Gratzl
 *
 */
public class KMeansClusterer extends ALinearClusterer {
	private static final int MAX_ITERATIONS = 1000;

	private static final Logger log = Logger.create(KMeansClusterer.class);

	private final int numberOfCluster;

	private final Map<Integer, Object> cache;

	private final float[] missingValues;

	public KMeansClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);
		KMeansClusterConfiguration kMeansClusterConfiguration = (KMeansClusterConfiguration) config
				.getClusterAlgorithmConfiguration();
		this.numberOfCluster = kMeansClusterConfiguration.getNumberOfClusters();

		if (kMeansClusterConfiguration.isCacheVectors())
			cache = new HashMap<Integer, Object>();
		else
			cache = null;

		missingValues = new float[oppositeVA.size()];
		Arrays.fill(missingValues, Float.NaN);
	}

	@Override
	protected PerspectiveInitializationData cluster() {
		if (progressAndCancel(1, false))
			return canceled();

		final int nrDimensions = oppositeVA.size();

		final int[] assignments = new int[va.size()];
		Arrays.fill(assignments, -1);

		float[] vector = new float[nrDimensions];

		final List<Cluster> clusters = createClusters(nrSamples);

		if (cache != null) {
			// build cache
			for (int i = 0; i < assignments.length; ++i) {
				Integer vid = va.get(i);
				cache.put(vid, fillVector(null, vid));
			}
		}

		int iteration = 0;
		boolean converged = false;

		if (progressAndCancel(10, false))
			return canceled();

		// progress scale
		int lastP = 10;
		final float scale = (80.f - 10.f) / MAX_ITERATIONS;

		for (; !converged && iteration < MAX_ITERATIONS; ++iteration) {
			converged = true;

			for (Cluster c : clusters) {
				c.prepareRound();
			}

			for (int i = 0; i < assignments.length; ++i) {
				Integer vid = va.get(i);
				vector = getValue(vector, vid);
				int best = -1;
				// find best matching cluster
				float distance = Float.POSITIVE_INFINITY;
				for (Cluster cluster : clusters) {
					float dc = cluster.distance(vector);
					if (dc < distance) {
						best = cluster.index;
						distance = dc;
					}
				}
				// update cluster assignment
				if (assignments[i] != best) {
					converged = false;
					assignments[i] = best;
				}

			}

			for (Cluster c : clusters)
				c.prepareMoving();

			// update cluster positions
			for (int i = 0; i < assignments.length; ++i) {
				int best = assignments[i];
				Integer vid = va.get(i);
				vector = getValue(vector, vid);
				clusters.get(best).add(vector);
			}

			eventListeners.processEvents();
			if (isClusteringCanceled) {
				return canceled();
			}
			int p = 10 + Math.round(scale * iteration);
			if (p > lastP)
				progress(p, false);
			lastP = p;
		}

		for (Cluster c : clusters)
			c.stopMoving();

		// final computing sample
		for (int i = 0; i < assignments.length; ++i) {
			int best = assignments[i];
			Integer vid = va.get(i);
			vector = getValue(vector, vid);
			clusters.get(best).isSample(vid, vector);
		}

		if (progressAndCancel(80, false))
			return canceled();

		// normalize the cluster indices and assignments and compute the representative indices
		List<Integer> clusterSamples = transformClusters(assignments, clusters);

		return postProcess(assignments, clusterSamples);
	}

	protected float[] getValue(float[] vector, Integer vid) {
		return cache != null ? (float[]) cache.get(vid) : fillVector(vector, vid);
	}



	/**
	 * transforms the clusters and assignments such that they have the format for the {@link #postProcess(int[], List)}
	 *
	 * @param assignments
	 * @param clusters
	 * @return the sample id represents of the clusters
	 */
	private List<Integer> transformClusters(final int[] assignments, List<Cluster> clusters) {

		int[] lookup = new int[numberOfCluster];
		int c = 0; // number of real clusters
		List<Integer> clusterSamples = new ArrayList<>();
		for (Cluster cluster : clusters) {
			if (cluster.isEmpty())
				continue;
			c++;
			lookup[cluster.index] = cluster.sampleId;
			clusterSamples.add(cluster.sampleId);
		}
		// we had empty ones
		if (c < numberOfCluster) {
			log.warn((numberOfCluster - c) + " empty clusters");
		}
		// map assignment data
		for (int i = 0; i < assignments.length; ++i)
			assignments[i] = lookup[assignments[i]];
		return clusterSamples;
	}

	/**
	 * initial random create of clusters and cluster centers
	 *
	 * @param nrSamples
	 * @return
	 */
	private List<Cluster> createClusters(final int nrSamples) {
		List<Cluster> clusters = new ArrayList<>(numberOfCluster);
		// init cluster by random selection
		Random r = new Random();
		BitSet used = new BitSet();
		for (int i = 0; i < numberOfCluster; ++i) {
			int p;
			do {
				p = r.nextInt(nrSamples - 1);
			} while (used.get(p));
			used.set(p);
			clusters.add(new Cluster(i, fillVector(null, va.get(p))));
		}
		return clusters;
	}

	private class Cluster {
		private final int index;
		private final float[] vector; // it's current centroid
		private int numSamples;

		// representative id
		private int sampleId = -1;
		private float sampleDistance = Float.POSITIVE_INFINITY;

		public Cluster(int index, float[] vector) {
			this.index = index;
			this.vector = vector;
			this.numSamples = 1; // the starting point at least
		}

		/**
		 * @param vid
		 * @param vector2
		 */
		public void isSample(Integer id, float[] sample) {
			float dc = KMeansClusterer.this.distance(sample, vector);

			// update representative index
			if (dc < sampleDistance) {
				sampleDistance = dc;
				sampleId = id;
			}
		}

		public void add(float[] sample) {
			numSamples++;
			for (int i = 0; i < vector.length; ++i)
				vector[i] += sample[i];
		}

		public boolean isEmpty() {
			return numSamples == 0;
		}

		public void prepareRound() {
			stopMoving();
			sampleId = -1;
			sampleDistance = Float.POSITIVE_INFINITY;
		}

		public void prepareMoving() {
			Arrays.fill(vector, 0);
			numSamples = 0;
		}

		public void stopMoving() {
			if (isEmpty())
				return;
			float invSamples = 1.f / numSamples;
			for (int i = 0; i < vector.length; ++i)
				vector[i] *= invSamples;
		}

		public float distance(float[] sample) {
			if (isEmpty())
				return Float.POSITIVE_INFINITY;
			float dc = KMeansClusterer.this.distance(sample, vector);
			return dc;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Cluster [index=");
			builder.append(index);
			builder.append(", numSamples=");
			builder.append(numSamples);
			builder.append(", sampleId=");
			builder.append(sampleId);
			builder.append(", sampleDistance=");
			builder.append(sampleDistance);
			builder.append("]");
			return builder.toString();
		}

	}

	/**
	 * fills the given vector with the data for the given id
	 *
	 * @param values
	 * @param vaID
	 * @return
	 */
	protected final float[] fillVector(float[] values, Integer vaID) {
		if (values == null)
			values = new float[oppositeVA.size()];
		int i = 0;
		for (Integer oppositeVaID : oppositeVA) {
			float v = get(vaID, oppositeVaID);
			if (Float.isNaN(v))
				v = getMissingValue(i, oppositeVaID);
			values[i++] = v;
		}
		return values;
	}

	private float getMissingValue(int i, Integer oppositeVaID) {
		float cache = missingValues[i];
		if (Float.isNaN(cache)) { // compute it
			cache = 0;
			int c = 0;
			for (Integer vaID : va) {
				float v = get(vaID, oppositeVaID);
				if (!Float.isNaN(v)) {
					cache += v;
					c++;
				}
			}
			if (c > 0)
				cache /= c;
			missingValues[i] = cache;
		}
		return cache;
	}
}
