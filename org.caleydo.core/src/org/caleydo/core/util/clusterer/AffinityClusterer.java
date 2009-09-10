package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;

// http://www.psi.toronto.edu/affinitypropagation/

/**
 * Affinity propagation clusterer.
 * 
 * @author Bernhard Schlegl
 */
public class AffinityClusterer
	extends AClusterer
	implements IClusterer {

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

	private int iMaxIterations = 400;

	private int iConvIterations = 30;

	/**
	 * Factor influences cluster result. The higher the factor the less clusters will be formed.
	 */
	private float fClusterFactor = 1.0f;

	private int iNrSamples = 0;

	private int iNrSimilarities = 0;

	private int iVAIdContent = 0;
	private int iVAIdStorage = 0;

	private ISet set;

	public AffinityClusterer(int iNrSamples) {
		try {
			this.iNrSamples = iNrSamples;
			this.iNrSimilarities = iNrSamples * iNrSamples;
			this.s = new float[this.iNrSimilarities];
			this.i = new int[this.iNrSimilarities];
			this.k = new int[this.iNrSimilarities];
			this.iVAIdContent = 0;
			this.iVAIdStorage = 0;
		}
		catch (OutOfMemoryError e) {
			throw new OutOfMemoryError();
		}
	}

	/**
	 * Calculates the similarity vector for a given set and given VAs
	 * 
	 * @param set
	 * @param clusterState
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities(ISet set, ClusterState clusterState) {

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		IDistanceMeasure distanceMeasure;

		if (clusterState.getDistanceMeasure() == EDistanceMeasure.MANHATTAHN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else if (clusterState.getDistanceMeasure() == EDistanceMeasure.CHEBYSHEV_DISTANCE)
			distanceMeasure = new ChebyshevDistance();
		else if (clusterState.getDistanceMeasure() == EDistanceMeasure.PEARSON_CORRELATION)
			distanceMeasure = new PearsonCorrelation();
		else
			distanceMeasure = new EuclideanDistance();

		int iPercentage = 1;

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			float[] dArInstance1 = new float[storageVA.size()];
			float[] dArInstance2 = new float[storageVA.size()];

			int icnt1 = 0, icnt2 = 0, isto = 0;
			int count = 0;

			for (Integer iContentIndex1 : contentVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / contentVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer iStorageIndex1 : storageVA) {
						dArInstance1[isto] =
							set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer iContentIndex2 : contentVA) {

						isto = 0;
						for (Integer iStorageIndex2 : storageVA) {
							dArInstance2[isto] =
								set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED,
									iContentIndex2);
							isto++;
						}

						if (icnt1 != icnt2) {
							s[count] = -distanceMeasure.getMeasure(dArInstance1, dArInstance2);
							i[count] = contentVA.indexOf(iContentIndex1);
							k[count] = contentVA.indexOf(iContentIndex2);
							count++;
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			int cnt = 0;
			for (Integer iContentIndex : contentVA) {
				s[count] = median * fClusterFactor;
				i[count] = contentVA.indexOf(iContentIndex);
				k[count] = contentVA.indexOf(iContentIndex);
				count++;
				cnt++;
			}
		}
		else {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			float[] dArInstance1 = new float[contentVA.size()];
			float[] dArInstance2 = new float[contentVA.size()];

			int isto1 = 0, isto2 = 0, icnt = 0;
			int count = 0;

			for (Integer iStorageIndex1 : storageVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) isto1 / storageVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					icnt = 0;
					for (Integer iContentIndex1 : contentVA) {
						dArInstance1[icnt] =
							set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
						icnt++;
					}

					isto2 = 0;
					for (Integer iStorageIndex2 : storageVA) {

						icnt = 0;
						for (Integer iContentIndex2 : contentVA) {
							dArInstance2[icnt] =
								set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED,
									iContentIndex2);
							icnt++;
						}

						if (isto1 != isto2) {
							s[count] = -distanceMeasure.getMeasure(dArInstance1, dArInstance2);
							i[count] = storageVA.indexOf(iStorageIndex1);
							k[count] = storageVA.indexOf(iStorageIndex2);
							count++;
						}
						isto2++;
					}
					isto1++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			int sto = 0;
			for (Integer iStorageIndex : storageVA) {
				s[count] = median * fClusterFactor;
				i[count] = storageVA.indexOf(iStorageIndex);
				k[count] = storageVA.indexOf(iStorageIndex);
				count++;
				sto++;
			}
		}
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));
		return 0;
	}

	/**
	 * Java-implementation of the affinity propagation clustering algorithm. See BJ Frey and D Dueck, Science
	 * 315, 972-976, Feb 16, 2007, for a description of the algorithm. Copyright 2007, BJ Frey and Delbert
	 * Dueck. This software may be freely used and distributed for non-commercial purposes.
	 * 
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private IVirtualArray affinityPropagation(EClustererType eClustererType) {
		// Arraylist holding clustered indexes
		ArrayList<Integer> alIndexes = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();
		// Arraylist holding number of elements per cluster
		ArrayList<Integer> alClusterSizes = new ArrayList<Integer>();

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
		// s[m] = (float) (s[m] + (1E-16 * s[j] + Float.MIN_VALUE * 100) * ((float) Math.random() / 2f));
		// }

		int iPercentage = 1;

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Affinity propagation of genes in progress"));
		else
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Affinity propagation of experiments in progress"));

		while (bIterate) {
			iNrIterations++;

			int tempPercentage = (int) ((float) iNrIterations / iMaxIterations * 100);
			if (iPercentage == tempPercentage) {
				GeneralManager.get().getEventPublisher().triggerEvent(
					new ClusterProgressEvent(iPercentage, false));
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
				}
				else if (tmp > mx2[i[j]]) {
					mx2[i[j]] = tmp;
				}
			}
			for (j = 0; j < iNrSimilarities; j++) {
				tmp = dArAvailabilities[j] + s[j];
				if (tmp == mx1[i[j]]) {
					dArResposibilities[j] =
						dLambda * dArResposibilities[j] + (1 - dLambda) * (s[j] - mx2[i[j]]);
				}
				else {
					dArResposibilities[j] =
						dLambda * dArResposibilities[j] + (1 - dLambda) * (s[j] - mx1[i[j]]);
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
				}
				else {
					tmp = srp[k[j]];
				}
				if (tmp < 0.0) {
					dArAvailabilities[j] = dLambda * dArAvailabilities[j] + (1 - dLambda) * tmp;
				}
				else {
					dArAvailabilities[j] = dLambda * dArAvailabilities[j];
				}
			}
			for (j = iNrSimilarities - iNrSamples; j < iNrSimilarities; j++) {
				dArAvailabilities[j] =
					dLambda * dArAvailabilities[j] + (1 - dLambda) * (srp[k[j]] - dArResposibilities[j]);
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
				}
				else {
					dec[decit][j] = 0;
				}
			iNrClusters = 0;
			for (j = 0; j < iNrSamples; j++) {
				iNrClusters = iNrClusters + dec[decit][j];
			}
			for (j = 0; j < iNrSamples; j++) {
				decsum[j] = decsum[j] + dec[decit][j];
			}
			if ((iNrIterations >= iConvIterations) || (iNrIterations >= iMaxIterations)) {
				// Check convergence
				bConverged = true;
				for (j = 0; j < iNrSamples; j++)
					if ((decsum[j] != 0) && (decsum[j] != iConvIterations)) {
						bConverged = false;
					}
				// Check to see if done
				if ((bConverged && (iNrClusters > 0)) || (iNrIterations == iMaxIterations)) {
					bIterate = false;
				}
			}
			processEvents();
			if (bClusteringCanceled) {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
				return null;
			}
		}

		// If clusters were identified, find the assignments
		if (iNrClusters > 0) {
			for (j = 0; j < iNrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				}
				else {
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
				}
				else {
					dec[decit][j] = 0;
				}
			for (j = 0; j < iNrSimilarities; j++)
				if (dec[decit][k[j]] == 1) {
					dArAvailabilities[j] = 0.0f;
				}
				else {
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
			// System.out.println("Number of identified clusters: " + iNrClusters);
			// System.out.println("Number of iterations: " + iNrIterations);
		}
		else {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
			// throw new IllegalStateException("Did not identify any clusters!!");
		}
		if (bConverged == false) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
			// throw new IllegalStateException("Algorithm did not converge!!");
		}

		ArrayList<Integer> idxExamples = new ArrayList<Integer>();

		for (int i = 0; i < alExamples.size(); i++) {
			alClusterSizes.add(0);
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(set, iVAIdContent, iVAIdStorage, alExamples, eClustererType);

		alIndexes = getAl(alExamples, alClusterSizes, idxExamples, idx, eClustererType);

		IVirtualArray virtualArray = null;
		if (eClustererType == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(set.getVA(iVAIdContent).getVAType(), set.depth(), alIndexes);
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(set.getVA(iVAIdStorage).getVAType(), set.size(), alIndexes);

		set.setAlClusterSizes(alClusterSizes);
		set.setAlExamples(idxExamples);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	/**
	 * Function returns an array list with ordered indexes out of the VA after clustering. Additionally the
	 * indexes of the examples (cluster representatives) will be determined.
	 * 
	 * @param alExamples
	 *            array list containing the indexes of the examples (cluster representatives) determined by
	 *            the cluster algorithm
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
	private ArrayList<Integer> getAl(ArrayList<Integer> alExamples, ArrayList<Integer> alClusterSizes,
		ArrayList<Integer> idxExamples, int[] idx, EClustererType eClustererType) {

		ArrayList<Integer> indexes = new ArrayList<Integer>();

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		int counter = 0;
		int idxCnt = 0;

		ArrayList<Integer> alIndexListContent = contentVA.getIndexList();
		ArrayList<Integer> alIndexListStorage = storageVA.getIndexList();

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			for (Integer example : alExamples) {
				for (int index = 0; index < alIndexListContent.size(); index++) {
					if (idx[index] == example) {
						indexes.add(alIndexListContent.get(index));
						alClusterSizes.set(counter, alClusterSizes.get(counter) + 1);
					}
					if (example == index) {
						idxExamples.add(contentVA.get(example));
						idxCnt = 0;
					}
					idxCnt++;
				}
				counter++;
			}
		}
		else {
			for (Integer example : alExamples) {
				for (int index = 0; index < alIndexListStorage.size(); index++) {
					if (idx[index] == example) {
						indexes.add(alIndexListStorage.get(index));
						alClusterSizes.set(counter, alClusterSizes.get(counter) + 1);
					}
					if (example == index) {
						idxExamples.add(storageVA.get(example));
						idxCnt = 0;
					}
					idxCnt++;
				}
				counter++;
			}
		}

		return indexes;
	}

	@Override
	public IVirtualArray getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		IVirtualArray virtualArray = null;

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			fClusterFactor = clusterState.getAffinityPropClusterFactorGenes();
		else
			fClusterFactor = clusterState.getAffinityPropClusterFactorExperiments();

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;
		this.iVAIdContent = clusterState.getContentVaId();
		this.iVAIdStorage = clusterState.getStorageVaId();

		int iReturnValue = 0;

		iReturnValue = determineSimilarities(set, clusterState);

		if (iReturnValue < 0) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}

		this.set = set;

		virtualArray = affinityPropagation(clusterState.getClustererType());

		return virtualArray;
	}
}
