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
	LOCATION_SHALLOW("Location Shallow"),
	LOCATION_DEEP("Location Deep"),
	OBJECT_TYPE_SHALLOW("Object Type Shallow"),
	OBJECT_TYPE_DEEP("Object Type Deep"),
	OTHER("Other");

	private String sName;

	private EClustererAlgo(String sName) {
		this.sName = sName;
	}

	public String getName() {
		return sName;
	}
}
