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
	GENE_NAME(EIDCategory.GENE, EStorageType.STRING, "Gene Name", "gene pseudo name"),
	/** The short name of the gene */
	GENE_SYMBOL(EIDCategory.GENE, EStorageType.STRING, "Gene Symbol", "gene pseudo symbol"),
	BIOCARTA_GENE_ID(EIDCategory.GENE, EStorageType.STRING, "BioCarta Gene ID", "biocarta geneid"),
	DAVID(EIDCategory.GENE, EStorageType.INT, "David ID", "David ID"),
	REFSEQ_MRNA(EIDCategory.GENE, EStorageType.STRING, "RefSeq MRNA ID", "refseq-mrna-id"),
	REFSEQ_MRNA_INT(
		EIDCategory.GENE,
		EStorageType.INT,
		"RefSeq MRNA int",
		"refseq-mrna-id int for internal usage in event system"),
	ENTREZ_GENE_ID(EIDCategory.GENE, EStorageType.INT, "Entrez Gene ID", "entrez-gene-id"),
	// FIXME: Make this general! Needed for Asslaber data
	OLIGO(EIDCategory.GENE, EStorageType.STRING, "Oligo ID", "oligo-id"),
	EXPRESSION_INDEX(
		EIDCategory.GENE,
		EStorageType.INT,
		"Expression Index",
		"The index of a gene expression value"),
	// EXPRESSION_EXPERIMENT(EStorageType.INT, "expression-experiment",
	// "A expression experiment containing gene expression values"),
	PATHWAY_VERTEX(EIDCategory.GENE, EStorageType.INT, "Pathway vertex ID", "The internal ID of the pathway vertex"),
	PATHWAY(EIDCategory.PATHWAY, EStorageType.INT, "Pathway ID", "The internal ID of a pathway"),
	EXPERIMENT(
		EIDCategory.EXPERIMENT,
		EStorageType.STRING,
		"Experiment ID",
		"The ID of the experiments"),
	EXPERIMENT_INDEX(EIDCategory.EXPERIMENT, EStorageType.INT, "Experiment Index", "The experiment index"),

	CELL_COMPONENT(EIDCategory.OTHER, EStorageType.STRING, "GO Cell Component", "The GO cell component"),

	CLUSTER_NUMBER(
		EIDCategory.OTHER,
		EStorageType.INT,
		"Cluster Number",
		"The cluster number of clusters and genes"),

	/**
	 * Used for external IDs that are read from arbitrary CSV file which should be used for linking between
	 * the views (e.g. use case cytokine data from Leipzig)
	 */
	UNSPECIFIED(
		EIDCategory.OTHER,
		EStorageType.STRING,
		"Unknown External Identifier",
		"unknown external identifier");

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

	public EIDCategory getCategory() {
		return category;
	}
}
