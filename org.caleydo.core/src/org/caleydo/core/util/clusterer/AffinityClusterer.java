package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

// http://www.psi.toronto.edu/affinitypropagation/

public class AffinityClusterer {
	private double[] s = null;
	private int[] i = null;
	private int[] k = null;

	private double dLambda = 0.5;

	private int iNrClusters = 0;

	private int iMaxIterations = 400;

	private int iConvIterations = 20;

	private double dClusterFactor = 5.0;

	private int iNrSamples = 0;

	private int iNrSimilarities = 0;

	public AffinityClusterer(int iNrSamples) {
		this.iNrSamples = iNrSamples;
		this.iNrSimilarities = iNrSamples * iNrSamples;
		this.s = new double[this.iNrSimilarities];
		this.i = new int[this.iNrSimilarities];
		this.k = new int[this.iNrSimilarities];
	}

	/**
	 * Calculates the euclidean distance for given vectors (double arrays)
	 * 
	 * @param dAr1
	 * @param dAr2
	 * @return double euclidean distance
	 */
	private double euclideanDistance(double[] dAr1, double[] dAr2) {
		double distance = 0;
		double sum = 0;

		if (dAr1.length != dAr2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < dAr1.length; i++) {
			sum = sum + Math.pow(dAr1[i] - dAr2[i], 2);
		}

		distance = Math.sqrt(sum);

		// return negative euclidean distance
		return -distance;
	}

	/**
	 * Calculates the median for a given vector (double array)
	 * 
	 * @param dArray
	 * @return double median
	 */
	private double median(double[] dArray) {

		double median = 0;
		double[] temp = new double[dArray.length];

		for (int i = 0; i < temp.length; i++) {
			temp[i] = dArray[i];
		}

		Arrays.sort(temp);

		if ((temp.length % 2) == 0) {
			median =
				(temp[(int) Math.floor(temp.length / 2)] + temp[(int) Math.floor((temp.length + 1) / 2)]) / 2;
		}
		else {
			median = temp[(int) Math.floor((temp.length + 1) / 2)];
		}

		// return median
		return median * dClusterFactor;
	}

	/**
	 * Calculates the minimum for a given vector (double array)
	 * 
	 * @param dArray
	 * @return double minimum
	 */
	private double minimum(double[] dArray) {
		double[] temp = new double[dArray.length];

		for (int i = 0; i < temp.length; i++) {
			temp[i] = dArray[i];
		}

		Arrays.sort(temp);

		// return minimum
		return temp[0];
	}

	/**
	 * Calculates the similarity matrix for a given set and VA´s
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @return
	 */
	public void determineSimilarities(ISet set, Integer iVAIdContent, Integer iVAIdStorage) {
		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		double[] dArInstance1 = new double[storageVA.size()];
		double[] dArInstance2 = new double[storageVA.size()];

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
					s[count] = euclideanDistance(dArInstance1, dArInstance2);
					i[count] = iContentIndex1 - 1;
					k[count] = iContentIndex2 - 1;
					count++;
				}
				icnt2++;
			}
			icnt1++;
		}

		// determine median of the similarity values
		double median = median(s);

		for (Integer iContentIndex1 : contentVA) {
			s[count] = median;
			i[count] = iContentIndex1 - 1;
			k[count] = iContentIndex1 - 1;
			count++;
		}

		System.out.println(" ");
		// System.out.println("icnt: " + icnt1 + " isto: " + isto);
	}

	/**
	 * Java-implementation of the affinity propagation clustering algorithm. See BJ Frey and D Dueck, Science
	 * 315, 972-976, Feb 16, 2007, for a description of the algorithm. Copyright 2007, BJ Frey and Delbert
	 * Dueck. This software may be freely used and distributed for non-commercial purposes.
	 * 
	 * @param set
	 * @return Integer
	 */
	public Integer affinityPropagation(ISet set) {
		// Arraylist holding clustered indexes
		ArrayList<Integer> AlIndexes = new ArrayList<Integer>();
		// Arraylist holding indeces of examples (cluster centers)
		ArrayList<Integer> alExamples = new ArrayList<Integer>();
		// Arraylist holding # of elements per cluster
		ArrayList<Integer> count = new ArrayList<Integer>();

		int iNrIterations = 0, decit = iConvIterations;
		boolean bIterate = true;
		boolean bConverged = false;
		double[] mx1 = new double[iNrSamples];
		double[] mx2 = new double[iNrSamples];
		double[] srp = new double[iNrSamples];
		double[] decsum = new double[iNrSamples];
		int[] idx = new int[iNrSamples];
		int[][] dec = new int[iConvIterations][iNrSamples];
		double tmp = 0;
		int j = 0;
		double[] dArResposibilities = new double[iNrSimilarities];
		double[] dArAvailabilities = new double[iNrSimilarities];

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
				mx1[j] = -Double.MAX_VALUE;
				mx2[j] = -Double.MAX_VALUE;
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
					dArAvailabilities[j] = 0.0;
				}
				else {
					dArAvailabilities[j] = -Double.MAX_VALUE;
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Double.MAX_VALUE;
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
				srp[j] = 0.0;
			}
			for (j = 0; j < iNrSimilarities; j++)
				if (idx[i[j]] == idx[k[j]]) {
					srp[k[j]] = srp[k[j]] + s[j];
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Double.MAX_VALUE;
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
					dArAvailabilities[j] = 0.0;
				}
				else {
					dArAvailabilities[j] = -Double.MAX_VALUE;
				}
			for (j = 0; j < iNrSamples; j++) {
				mx1[j] = -Double.MAX_VALUE;
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
			System.out.println("Number of identified clusters: " + iNrClusters);
			System.out.println("Number of iterations: " + iNrIterations);
		}
		else
			throw new IllegalStateException("Did not identify any clusters!!");
		if (bConverged == false)
			throw new IllegalStateException("Algorithm did not converg!!");

		for (int i = 0; i < alExamples.size(); i++) {
			count.add(0);
		}

		int counter = 0;
		for (Integer index : alExamples) {
			for (int index2 = 0; index2 < iNrSamples; index2++) {
				if (idx[index2] == index) {
					AlIndexes.add(index2 + 1);
					count.set(counter, count.get(counter) + 1);
				}
			}
			counter++;
		}

		Integer clusteredVAId = set.createStorageVA(AlIndexes);
		set.setAlClusterSizes(count);

		return clusteredVAId;
	}

	// public void setNrCluteres(int iNrClusters)
	// {
	// this.iNrClusters = iNrClusters;
	// }

	public int getNrCluteres() {
		return iNrClusters;
	}

	public void setClusterFactor(double dClusterFactor) {
		this.dClusterFactor = dClusterFactor;
	}

	// public double getClusterFactor()
	// {
	// return dClusterFactor;
	// }
}
