package org.caleydo.core.data.mapping;

import org.caleydo.core.data.collection.EStorageType;

/**
 * Enum that defines all genome data types that could possibly be loaded to the
 * system.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum EIDType
{

	GENE_NAME(EStorageType.STRING, "gene-name", "gene pseudo name"),

	GENE_SYMBOL(EStorageType.STRING, "gene-symbol", "gene pseudo symbol"),

	BIOCARTA_GENE_ID(EStorageType.STRING, "biocarta geneid", "biocarta geneid"),

	DAVID(EStorageType.INT, "david", "david-id"),

	REFSEQ_MRNA(EStorageType.STRING, "refseq-mrna", "refseq-mrna-id"),

	ENTREZ_GENE_ID(EStorageType.INT, "entrez-gene-id", "entrez-gene-id"),

	EXPRESSION_INDEX(EStorageType.INT, "expression-index", "The index of a gene expression value"),

	EXPRESSION_EXPERIMENT(EStorageType.INT, "expression-experiment", "A expression experiment containing gene expression values"),

	PATHWAY_VERTEX(EStorageType.INT, "pathway vertex id", "The id of the pathway vertex"),

	PATHWAY(EStorageType.INT, "pathway id", "The ID of a pathway"),

	CLINICAL_ID(EStorageType.INT, "clinical id", "The ID in the clinical data file");

	private final EStorageType storageType;

	private final String sName;

	private final String sDescription;

	/**
	 * Constructor
	 * 
	 * @param storageType the type of the storage
	 * @param sName
	 * @param sDesciption
	 */
	private EIDType(final EStorageType storageType, final String sName,
			final String sDesciption)
	{

		this.sName = sName;
		this.sDescription = sDesciption;
		this.storageType = storageType;
	}

	/**
	 * @return the entities description.
	 */
	public String getDescription()
	{

		return sDescription;
	}

	/**
	 * Define type of storage required for this type.
	 * 
	 * @return type of storage needed
	 */
	public EStorageType getStorageType()
	{

		return storageType;
	}

	/**
	 * @return the entities name
	 */
	public String getName()
	{

		return sName;
	}
}
