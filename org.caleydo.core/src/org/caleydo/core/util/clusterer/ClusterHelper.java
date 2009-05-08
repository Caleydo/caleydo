package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;

public class ClusterHelper {

	/**
	 * Calculates the arithmetic mean for a given vector (float array)
	 * 
	 * @param vector
	 * @return mean
	 */
	public static float arithmeticMean(float[] vector) {
		float mean = 0;

		float temp = 0;

		for (int i = 0; i < vector.length; i++) {

			if (Float.isNaN(vector[i]))
				temp = 0;
			else
				temp = vector[i];

			mean += temp;
		}

		return mean / vector.length;
	}

	/**
	 * Calculates the standard deviation for a given vector (float array)
	 * 
	 * @param vector
	 * @param arithmeticMean
	 * @return
	 */
	public static float standardDeviation(float[] vector, float arithmeticMean) {
		float standardDeviation = 0;

		float temp = 0;

		for (int i = 0; i < vector.length; i++) {

			if (Float.isNaN(vector[i]))
				temp = 0;
			else
				temp = vector[i];

			standardDeviation += Math.pow(temp - arithmeticMean, 2);
		}

		return (float) Math.sqrt(standardDeviation / vector.length);
	}

	public static void determineHierarchyDepth(Tree<ClusterNode> tree) {
//		int maxDepth = 0;
//		maxDepth = determineHierarchyDepthRec(tree, tree.getRoot());
		// System.out.println("maxDepth: " + maxDepth);
	}

//	private static int determineHierarchyDepthRec(Tree<ClusterNode> tree, ClusterNode node) {
//
//		if (tree.hasChildren(node)) {
//			int temp = node.getDepth();
//
//			for (ClusterNode current : tree.getChildren(node)) {
//				if (temp < determineHierarchyDepthRec(tree, current))
//					temp = determineHierarchyDepthRec(tree, current) + 1;
//			}
//
//			node.setDepth(temp);
//		}
//		else
//			node.setDepth(1);
//
//		return node.getDepth();
//	}

	public static void determineNrElements(Tree<ClusterNode> tree) {

//		int iNrElements = 0;
//		iNrElements = determineNrElementsRec(tree, tree.getRoot());
		// System.out.println("iNrElements: " + iNrElements);
	}

	private static int determineNrElementsRec(Tree<ClusterNode> tree, ClusterNode node) {

		if (tree.hasChildren(node)) {
			int temp = 0;

			for (ClusterNode current : tree.getChildren(node)) {
				temp += determineNrElementsRec(tree, current);
			}

			node.setNrElements(temp);
		}

		return node.getNrElements();

	}

	public static void sortClusters(ISet set, int iVAIdContent, int iVAIdStorage,
		ArrayList<Integer> examples, EClustererType eClustererType) {

		int iNrExamples = examples.size();
		float[] fColorSum = null;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			IVirtualArray storageVA = set.getVA(iVAIdStorage);

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iContentIndex1 : examples) {

				for (Integer iStorageIndex1 : storageVA) {
					fColorSum[icontent] +=
						set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
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
						set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
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

		for (int i = 0; i < temp.length; i++) {

			if (Float.isNaN(vector[i]))
				temp[i] = 0;
			else
				temp[i] = vector[i];
		}

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
