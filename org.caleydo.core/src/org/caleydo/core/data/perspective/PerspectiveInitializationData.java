package org.caleydo.core.data.perspective;

import java.util.List;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.virtualarray.VirtualArray;

public class PerspectiveInitializationData {
	/**
	 * Indices of elements that represent a cluster (cluster centers). Used for initialization to create a
	 * sample element for every group.
	 */
	private List<Integer> indices;

	private List<Integer> sampleElements;
	/**
	 * The sizes of the clusters in a list sorted so that combined with the {@link VirtualArray} the clusters
	 * are uniquely identified. Used for initialization.
	 */
	private List<Integer> clusterSizes;

	/**
	 * The tree that shows relation between the elements in the {@link VirtualArray}. Always needs to be in
	 * sync with the VAs.
	 */
	private ClusterTree tree;
	/**
	 * The root of the {@link #tree} which must not be the actual root but can be any node.
	 */
	private ClusterNode rootNode;

	private VirtualArray<?, ?, ?> virtualArray;

	/**
	 * Initialize from index list only
	 * 
	 * @param indices
	 */
	public void setData(List<Integer> indices) {
		if (indices == null)
			throw new IllegalArgumentException("Argument indices was null");
		this.indices = indices;
	}

	/**
	 * Initialize with index list, grouping and sample elements
	 * 
	 * @param indices
	 * @param clusterSizes
	 * @param sampleElements
	 */
	public void setData(List<Integer> indices, List<Integer> clusterSizes, List<Integer> sampleElements) {
		if (indices == null || clusterSizes == null || sampleElements == null)
			throw new IllegalArgumentException("An argument was null. Indices: " + indices
				+ " clusterSizes: " + clusterSizes + " sampleElements: " + sampleElements);

		this.indices = indices;
		this.clusterSizes = clusterSizes;
		this.sampleElements = sampleElements;
	}

	/**
	 * Initialize with tree only
	 * 
	 * @param tree
	 */
	public void setData(ClusterTree tree) {
		if (tree == null)
			throw new IllegalArgumentException("Tree was null");
		this.tree = tree;
	}

	/**
	 * Initialize with tree and an artificial root node (i.e., only the sub-tree starting at the artificial
	 * root will be used.
	 * 
	 * @param tree
	 * @param rootNode
	 */
	public void setData(ClusterTree tree, ClusterNode rootNode) {
		if (tree == null)
			throw new IllegalArgumentException("Tree was null");
		if (rootNode == null)
			throw new IllegalArgumentException("RootNode was null");
		this.tree = tree;
		this.rootNode = rootNode;
	}

	public void setData(VirtualArray<?, ?, ?> virtualArray) {
		if (virtualArray == null)
			throw new IllegalArgumentException("VA was null");
		this.virtualArray = virtualArray;
	}

	/**
	 * @return the virtualArray, see {@link #virtualArray}
	 */
	public VirtualArray<?, ?, ?> getVirtualArray() {
		return virtualArray;
	}
	
	public List<Integer> getIndices() {
		return indices;
	}

	public List<Integer> getClusterSizes() {
		return clusterSizes;
	}

	public List<Integer> getSampleElements() {
		return sampleElements;
	}

	/**
	 * @return the tree, see {@link #tree}
	 */
	public ClusterTree getTree() {
		return tree;
	}

	/**
	 * @return the rootNode, see {@link #rootNode}
	 */
	public ClusterNode getRootNode() {
		return rootNode;
	}
}
