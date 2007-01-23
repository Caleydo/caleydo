/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeIdType;

/**
 * @author Michael Kalkusch
 *
 */
public enum GenomeMappingType
{

	ACCESSION_CODE_2_ACCESSION (
			GenomeIdType.ACCESSION_CODE,
			GenomeIdType.ACCESSION, 
			GenomeMappingDataType.STRING2INT),
	
	ACCESSION_2_NCBI_GENEID (
			GenomeIdType.ACCESSION,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT),
			
	ACCESSION_2_NCBI_GENEID_REVERSE (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.INT2INT),
	
	KEGG_2_ENZYMEID (
			GenomeIdType.KEGG_ID,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.MULTI_INT2INT),
			
	KEGG_2_ENZYMEID_R (
			GenomeIdType.ENZYME,
			GenomeIdType.KEGG_ID,
			GenomeMappingDataType.MULTI_INT2INT),
	
	NCBI_GENEID_2_KEGG  (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.KEGG_ID,
			GenomeMappingDataType.INT2INT),
	
	PATHWAY_2_NCBI_GENEID  (
			GenomeIdType.PATHWAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT),
			
	MICROARRAY_2_NCBI_GENEID (GenomeIdType.MICROARRAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT),
			
	MICROARRAY_2_ACCESSION (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.STRING2STRING),
	
	MICROARRAY_2_ACCESSION_STRING( 
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.MULTI_STRING2STRING),
	
	ENZYME_CODE_2_ENZYME  (
			GenomeIdType.ENZYME_CODE,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.STRING2INT),
	
	ENZYME_CODE_2_ENZYME_R (
			GenomeIdType.ENZYME,
			GenomeIdType.ENZYME_CODE,
			GenomeMappingDataType.INT2STRING),
	
	NON_MAPPING(GenomeIdType.NONE,
			GenomeIdType.NONE,
			GenomeMappingDataType.NONE);
	
	
	private final GenomeIdType originType;
	
	private final GenomeIdType targetType;
	
	private final boolean bIsMultiMap;
	
	private final GenomeMappingDataType enumDataMappingType;
	
	
	private GenomeMappingType( GenomeIdType destination,
			GenomeIdType target,
			GenomeMappingDataType setDataMappingType ) {
		enumDataMappingType = setDataMappingType;
		
		originType = destination;
		targetType = target;
		
		bIsMultiMap = setDataMappingType.isMultiMapUsed();
	}
	
//	private GenomeMappingType( GenomeMappingDataType setDataMappingType ) {
//		enumDataMappingType = setDataMappingType;
//		
//		originType = GenomeIdType.ACCESSION_2_NCBI_GENEID;
//		targetType = GenomeIdType.ACCESSION_2_NCBI_GENEID;
//		
//		bIsMultiMap = setDataMappingType.isMultiMapUsed();
//	}

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
	
	public GenomeIdType getTypeOrigin()
	{
		return originType;
	}
	
	public GenomeIdType getTypeTarget()
	{
		return targetType;
	}
}
