/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Arrays;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
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

	// public static void calculateClusterAverages(DimensionPerspective dimensionData,
	// RecordPerspective recordPerspective, ClustererType eClustererType, ATableBasedDataDomain dataDomain) {
	//
	// DimensionVirtualArray dimensionVA = dimensionData.getVirtualArray();
	// RecordVirtualArray recordVA = recordPerspective.getVirtualArray();
	// if (eClustererType == ClustererType.RECORD_CLUSTERING) {
	// calculateClusterAveragesRecursive(recordPerspective.getTree(), recordPerspective.getTree().getRoot(),
	// eClustererType, dataDomain.getTable(), dimensionVA, recordVA);
	// }
	// else if (eClustererType == ClustererType.DIMENSION_CLUSTERING) {
	// calculateClusterAveragesRecursive(dimensionData.getTree(), dimensionData.getTree().getRoot(),
	// eClustererType, dataDomain.getTable(), dimensionVA, recordVA);
	// }
	// }

	public static float[] calculateClusterAveragesRecursive(Tree<ClusterNode> tree, ClusterNode node,
		EClustererTarget clustererType, DataTable table, DimensionVirtualArray dimensionVA,
		RecordVirtualArray recordVA) {

		float[] values;

		if (tree.hasChildren(node)) {

			int numberOfChildren = tree.getChildren(node).size();
			int numberOfElements = 0;
			float[][] tempValues;

			if (clustererType == EClustererTarget.RECORD_CLUSTERING) {
				numberOfElements = dimensionVA.size();
			}
			else {
				numberOfElements = recordVA.size();
			}

			tempValues = new float[numberOfChildren][numberOfElements];

			int cnt = 0;

			for (ClusterNode currentNode : tree.getChildren(node)) {
				tempValues[cnt] =
					calculateClusterAveragesRecursive(tree, currentNode, clustererType, table, dimensionVA,
						recordVA);
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

			if (clustererType == EClustererTarget.RECORD_CLUSTERING) {
				values = new float[dimensionVA.size()];

				int isto = 0;
				for (Integer iDimensionIndex : dimensionVA) {
					values[isto] =
						table.getFloat(DataRepresentation.NORMALIZED, node.getLeafID(), iDimensionIndex);
					isto++;
				}

			}
			else {
				values = new float[recordVA.size()];

				int icon = 0;
				for (Integer recordIndex : recordVA) {
					values[icon] =
						table.getFloat(DataRepresentation.NORMALIZED, recordIndex, node.getLeafID());
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

	public static void calculateAggregatedUncertainties(RecordPerspective recordData, DataTable table) {
		RecordVirtualArray recordVA = recordData.getVirtualArray();
		calculateAggregatedUncertaintiesRecursive(recordData.getTree(), recordData.getTree().getRoot(),
			table, recordVA);
	}

	private static Pair<Float, Integer> calculateAggregatedUncertaintiesRecursive(Tree<ClusterNode> tree,
		ClusterNode node, DataTable table, RecordVirtualArray recordVA) {

		Pair<Float, Integer> result = new Pair<Float, Integer>();

		if (node.isLeaf()) {
			float uncertainty = 0;
			// FIXME
			// (float) table.getStatisticsResult().getAggregatedUncertainty()[node.getLeafID()];
			result.setFirst(uncertainty);
			result.setSecond(1);
			node.setUncertainty(uncertainty);
			return result;
		}

		int childCount = 0;
		float uncertaintySum = 0;
		for (ClusterNode child : node.getChildren()) {
			Pair<Float, Integer> childResult =
				calculateAggregatedUncertaintiesRecursive(tree, child, table, recordVA);
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
	 * @param iVAIdDimension
	 * @param examples
	 * @param eClustererType
	 */
	public static void sortClusters(DataTable table, RecordVirtualArray recordVA,
		DimensionVirtualArray dimensionVA, ArrayList<Integer> examples, EClustererTarget eClustererType) {

		int iNrExamples = examples.size();
		float[] fColorSum = null;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer recordIndex : examples) {

				for (Integer dimensionIndex : dimensionVA) {
					float temp =
						table.getFloat(DataRepresentation.NORMALIZED, recordVA.get(recordIndex),
							dimensionIndex);
					if (Float.isNaN(temp))
						fColorSum[icontent] += 0;
					else
						fColorSum[icontent] += temp;
				}
				icontent++;
			}
		}
		else if (eClustererType == EClustererTarget.DIMENSION_CLUSTERING) {

			int icontent = 0;
			fColorSum = new float[iNrExamples];

			for (Integer iDimensionIndex : examples) {

				for (Integer recordIndex : recordVA) {
					float temp =
						table.getFloat(DataRepresentation.NORMALIZED, recordIndex,
							dimensionVA.get(iDimensionIndex));
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
