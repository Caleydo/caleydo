package org.caleydo.core.data.mapping;

import org.caleydo.core.data.collection.EStorageType;

/**
 * Enum that defines all genome data types that could possibly be loaded to the
 * system.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public enum EGenomeIdType
{

	GENE_NAME(EStorageType.STRING, "gene-name", "gene pseudo name"),

	GENE_SYMBOL(EStorageType.STRING, "gene-symbol", "gene pseudo symbol"),

	BIOCARTA_GENE_ID(EStorageType.STRING, "biocarta geneid", "biocarta geneid"),

	DAVID(EStorageType.INT, "david", "david-id"),

	REFSEQ_MRNA(EStorageType.INT, "refseq-mrna", "refseq-mrna-id"),

	ENTREZ_GENE_ID(EStorageType.INT, "entrez-gene-id", "entrez-gene-id"),

	EXPRESSION_STORAGE_ID(EStorageType.INT, "expression-storage-id", "expression-storage-id");

	private final EStorageType storageType;

	private final String sName;

	private final String sDescription;

	/**
	 * Constructor
	 * 
	 * @param storageType
	 *            the type of the storage
	 * @param sName
	 * @param sDesciption
	 */
	private EGenomeIdType(final EStorageType storageType, final String sName,
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
