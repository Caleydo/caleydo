package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;

public class TempResult {
	ArrayList<Integer> indices;
	/** indices of examples (cluster centers) */
	ArrayList<Integer> sampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> clusterSizes;

	ClusterTree tree;

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
}
