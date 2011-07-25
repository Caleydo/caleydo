package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.util.collection.Pair;

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

	public static void calculateClusterAverages(Tree<ClusterNode> tree, EClustererType eClustererType,
		DataTable set) {
		// FIXME - direct references here - should be parameters
		DimensionVirtualArray storageVA = set.getStorageData(DataTable.DIMENSION).getStorageVA();
		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD).getContentVA();
		calculateClusterAveragesRecursive(tree, tree.getRoot(), eClustererType, set, storageVA, contentVA);
	}

	private static float[] calculateClusterAveragesRecursive(Tree<ClusterNode> tree, ClusterNode node,
		EClustererType clustererType, DataTable set, DimensionVirtualArray storageVA, ContentVirtualArray contentVA) {

		float[] values;

		if (tree.hasChildren(node)) {

			int numberOfChildren = tree.getChildren(node).size();
			int numberOfElements = 0;
			float[][] tempValues;

			if (clustererType == EClustererType.CONTENT_CLUSTERING) {
				numberOfElements = storageVA.size();
			}
			else {
				numberOfElements = contentVA.size();
			}

			tempValues = new float[numberOfChildren][numberOfElements];

			int cnt = 0;

			for (ClusterNode currentNode : tree.getChildren(node)) {
				tempValues[cnt] =
					calculateClusterAveragesRecursive(tree, currentNode, clustererType, set, storageVA,
						contentVA);
				cnt++;
			}

			values = new float[numberOfElements];

			for (int i = 0; i < numberOfElements; i++) {
				float means = 0;

				for (int nodes = 0; nodes < numberOfChildren; nodes++) {
					means += tempValues[nodes][i];
				}
				values[i] = means / numberOfChildren;
			}
		}
		// no children --> leaf node
		else {

			if (clustererType == EClustererType.CONTENT_CLUSTERING) {
				values = new float[storageVA.size()];

				int isto = 0;
				for (Integer iStorageIndex : storageVA) {
					values[isto] =
						set.get(iStorageIndex).getFloat(DataRepresentation.NORMALIZED, node.getLeafID());
					isto++;
				}

			}
			else {
				values = new float[contentVA.size()];

				int icon = 0;
				for (Integer contentIndex : contentVA) {
					values[icon] =
						set.get(node.getLeafID()).getFloat(DataRepresentation.NORMALIZED, contentIndex);
					icon++;
				}
			}
		}
		float averageExpressionvalue = ClusterHelper.arithmeticMean(values);
		float deviation = ClusterHelper.standardDeviation(values, averageExpressionvalue);
		node.setAverageExpressionValue(averageExpressionvalue);
		// Setting an float array for the representative element in each node causes a very big xml-file when
		// exporting the tree
		// node.setRepresentativeElement(fArExpressionValues);
		node.setStandardDeviation(deviation);

		return values;
	}

	public static void calculateAggregatedUncertainties(Tree<ClusterNode> tree, DataTable set) {
		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD).getContentVA();
		calculateAggregatedUncertaintiesRecursive(tree, tree.getRoot(), set, contentVA);
	}

	private static Pair<Float, Integer> calculateAggregatedUncertaintiesRecursive(Tree<ClusterNode> tree,
		ClusterNode node, DataTable set, ContentVirtualArray contentVA) {

		Pair<Float, Integer> result = new Pair<Float, Integer>();

		if (node.isLeaf()) {
			float uncertainty = (float)set.getStatisticsResult().getAggregatedUncertainty()[node.getLeafID()];
			result.setFirst(uncertainty);
			result.setSecond(1);
			node.setUncertainty(uncertainty);
			return result;
		}

		int childCount = 0;
		float uncertaintySum = 0;
		for (ClusterNode child : node.getChildren()) {
			Pair<Float, Integer> childResult =
				calculateAggregatedUncertaintiesRecursive(tree, child, set, contentVA);
			uncertaintySum += childResult.getFirst();
			childCount += childResult.getSecond();

		}
		node.setUncertainty(uncertaintySum / childCount);
		result.setFirst(uncertaintySum);
		result.setSecond(childCount);
		return result;
	}

	/**
	 * Function sorts clusters depending on their average value (in case of genes: expression value).
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @param examples
	 * @param eClustererType
	 */
	public static void sortClusters(DataTable set, ContentVirtualArray contentVA, DimensionVirtualArray storageVA,
		ArrayList<Integer> examples, EClustererType eClustererType) {

		int iNrExamples = examples.size();
		float[] fColorSum = null;

		if (eClustererType == EClustererType.CONTENT_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer contentIndex : examples) {

				for (Integer storageIndex : storageVA) {
					float temp =
						set.get(storageIndex).getFloat(DataRepresentation.NORMALIZED,
							contentVA.get(contentIndex));
					if (Float.isNaN(temp))
						fColorSum[icontent] += 0;
					else
						fColorSum[icontent] += temp;
				}
				icontent++;
			}
		}
		else if (eClustererType == EClustererType.STORAGE_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iStorageIndex : examples) {

				for (Integer iContentIndex : contentVA) {
					float temp =
						set.get(storageVA.get(iStorageIndex)).getFloat(DataRepresentation.NORMALIZED,
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
