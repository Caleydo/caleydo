package org.caleydo.core.util.clusterer;

public enum EClustererType {

	GENE_CLUSTERING,
	EXPERIMENTS_CLUSTERING,
	/**
	 * Pseudo biclustering: first experiments afterwards genes will be clustered
	 */
	BI_CLUSTERING;

}
