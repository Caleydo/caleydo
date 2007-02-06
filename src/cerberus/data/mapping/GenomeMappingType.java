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

	ACCESSION_2_MICROARRAY (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.INT2STRING),
			
	KEGG_CODE_2_KEGG (
			GenomeIdType.KEGG,
			GenomeIdType.KEGG,
			GenomeMappingDataType.STRING2INT),
					
	KEGG_2_ENZYMEID (
			GenomeIdType.KEGG,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.MULTI_INT2INT),
			
	KEGG_2_ENZYMEID_R (
			GenomeIdType.ENZYME,
			GenomeIdType.KEGG,
			GenomeMappingDataType.MULTI_INT2INT),
	
	NCBI_GENEID_2_KEGG  (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.KEGG,
			GenomeMappingDataType.INT2INT),
			
	NCBI_GENEID_CODE_2_NCBI_GENEID  (
			GenomeIdType.NCBI_GENEID_CODE,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT),
	
	PATHWAY_2_NCBI_GENEID  (
			GenomeIdType.PATHWAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT),
			
	METABOLIT_CODE_2_METABOLIT (GenomeIdType.METABOLIT_CODE,
			GenomeIdType.METABOLIT,
			GenomeMappingDataType.STRING2INT),
					
	MICROARRAY_2_NCBI_GENEID (GenomeIdType.MICROARRAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT),
			
	MICROARRAY_2_ACCESSION (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.STRING2STRING),
			
	MICROARRAY_CODE_2_ACCESSION (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.STRING2INT),
			
	MICROARRAY_CODE_2_ACCESSION_CODE (
			GenomeIdType.MICROARRAY_CODE,
			GenomeIdType.ACCESSION_CODE,
			GenomeMappingDataType.MULTI_STRING2STRING ),

	MICROARRAY_CODE_2_ACCESSION_CODE_USE_LUT (
			GenomeIdType.MICROARRAY_CODE,
			GenomeIdType.ACCESSION_CODE,
			GenomeMappingDataType.MULTI_STRING2STRING_USE_LUT ),
					
	MICROARRAY_CODE_2_MICROARRAY (
			GenomeIdType.MICROARRAY,
			GenomeIdType.MICROARRAY,
			GenomeMappingDataType.STRING2INT),
					
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
