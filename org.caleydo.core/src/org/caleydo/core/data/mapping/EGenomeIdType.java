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

	ACCESSION(StorageType.INT, 
			EGenomeMappingType.ACCESSION_CODE_2_ACCESSION,
			"acc","accession"),
	ACCESSION_CODE    (StorageType.STRING,
			EGenomeMappingType.ACCESSION_CODE_2_ACCESSION,
			"acc","accession_code"),
			
	ENZYME(StorageType.INT,
			EGenomeMappingType.ENZYME_CODE_2_ENZYME,
			"ec","EC_number"),
	ENZYME_CODE(StorageType.STRING,
			EGenomeMappingType.ENZYME_CODE_2_ENZYME,
			"ec","EC_number as String"),
			
	METABOLIT(StorageType.INT,
			EGenomeMappingType.METABOLIT_CODE_2_METABOLIT,
			"ko","methobliot"),
	METABOLIT_CODE(StorageType.STRING,
			EGenomeMappingType.METABOLIT_CODE_2_METABOLIT,
			"ko","methobliot"),
			
	MICROARRAY(StorageType.INT,
			EGenomeMappingType.MICROARRAY_CODE_2_MICROARRAY,
			"IMAGp","Microarray LUT"),
	MICROARRAY_CODE   (StorageType.STRING,
			EGenomeMappingType.MICROARRAY_CODE_2_MICROARRAY,
			"IMAGp","Microarray LUT"),
	MICROARRAY_EXPRESSION   (StorageType.INT,
			EGenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION,
			"expression","Microarray Expression Value"),
			
	NCBI_GENEID(StorageType.INT,
			EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID,
			"ncbi-geneid","ncbi-geneid"),
	NCBI_GENEID_CODE  (StorageType.STRING,
			EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID,
			"ncbi-geneid","ncbi-geneid"),
	
	PATHWAY(StorageType.INT,
			EGenomeMappingType.NON_MAPPING,
			"path","pathway-id"),		
	PATHWAY_CODE      (StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"path","pathway-id"),
			
	GENE_NAME(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"gene-name","gene pseudo name"),	
	GENE_SHORT_NAME(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"gene-short-name","gene pseudo short name"),
			
	BIOCARTA_GENEID(StorageType.STRING,
			EGenomeMappingType.NON_MAPPING,
			"biocarta geneid","biocarta geneid"),			
					
	KEGG(StorageType.INT,
			EGenomeMappingType.KEGG_CODE_2_KEGG,
			"kegg","kegg-id"),
		
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
