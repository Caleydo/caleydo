/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.tree;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.function.FloatStatistics;

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
	private static class ClosestPair {
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

	private final ClusterTree tree;
	private final int iNrSamples;
	private float[][] similarities;
	/**
	 * Each node in the tree needs an unique number. Because of this we need a node counter
	 */
	private int iNodeCounter = (int) Math.floor(Integer.MAX_VALUE / 2);

	public TreeClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);

		this.tree = new ClusterTree();
		this.iNrSamples = va.size();
		this.similarities = new float[this.iNrSamples][this.iNrSamples];
	}

	/**
	 * Calculates the similarity matrix for a given set and given VAs
	 *
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities() {
		EDistanceMeasure distanceMeasure = config.getDistanceMeasure();

		int icnt1 = 0, icnt2 = 0, isto = 0;
		int iPercentage = 1;

		rename("Determine Similarities for " + getPerspectiveLabel() + " clustering");

		float[] dArInstance1 = new float[oppositeVA.size()];
		float[] dArInstance2 = new float[oppositeVA.size()];

		for (Integer vaID : va) {
			if (isClusteringCanceled) {
				progress(100, true);
				return -2;
			}

			int tempPercentage = (int) ((float) icnt1 / va.size() * 100);
			if (iPercentage == tempPercentage) {
				progress(iPercentage, false);
				iPercentage++;
			}

			isto = 0;
			for (Integer opppositeID : oppositeVA) {
				dArInstance1[isto] = table.getDataDomain().getNormalizedValue(va.getIdType(), vaID,
						oppositeVA.getIdType(), opppositeID);
				isto++;
			}

			icnt2 = 0;
			for (Integer vaID2 : va) {
				isto = 0;

				if (icnt2 < icnt1) {
					for (Integer oppositeID2 : oppositeVA) {
						dArInstance2[isto] = table.getDataDomain().getNormalizedValue(va.getIdType(), vaID2,
								oppositeVA.getIdType(), oppositeID2);
						isto++;
					}

					similarities[va.indexOf(vaID)][va.indexOf(vaID2)] = distanceMeasure.apply(dArInstance1,
							dArInstance2);
				}
				icnt2++;
			}
			icnt1++;
			eventListeners.processEvents();
		}

		progressScaled(25);

		normalizeSimilarities();

		return 0;
	}

	/**
	 * Helper function providing unique node numbers for all nodes in the tree.
	 *
	 * @return number of current node
	 */
	private int getNextNodeId() {
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
	private static ClosestPair find_closest_pair(int n, float[][] distmatrix) {

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
	 * The palcluster routine performs clustering using single linking on the given distance matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData pslcluster() {

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

		return convert(result2);
	}

	/**
	 * The palcluster routine performs clustering using pairwise average linking on the given distance matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData palcluster() {

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

		rename("Tree clustering of " + getPerspectiveLabel() + " in progress");

		for (int n = iNrSamples; n > 1; n--) {
			if (isClusteringCanceled) {
				progress(100);
				return null;
			}

			int sum;
			int is = 1;
			int js = 0;

			int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
			if (iPercentage == tempPercentage) {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(iPercentage, false));
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
				distmatrix[js][j] = distmatrix[is][j] * number[is] + distmatrix[js][j] * number[js];
				distmatrix[js][j] /= sum;
			}
			for (j = js + 1; j < is; j++) {
				distmatrix[j][js] = distmatrix[is][j] * number[is] + distmatrix[j][js] * number[js];
				distmatrix[j][js] /= sum;
			}
			for (j = is + 1; j < n; j++) {
				distmatrix[j][js] = distmatrix[j][is] * number[is] + distmatrix[j][js] * number[js];
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
			eventListeners.processEvents();
		}

		// set cluster result in Set

		return convert(result);
	}

	protected PerspectiveInitializationData convert(Node[] result) {
		ClusterNode node = new ClusterNode(tree, "Root", getNextNodeId(), true, -1);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1);

		calculateClusterAveragesRecursive(node, config.getSourceDimensionPerspective().getVirtualArray(), config
				.getSourceRecordPerspective().getVirtualArray());

		progressScaled(50);

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(tree);
		return tempResult;
	}

	private float[] calculateClusterAveragesRecursive(ClusterNode node, VirtualArray dimensionVA, VirtualArray recordVA) {
		float[] values;
		if (tree.hasChildren(node)) {
			int numberOfChildren = tree.getChildren(node).size();
			int numberOfElements = va.size();
			float[][] tempValues;

			tempValues = new float[numberOfChildren][numberOfElements];

			int cnt = 0;

			for (ClusterNode currentNode : tree.getChildren(node)) {
				tempValues[cnt] = calculateClusterAveragesRecursive(currentNode, dimensionVA, recordVA);
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
			values = new float[va.size()];
			int isto = 0;
			for (Integer oppositeID : oppositeVA) {
				values[isto] = table.getDataDomain().getNormalizedValue(va.getIdType(), node.getLeafID(),
						oppositeVA.getIdType(), oppositeID);
				isto++;
			}
		}
		FloatStatistics stats = FloatStatistics.of(values);

		float averageExpressionvalue = stats.getMean();
		float deviation = stats.getSd();

		node.setAverageExpressionValue(averageExpressionvalue);
		// Setting an float array for the representative element in each node causes a very big xml-file when
		// exporting the tree
		// node.setRepresentativeElement(fArExpressionValues);
		node.setStandardDeviation(deviation);

		return values;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given distance
	 * matrix.
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private PerspectiveInitializationData pmlcluster() {

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

		rename("Tree clustering of " + getPerspectiveLabel() + " in progress");

		for (int n = iNrSamples; n > 1; n--) {
			if (isClusteringCanceled) {
				progress(100);
				return null;
			}

			int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
			if (iPercentage == tempPercentage) {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(iPercentage, false));
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
			eventListeners.processEvents();
		}

		// set cluster result in Set

		return convert(result);
	}

	/**
	 * Function returns the name of the current node. Therefore we need an index of the gene/experiment in the VA. To
	 * avoid problems with the tree all nodes in the tree must have unique names. Therefore we need to take care of two
	 * hash maps holding the currently used names and their frequency of occurrence.
	 *
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return name of the current node
	 */
	private String getNodeName(int index) {
		String nodeName = null;
		if (config.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
			nodeName = table.getDataDomain().getRecordLabel(va.get(index));
		} else {
			nodeName = table.getDataDomain().getDimensionLabel(va.get(index));
		}
		return nodeName;
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
	private void treeStructureToTree(ClusterNode node, Node[] treeStructure, int index) {

		ClusterNode left = null;
		ClusterNode right = null;

		if (treeStructure[index].getLeft() >= 0) {

			String nodeName = getNodeName(treeStructure[index].getLeft());
			int LeaveID = va.get(treeStructure[index].getLeft());

			// this was in the constructor:
			// treeStructure[index].getCorrelation(), 0
			left = new ClusterNode(tree, nodeName, getNextNodeId(), false, LeaveID);

			// left.setNrElements(1);

			tree.addChild(node, left);

		} else {
			int random = getNextNodeId();

			left = new ClusterNode(tree, "Node_" + (-(treeStructure[index].getLeft()) - 1), random, false, -1);
			tree.addChild(node, left);
			treeStructureToTree(left, treeStructure, -(treeStructure[index].getLeft()) - 1);
		}

		if (treeStructure[index].getRight() >= 0) {

			String NodeName = getNodeName(treeStructure[index].getRight());
			int LeaveID = va.get(treeStructure[index].getRight());

			right = new ClusterNode(tree, NodeName, getNextNodeId(), false, LeaveID);

			// right.setNrElements(1);

			tree.addChild(node, right);

		} else {
			int random = getNextNodeId();

			right = new ClusterNode(tree, "Node_" + (-(treeStructure[index].getRight()) - 1), random, false, -1);
			tree.addChild(node, right);
			treeStructureToTree(right, treeStructure, -(treeStructure[index].getRight()) - 1);
		}

	}

	@Override
	protected PerspectiveInitializationData cluster() {
		int iReturnValue = 0;

		iReturnValue = determineSimilarities();

		if (iReturnValue < 0) {
			progress(100);
			return null;
		}

		TreeClusterConfiguration tConfig = (TreeClusterConfiguration) config.getClusterAlgorithmConfiguration();

		PerspectiveInitializationData tempResult;

		switch (tConfig.getTreeClustererAlgo()) {
		case COMPLETE_LINKAGE:
			tempResult = pmlcluster();
			break;
		case AVERAGE_LINKAGE:
			tempResult = palcluster();
			break;
		case SINGLE_LINKAGE:
			tempResult = pslcluster();
			break;
		default:
			throw new IllegalStateException("Unkonwn cluster type: " + tConfig.getTreeClustererAlgo());
		}

		return tempResult;
	}
}
