/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.tree;

import java.util.Arrays;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.function.DoubleStatistics;

import com.google.common.base.Stopwatch;

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
		float correlation;
		int x;
		int y;
		boolean update;

		public ClosestPair() {
			reset();
		}

		public void reset() {
			this.correlation = 0;
			this.x = 0;
			this.y = 0;
			this.update = false;
		}
	}

	private final ClusterTree tree;
	private final int samples;
	private float[][] similarities;
	/**
	 * Each node in the tree needs an unique number. Because of this we need a node counter
	 */
	private int iNodeCounter = (int) Math.floor(Integer.MAX_VALUE / 2);

	public TreeClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);

		this.tree = new ClusterTree();
		this.samples = va.size();
		this.similarities = new float[this.samples][this.samples];
	}

	/**
	 * Calculates the similarity matrix for a given set and given VAs
	 *
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities() {
		rename("Determine Similarities for " + getPerspectiveLabel() + " clustering");

		float[][] vectors = new float[va.size()][oppositeVA.size()];

		final int triggerEvery = va.size() / 100;
		int nextTrigger = triggerEvery;

		float max = Float.MIN_VALUE;

		for (int vaID_i = 0; vaID_i < va.size(); ++vaID_i) {
			Integer vaID = va.get(vaID_i);


			if (nextTrigger-- == 0) {
				progress(vaID_i / triggerEvery, false);
				nextTrigger = triggerEvery;

				eventListeners.processEvents();
				if (isClusteringCanceled) {
					progress(100, true);
					return -2;
				}
			}

			float[] d1 = vectors[vaID_i];

			fillVector(vectors[vaID_i], vaID);

			for (int vaID_j = 0; vaID_j < vaID_i; ++vaID_j) {
				// Integer vaID2 = va.get(vaID_j);
				float[] d2 = vectors[vaID_j];
				// as only the past already filled fillVector(dArInstance2, vaID2);

				float distance = distance(d1, d2);
				if (distance > max)
					max = distance;
				similarities[vaID_i][vaID_j] = distance;
			}
		}
		eventListeners.processEvents();
		if (isClusteringCanceled) {
			progress(100, true);
			return -2;
		}
		progressScaled(25);

		normalizeSimilarities(max);

		return 0;
	}

	private void fillVector(float[] v, Integer vaID2) {
		int isto = 0;
		for (Integer oppositeID2 : oppositeVA) {
			v[isto] = get(vaID2, oppositeID2);
			isto++;
		}
	}
	/**
	 * Function normalizes similarities between 0 and 1
	 */
	private void normalizeSimilarities(float max) {
		for (int i = 0; i < similarities.length; i++) {
			for (int j = 0; j < i; j++) {
				similarities[i][j] /= max;
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
	private static ClosestPair find_closest_pair(ClosestPair pair, int n, float[][] distmatrix) {
		if (pair == null)
			pair = new ClosestPair();
		else
			pair.reset();
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
	private Node[] pslcluster() {

		final int nnodes = samples - 1;
		final int[] vector = new int[nnodes];
		final float[] temp = new float[nnodes];
		final int[] index = new int[samples];
		Node[] result = new Node[samples];

		for (int i = 0; i < samples; i++) {
			result[i] = new Node();
			result[i].setCorrelation(Float.MAX_VALUE);
			for (int j = 0; j < i; j++)
				temp[j] = similarities[i][j];
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

		for (int i = 0; i < samples; i++)
			index[i] = i;

		for (int i = 0; i < nnodes; i++) {
			int j = result[i].getLeft();
			int k = vector[j];
			result[i].setLeft(index[j]);
			result[i].setRight(index[k]);
			index[k] = -i - 1;
		}

		// remove last
		Node[] result2 = Arrays.copyOf(result, nnodes);

		// set cluster result in Set

		return result2;
	}

	/**
	 * The palcluster routine performs clustering using pairwise average linking on the given distance matrix.
	 *
	 * warning: manipulates {@link #similarities}
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private Node[] palcluster() {

		int[] clusterid = new int[samples];
		int[] number = new int[samples];
		Node[] result = new Node[samples - 1];

		for (int j = 0; j < samples; j++) {
			number[j] = 1;
			clusterid[j] = j;
		}


		final ClosestPair pair = new ClosestPair();

		rename("Tree clustering of " + getPerspectiveLabel() + " in progress");
		final int triggerEvery = samples / 100;
		int nextTrigger = triggerEvery;

		for (int n = samples; n > 1; n--) {
			if (isClusteringCanceled) {
				progress(100);
				return null;
			}
			if (nextTrigger-- == 0) {
				progress((samples - n) / triggerEvery, false);
				nextTrigger = triggerEvery;
			}

			find_closest_pair(pair, n, similarities);

			int is = 1;
			int js = 0;

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
			result[samples - n] = node;

			// Fix the distances
			final int n_is = number[is];
			final int n_js = number[js];
			int sum = n_is + n_js;

			for (int j = 0; j < js; j++) {
				similarities[js][j] = similarities[is][j] * n_is + similarities[js][j] * n_js;
				similarities[js][j] /= sum;
			}
			for (int j = js + 1; j < is; j++) {
				similarities[j][js] = similarities[is][j] * n_is + similarities[j][js] * n_js;
				similarities[j][js] /= sum;
			}
			for (int j = is + 1; j < n; j++) {
				similarities[j][js] = similarities[j][is] * n_is + similarities[j][js] * n_js;
				similarities[j][js] /= sum;
			}

			for (int j = 0; j < is; j++)
				similarities[is][j] = similarities[n - 1][j];
			for (int j = is + 1; j < n - 1; j++)
				similarities[j][is] = similarities[n - 1][j];

			// Update number of elements in the clusters
			number[js] = sum;
			number[is] = number[n - 1];

			// Update clusterids
			clusterid[js] = n - samples - 1;
			clusterid[is] = clusterid[n - 1];
			eventListeners.processEvents();
		}

		// set cluster result in Set

		return result;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given distance
	 * matrix.
	 *
	 * warning: manipulates {@link #similarities}
	 *
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private Node[] pmlcluster() {

		int[] clusterid = new int[samples];
		Node[] result = new Node[samples - 1];

		// Setup a list specifying to which cluster a gene belongs
		for (int j = 0; j < samples; j++)
			clusterid[j] = j;
		final ClosestPair pair = new ClosestPair();

		rename("Tree clustering of " + getPerspectiveLabel() + " in progress");
		final int triggerEvery = samples / 100;
		int nextTrigger = triggerEvery;

		for (int n = samples; n > 1; n--) {
			if (isClusteringCanceled) {
				progress(100);
				return null;
			}
			if (nextTrigger-- == 0) {
				progress((samples - n) / triggerEvery, false);
				nextTrigger = triggerEvery;
			}

			find_closest_pair(pair, n, similarities);

			int is = 1;
			int js = 0;
			if (pair.update) {
				is = pair.x;
				js = pair.y;
			}

			// Fix the distances
			for (int j = 0; j < js; j++)
				similarities[js][j] = Math.max(similarities[is][j], similarities[js][j]);
			for (int j = js + 1; j < is; j++)
				similarities[j][js] = Math.max(similarities[is][j], similarities[j][js]);
			for (int j = is + 1; j < n; j++)
				similarities[j][js] = Math.max(similarities[j][is], similarities[j][js]);

			for (int j = 0; j < is; j++)
				similarities[is][j] = similarities[n - 1][j];
			for (int j = is + 1; j < n - 1; j++)
				similarities[j][is] = similarities[n - 1][j];

			// Update clusterids
			Node node = new Node();

			node.setCorrelation(pair.correlation);
			node.setLeft(clusterid[is]);
			node.setRight(clusterid[js]);

			result[samples - n] = node;

			clusterid[js] = n - samples - 1;
			clusterid[is] = clusterid[n - 1];
			eventListeners.processEvents();
		}

		// set cluster result in Set

		return result;
	}

	/**
	 * Helper function providing unique node numbers for all nodes in the tree.
	 *
	 * @return number of current node
	 */
	private int getNextNodeId() {
		return iNodeCounter++;
	}

	protected PerspectiveInitializationData convert(Node[] result) {
		ClusterNode node = new ClusterNode(tree, "Root", getNextNodeId(), true, -1);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1);

		calculateClusterAveragesRecursive(node);

		progressScaled(50);

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(tree);
		return tempResult;
	}

	private float[] calculateClusterAveragesRecursive(ClusterNode node) {
		float[] values;
		if (tree.hasChildren(node)) {
			int numberOfChildren = tree.getChildren(node).size();
			int numberOfElements = oppositeVA.size();
			float[][] tempValues;

			tempValues = new float[numberOfChildren][numberOfElements];

			int cnt = 0;

			for (ClusterNode currentNode : tree.getChildren(node)) {
				tempValues[cnt] = calculateClusterAveragesRecursive(currentNode);
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
			values = new float[oppositeVA.size()];
			int isto = 0;
			for (Integer oppositeID : oppositeVA) {
				values[isto] = table.getDataDomain().getNormalizedValue(va.getIdType(), node.getLeafID(),
						oppositeVA.getIdType(), oppositeID);
				isto++;
			}
		}
		DoubleStatistics stats = DoubleStatistics.of(values);

		float averageExpressionvalue = (float) stats.getMean();
		float deviation = (float) stats.getSd();

		node.setAverageExpressionValue(averageExpressionvalue);
		// Setting an float array for the representative element in each node causes a very big xml-file when
		// exporting the tree
		// node.setRepresentativeElement(fArExpressionValues);
		node.setStandardDeviation(deviation);

		return values;
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
		int r = 0;

		Stopwatch w = new Stopwatch().start();
		r = determineSimilarities();
		System.out.println("determine similarties: " + w);
		w.stop().reset();
		if (r < 0) {
			progress(100);
			return null;
		}

		TreeClusterConfiguration tConfig = (TreeClusterConfiguration) config.getClusterAlgorithmConfiguration();

		Node[] result;

		w.start();
		switch (tConfig.getTreeClustererAlgo()) {
		case COMPLETE_LINKAGE:
			result = pmlcluster();
			System.out.println("pmlcluster: " + w);
			break;
		case AVERAGE_LINKAGE:

			result = palcluster();
			System.out.println("palcluster: " + w);
			break;
		case SINGLE_LINKAGE:
			result = pslcluster();
			System.out.println("pslcluster: " + w);
			break;
		default:
			throw new IllegalStateException("Unkonwn cluster type: " + tConfig.getTreeClustererAlgo());
		}

		w.reset().start();
		PerspectiveInitializationData p = convert(result);
		System.out.println("convert: " + w);
		return p;
	}
}
