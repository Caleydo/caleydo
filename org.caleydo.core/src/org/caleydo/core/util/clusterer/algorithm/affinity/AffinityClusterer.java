package org.caleydo.core.util.clusterer.algorithm.affinity;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.distancemeasures.IDistanceMeasure;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.logging.Logger;

import com.google.common.base.Stopwatch;

/**
 * Affinity propagation clusterer. See <a
 * href="http://www.psi.toronto.edu/affinitypropagation/"> affinity
 * documentation</a>
 *
 * @author Bernhard Schlegl
 */
public class AffinityClusterer extends AClusterer {
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

	private final int nrSamples;

	private final int nrSimilarities;

	private final float lambda = 0.5f;
	private final int maxIterations = 400;
	private final int convIterations = 30;


	public AffinityClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);

		AffinityClusterConfiguration c = (AffinityClusterConfiguration) config.getClusterAlgorithmConfiguration();
		this.clusterFactor = c.getClusterFactor();

		this.nrSamples = va.size();
		this.nrSimilarities = nrSamples * nrSamples;

		this.s = new float[this.nrSimilarities];
		this.i = new int[this.nrSimilarities];
		this.k = new int[this.nrSimilarities];
	}

	/**
	 * Calculates the similarity vector for a given set and given VAs
	 *
	 * @param table
	 * @param clusterState
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities() {
		final IDistanceMeasure distanceMeasure = config.getDistanceMeasure().create();
		rename("Determine Similarities for " + getPerspectiveLabel() + " clustering");

		int counter = 1;

		float[] dArInstance1 = new float[va.size()];
		float[] dArInstance2 = new float[va.size()];

		int icnt1 = 0, icnt2 = 0, isto = 0;
		int count = 0;

		for (Integer oppositeID : oppositeVA) {
			if (isClusteringCanceled) {
				progress(100, true);
				return -2;
			}

			int tempPercentage = (int) ((float) icnt1 / oppositeVA.size() * 100);

			if (counter == tempPercentage) {
				progress(counter, false);
				counter++;
			}

			isto = 0;
			for (Integer vaID : va) {
				dArInstance1[isto] = getNormalizedValue(vaID, oppositeID);
				isto++;
			}

			icnt2 = 0;
			for (Integer oppositeID2 : oppositeVA) {
				isto = 0;
				for (Integer vaID2 : va) {
					dArInstance2[isto] = getNormalizedValue(vaID2, oppositeID2);
					isto++;
				}

				if (icnt1 != icnt2) {
					s[count] = -distanceMeasure.getMeasure(dArInstance1, dArInstance2);
					i[count] = oppositeVA.indexOf(oppositeID);
					k[count] = oppositeVA.indexOf(oppositeID2);
					count++;
				}
				icnt2++;
			}
			icnt1++;
			eventListeners.processEvents();
		}

		// determine median of the similarity values
		float median = ClusterHelper.median(s);

		for (Integer recordIndex : oppositeVA) {
			s[count] = median * clusterFactor;
			i[count] = oppositeVA.indexOf(recordIndex);
			k[count] = oppositeVA.indexOf(recordIndex);
			count++;
		}

		progressScaled(25);
		return 0;
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
					dArResposibilities[j] = lambda * dArResposibilities[j] + (1 - lambda) * (s[j] - mx2[i[j]]);
				} else {
					dArResposibilities[j] = lambda * dArResposibilities[j] + (1 - lambda) * (s[j] - mx1[i[j]]);
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
					dArAvailabilities[j] = lambda * dArAvailabilities[j] + (1 - lambda)
							* tmp;
				} else {
					dArAvailabilities[j] = lambda * dArAvailabilities[j];
				}
			}
			for (j = nrSimilarities - nrSamples; j < nrSimilarities; j++) {
				dArAvailabilities[j] = lambda * dArAvailabilities[j] + (1 - lambda)
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
			return null;

		}
		if (isConverged == false) {
			progress(100, true);
			log.error("Affinity propagation did not converge!");
			return null;
		}

		List<Integer> sampleElements = new ArrayList<Integer>();

		// Arraylist holding clustered indexes
		List<Integer> indices = new ArrayList<Integer>();
		// Arraylist holding number of elements per cluster
		List<Integer> clusterSizes = new ArrayList<Integer>(alExamples.size());

		for (int i = 0; i < alExamples.size(); i++) {
			clusterSizes.add(0);
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(table, config.getSourceRecordPerspective().getVirtualArray(), config
				.getSourceDimensionPerspective().getVirtualArray(), alExamples, config.getClusterTarget());

		indices = getAl(alExamples, clusterSizes, sampleElements, idx);

		progressScaled(50);

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(indices, clusterSizes, sampleElements);
		return tempResult;
	}

	/**
	 * Function returns an array list with ordered indexes out of the VA after
	 * clustering. Additionally the indexes of the examples (cluster
	 * representatives) will be determined.
	 *
	 * @param examples
	 *            array list containing the indexes of the examples (cluster
	 *            representatives) determined by the cluster algorithm
	 * @param clusterSizes
	 *            array list which will be filled with the sizes of each cluster
	 * @param idxExamples
	 *            array list containing indexes of examples in the VA
	 * @param idx
	 *            cluster result determined by affinity propagation
	 * @param eClustererType
	 *            the cluster type
	 * @return An array list with indexes in the VA
	 */
	private List<Integer> getAl(List<Integer> examples, List<Integer> clusterSizes,
			List<Integer> idxExamples, int[] idx) {
		List<Integer> indices = new ArrayList<Integer>();
		int counter = 0;

		for (Integer example : examples) {
			for (int i = 0; i < va.size(); i++) {
				if (idx[i] == example) {
					indices.add(va.get(i));
					clusterSizes.set(counter, clusterSizes.get(counter) + 1);
				}
				if (example == i) {
					idxExamples.add(va.get(example));
				}
			}
			counter++;
		}
		return indices;
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
