package org.caleydo.core.util.clusterer;

public enum EClustererAlgo {

	// hierarchical clusterer
	TREE_CLUSTERER("Tree Clusterer"),
	COBWEB_CLUSTERER("Cobweb Hierarchical Clusterer"),

	// partitional clusterer
	AFFINITY_PROPAGATION("Affinity Propagation Clusterer"),
	KMEANS_CLUSTERER("K-Means Clusterer"),

	// custom clusterer
	ALPHABETICAL("Alphabetical"),
	OTHER("Other");

	private String sName;

	private EClustererAlgo(String sName) {
		this.sName = sName;
	}

	public String getName() {
		return sName;
	}
}
