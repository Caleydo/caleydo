package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

// http://www.psi.toronto.edu/affinitypropagation/

public class AffinityClusterer
	implements IClusterer {
	private float[] s = null;
	private int[] i = null;
	private int[] k = null;

	private float dLambda = 0.5f;

	private int iNrClusters = 0;

	private int iMaxIterations = 400;

	private int iConvIterations = 30;

	// cluster genes
	private float fClusterFactor = 1.0f;

	private int iNrSamples = 0;

	private int iNrSimilarities = 0;

	private int iVAIdContent = 0;
	private int iVAIdStorage = 0;

	private boolean bStart0 = true;

	public AffinityClusterer(int iNrSamples) {
		this.iNrSamples = iNrSamples;
		this.iNrSimilarities = iNrSamples * iNrSamples;
		this.s = new float[this.iNrSimilarities];
		this.i = new int[this.iNrSimilarities];
		this.k = new int[this.iNrSimilarities];
		this.iVAIdContent = 0;
		this.iVAIdStorage = 0;
	}

	/**
	 * Calculates the similarity matrix for a given set and VA´s
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @return
	 */
	public void determineSimilarities(ISet set, Integer iVAIdContent, Integer iVAIdStorage,
		EClustererType eClustererType) {

		this.iVAIdContent = iVAIdContent;
		this.iVAIdStorage = iVAIdStorage;

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		IDistanceMeasure distanceMeasure = new EuclideanDistance();

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			if (contentVA.get(0) == 0)
				bStart0 = true;
			else
				bStart0 = false;

			float[] dArInstance1 = new float[storageVA.size()];
			float[] dArInstance2 = new float[storageVA.size()];

			int icnt1 = 0, icnt2 = 0, isto = 0;
			int count = 0;

			for (Integer iContentIndex1 : contentVA) {

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
							set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED, iContentIndex2);
						isto++;
					}

					if (icnt1 != icnt2) {
						s[count] = -distanceMeasure.getMeasure(dArInstance1, dArInstance2);
						if (bStart0 == true) {
							i[count] = iContentIndex1;
							k[count] = iContentIndex2;
						}
						else {
							i[count] = iContentIndex1 - 1;
							k[count] = iContentIndex2 - 1;
						}
						count++;
					}
					icnt2++;
				}
				icnt1++;
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			for (Integer iContentIndex1 : contentVA) {
				s[count] = median * fClusterFactor;
				if (bStart0 == true) {
					i[count] = iContentIndex1;
					k[count] = iContentIndex1;
				}
				else {
					i[count] = iContentIndex1 - 1;
					k[count] = iContentIndex1 - 1;
				}
				count++;
			}
		}
		else {
			if (storageVA.get(0) == 0)
				bStart0 = true;
			else
				bStart0 = false;

			float[] dArInstance1 = new float[contentVA.size()];
			float[] dArInstance2 = new float[contentVA.size()];

			int icnt1 = 0, icnt2 = 0, isto = 0;
			int count = 0;

			for (Integer iStorageIndex1 : storageVA) {

				isto = 0;
				for (Integer iContentIndex1 : contentVA) {
					dArInstance1[isto] =
						set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
					isto++;
				}

				icnt2 = 0;
				for (Integer iStorageIndex2 : storageVA) {

					isto = 0;
					for (Integer iContentIndex2 : contentVA) {
						dArInstance2[isto] =
							set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED, iContentIndex2);
						isto++;
					}

					if (icnt1 != icnt2) {
						s[count] = -distanceMeasure.getMeasure(dArInstance1, dArInstance2);
						if (bStart0 == true) {
							i[count] = iStorageIndex1;
							k[count] = iStorageIndex2;
						}
						else {
							i[count] = iStorageIndex1 - 1;
							k[count] = iStorageIndex2 - 1;
						}
						count++;
					}
					icnt2++;
				}
				icnt1++;
			}

			// determine median of the similarity values
			float median = ClusterHelper.median(s);

			for (Integer iStorageIndex1 : storageVA) {
				s[count] = median * fClusterFactor;
				if (bStart0 == true) {
					i[count] = iStorageIndex1;
					k[count] = iStorageIndex1;
				}
				else {
					i[count] = iStorageIndex1 - 1;
					k[count] = iStorageIndex1 - 1;
				}
				count++;
			}
		}
	}

	/**
	 * Java-implementation of the affinity propagation clustering algorithm. See BJ Frey and D Dueck, Science
	 * 315, 972-976, Feb 16, 2007, for a description of the algorithm. Copyright 2007, BJ Frey and Delbert
	 * Dueck. This software may be freely used and distributed for non-commercial purposes.
	 * 
	 * @param set
	 * @return Integer
	 */
	public Integer affinityPropagation(ISet set, EClustererType eClustererType) {
		// Arraylist holding clustered indexes
		ArrayList<Integer> AlIndexes = new ArrayList<Integer>();
		// Arraylist holding indices of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();
		// Arraylist holding number of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();

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
		// for (int m = 0; m < s.length; m++)
		// {
		// s[m] = s[m] + (1E-16 * s[j] + Double.MIN_VALUE * 100) *
		// (Math.random() / 2f);
		// }

		while (bIterate) {
			iNrIterations++;

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
		else
			throw new IllegalStateException("Did not identify any clusters!!");
		if (bConverged == false)
			throw new IllegalStateException("Algorithm did not converge!!");

		ArrayList<Integer> idxExamples = new ArrayList<Integer>();

		for (int i = 0; i < alExamples.size(); i++) {
			count.add(0);
		}

		// Sort cluster depending on their color values
		// TODO find a better solution for sorting
		ClusterHelper.sortClusters(set, iVAIdContent, iVAIdStorage, alExamples, eClustererType);

		int counter = 0;
		int idxCnt = 0;
		for (Integer example : alExamples) {
			for (int sampleNr = 0; sampleNr < iNrSamples; sampleNr++) {
				if (idx[sampleNr] == example) {
					if (bStart0 == true)
						AlIndexes.add(sampleNr);
					else
						AlIndexes.add(sampleNr + 1);
					count.set(counter, count.get(counter) + 1);
				}
				if (example == sampleNr) {
					idxExamples.add(idxCnt);
					idxCnt = 0;
				}
				idxCnt++;
			}
			counter++;
		}

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		set.setAlClusterSizes(count);
		set.setAlExamples(idxExamples);

		return clusteredVAId;
	}

	public int getNrClusters() {
		return iNrClusters;
	}

	public void setClusterFactor(float dClusterFactor) {
		this.fClusterFactor = dClusterFactor;
	}

	public float getClusterFactor() {
		return fClusterFactor;
	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, ClusterState clusterState) {

		Integer VAId = 0;

		fClusterFactor = clusterState.getAffinityPropClusterFactor();
		
		determineSimilarities(set, idContent, idStorage, clusterState.getClustererType());

		VAId = affinityPropagation(set, clusterState.getClustererType());

		return VAId;
	}
}
