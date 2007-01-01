/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.mapping.GenomeMappingDataType;

/**
 * Class stores all kind of mappings and 
 * the corresponding data types.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GenomeMappingType {

	ACCESSION_NUMBER_2_GENE_ID (GenomeMappingDataType.INT2INT),
	ACCESSION_NUMBER_2_GENE_ID_REVERSE (GenomeMappingDataType.INT2INT),
	
	ENZYME_CODE_2_ENZYME_ID (GenomeMappingDataType.STRING2INT),
	ENZYME_CODE_2_ENZYME_ID_REVERSE (GenomeMappingDataType.INT2STRING),
	
	ACCESSION_NUMBER_2_ENZYME_ID (GenomeMappingDataType.MULTI_INT2INT),
	ACCESSION_NUMBER_2_ENZYME_ID_REVERSE (GenomeMappingDataType.MULTI_INT2INT);

//	GENEID_2_ENZYMEID (GenomeMappingDataType.MULTI_INT2INT),
//	GENEID_2_ENZYMEID_REVERSE (GenomeMappingDataType.MULTI_INT2INT);
	
//	PATHWAY_2_NCBI_GENEID  (GenomeMappingDataType.INT2INT),
//	MICROARRAY_2_NCBI_GENEID (GenomeMappingDataType.STRING2INT),
//	ENZYME_CODE_2_ENZYME  (GenomeMappingDataType.STRING2INT),
//	
//	ENZYME_CODE_2_ENZYME_R (GenomeMappingDataType.INT2STRING),
//	
//	Z_NO_MAPPING(GenomeMappingDataType.NONE);
	
	private boolean bIsMultiMap;
	
	private GenomeMappingDataType enumDataMappingType;
	
	private GenomeMappingType( GenomeMappingDataType refDataMappingType ) {
		
		enumDataMappingType = refDataMappingType;
		
		bIsMultiMap = refDataMappingType.isMultiMapUsed();
	}

	/**
	 * TRUE if this is a MultiMap <Integer,ArrayList<Integer>>
	 * FALSE if it is a HashMap.
	 * Two types of HashMaps are in use:  
	 * <String,Integer> 
	 * and <Integer,String> see isReversHashMap()
	 * 
	 * @return
	 */
	public boolean isMultiMap() {
		return bIsMultiMap;
	}
	
	/**
	 * Always FALSE this is a MultiMap, 
	 * else TRUE indicates Map <Integer,String> and FALSE indicats Map <String,Int>
	 * 
	 * @return
	 */
	public GenomeMappingDataType getDataMapppingType() {
		
		return enumDataMappingType;
	}
}
