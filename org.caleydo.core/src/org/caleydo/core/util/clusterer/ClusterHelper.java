package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;

/**
 * Cluster helper provides methods needed in cluster algorithms such as median, arithmetic mean, etc.
 * 
 * @author Bernhard Schlegl
 */
public class ClusterHelper {

	/**
	 * Calculates the arithmetic mean for a given vector (float array)
	 * 
	 * @param vector
	 * @return arithmetic mean
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
	// public static void determineHierarchyDepth(Tree<ClusterNode> tree) {
	// // int maxDepth = 0;
	// // maxDepth = determineHierarchyDepthRec(tree, tree.getRoot());
	// // System.out.println("maxDepth: " + maxDepth);
	// determineHierarchyDepthRec(tree, tree.getRoot());
	// }

	/**
	 * Recursive function which determines the hierarchy depth in each node of the tree.
	 * 
	 * @param tree
	 * @param node
	 *            current node
	 * @return depth of the current node
	 */
	// private static int determineHierarchyDepthRec(Tree<ClusterNode> tree, ClusterNode node) {
	//
	// if (tree.hasChildren(node)) {
	// int temp = node.getDepth();
	//
	// for (ClusterNode current : tree.getChildren(node)) {
	// int iChildDepth = determineHierarchyDepthRec(tree, current);
	// if (temp <= iChildDepth)
	// temp = iChildDepth + 1;
	// }
	//
	// node.setDepth(temp);
	// }
	// else
	// node.setDepth(1);
	//
	// return node.getDepth();
	// }

	// /**
	// * The function is responsible for calculating the number of elements in each node of the tree. To
	// handle
	// * this an other recursive function which does the whole work is called.
	// *
	// * @param tree
	// * the tree
	// */
	// public static void determineNrElements(Tree<ClusterNode> tree) {
	//
	// // int iNrElements = 0;
	// // iNrElements = determineNrElementsRec(tree, tree.getRoot());
	// // System.out.println("iNrElements: " + iNrElements);
	// determineNrElementsRec(tree, tree.getRoot());
	// }

	public static void determineExpressionValue(Tree<ClusterNode> tree, EClustererType eClustererType,
		ISet set) {
		// FIXME - direct references here - should be parameters
		StorageVirtualArray storageVA = set.getStorageVA(StorageVAType.STORAGE);
		ContentVirtualArray contentVA = set.getContentVA(ContentVAType.CONTENT);
		determineExpressionValueRec(tree, tree.getRoot(), eClustererType, set, storageVA, contentVA);
	}

	private static float[] determineExpressionValueRec(Tree<ClusterNode> tree, ClusterNode node,
		EClustererType eClustererType, ISet set, StorageVirtualArray storageVA, ContentVirtualArray contentVA) {

		float[] fArExpressionValues;

		if (tree.hasChildren(node)) {

			int iNrNodes = tree.getChildren(node).size();
			int iNrElements = 0;
			float[][] fArTempValues;

			if (eClustererType == EClustererType.GENE_CLUSTERING) {
				// IVirtualArray storageVA = set.getVA(iVAIdStorage);
				// IVirtualArray storageVA = set.createCompleteStorageVA();
				iNrElements = storageVA.size();
			}
			else {
				// IVirtualArray contentVA = set.getVA(iVAIdContent);
				// IVirtualArray contentVA =
				// GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).getVA(EVAType.CONTENT);
				iNrElements = contentVA.size();
			}

			fArTempValues = new float[iNrNodes][iNrElements];

			int cnt = 0;

			for (ClusterNode current : tree.getChildren(node)) {
				fArTempValues[cnt] =
					determineExpressionValueRec(tree, current, eClustererType, set, storageVA, contentVA);
				cnt++;
			}

			fArExpressionValues = new float[iNrElements];

			for (int i = 0; i < iNrElements; i++) {
				float means = 0;

				for (int nodes = 0; nodes < iNrNodes; nodes++) {
					means += fArTempValues[nodes][i];
				}
				fArExpressionValues[i] = means / iNrNodes;
			}
		}
		// no children --> leaf node
		else {

			if (eClustererType == EClustererType.GENE_CLUSTERING) {
				// IVirtualArray storageVA = set.getVA(iVAIdStorage);
				// IVirtualArray storageVA = set.createCompleteStorageVA();
				fArExpressionValues = new float[storageVA.size()];

				int isto = 0;
				for (Integer iStorageIndex : storageVA) {
					fArExpressionValues[isto] =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, node.getLeafID());
					isto++;
				}

			}
			else {
				// IVirtualArray contentVA = set.getVA(iVAIdContent);
				// IVirtualArray contentVA =
				// GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).getVA(EVAType.CONTENT);
				fArExpressionValues = new float[contentVA.size()];

				int icon = 0;
				for (Integer iContentIndex : contentVA) {
					fArExpressionValues[icon] =
						set.get(node.getLeafID()).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
					icon++;
				}
			}
		}
		float averageExpressionvalue = ClusterHelper.arithmeticMean(fArExpressionValues);
		float deviation = ClusterHelper.standardDeviation(fArExpressionValues, averageExpressionvalue);
		node.setAverageExpressionValue(averageExpressionvalue);
		// Setting an float array for the representative element in each node causes a very big xml-file when
		// exporting the tree
		// node.setRepresentativeElement(fArExpressionValues);
		node.setStandardDeviation(deviation);

		return fArExpressionValues;
	}

	// /**
	// * Recursive function which determines the number of elements in each node of the tree.
	// *
	// * @param tree
	// * @param node
	// * current node
	// * @return number of elements in the current node
	// */
	// private static int determineNrElementsRec(Tree<ClusterNode> tree, ClusterNode node) {
	//
	// if (tree.hasChildren(node)) {
	// int temp = 0;
	//
	// for (ClusterNode current : tree.getChildren(node)) {
	// temp += determineNrElementsRec(tree, current);
	// }
	//
	// node.setNrElements(temp);
	// } else {
	// node.setNrElements(1);
	// }
	//
	// return node.getNrElements();
	//
	// }

	/**
	 * Function sorts clusters depending on their average value (in case of genes: expression value).
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @param examples
	 * @param eClustererType
	 */
	public static void sortClusters(ISet set, ContentVirtualArray contentVA, StorageVirtualArray storageVA,
		ArrayList<Integer> examples, EClustererType eClustererType) {

		int iNrExamples = examples.size();
		float[] fColorSum = null;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iContentIndex : examples) {

				for (Integer iStorageIndex : storageVA) {
					float temp =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
							contentVA.get(iContentIndex));
					if (Float.isNaN(temp))
						fColorSum[icontent] += 0;
					else
						fColorSum[icontent] += temp;
				}
				icontent++;
			}
		}
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iStorageIndex : examples) {

				for (Integer iContentIndex : contentVA) {
					float temp =
						set.get(storageVA.get(iStorageIndex)).getFloat(EDataRepresentation.NORMALIZED,
							iContentIndex);
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
			if (fColorSum[f] < fColorSum[f - 1])
				continue;
			temp = fColorSum[f];
			iTemp = examples.get(f);
			i = f - 1;
			while ((i >= 0) && (fColorSum[i] < temp)) {
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
