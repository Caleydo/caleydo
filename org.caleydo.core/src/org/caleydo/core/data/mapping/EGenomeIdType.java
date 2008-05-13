package org.caleydo.core.data.mapping;

import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.util.ICaleydoDefaultType;

/**
 * Enum that defines all genome data types that could possibly be loaded
 * to the system.
 * 
 * Note: *_CODE indicates, that it is a String that is mapped to an integer internally.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum EGenomeIdType
implements ICaleydoDefaultType <EGenomeIdType> {
			
	GENE_NAME(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"gene-name","gene pseudo name"),	
	GENE_SHORT_NAME(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"gene-short-name","gene pseudo short name"),
			
	BIOCARTA_GENE_ID(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"biocarta geneid","biocarta geneid"),
		
	DAVID(StorageType.INT,
			EGenomeMappingType.NON_MAPPING,
			"david","david-id"),
			
	REFSEQ_MRNA(StorageType.INT,
			EGenomeMappingType.NON_MAPPING,
			"refseq-mrna","refseq-mrna-id"),
			
	ENTREZ_GENE_ID(StorageType.INT,
			EGenomeMappingType.NON_MAPPING,
			"entrez-gene-id","entrez-gene-id"),
				
	EXPRESSION_STORAGE_ID(StorageType.INT,
			EGenomeMappingType.NON_MAPPING,
			"expression-storage-id", "expression-storage-id"),			
			
	NONE(StorageType.NONE,
			EGenomeMappingType.NON_MAPPING,
			"none","none");
	
	private final StorageType storageType;
	
	private final String sName;
	
	private final String sDesciption;
	
	private final EGenomeMappingType basicConversion;
	
	private EGenomeIdType( final StorageType setStorageType, 
			final EGenomeMappingType setBasicConversion,
			final String name, 
			final String desciption ) {
		sName = name;
		sDesciption = desciption;
		storageType = setStorageType;
		basicConversion = setBasicConversion;
	}
	
	/**
	 * @return the entitis description.
	 */
	public String getDesciption() {
	
		return sDesciption;
	}

	/**
	 * Define type of storage required for this type.
	 * 
	 * @return type of storage needed
	 */
	public StorageType getStorageType() {
	
		return storageType;
	}
	
	public EGenomeMappingType getBasicConversion() {
		return basicConversion;
	}
	
	/**
	 * @return the entities name
	 */
	public String getName() {
	
		return sName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public EGenomeIdType getTypeDefault() {

		return EGenomeIdType.NONE;
	}
}
