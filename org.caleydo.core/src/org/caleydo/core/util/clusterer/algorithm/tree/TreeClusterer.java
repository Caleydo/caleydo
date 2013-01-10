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
package org.caleydo.core.util.clusterer.algorithm.tree;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.distancemeasures.ChebyshevDistance;
import org.caleydo.core.util.clusterer.distancemeasures.EuclideanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.IDistanceMeasure;
import org.caleydo.core.util.clusterer.distancemeasures.ManhattanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.PearsonCorrelation;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;

/**
 * Tree clusterer
 *
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class TreeClusterer extends AClusterer {

	/**
	 * Helper class needed in tree cluster algorithm.
	 *
	 * @author Bernhard Schlegl
	 */
	private class ClosestPair {
		private float correlation;
		private int x;
		private int y;
		private boolean update;

		public ClosestPair() {
			this.correlation = 0;
			this.x = 0;
			this.y = 0;
			this.update = false;
		}
	}

	private ATableBasedDataDomain dataDomain = null;

	private float[][] similarities = null;

	private int iNrSamples = 0;

	private ClusterTree tree;

	private EDistanceMeasure eDistanceMeasure;

	/**
	 * Each node in the tree needs an unique number. Because of this we need a
	 * node counter
	 */
	private int iNodeCounter = (int) Math.floor(Integer.MAX_VALUE / 2);

	// Hash maps needed for determine cluster names. The name of a cluster has
	// to be unique.
	// HashMap<String, Integer> hashedNodeNames = new HashMap<String,
	// Integer>();
	// HashMap<String, Integer> duplicatedNodes = new HashMap<String,
	// Integer>();

	@Override
	public void setClusterState(ClusterConfiguration clusterState) {
		super.setClusterState(clusterState);
		try {

			if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
				tree = new ClusterTree();
				// tree.setSortingStrategy(ESortingStrategy.AVERAGE_VALUE);
				this.iNrSamples = clusterState.getSourceRecordPerspective()
						.getVirtualArray().size();
			} else if (clusterState.getClusterTarget() == EClustererTarget.DIMENSION_CLUSTERING) {
				tree = new ClusterTree();
				this.iNrSamples = clusterState.getSourceDimensionPerspective()
						.getVirtualArray().size();
			} else
				throw new IllegalArgumentException("Can not handle cluster type "
						+ clusterState.getClusterTarget());

			this.similarities = new float[this.iNrSamples][this.iNrSamples];
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError();
		}
	}

	/**
	 * Calculates the similarity matrix for a given set and given VAs
	 *
	 * @param set
	 * @param eClustererType
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities(DataTable table, EClustererTarget eClustererType) {

		IDistanceMeasure distanceMeasure;

		if (eDistanceMeasure == EDistanceMeasure.MANHATTAN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else if (eDistanceMeasure == EDistanceMeasure.CHEBYSHEV_DISTANCE)
			distanceMeasure = new ChebyshevDistance();
		else if (eDistanceMeasure == EDistanceMeasure.PEARSON_CORRELATION)
			distanceMeasure = new PearsonCorrelation();
		else
			distanceMeasure = new EuclideanDistance();

		int icnt1 = 0, icnt2 = 0, isto = 0;
		int iPercentage = 1;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING) {

			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Determine Similarities for gene clustering"));

			float[] dArInstance1 = new float[dimensionVA.size()];
			float[] dArInstance2 = new float[dimensionVA.size()];

			for (Integer recordIndex1 : recordVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / recordVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager
								.get()
								.getEventPublisher()
								.triggerEvent(
										new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer iDimensionIndex1 : dimensionVA) {
						dArInstance1[isto] = table.getNormalizedValue(recordIndex1,
								iDimensionIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer recordIndex2 : recordVA) {
						processEvents();
						isto = 0;

						if (icnt2 < icnt1) {
							for (Integer iDimensionIndex2 : dimensionVA) {
								dArInstance2[isto] = table.getNormalizedValue(recordIndex2,
										iDimensionIndex2);
								isto++;
							}

							similarities[recordVA.indexOf(recordIndex1)][recordVA
									.indexOf(recordIndex2)] = distanceMeasure.getMeasure(
									dArInstance1, dArInstance2);
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				} else {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		} else {

			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Determine Similarities for experiment clustering"));

			float[] dArInstance1 = new float[recordVA.size()];
			float[] dArInstance2 = new float[recordVA.size()];

			for (Integer iDimensionIndex1 : dimensionVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / dimensionVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager
								.get()
								.getEventPublisher()
								.triggerEvent(
										new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer recordIndex1 : recordVA) {
						dArInstance1[isto] = table.getNormalizedValue(recordIndex1,
								iDimensionIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer iDimensionIndex2 : dimensionVA) {
						isto = 0;

						if (icnt2 < icnt1) {
							for (Integer recordIndex2 : recordVA) {
								dArInstance2[isto] = table.getNormalizedValue(recordIndex2,
										iDimensionIndex2);
								isto++;
							}

							similarities[dimensionVA.indexOf(iDimensionIndex1)][dimensionVA
									.indexOf(iDimensionIndex2)] = distanceMeasure
									.getMeasure(dArInstance1, dArInstance2);
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				} else {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		}
		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(iProgressBarMultiplier * 25
								+ iProgressBarOffsetValue, true));
		normalizeSimilarities();

		return 0;
	}

	/**
	 * Helper function providing unique node numbers for all nodes in the tree.
	 *
	 * @return number of current node
	 */
	private int getNodeCounter() {
		return iNodeCounter++;
	}

	/**
	 * Function normalizes similarities between 0 and 1
	 */
	private void normalizeSimilarities() {

		float max = Float.MIN_VALUE;

		for (int i = 0; i < similarities.length; i++) {
			for (int j = 0; j < similarities.length; j++) {
				max = Math.max(max, similarities[i][j]);
			}
		}

		for (int i = 0; i < similarities.length; i++) {
			for (int j = 0; j < similarities.length; j++) {
				similarities[i][j] = similarities[i][j] / max;
			}
		}

	}

	/**
	 * Function looks for the next closest pair in the distance matrix.
	 *
	 * @param n
	 *            current size of the similarity matrix
	 * @param distmatrix
	 *            the similarity matrix
	 * @return the closest pair
	 */
	private ClosestPair find_closest_pair(int n, float[][] distmatrix) {

		ClosestPair pair = new ClosestPair();
		float temp;
		float distance = distmatrix[1][0];

		for (int x = 1; x < n; x++) {
			for (int y = 0; y < x; y++) {

				temp = distmatrix[x][y];
				if (temp < distance) {
					distance = temp;
					pair.correlation = temp;
					pair.x = x;
					pair.y = y;
					pair.update = true;
				}
			}
		}
		return pair;
	}

	/**
	 * The palcluster routine performs clustering using single linking on the
	 * given distance matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData pslcluster(EClustererTarget eClustererType) {

		int nnodes = iNrSamples - 1;
		int[] vector = new int[nnodes];
		float[] temp = new float[nnodes];
		int[] index = new int[iNrSamples];
		Node[] result = null;

		float[][] distmatrix;

		try {
			result = new Node[iNrSamples];
			distmatrix = new float[iNrSamples][iNrSamples];
		} catch (OutOfMemoryError e) {
			return null;
		}

		distmatrix = similarities.clone();

		for (int i = 0; i < iNrSamples; i++) {
			result[i] = new Node();
			result[i].setCorrelation(Float.MAX_VALUE);
			for (int j = 0; j < i; j++)
				temp[j] = distmatrix[i][j];
			for (int j = 0; j < i; j++) {
				int k = vector[j];
				if (result[j].getCorrelation() >= temp[j]) {
					if (result[j].getCorrelation() < temp[k])
						temp[k] = result[j].getCorrelation();
					result[j].setCorrelation(temp[j]);
					vector[j] = i;
				} else if (temp[j] < temp[k])
					temp[k] = temp[j];
			}
			for (int j = 0; j < i; j++) {
				if (result[j].getCorrelation() >= result[vector[j]].getCorrelation())
					vector[j] = i;
			}
		}

		for (int i = 0; i < nnodes; i++)
			result[i].setLeft(i);

		for (int i = 0; i < iNrSamples; i++)
			index[i] = i;

		for (int i = 0; i < nnodes; i++) {
			int j = result[i].getLeft();
			int k = vector[j];
			result[i].setLeft(index[j]);
			result[i].setRight(index[k]);
			index[k] = -i - 1;
		}

		Node[] result2 = new Node[nnodes];
		for (int i = 0; i < nnodes; i++)
			result2[i] = result[i];

		// set cluster result in Set

		ClusterNode node = new ClusterNode(tree, "Root", getNodeCounter(), true, -1);
		tree.setRootNode(node);
		treeStructureToTree(node, result2, result2.length - 1, eClustererType);

		ClusterHelper.calculateClusterAveragesRecursive(tree, node, clusterState
				.getClusterTarget(), dataDomain.getTable(), clusterState
				.getSourceDimensionPerspective().getVirtualArray(), clusterState
				.getSourceRecordPerspective().getVirtualArray());

		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(iProgressBarMultiplier * 50
								+ iProgressBarOffsetValue, true));

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(tree);
		return tempResult;
	}

	/**
	 * The palcluster routine performs clustering using pairwise average linking
	 * on the given distance matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData palcluster(EClustererTarget eClustererType) {

		int[] clusterid = new int[iNrSamples];
		int[] number = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		for (int j = 0; j < iNrSamples; j++) {
			number[j] = 1;
			clusterid[j] = j;
		}

		int j;

		ClosestPair pair = null;

		float[][] distmatrix;

		try {
			distmatrix = new float[iNrSamples][iNrSamples];
		} catch (OutOfMemoryError e) {
			return null;
		}

		distmatrix = similarities.clone();

		int iPercentage = 1;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING)
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Tree clustering of genes in progress"));
		else
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Tree clustering of experiments in progress"));

		for (int n = iNrSamples; n > 1; n--) {
			if (bClusteringCanceled == false) {
				int sum;
				int is = 1;
				int js = 0;

				int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
				if (iPercentage == tempPercentage) {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(iPercentage, false));
					iPercentage++;
				}

				pair = find_closest_pair(n, distmatrix);

				if (pair.update) {
					is = pair.x;
					js = pair.y;
				}

				// Update clusterids
				Node node = new Node();

				node.setCorrelation(pair.correlation);
				node.setLeft(clusterid[is]);
				node.setRight(clusterid[js]);

				// Save result
				result[iNrSamples - n] = node;

				// Fix the distances
				sum = number[is] + number[js];
				for (j = 0; j < js; j++) {
					distmatrix[js][j] = distmatrix[is][j] * number[is]
							+ distmatrix[js][j] * number[js];
					distmatrix[js][j] /= sum;
				}
				for (j = js + 1; j < is; j++) {
					distmatrix[j][js] = distmatrix[is][j] * number[is]
							+ distmatrix[j][js] * number[js];
					distmatrix[j][js] /= sum;
				}
				for (j = is + 1; j < n; j++) {
					distmatrix[j][js] = distmatrix[j][is] * number[is]
							+ distmatrix[j][js] * number[js];
					distmatrix[j][js] /= sum;
				}

				for (j = 0; j < is; j++)
					distmatrix[is][j] = distmatrix[n - 1][j];
				for (j = is + 1; j < n - 1; j++)
					distmatrix[j][is] = distmatrix[n - 1][j];

				// Update number of elements in the clusters
				number[js] = sum;
				number[is] = number[n - 1];

				// Update clusterids
				clusterid[js] = n - iNrSamples - 1;
				clusterid[is] = clusterid[n - 1];
				processEvents();
			} else {
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
				return null;
			}
		}

		// set cluster result in Set

		ClusterNode node = new ClusterNode(tree, "Root", getNodeCounter(), true, -1);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.calculateClusterAveragesRecursive(tree, node, clusterState
				.getClusterTarget(), dataDomain.getTable(), clusterState
				.getSourceDimensionPerspective().getVirtualArray(), clusterState
				.getSourceRecordPerspective().getVirtualArray());

		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(iProgressBarMultiplier * 50
								+ iProgressBarOffsetValue, true));

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(tree);
		return tempResult;
	}

	/**
	 * The function is responsible for calculating the expression value in each
	 * node of the tree. To handle this an other recursive function which does
	 * the whole work is called.
	 *
	 * @param eClustererType
	 */
	// private void determineExpressionValue(Tree<ClusterNode> clusterTree,
	// EClustererType eClustererType) {
	//
	// determineExpressionValueRec(clusterTree, clusterTree.getRoot(),
	// eClustererType);
	// }

	/**
	 * Recursive function which determines the expression value in each node of
	 * the tree.
	 *
	 * @param tree
	 * @param node
	 *            current node
	 * @return depth of the current node
	 */
	// private float[] determineExpressionValueRec(Tree<ClusterNode>
	// clusterTree, ClusterNode node,
	// EClustererType eClustererType) {
	//
	// float[] fArExpressionValues;
	//
	// if (clusterTree.hasChildren(node)) {
	//
	// int iNrNodes = clusterTree.getChildren(node).size();
	// int iNrElements = 0;
	// float[][] fArTempValues;
	//
	// if (eClustererType == EClustererType.GENE_CLUSTERING) {
	// IVirtualArray dimensionVA = table.getVA(iVAIdDimension);
	// iNrElements = dimensionVA.size();
	// }
	// else {
	// IVirtualArray recordVA = table.getVA(iVAIdContent);
	// iNrElements = recordVA.size();
	// }
	//
	// fArTempValues = new float[iNrNodes][iNrElements];
	//
	// int cnt = 0;
	//
	// for (ClusterNode current : clusterTree.getChildren(node)) {
	// fArTempValues[cnt] = determineExpressionValueRec(clusterTree, current,
	// eClustererType);
	// cnt++;
	// }
	//
	// fArExpressionValues = new float[iNrElements];
	//
	// for (int i = 0; i < iNrElements; i++) {
	// float means = 0;
	//
	// for (int nodes = 0; nodes < iNrNodes; nodes++) {
	// means += fArTempValues[nodes][i];
	// }
	// fArExpressionValues[i] = means / iNrNodes;
	// }
	// }
	// // no children --> leaf node
	// else {
	//
	// if (eClustererType == EClustererType.GENE_CLUSTERING) {
	// IVirtualArray dimensionVA = table.getVA(iVAIdDimension);
	// fArExpressionValues = new float[dimensionVA.size()];
	//
	// int isto = 0;
	// for (Integer iDimensionIndex : dimensionVA) {
	// fArExpressionValues[isto] =
	// table.get(iDimensionIndex).getFloat(EDataRepresentation.NORMALIZED,
	// node.getLeaveID());
	// isto++;
	// }
	//
	// }
	// else {
	// IVirtualArray recordVA = table.getVA(iVAIdContent);
	// fArExpressionValues = new float[recordVA.size()];
	//
	// int icon = 0;
	// for (Integer recordIndex : recordVA) {
	// fArExpressionValues[icon] =
	// table.get(node.getLeaveID()).getFloat(EDataRepresentation.NORMALIZED,
	// recordIndex);
	// icon++;
	// }
	// }
	// }
	// float averageExpressionvalue =
	// ClusterHelper.arithmeticMean(fArExpressionValues);
	// float deviation = ClusterHelper.standardDeviation(fArExpressionValues,
	// averageExpressionvalue);
	// node.setAverageExpressionValue(averageExpressionvalue);
	// // Setting an float array for the representative element in each node
	// causes a very big xml-file when
	// // exporting the tree
	// // node.setRepresentativeElement(fArExpressionValues);
	// node.setStandardDeviation(deviation);
	//
	// return fArExpressionValues;
	// }

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum-
	 * (complete-) linking on the given distance matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData pmlcluster(EClustererTarget eClustererType) {

		int[] clusterid = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		// Setup a list specifying to which cluster a gene belongs
		for (int j = 0; j < iNrSamples; j++)
			clusterid[j] = j;

		int j;

		ClosestPair pair = null;

		float[][] distmatrix;

		try {
			distmatrix = new float[iNrSamples][iNrSamples];
		} catch (OutOfMemoryError e) {
			return null;
		}

		distmatrix = similarities.clone();

		int iPercentage = 1;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING)
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Tree clustering of genes in progress"));
		else
			GeneralManager
					.get()
					.getEventPublisher()
					.triggerEvent(
							new RenameProgressBarEvent(
									"Tree clustering of experiments in progress"));

		for (int n = iNrSamples; n > 1; n--) {

			if (bClusteringCanceled == false) {
				int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
				if (iPercentage == tempPercentage) {
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(iPercentage, false));
					iPercentage++;
				}

				int is = 1;
				int js = 0;

				pair = find_closest_pair(n, distmatrix);

				if (pair.update) {
					is = pair.x;
					js = pair.y;
				}

				// Fix the distances
				for (j = 0; j < js; j++)
					distmatrix[js][j] = Math.max(distmatrix[is][j], distmatrix[js][j]);
				for (j = js + 1; j < is; j++)
					distmatrix[j][js] = Math.max(distmatrix[is][j], distmatrix[j][js]);
				for (j = is + 1; j < n; j++)
					distmatrix[j][js] = Math.max(distmatrix[j][is], distmatrix[j][js]);

				for (j = 0; j < is; j++)
					distmatrix[is][j] = distmatrix[n - 1][j];
				for (j = is + 1; j < n - 1; j++)
					distmatrix[j][is] = distmatrix[n - 1][j];

				// Update clusterids
				Node node = new Node();

				node.setCorrelation(pair.correlation);
				node.setLeft(clusterid[is]);
				node.setRight(clusterid[js]);

				result[iNrSamples - n] = node;

				clusterid[js] = n - iNrSamples - 1;
				clusterid[is] = clusterid[n - 1];
				processEvents();
			} else {
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
				return null;
			}
		}

		// set cluster result in Set

		ClusterNode node = new ClusterNode(tree, "Root", getNodeCounter(), true, -1);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.calculateClusterAveragesRecursive(tree, node, clusterState
				.getClusterTarget(), dataDomain.getTable(), clusterState
				.getSourceDimensionPerspective().getVirtualArray(), clusterState
				.getSourceRecordPerspective().getVirtualArray());

		GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(
						new ClusterProgressEvent(iProgressBarMultiplier * 50
								+ iProgressBarOffsetValue, true));

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(tree);
		return tempResult;
	}

	/**
	 * Function returns the name of the current node. Therefore we need an index
	 * of the gene/experiment in the VA. To avoid problems with the tree all
	 * nodes in the tree must have unique names. Therefore we need to take care
	 * of two hash maps holding the currently used names and their frequency of
	 * occurrence.
	 *
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return name of the current node
	 */
	private String getNodeName(EClustererTarget eClustererType, int index) {
		String nodeName = null;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING) {
			nodeName = dataDomain.getRecordLabel(recordVA.get(index));
		} else {
			nodeName = dataDomain.getDimensionLabel(dimensionVA.get(index));
		}

		// // check if current node name was already used. If yes we add signs
		// to make it unique.
		// if (hashedNodeNames.containsKey(nodeName)) {
		// int iNr = 1;
		// if (duplicatedNodes.containsKey(nodeName)) {
		// iNr = duplicatedNodes.get(nodeName);
		// duplicatedNodes.put(nodeName, ++iNr);
		// }
		// else
		// duplicatedNodes.put(nodeName, iNr);
		//
		// nodeName = nodeName + "__" + iNr;
		// }
		// else
		// hashedNodeNames.put(nodeName, 1);

		return nodeName;
	}

	/**
	 * Function returns the number of the current node. Therefore we need an
	 * index of the gene/experiment in the VA.
	 *
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return number of the current node
	 */
	private int getNodeNr(EClustererTarget eClustererType, int index) {

		int nodeNr = 0;

		if (eClustererType == EClustererTarget.RECORD_CLUSTERING) {
			nodeNr = recordVA.get(index);
		} else {
			nodeNr = dimensionVA.get(index);
		}

		return nodeNr;
	}

	/**
	 * Recursive function responsible for mapping tree structure to a tree
	 *
	 * @param node
	 *            current node
	 * @param treeStructure
	 * @param index
	 *            current index in the tree structure
	 * @param eClustererType
	 */
	private void treeStructureToTree(ClusterNode node, Node[] treeStructure, int index,
			EClustererTarget eClustererType) {

		ClusterNode left = null;
		ClusterNode right = null;

		if (treeStructure[index].getLeft() >= 0) {

			String nodeName = getNodeName(eClustererType, treeStructure[index].getLeft());
			int LeaveID = getNodeNr(eClustererType, treeStructure[index].getLeft());

			// this was in the constructor:
			// treeStructure[index].getCorrelation(), 0
			left = new ClusterNode(tree, nodeName, getNodeCounter(), false, LeaveID);

			// left.setNrElements(1);

			tree.addChild(node, left);

		} else {
			int random = getNodeCounter();

			left = new ClusterNode(tree, "Node_"
					+ (-(treeStructure[index].getLeft()) - 1), random, false, -1);
			tree.addChild(node, left);
			treeStructureToTree(left, treeStructure,
					-(treeStructure[index].getLeft()) - 1, eClustererType);
		}

		if (treeStructure[index].getRight() >= 0) {

			String NodeName = getNodeName(eClustererType, treeStructure[index].getRight());
			int LeaveID = getNodeNr(eClustererType, treeStructure[index].getRight());

			right = new ClusterNode(tree, NodeName, getNodeCounter(), false, LeaveID);

			// right.setNrElements(1);

			tree.addChild(node, right);

		} else {
			int random = getNodeCounter();

			right = new ClusterNode(tree, "Node_"
					+ (-(treeStructure[index].getRight()) - 1), random, false, -1);
			tree.addChild(node, right);
			treeStructureToTree(right, treeStructure,
					-(treeStructure[index].getRight()) - 1, eClustererType);
		}

	}

	@Override
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
			ClusterConfiguration clusterState, int iProgressBarOffsetValue,
			int iProgressBarMultiplier) {

		this.dataDomain = dataDomain;

		eDistanceMeasure = clusterState.getDistanceMeasure();
		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		int iReturnValue = 0;

		iReturnValue = determineSimilarities(dataDomain.getTable(),
				clusterState.getClusterTarget());

		if (iReturnValue < 0) {
			GeneralManager.get().getEventPublisher()
					.triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}

		PerspectiveInitializationData tempResult;

		TreeClusterConfiguration treeClusterConfiguration = (TreeClusterConfiguration) clusterState.getClusterAlgorithmConfiguration();

		switch (treeClusterConfiguration.getTreeClustererAlgo()) {

		case COMPLETE_LINKAGE:
			tempResult = pmlcluster(clusterState.getClusterTarget());
			break;
		case AVERAGE_LINKAGE:
			tempResult = palcluster(clusterState.getClusterTarget());
			break;
		case SINGLE_LINKAGE:
			tempResult = pslcluster(clusterState.getClusterTarget());
			break;
		default:
			throw new IllegalStateException("Unkonwn cluster type: "
					+ treeClusterConfiguration.getTreeClustererAlgo());
		}

		return tempResult;

	}
}
