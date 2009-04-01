package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

public class ClusterHelper {
	
	/**
	 * Calculates the mean for a given vector (float array)
	 * 
	 * @param vector
	 * @return mean
	 */
	public static float mean(float[] vector) {
		float mean = 0;

		for (int i = 0; i < vector.length; i++)
			mean += vector[i];

		return mean / vector.length;
	}

	public static void sortClusters(ISet set, int iVAIdContent, int iVAIdStorage, ArrayList<Integer> examples, EClustererType eClustererType) {

		int iNrExamples = examples.size();
		float[] fColorSum = null;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			IVirtualArray storageVA = set.getVA(iVAIdStorage);

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iContentIndex1 : examples) {

				for (Integer iStorageIndex1 : storageVA) {
					fColorSum[icontent] +=
						set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1 + 1);
				}
				icontent++;
			}
		}
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING) {

			IVirtualArray contentVA = set.getVA(iVAIdContent);

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iStorageIndex1 : examples) {

				for (Integer iContentIndex1 : contentVA) {
					fColorSum[icontent] +=
						set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1 + 1);
				}
				icontent++;
			}
		}
		float temp;
		int iTemp;
		int i = 0;

		for (int f = 1; f < iNrExamples; f++) {
			if (fColorSum[f] > fColorSum[f - 1])
				continue;
			temp = fColorSum[f];
			iTemp = examples.get(f);
			i = f - 1;
			while ((i >= 0) && (fColorSum[i] > temp)) {
				fColorSum[i + 1] = fColorSum[i];
				examples.set(i + 1, examples.get(i));
				i--;
			}
			fColorSum[i + 1] = temp;
			examples.set(i + 1, iTemp);
		}
		int o;
		o = 0;
	}
	
	/**
	 * Calculates the median for a given vector (float array)
	 * 
	 * @param vector
	 * @return median
	 */
	public static float median(float[] vector) {
		float median = 0;
		float[] temp = new float[vector.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = vector[i];

		Arrays.sort(temp);

		if ((temp.length % 2) == 0)
			median =
				(temp[(int) Math.floor(temp.length / 2)] + temp[(int) Math.floor((temp.length + 1) / 2)]) / 2;
		else
			median = temp[(int) Math.floor((temp.length + 1) / 2)];

		return median;
	}

	/**
	 * Calculates the minimum for a given vector (float array)
	 * 
	 * @param vector
	 * @return double minimum
	 */
	public static float minimum(float[] dArray) {
		float[] temp = new float[dArray.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = dArray[i];

		Arrays.sort(temp);

		return temp[0];
	}
}
