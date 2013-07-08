/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.perspective.variable;

import java.util.List;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.virtualarray.VirtualArray;

public class PerspectiveInitializationData {

	/** A label of the perspective */
	private String label;
	/**
	 * Indices of elements that represent a group/cluster (cluster centers).
	 * Used for initialization to create a sample element for every group.
	 */
	private List<Integer> indices;

	private List<Integer> sampleElements;
	/**
	 * The sizes of the group in a list sorted so that combined with the
	 * {@link VirtualArray} the clusters are uniquely identified. Used for
	 * initialization.
	 */
	private List<Integer> groupSizes;

	/**
	 * The names of the cluster. May be null
	 */
	private List<String> groupNames;

	/**
	 * The tree that shows relation between the elements in the
	 * {@link VirtualArray}. Always needs to be in sync with the VAs.
	 */
	private ClusterTree tree;
	/**
	 * The root of the {@link #tree} which must not be the actual root but can
	 * be any node.
	 */
	private ClusterNode rootNode;

	private VirtualArray virtualArray;

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
	 * @param groupSizes
	 * @param sampleElements
	 */
	public void setData(List<Integer> indices, List<Integer> groupSizes,
			List<Integer> sampleElements) {
		if (indices == null || groupSizes == null || sampleElements == null)
			throw new IllegalArgumentException("An argument was null. Indices: "
					+ indices + " clusterSizes: " + groupSizes + " sampleElements: "
					+ sampleElements);

		this.indices = indices;
		this.groupSizes = groupSizes;
		this.sampleElements = sampleElements;
	}

	/**
	 * Same as {@link #setData(List, List, List)} but with additional
	 * clusterNames
	 *
	 * @param indices
	 * @param groupSizes
	 * @param sampleElements
	 * @param groupNames
	 */
	public void setData(List<Integer> indices, List<Integer> groupSizes,
			List<Integer> sampleElements, List<String> groupNames) {
		if (indices == null || groupSizes == null || sampleElements == null)
			throw new IllegalArgumentException("An argument was null. Indices: "
					+ indices + " groupSizes: " + groupSizes + " sampleElements: "
					+ sampleElements);

		this.indices = indices;
		this.groupSizes = groupSizes;
		this.sampleElements = sampleElements;
		this.groupNames = groupNames;
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
	 * Initialize with tree and an artificial root node (i.e., only the sub-tree
	 * starting at the artificial root will be used.
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

	/**
	 * Initialize with an existing virtual array. The virtual array must have
	 *
	 * @param virtualArray
	 */
	public void setData(VirtualArray virtualArray) {
		if (virtualArray == null)
			throw new IllegalArgumentException("VA was null");
		if (virtualArray.getGroupList() == null) {
			throw new IllegalStateException("Group List of virtual array was null");
		}
		this.virtualArray = virtualArray;
	}

	/**
	 * @return the virtualArray, see {@link #virtualArray}
	 */
	public VirtualArray getVirtualArray() {
		return virtualArray;
	}

	/**
	 * @return the indices, see {@link #indices}
	 */
	public List<Integer> getIndices() {
		return indices;
	}

	/**
	 * @return the groupSizes, see {@link #groupSizes}
	 */
	public List<Integer> getGroupSizes() {
		return groupSizes;
	}

	/**
	 * @return the groupNames, see {@link #groupNames}
	 */
	public List<String> getGroupNames() {
		return groupNames;
	}

	/**
	 * @return the sampleElements, see {@link #sampleElements}
	 */
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

	/**
	 * @return the label, see {@link #label}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            setter, see {@link #label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
