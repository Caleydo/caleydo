/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.affinity;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.algorithm.ALinearClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.logging.Logger;

import com.google.common.base.Stopwatch;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * Affinity propagation clusterer. See <a href="http://www.psi.toronto.edu/affinitypropagation/faq.html"> affinity
 * documentation</a>
 *
 * @author Bernhard Schlegl
 */
public class AffinityClusterer extends ALinearClusterer {
	private static final Logger log = Logger.create(AffinityClusterer.class);

	/**
	 * Factor influences cluster result. The higher the factor the less clusters will be formed.
	 */
	private final float clusterFactor;

	/**
	 * array of similarities
	 */
	private final float[] s;
	/**
	 * array with indexes
	 */
	private final int[] i;
	/**
	 * array with indexes
	 */
	private final int[] k;

	private final int nrSimilarities;

	/** damping factor */
	private final float dampingFactor = 0.7f;
	private final int maxIterations = 800;
	private final int convIterations = 100;

	private IntObjectHashMap cache;


	public AffinityClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);

		AffinityClusterConfiguration c = (AffinityClusterConfiguration) config.getClusterAlgorithmConfiguration();
		this.clusterFactor = c.getClusterFactor();

		this.nrSimilarities = nrSamples * nrSamples;

		this.s = new float[this.nrSimilarities];
		this.i = new int[this.nrSimilarities];
		this.k = new int[this.nrSimilarities];

		if (c.isCacheVectors())
			cache = new IntObjectHashMap();
		else
			cache = null;
	}

	/**
	 * Calculates the similarity vector for a given set and given VAs
	 *
	 * @param table
	 * @param clusterState
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities() {
		Stopwatch w = new Stopwatch().start();
		final EDistanceMeasure distanceMeasure = config.getDistanceMeasure();
		rename("Determine Similarities for " + getPerspectiveLabel() + " clustering");

		int counter = 1;

		final int vaSize = va.size();
		final int oppositeSize = oppositeVA.size();
		float[] dArInstance1 = new float[oppositeSize];
		float[] instance2 = new float[oppositeSize];

		int count = 0;

		if (cache != null) {
			// build cache
			for (int i = 0; i < vaSize; ++i) {
				Integer vid = va.get(i);
				cache.put(vid, fillVector(null, vid));
			}
		}

		// TODO: aren't the distances symmetrical so why compute everything
		for (int va_i = 0; va_i < vaSize; ++va_i) {
			Integer vaID = va.get(va_i);

			if (isClusteringCanceled) {
				progress(100, true);
				return -2;
			}

			int tempPercentage = (int) ((float) va_i / vaSize * 100);

			if (counter == tempPercentage) {
				progress(counter, false);
				counter++;
			}

			fillVector(dArInstance1, vaID);

			for (int va_j = 0; va_j < vaSize; ++va_j) {
				if (va_i == va_j)
					continue;
				Integer vaID2 = va.get(va_j);
				fillVector(instance2, vaID2);

				s[count] = -distanceMeasure.apply(dArInstance1, instance2);
				i[count] = va_i; // va.indexOf(vaID);
				k[count] = va_j; // va.indexOf(vaID2);
				count++;
			}
			eventListeners.processEvents();
		}

		// determine median of the similarity values
		float median = ClusterHelper.median(s);

		for (int va_i = 0; va_i < vaSize; ++va_i) {
			s[count] = median * clusterFactor;
			i[count] = va_i; // va.indexOf(recordIndex);
			k[count] = va_i; // va.indexOf(recordIndex);
			count++;
		}

		progressScaled(25);
		log.debug("determined similarities: " + w);

		cache = null;
		return 0;
	}

	protected float[] getValue(float[] vector, Integer vid) {
		return cache != null ? (float[]) cache.get(vid) : fillVector(vector, vid);
	}

	/**
	 * fills the given vector with the data for the given id
	 *
	 * @param values
	 * @param vaID
	 * @return
	 */
	private float[] fillVector(float[] values, Integer vaID) {
		if (values == null)
			values = new float[oppositeVA.size()];
		int i = 0;
		for (Integer oppositeVaID : oppositeVA) {
			float v = table.getDataDomain().getNormalizedValue(va.getIdType(), vaID, oppositeVA.getIdType(),
					oppositeVaID);
			values[i++] = v;
		}
		return values;
	}

	private PerspectiveInitializationData affinityPropagation() {
		Stopwatch w = new Stopwatch().start();

		int nrClusters = 0;

		int iNrIterations = 0, decit = convIterations;
		boolean doIterate = true;
		boolean isConverged = false;
		float[] mx1 = new float[nrSamples];
		float[] mx2 = new float[nrSamples];
		float[] srp = new float[nrSamples];
		float[] decsum = new float[nrSamples];
		int[] idx = new int[nrSamples];
		int[][] dec = new int[convIterations][nrSamples];
		float tmp = 0;
		int j = 0;
		float[] dArResposibilities = new float[nrSimilarities];
		float[] dArAvailabilities = new float[nrSimilarities];

		// add noise to similarities
		// for (int m = 0; m < s.length; m++) {
		// s[m] = (float) (s[m] + (1E-16 * s[j] + Float.MIN_VALUE * 100) *
		// ((float) Math.random() / 2f));
		// }

		int iPercentage = 1;

		rename("Affinitiy propagpation of " + getPerspectiveLabel() + " in progress");

		while (doIterate) {
			iNrIterations++;

			int tempPercentage = (int) ((float) iNrIterations / maxIterations * 100);
			if (iPercentage == tempPercentage) {
				progress(iPercentage, false);
				iPercentage++;
			}

			// Compute responsibilities
			for (j = 0; j < nrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
				mx2[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < nrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx2[i[j]] = mx1[i[j]];
					mx1[i[j]] = tmp;
				} else if (tmp > mx2[i[j]]) {
					mx2[i[j]] = tmp;
				}
			}
			for (j = 0; j < nrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp == mx1[i[j]]) {
					dArResposibilities[j] = dampingFactor * dArResposibilities[j] + (1 - dampingFactor)
							* (s[j] - mx2[i[j]]);
				} else {
					dArResposibilities[j] = dampingFactor * dArResposibilities[j] + (1 - dampingFactor)
							* (s[j] - mx1[i[j]]);
				}
			}

			// Compute availabilities
			for (j = 0; j < nrSimilarities - nrSamples; j++)
				if (dArResposibilities[j] > 0.0) {
					srp[k[j]] = srp[k[j]] + dArResposibilities[j];
				}
			for (j = nrSimilarities - nrSamples; j < nrSimilarities; j++) {
				srp[k[j]] = srp[k[j]] + dArResposibilities[j];
			}
			for (j = 0; j < nrSimilarities - nrSamples; j++) {
				if (dArResposibilities[j] > 0.0) {
					tmp = srp[k[j]] - dArResposibilities[j];
				} else {
					tmp = srp[k[j]];
				}
				if (tmp < 0.0) {
					dArAvailabilities[j] = dampingFactor * dArAvailabilities[j] + (1 - dampingFactor)
							* tmp;
				} else {
					dArAvailabilities[j] = dampingFactor * dArAvailabilities[j];
				}
			}
			for (j = nrSimilarities - nrSamples; j < nrSimilarities; j++) {
				dArAvailabilities[j] = dampingFactor * dArAvailabilities[j] + (1 - dampingFactor)
						* (srp[k[j]] - dArResposibilities[j]);
			}

			// Identify exemplars and check to see if finished
			decit++;
			if (decit >= convIterations) {
				decit = 0;
			}
			for (j = 0; j < nrSamples; j++) {
				decsum[j] = decsum[j] - dec[decit][j];
			}
			for (j = 0; j < nrSamples; j++)
				if (dArAvailabilities[nrSimilarities - nrSamples + j]
						+ dArResposibilities[nrSimilarities - nrSamples + j] > 0.0) {
					dec[decit][j] = 1;
				} else {
					dec[decit][j] = 0;
				}
			nrClusters = 0;
			for (j = 0; j < nrSamples; j++) {
				nrClusters = nrClusters + dec[decit][j];
			}
			for (j = 0; j < nrSamples; j++) {
				decsum[j] = decsum[j] + dec[decit][j];
			}
			if ((iNrIterations >= convIterations) || (iNrIterations >= maxIterations)) {
				// Check convergence
				isConverged = true;
				for (j = 0; j < nrSamples; j++)
					if ((decsum[j] != 0) && (decsum[j] != convIterations)) {
						isConverged = false;
					}
				// Check to see if done
				if ((isConverged && (nrClusters > 0)) || (iNrIterations == maxIterations)) {
					doIterate = false;
				}
			}
			eventListeners.processEvents();
			if (isClusteringCanceled) {
				log.info("Affinity propagation clustering was canceled!");
				progress(100, true);
				return null;
			}
		}

		// Arraylist holding indices of examples (cluster centers)
		List<Integer> alExamples = new ArrayList<Integer>();

		// If clusters were identified, find the assignments
		if (nrClusters > 0) {
			for (j = 0; j < nrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				} else {
					dArAvailabilities[j] = -Float.MAX_VALUE;
				}
			for (j = 0; j < nrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < nrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < nrSamples; j++)
				if (dec[decit][j] == 1) {
					idx[j] = j;
				}
			for (j = 0; j < nrSamples; j++) {
				srp[j] = 0.0f;
			}
			for (j = 0; j < nrSimilarities; j++)
				if (idx[i[j]] == idx[k[j]]) {
					srp[k[j]] = srp[k[j]] + s[j];
				}
			for (j = 0; j < nrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < nrSamples; j++)
				if (srp[j] > mx1[idx[j]]) {
					mx1[idx[j]] = srp[j];
				}
			for (j = 0; j < nrSamples; j++)
				if (srp[j] == mx1[idx[j]]) {
					dec[decit][j] = 1;
				} else {
					dec[decit][j] = 0;
				}
			for (j = 0; j < nrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				} else {
					dArAvailabilities[j] = -Float.MAX_VALUE;
				}
			for (j = 0; j < nrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < nrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < nrSamples; j++)
				if (dec[decit][j] == 1) {
					idx[j] = j;
					alExamples.add(j);
				}

			StringBuilder b = new StringBuilder();
			b.append("runtime: ").append(w).append('\n');
			b.append("Cluster factor: ").append(clusterFactor).append('\n');
			b.append("Number of identified clusters: ").append(nrClusters).append('\n');
			b.append("Number of iterations: ").append(iNrIterations);
			log.debug(b.toString());
		} else {
			progress(100, true);
			log.error("Affinity clustering could not identify any clusters.");
			log.debug("affinity propagation " + w);
			return null;

		}
		if (isConverged == false) {
			progress(100, true);
			log.error("Affinity propagation did not converge!");
			log.debug("affinity propagation " + w);
			return null;
		}

		log.debug("affinity propagation " + w);
		return postProcess(idx, alExamples);
	}


	@Override
	protected PerspectiveInitializationData cluster() {
		int r = determineSimilarities();

		if (r < 0) {
			progress(100, true);
			log.error("Could not determine similarities.");
			return null;
		}
		return affinityPropagation();
	}

}
