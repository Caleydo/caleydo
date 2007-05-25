/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.collection.StorageType;
import cerberus.data.mapping.GenomeMappingType;

/**
 * Note: *_CODE indicates, that is it a String that is mapped to an integer internally.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GenomeIdType
{

	ACCESSION    (StorageType.INT, 
			GenomeMappingType.ACCESSION_CODE_2_ACCESSION,
			"acc","accession"),
	ACCESSION_CODE    (StorageType.STRING,
			GenomeMappingType.ACCESSION_CODE_2_ACCESSION,
			"acc","accession_code"),
			
	ENZYME       (StorageType.INT,
			GenomeMappingType.ENZYME_CODE_2_ENZYME,
			"ec","EC_number"),
	ENZYME_CODE  (StorageType.STRING,
			GenomeMappingType.ENZYME_CODE_2_ENZYME,
			"ec","EC_number as String"),
			
	METABOLIT   (StorageType.INT,
			GenomeMappingType.METABOLIT_CODE_2_METABOLIT,
			"ko","methobliot"),
	METABOLIT_CODE   (StorageType.STRING,
			GenomeMappingType.METABOLIT_CODE_2_METABOLIT,
			"ko","methobliot"),
			
	MICROARRAY   (StorageType.INT,
			GenomeMappingType.MICROARRAY_CODE_2_MICROARRAY,
			"IMAGp","Microarray LUT"),
	MICROARRAY_CODE   (StorageType.STRING,
			GenomeMappingType.MICROARRAY_CODE_2_MICROARRAY,
			"IMAGp","Microarray LUT"),
	MICROARRAY_EXPRESSION   (StorageType.INT,
			GenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION,
			"expression","Microarray Expression Value"),
			
			
			
	NCBI_GENEID  (StorageType.INT,
			GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID,
			"ncbi-geneid","ncbi-geneid"),
			
	NCBI_GENEID_CODE  (StorageType.STRING,
			GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID,
			"ncbi-geneid","ncbi-geneid"),
		
//	/**
//	 * @deprecated use NCBI_GENEID instead
//	 */
//	NCBI_GI      (StorageType.INT,
//			GenomeMappingType.NON_MAPPING,
//			"ncbi-gi","ncbi-gi"),
	
	PATHWAY      (StorageType.INT,
			GenomeMappingType.NON_MAPPING,
			"path","pathway-id"),
			
	PATHWAY_CODE      (StorageType.STRING,
			GenomeMappingType.NON_MAPPING,
			"path","pathway-id"),
			
	GENE_NAME      (StorageType.STRING,
			GenomeMappingType.NON_MAPPING,
			"gene-name","gene pseudo name"),	
			
	/**
	 * use NCBI_GENEID instead
	 */
	KEGG      (StorageType.INT,
			GenomeMappingType.KEGG_CODE_2_KEGG,
			"kegg","kegg-id"),
			
	NONE         (StorageType.NONE,
			GenomeMappingType.NON_MAPPING,
			"none","none");
	
	
	private final StorageType storageType;
	
	private final String sName;
	
	private final String sDesciption;
	
	private final GenomeMappingType basicConversion;
	
	private GenomeIdType( final StorageType setStorageType, 
			final GenomeMappingType setBasicConversion,
			final String name, 
			final String desciption ) {
		sName = name;
		sDesciption = desciption;
		storageType = setStorageType;
		basicConversion = setBasicConversion;
	}
	
	/**
	 * @return Returns the desciption.
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
	
	public GenomeMappingType getBasicConversion() {
		return basicConversion;
	}
	
//	/**
//	 * @param desciption The desciption to set.
//	 */
//	public void setDesciption(String desciption) {
//	
//		sDesciption = desciption;
//	}

	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
	
		return sName;
	}

	
//	/**
//	 * @param name The name to set.
//	 */
//	public void setName(String name) {
//	
//		sName = name;
//	}
}
