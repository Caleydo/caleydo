package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;

public class PerspectiveInitializationData {
	private ArrayList<Integer> indices;
	/** indices of examples (cluster centers) */
	private ArrayList<Integer> sampleElements;
	/** number of elements per cluster */
	private ArrayList<Integer> clusterSizes;

	private ClusterTree tree;

	public void setIndices(ArrayList<Integer> indices) {
		this.indices = indices;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public void setClusterSizes(ArrayList<Integer> clusterSizes) {
		this.clusterSizes = clusterSizes;
	}

	public ArrayList<Integer> getClusterSizes() {
		return clusterSizes;
	}

	public void setSampleElements(ArrayList<Integer> sampleElements) {
		this.sampleElements = sampleElements;
	}

	public ArrayList<Integer> getSampleElements() {
		return sampleElements;
	}

	/**
	 * @param tree
	 *            setter, see {@link #tree}
	 */
	public void setTree(ClusterTree tree) {
		this.tree = tree;
	}

	/**
	 * @return the tree, see {@link #tree}
	 */
	public ClusterTree getTree() {
		return tree;
	}
}
