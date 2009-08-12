package org.caleydo.core.data.mapping;

import org.caleydo.core.data.collection.EStorageType;

/**
 * Enum that defines all genome data types that could possibly be loaded to the system.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum EIDType {

	// Genes
	/** The full name of the gene */
	GENE_NAME(EIDCategory.GENE, EStorageType.STRING, "gene-name", "gene pseudo name"),
	/** The short name of the gene */
	GENE_SYMBOL(EIDCategory.GENE, EStorageType.STRING, "gene-symbol", "gene pseudo symbol"),
	BIOCARTA_GENE_ID(EIDCategory.GENE, EStorageType.STRING, "biocarta geneid", "biocarta geneid"),
	DAVID(EIDCategory.GENE, EStorageType.INT, "david", "david-id"),
	REFSEQ_MRNA(EIDCategory.GENE, EStorageType.STRING, "refseq-mrna", "refseq-mrna-id"),
	REFSEQ_MRNA_INT(
		EIDCategory.GENE,
		EStorageType.INT,
		"refseq-mrna int",
		"refseq-mrna-id int for internal usage in event system"),
	ENTREZ_GENE_ID(EIDCategory.GENE, EStorageType.INT, "entrez-gene-id", "entrez-gene-id"),
	// FIXME: Make this general! Needed for Asslaber data
	OLIGO(EIDCategory.GENE, EStorageType.STRING, "oligo", "oligo-id"),
	EXPRESSION_INDEX(EIDCategory.GENE, EStorageType.INT, "expression-index", "The index of a gene expression value"),
	// EXPRESSION_EXPERIMENT(EStorageType.INT, "expression-experiment",
	// "A expression experiment containing gene expression values"),
	PATHWAY_VERTEX(EIDCategory.GENE, EStorageType.INT, "pathway vertex id", "The id of the pathway vertex"),

	PATHWAY(EIDCategory.PATHWAY, EStorageType.INT, "pathway id", "The ID of a pathway"),
	EXPERIMENT(EIDCategory.EXPERIMENT,  EStorageType.STRING, "experiment id", "The ID that connects clinical with microarray data"),
	EXPERIMENT_INDEX(EIDCategory.EXPERIMENT, EStorageType.INT, "experiment index", "The experiment index"),

	CELL_COMPONENT(EIDCategory.OTHER, EStorageType.STRING, "GO cell component", "The GO cell component"),

	CLUSTER_NUMBER(EIDCategory.OTHER, EStorageType.INT, "cluster-number", "The cluster number of clusters and genes"),

	/**
	 * Used for external IDs that are read from arbitrary CSV file which should be used for linking between
	 * the views (e.g. use case cytokine data from Leipzig)
	 */
	UNSPECIFIED(EIDCategory.OTHER, EStorageType.STRING, "unknown external identifier", "unknown external identifier");

	private final EStorageType storageType;

	private final String sName;

	private final String sDescription;
	
	private final EIDCategory category;

	/**
	 * Constructor
	 * 
	 * @param storageType
	 *            the type of the storage
	 * @param sName
	 * @param sDesciption
	 */
	private EIDType(final EIDCategory category, final EStorageType storageType, final String sName,
		final String sDesciption) {

		this.sName = sName;
		this.sDescription = sDesciption;
		this.storageType = storageType;
		this.category = category; 
	}

	/**
	 * @return the entities description.
	 */
	public String getDescription() {

		return sDescription;
	}

	/**
	 * Define type of storage required for this type.
	 * 
	 * @return type of storage needed
	 */
	public EStorageType getStorageType() {

		return storageType;
	}

	/**
	 * @return the entities name
	 */
	public String getName() {

		return sName;
	}
	
	public EIDCategory getCategory(){
		return category;
	}
}
