package org.caleydo.core.util.clusterer.algorithm.affinity;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.distancemeasures.ChebyshevDistance;
import org.caleydo.core.util.clusterer.distancemeasures.EuclideanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.IDistanceMeasure;
import org.caleydo.core.util.clusterer.distancemeasures.ManhattanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.PearsonCorrelation;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

//
/**
 * Affinity propagation clusterer. See <a
 * href="http://www.psi.toronto.edu/affinitypropagation/"> affinity
 * documentation</a>
 *
 * @author Bernhard Schlegl
 */
public class AffinityClusterer extends AClusterer implements IClusterer {

	/**
	 * array of similarities
	 */
	private float[] s = null;
	/**
	 * array with indexes
	 */
	private int[] i = null;
	/**
	 * array with indexes
	 */
	private int[] k = null;

	private float dLambda = 0.5f;

	private int iNrClusters = 0;

	private int maxIterations = 400;

	private int iConvIterations = 30;

	/**
	 * Factor influences cluster result. The higher the factor the less clusters
	 * will be formed.
	 */
	private float fClusterFactor = 1.0f;

	private int iNrSamples = 0;

	private int iNrSimilarities = 0;

	private ATableBasedDataDomain dataDomain;

	public AffinityClusterer() {

	}

	@Override
	public void setClusterState(ClusterConfiguration clusterState) {
		super.setClusterState(clusterState);

		try {
			if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING)
				this.iNrSamples = clusterState.getSourceRecordPerspective()
						.getVirtualArray().size();
			else if (clusterState.getClusterTarget() == EClustererTarget.DIMENSION_CLUSTERING)
				this.iNrSamples = clusterState.getSourceDimensionPerspective()
						.getVirtualArray().size();

			this.iNrSimilarities = iNrSamples * iNrSamples;
			this.s = new float[this.iNrSimilarities];
			this.i = new int[this.iNrSimilarities];
			this.k = new int[this.iNrSimilarities];
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError();
		}
	}

	/**
	 * Calculates the similarity vector for a given set and given VAs
	 *
	 * @param table
	 * @param clusterState
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities(Table table, ClusterConfiguration clusterState) {

		VirtualArray recordVA = clusterState.getSourceRecordPerspective()
				.getVirtualArray();
		VirtualArray dimensionVA = clusterState.getSourceDimensionPerspective()
				.getVirtualArray();

		IDistanceMeasure distanceMeasure;

		if (clusterState.getDistanceMeasure() == EDistanceMeasure.MANHATTAN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else if (clusterState.getDistanceMeasure() == EDistanceMeasure.CHEBYSHEV_DISTANCE)
			distanceMeasure = new ChebyshevDistance();
		else if (clusterState.getDistanceMeasure() == EDistanceMeasure.PEARSON_CORRELATION)
			distanceMeasure = new PearsonCorrelation();
		else
			distanceMeasure = new EuclideanDistance();

		int iPercentage = 1;

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {

			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Determine Similarities for gene clustering"));

			float[] dArInstance1 = new float[dimensionVA.size()];
			float[] dArInstance2 = new float[dimensionVA.size()];

			int icnt1 = 0, icnt2 = 0, isto = 0;
			int count = 0;

			for (Integer recordIndex1 : recordVA) {

				if (isClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / recordVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager
								.get()
								.getEventPublisher()
								.triggerEvent(
										new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer iDimensionIndex1 : dimensionVA) {
						dArInstance1[isto] = table.getNormalizedValue(iDimensionIndex1,
								recordIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer recordIndex2 : recordVA) {

						isto = 0;
						for (Integer iDimensionIndex2 : dimensionVA) {
							dArInstance2[isto] = table.getNormalizedValue(iDimensionIndex2,
									recordIndex2);
							isto++;
						}

						if (icnt1 != icnt2) {
							s[count] = -distanceMeasure.getMeasure(dArInstance1,
									dArInstance2);
							i[count] = recordVA.indexOf(recordIndex1);
							k[count] = recordVA.indexOf(recordIndex2);
							count++;
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				} else {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			int cnt = 0;
			for (Integer recordIndex : recordVA) {
				s[count] = median * fClusterFactor;
				i[count] = recordVA.indexOf(recordIndex);
				k[count] = recordVA.indexOf(recordIndex);
				count++;
				cnt++;
			}
		} else {

			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Determine Similarities for experiment clustering"));

			float[] dArInstance1 = new float[recordVA.size()];
			float[] dArInstance2 = new float[recordVA.size()];

			int isto1 = 0, isto2 = 0, icnt = 0;
			int count = 0;

			for (Integer iDimensionIndex1 : dimensionVA) {

				if (isClusteringCanceled == false) {
					int tempPercentage = (int) ((float) isto1 / dimensionVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager
								.get()
								.getEventPublisher()
								.triggerEvent(
										new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					icnt = 0;
					for (Integer recordIndex1 : recordVA) {
						dArInstance1[icnt] = table.getNormalizedValue(iDimensionIndex1,
								recordIndex1);
						icnt++;
					}

					isto2 = 0;
					for (Integer iDimensionIndex2 : dimensionVA) {

						icnt = 0;
						for (Integer recordIndex2 : recordVA) {
							dArInstance2[icnt] = table.getNormalizedValue(iDimensionIndex2,
									recordIndex2);
							icnt++;
						}

						if (isto1 != isto2) {
							s[count] = -distanceMeasure.getMeasure(dArInstance1,
									dArInstance2);
							i[count] = dimensionVA.indexOf(iDimensionIndex1);
							k[count] = dimensionVA.indexOf(iDimensionIndex2);
							count++;
						}
						isto2++;
					}
					isto1++;
					processEvents();
				} else {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			int sto = 0;
			for (Integer iDimensionIndex : dimensionVA) {
				s[count] = median * fClusterFactor;
				i[count] = dimensionVA.indexOf(iDimensionIndex);
				k[count] = dimensionVA.indexOf(iDimensionIndex);
				count++;
				sto++;
			}
		}
		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(25 * iProgressBarMultiplier
								+ iProgressBarOffsetValue, true));
		return 0;
	}

	private PerspectiveInitializationData affinityPropagation(
			EClustererTarget eClustererType) {
		// Arraylist holding clustered indexes
		ArrayList<Integer> indices = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();
		// Arraylist holding number of elements per cluster
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();

		// long original = System.currentTimeMillis();

		int iNrIterations = 0, decit = iConvIterations;
		boolean bIterate = true;
		boolean bConverged = false;
		float[] mx1 = new float[iNrSamples];
		float[] mx2 = new float[iNrSamples];
		float[] srp = new float[iNrSamples];
		float[] decsum = new float[iNrSamples];
		int[] idx = new int[iNrSamples];
		int[][] dec = new int[iConvIterations][iNrSamples];
		float tmp = 0;
		int j = 0;
		float[] dArResposibilities = new float[iNrSimilarities];
		float[] dArAvailabilities = new float[iNrSimilarities];

		// add noise to similarities
		// for (int m = 0; m < s.length; m++) {
		// s[m] = (float) (s[m] + (1E-16 * s[j] + Float.MIN_VALUE * 100) *
		// ((float) Math.random() / 2f));
		// }

		int iPercentage = 1;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING)
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Affinity propagation of genes in progress"));
		else
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Affinity propagation of experiments in progress"));

		while (bIterate) {
			iNrIterations++;

			int tempPercentage = (int) ((float) iNrIterations / maxIterations * 100);
			if (iPercentage == tempPercentage) {
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(iPercentage, false));
				iPercentage++;
			}

			// Compute responsibilities
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
				mx2[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < iNrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx2[i[j]] = mx1[i[j]];
					mx1[i[j]] = tmp;
				} else if (tmp > mx2[i[j]]) {
					mx2[i[j]] = tmp;
				}
			}
			for (j = 0; j < iNrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp == mx1[i[j]]) {
					dArResposibilities[j] = dLambda * dArResposibilities[j]
							+ (1 - dLambda) * (s[j] - mx2[i[j]]);
				} else {
					dArResposibilities[j] = dLambda * dArResposibilities[j]
							+ (1 - dLambda) * (s[j] - mx1[i[j]]);
				}
			}

			// Compute availabilities
			for (j = 0; j < iNrSimilarities - iNrSamples; j++)
				if (dArResposibilities[j] > 0.0) {
					srp[k[j]] = srp[k[j]] + dArResposibilities[j];
				}
			for (j = iNrSimilarities - iNrSamples; j < iNrSimilarities; j++) {
				srp[k[j]] = srp[k[j]] + dArResposibilities[j];
			}
			for (j = 0; j < iNrSimilarities - iNrSamples; j++) {
				if (dArResposibilities[j] > 0.0) {
					tmp = srp[k[j]] - dArResposibilities[j];
				} else {
					tmp = srp[k[j]];
				}
				if (tmp < 0.0) {
					dArAvailabilities[j] = dLambda * dArAvailabilities[j] + (1 - dLambda)
							* tmp;
				} else {
					dArAvailabilities[j] = dLambda * dArAvailabilities[j];
				}
			}
			for (j = iNrSimilarities - iNrSamples; j < iNrSimilarities; j++) {
				dArAvailabilities[j] = dLambda * dArAvailabilities[j] + (1 - dLambda)
						* (srp[k[j]] - dArResposibilities[j]);
			}

			// Identify exemplars and check to see if finished
			decit++;
			if (decit >= iConvIterations) {
				decit = 0;
			}
			for (j = 0; j < iNrSamples; j++) {
				decsum[j] = decsum[j] - dec[decit][j];
			}
			for (j = 0; j < iNrSamples; j++)
				if (dArAvailabilities[iNrSimilarities - iNrSamples + j]
						+ dArResposibilities[iNrSimilarities - iNrSamples + j] > 0.0) {
					dec[decit][j] = 1;
				} else {
					dec[decit][j] = 0;
				}
			iNrClusters = 0;
			for (j = 0; j < iNrSamples; j++) {
				iNrClusters = iNrClusters + dec[decit][j];
			}
			for (j = 0; j < iNrSamples; j++) {
				decsum[j] = decsum[j] + dec[decit][j];
			}
			if ((iNrIterations >= iConvIterations) || (iNrIterations >= maxIterations)) {
				// Check convergence
				bConverged = true;
				for (j = 0; j < iNrSamples; j++)
					if ((decsum[j] != 0) && (decsum[j] != iConvIterations)) {
						bConverged = false;
					}
				// Check to see if done
				if ((bConverged && (iNrClusters > 0)) || (iNrIterations == maxIterations)) {
					bIterate = false;
				}
			}
			processEvents();
			if (isClusteringCanceled) {
				Logger.log(new Status(IStatus.INFO, toString(),
						"Affinity propagation clustering was canceled!"));
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
				return null;
			}
		}

		// If clusters were identified, find the assignments
		if (iNrClusters > 0) {
			for (j = 0; j < iNrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				} else {
					dArAvailabilities[j] = -Float.MAX_VALUE;
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < iNrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < iNrSamples; j++)
				if (dec[decit][j] == 1) {
					idx[j] = j;
				}
			for (j = 0; j < iNrSamples; j++) {
				srp[j] = 0.0f;
			}
			for (j = 0; j < iNrSimilarities; j++)
				if (idx[i[j]] == idx[k[j]]) {
					srp[k[j]] = srp[k[j]] + s[j];
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < iNrSamples; j++)
				if (srp[j] > mx1[idx[j]]) {
					mx1[idx[j]] = srp[j];
				}
			for (j = 0; j < iNrSamples; j++)
				if (srp[j] == mx1[idx[j]]) {
					dec[decit][j] = 1;
				} else {
					dec[decit][j] = 0;
				}
			for (j = 0; j < iNrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				} else {
					dArAvailabilities[j] = -Float.MAX_VALUE;
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Float.MAX_VALUE;
			}
			for (j = 0; j < iNrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < iNrSamples; j++)
				if (dec[decit][j] == 1) {
					idx[j] = j;
					alExamples.add(j);
				}
			// long end = System.currentTimeMillis();
			// long duration = end-original;
			// System.out.println("runtime: " + duration + "ms");
			// System.out.println("Cluster factor: " + fClusterFactor);
			// System.out.println("Number of identified clusters: " +
			// iNrClusters);
			// System.out.println("Number of iterations: " + iNrIterations);
		} else {
			GeneralManager.get().getEventPublisher()
					.triggerEvent(new ClusterProgressEvent(100, true));
			Logger.log(new Status(IStatus.ERROR, toString(),
					"Affinity clustering could not identify any clusters."));
			return null;

		}
		if (bConverged == false) {
			GeneralManager.get().getEventPublisher()
					.triggerEvent(new ClusterProgressEvent(100, true));
			Logger.log(new Status(IStatus.ERROR, toString(),
					"Affinity propagation did not converge!"));
			return null;
		}

		ArrayList<Integer> sampleElements = new ArrayList<Integer>();

		for (int i = 0; i < alExamples.size(); i++) {
			clusterSizes.add(0);
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(dataDomain.getTable(), recordVA, dimensionVA,
				alExamples, eClustererType);

		indices = getAl(alExamples, clusterSizes, sampleElements, idx, eClustererType);

		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(50 * iProgressBarMultiplier
								+ iProgressBarOffsetValue, true));

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();

		tempResult.setData(indices, clusterSizes, sampleElements);
		return tempResult;

		// IVirtualArray virtualArray = null;
		// if (eClustererType == EClustererType.GENE_CLUSTERING)
		// virtualArray = new
		// VirtualArray(table.getVA(iVAIdContent).getVAType(), table.depth(),
		// indexes);
		// else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
		// virtualArray = new
		// VirtualArray(table.getVA(iVAIdDimension).getVAType(), table.size(),
		// indexes);

	}

	/**
	 * Function returns an array list with ordered indexes out of the VA after
	 * clustering. Additionally the indexes of the examples (cluster
	 * representatives) will be determined.
	 *
	 * @param alExamples
	 *            array list containing the indexes of the examples (cluster
	 *            representatives) determined by the cluster algorithm
	 * @param alClusterSizes
	 *            array list which will be filled with the sizes of each cluster
	 * @param idxExamples
	 *            array list containing indexes of examples in the VA
	 * @param idx
	 *            cluster result determined by affinity propagation
	 * @param eClustererType
	 *            the cluster type
	 * @return An array list with indexes in the VA
	 */
	private ArrayList<Integer> getAl(ArrayList<Integer> alExamples,
			ArrayList<Integer> alClusterSizes, ArrayList<Integer> idxExamples, int[] idx,
			EClustererTarget eClustererType) {

		ArrayList<Integer> indices = new ArrayList<Integer>();

		int counter = 0;
		int idxCnt = 0;

		ArrayList<Integer> alIndexListContent = recordVA.getIDs();
		ArrayList<Integer> alIndexListDimension = dimensionVA.getIDs();

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING) {
			for (Integer example : alExamples) {
				for (int index = 0; index < alIndexListContent.size(); index++) {
					if (idx[index] == example) {
						indices.add(alIndexListContent.get(index));
						alClusterSizes.set(counter, alClusterSizes.get(counter) + 1);
					}
					if (example == index) {
						idxExamples.add(recordVA.get(example));
						idxCnt = 0;
					}
					idxCnt++;
				}
				counter++;
			}
		} else {
			for (Integer example : alExamples) {
				for (int index = 0; index < alIndexListDimension.size(); index++) {
					if (idx[index] == example) {
						indices.add(alIndexListDimension.get(index));
						alClusterSizes.set(counter, alClusterSizes.get(counter) + 1);
					}
					if (example == index) {
						idxExamples.add(dimensionVA.get(example));
						idxCnt = 0;
					}
					idxCnt++;
				}
				counter++;
			}
		}

		return indices;
	}

	@Override
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
			ClusterConfiguration clusterConfiguration, int iProgressBarOffsetValue,
			int iProgressBarMultiplier) {

		AffinityClusterConfiguration affinityClusterConfiguration = (AffinityClusterConfiguration) clusterConfiguration
				.getClusterAlgorithmConfiguration();

		this.dataDomain = dataDomain;

		fClusterFactor = affinityClusterConfiguration.getClusterFactor();

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		int iReturnValue = 0;

		iReturnValue = determineSimilarities(dataDomain.getTable(), clusterConfiguration);

		if (iReturnValue < 0) {
			GeneralManager.get().getEventPublisher()
					.triggerEvent(new ClusterProgressEvent(100, true));
			Logger.log(new Status(IStatus.ERROR, toString(),
					"Could not determine similarities."));
			return null;
		}

		return affinityPropagation(clusterConfiguration.getClusterTarget());

	}

}
