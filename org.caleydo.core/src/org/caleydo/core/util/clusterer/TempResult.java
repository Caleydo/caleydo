package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

public class TempResult {
	ArrayList<Integer> indices;
	/** indices of examples (cluster centers) */
	ArrayList<Integer> sampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> clusterSizes;

	Tree<ClusterNode> tree;
}
