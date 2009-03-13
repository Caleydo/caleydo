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

	// lambda
	private double lam = 0.7f;

	// indices of the examples for the data points
	private ArrayList<Integer> AlIndices = new ArrayList<Integer>();

	private int iNrClusters = 0;

	private int maxits = 500;

	private int convits = 200;

	// n
	private int n = 0;

	// m
	private int m = 0;

	public AffinityClusterer(int iNrSamples) {
		this.n = iNrSamples;
		this.m = iNrSamples * (iNrSamples - 1);
		this.s = new double[iNrSamples * iNrSamples];
		this.i = new int[iNrSamples * iNrSamples];
		this.k = new int[iNrSamples * iNrSamples];
	}

	/**
	 * Calculates the euclidean distance for given vectors (double arrays)
	 * 
	 * @param dAr1
	 * @param dAr2
	 * @return double
	 */
	private double euclideanDistance(double[] dAr1, double[] dAr2) {
		double distance = 0;

		if (dAr1.length != dAr2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < dAr1.length; i++) {
			distance = Math.sqrt(Math.pow((dAr1[i] - dAr2[i]), 2));
		}

		// return negative euclidean distance
		return - distance;
	}

	/**
	 * Calculates the median for a given vector (double array)
	 * 
	 * @param dArray
	 * @return double
	 */
	private double median(double[] dArray) {

		double median = 0;

		Arrays.sort(dArray);

		if ((dArray.length % 2) == 0)
			median =
				(dArray[(int) Math.floor(dArray.length / 2)] + dArray[(int) Math.floor((dArray.length + 1) / 2)]) / 2;
		else
			median = dArray[(int) Math.floor((dArray.length + 1) / 2)];

		// return negative median
		return - median;
	}

	/**
	 * Calculates the similarity matrix for a given set and VA´s
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @return
	 */
	public void determineSimilaries(ISet set, Integer iVAIdContent, Integer iVAIdStorage) {

//		System.out.println("n: " + n);
//		System.out.println("m: " + n*(n-1));
//		System.out.println("sims: " + n*n);
		
		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		double[] dArInstance1 = new double[storageVA.size()];
		double[] dArInstance2 = new double[storageVA.size()];

		int icnt1 = 0, icnt2 = 0, isto = 0;
		int count = 0;

		for (Integer iContentIndex1 : contentVA) {

			isto = 0;
			for (Integer iStorageIndex1 : storageVA) {
				dArInstance1[isto] = set.get(iStorageIndex1).getFloat(EDataRepresentation.RAW, iContentIndex1);
				isto++;
			}

			icnt2 = 0;
			for (Integer iContentIndex2 : contentVA) {

				isto = 0;
				for (Integer iStorageIndex2 : storageVA) {
					dArInstance2[isto] = set.get(iStorageIndex2).getFloat(EDataRepresentation.RAW, iContentIndex2);
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

		for (Integer iContentIndex1 : contentVA) {
			isto = 0;
			for (Integer iStorageIndex1 : storageVA) {
				dArInstance1[isto] = set.get(iStorageIndex1).getFloat(EDataRepresentation.RAW, iContentIndex1);
				isto++;
			}
			
			s[count] = median(dArInstance1);
			i[count] = iContentIndex1 - 1;
			k[count] = iContentIndex1 - 1;
			count++;
		}

		// System.out.println("icnt: " + icnt1 + " isto: " + isto);
	}

	public Integer affinityPropagation(ISet set) {

		ArrayList<Integer> indexes = new ArrayList<Integer>();

		int it = 0, dn = 0, decit = convits, conv = 1;
		double K = 0, netsim = 0, dpsim = 0, expref = 0;
		double[] mx1 = new double[n];
		double[] mx2 = new double[n];
		double[] srp = new double[n];
		double[] decsum = new double[n];
		int[] idx = new int[n];
		double[][] dec = new double[convits][n];
		double tmp = 0;
		int j = 0;
		double[] r = new double[m + n];
		double[] a = new double[m + n];

		// add noise in similarities to avoid oscillation

		while (dn == 0) {
			it++;

			// Compute responsibilities
			for (j = 0; j < n; j++) {
				mx1[j] = -Double.MAX_VALUE;
				mx2[j] = -Double.MAX_VALUE;
			}
			for (j = 0; j < m; j++) {
				tmp = a[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx2[i[j]] = mx1[i[j]];
					mx1[i[j]] = tmp;
				}
				else if (tmp > mx2[i[j]])
					mx2[i[j]] = tmp;
			}
			for (j = 0; j < m; j++) {
				tmp = a[j] + s[j];
				if (tmp == mx1[i[j]])
					r[j] = lam * r[j] + (1 - lam) * (s[j] - mx2[i[j]]);
				else
					r[j] = lam * r[j] + (1 - lam) * (s[j] - mx1[i[j]]);
			}

			// Compute availabilities
			for (j = 0; j < n; j++)
				srp[j] = 0.0;
			for (j = 0; j < m - n; j++)
				if (r[j] > 0.0)
					srp[k[j]] = srp[k[j]] + r[j];
			for (j = m - n; j < m; j++)
				srp[k[j]] = srp[k[j]] + r[j];
			for (j = 0; j < m - n; j++) {
				if (r[j] > 0.0)
					tmp = srp[k[j]] - r[j];
				else
					tmp = srp[k[j]];
				if (tmp < 0.0)
					a[j] = lam * a[j] + (1 - lam) * tmp;
				else
					a[j] = lam * a[j];
			}
			for (j = m - n; j < m; j++)
				a[j] = lam * a[j] + (1 - lam) * (srp[k[j]] - r[j]);

			/* Identify exemplars and check to see if finished */
			decit++;
			if (decit >= convits)
				decit = 0;
			for (j = 0; j < n; j++)
				decsum[j] = decsum[j] - dec[decit][j];
			for (j = 0; j < n; j++)
				if (a[m - n + j] + r[m - n + j] > 0.0)
					dec[decit][j] = 1;
				else
					dec[decit][j] = 0;
			K = 0;
			for (j = 0; j < n; j++)
				K = K + dec[decit][j];
			for (j = 0; j < n; j++)
				decsum[j] = decsum[j] + dec[decit][j];
			if ((it >= convits) || (it >= maxits)) {
				/* Check convergence */
				conv = 1;
				for (j = 0; j < n; j++)
					if ((decsum[j] != 0) && (decsum[j] != convits))
						conv = 0;
				/* Check to see if done */
				if (((conv == 1) && (K > 0)) || (it == maxits))
					dn = 1;
			}
		}

		// If clusters were identified, find the assignments
		if (K > 0) {
			
			iNrClusters = (int)K;
			
			for (j = 0; j < m; j++)
				if (dec[decit][k[j]] == 1)
					a[j] = 0.0;
				else
					a[j] = -Double.MAX_VALUE;
			for (j = 0; j < n; j++)
				mx1[j] = -Double.MAX_VALUE;
			for (j = 0; j < m; j++) {
				tmp = a[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < n; j++)
				if (dec[decit][j] >= 0)
					idx[j] = j;
			for (j = 0; j < n; j++)
				srp[j] = 0.0;
			for (j = 0; j < m; j++)
				if (idx[i[j]] == idx[k[j]])
					srp[k[j]] = srp[k[j]] + s[j];
			for (j = 0; j < n; j++)
				mx1[j] = -Double.MAX_VALUE;
			for (j = 0; j < n; j++)
				if (srp[j] > mx1[idx[j]])
					mx1[idx[j]] = srp[j];
			for (j = 0; j < n; j++)
				if (srp[j] == mx1[idx[j]])
					dec[decit][j] = 1;
				else
					dec[decit][j] = 0;
			for (j = 0; j < m; j++)
				if (dec[decit][k[j]] == 1)
					a[j] = 0.0;
				else
					a[j] = -Double.MAX_VALUE;
			for (j = 0; j < n; j++)
				mx1[j] = -Double.MAX_VALUE;
			for (j = 0; j < m; j++) {
				tmp = a[j] + s[j];
				if (tmp > mx1[i[j]]) {
					mx1[i[j]] = tmp;
					idx[i[j]] = k[j];
				}
			}
			for (j = 0; j < n; j++)
				if (dec[decit][j] >= 0)
					idx[j] = j;
			dpsim = 0.0;
			expref = 0.0;
			for (j = 0; j < m; j++) {
				if (idx[i[j]] == k[j]) {
					if (i[j] == k[j])
						expref = expref + s[j];
					else
						dpsim = dpsim + s[j];
				}
			}
			netsim = dpsim + expref;
			System.out.println("Number of identified clusters: " + iNrClusters);
			System.out.println("Fitness (net similarity): " + netsim);
			System.out.println("  Similarities of data points to exemplars: " + dpsim);
			System.out.println("  Preferences of selected exemplars: " + expref);
			System.out.println("Number of iterations: " + it);
		}
		else
			System.out.println("Did not identify any clusters");
		if (conv == 0) {
			System.out.println("*** Warning: Algorithm did not converge");
		}

		Integer clusteredVAId = set.createStorageVA(indexes);
		return clusteredVAId;
	}

	// public void setNrCluteres(int iNrClusters) {
	// this.iNrClusters = iNrClusters;
	// }

	public int getNrCluteres() {
		return iNrClusters;
	}

	// public void setIndices(ArrayList<Integer> alIndices) {
	// AlIndices = alIndices;
	// }

	public ArrayList<Integer> getIndices() {
		return AlIndices;
	}

}
