/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.mapping.GenomeMappingDataType;

/**
 * @author Michael Kalkusch
 *
 */
public enum GenomeMappingType
{

	ACCESSION_2_NCBI_GENEID (GenomeMappingDataType.INT2INT),
	ACCESSION_2_NCBI_GENEID_REVERSE (GenomeMappingDataType.INT2INT),
	
	KEGG_2_ENZYMEID (GenomeMappingDataType.MULTI_INT2INT),
	KEGG_2_ENZYMEID_R (GenomeMappingDataType.MULTI_INT2INT),
	
	PATHWAY_2_NCBI_GENEID  (GenomeMappingDataType.INT2INT),
	MICROARRAY_2_NCBI_GENEID (GenomeMappingDataType.STRING2INT),
	ENZYME_CODE_2_ENZYME  (GenomeMappingDataType.STRING2INT),
	
	ENZYME_CODE_2_ENZYME_R (GenomeMappingDataType.INT2STRING),
	
	Z_NO_MAPPING(GenomeMappingDataType.NONE);
	
	
	
	private boolean bIsMultiMap;
	
	private GenomeMappingDataType enumDataMappingType;
	
	
	private GenomeMappingType( GenomeMappingDataType setDataMappingType ) {
		enumDataMappingType = setDataMappingType;
		
		bIsMultiMap = setDataMappingType.isMultiMapUsed();
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
	 * Always FALSE this is a MultiMap, else TRUE indicates Map <Integer,String> and FALSE indicats Map <String,Int>
	 * 
	 * @return
	 */
	public GenomeMappingDataType getDataMapppingType() {
		return enumDataMappingType;
	}
}
