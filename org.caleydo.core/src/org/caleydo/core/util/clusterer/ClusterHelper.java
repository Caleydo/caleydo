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
	 * @return arthmetic mean
	 */
	public static float arithmeticMean(float[] vector) {
		float mean = 0;
		float temp = 0;
		int iCnt = 0;

		for (int i = 0; i < vector.length; i++) {

			if (Float.isNaN(vector[i]))
				temp = 0;
			else {
				temp = vector[i];
				iCnt++;
			}

			mean += temp;
		}

		return mean / iCnt;
	}

	/**
	 * Calculates the standard deviation for a given vector (float array)
	 * 
	 * @param vector
	 * @param arithmeticMean
	 * @return standard deviation
	 */
	public static float standardDeviation(float[] vector, float arithmeticMean) {
		float standardDeviation = 0;
		float temp = 0;
		int iCnt = 0;

		for (int i = 0; i < vector.length; i++) {

			if (Float.isNaN(vector[i]))
				temp = 0;
			else {
				temp = (float) Math.pow(vector[i] - arithmeticMean, 2);
				iCnt++;
			}

			standardDeviation += temp;
		}

		return (float) Math.sqrt(standardDeviation / iCnt);
	}

	/**
	 * The function is responsible for calculating the hierarchy depth in each node of the tree. To handle
	 * this an other recursive function which does the whole work is called.
	 * 
	 * @param tree
	 *            the tree
	 */
	public static void determineHierarchyDepth(Tree<ClusterNode> tree) {
		// int maxDepth = 0;
		// maxDepth = determineHierarchyDepthRec(tree, tree.getRoot());
		// System.out.println("maxDepth: " + maxDepth);
		determineHierarchyDepthRec(tree, tree.getRoot());
	}

	private static int determineHierarchyDepthRec(Tree<ClusterNode> tree, ClusterNode node) {

		if (tree.hasChildren(node)) {
			int temp = node.getDepth();

			for (ClusterNode current : tree.getChildren(node)) {
				if (temp < determineHierarchyDepthRec(tree, current))
					temp = determineHierarchyDepthRec(tree, current) + 1;
			}

			node.setDepth(temp);
		}
		else
			node.setDepth(1);

		return node.getDepth();
	}

	/**
	 * The function is responsible for calculating the number of elements in each node of the tree. To handle
	 * this an other recursive function which does the whole work is called.
	 * 
	 * @param tree
	 *            the tree
	 */
	public static void determineNrElements(Tree<ClusterNode> tree) {

		// int iNrElements = 0;
		// iNrElements = determineNrElementsRec(tree, tree.getRoot());
		// System.out.println("iNrElements: " + iNrElements);
		determineNrElementsRec(tree, tree.getRoot());
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

			for (Integer iContentIndex : examples) {

				for (Integer iStorageIndex : storageVA) {
					float temp =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
					if (Float.isNaN(temp))
						fColorSum[icontent] += 0;
					else
						fColorSum[icontent] += temp;
				}
				icontent++;
			}
		}
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING) {

			IVirtualArray contentVA = set.getVA(iVAIdContent);

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iStorageIndex : examples) {

				for (Integer iContentIndex : contentVA) {
					float temp =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
					if (Float.isNaN(temp))
						fColorSum[icontent] += 0;
					else
						fColorSum[icontent] += temp;

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
