/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeIdType;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum GenomeMappingType
{
	/* --- Accession Id --- */
	ACCESSION_CODE_2_ACCESSION (
			GenomeIdType.ACCESSION_CODE,
			GenomeIdType.ACCESSION, 
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.ACCESSION_2_ACCESSION_CODE),
			
	ACCESSION_2_ACCESSION_CODE (
			GenomeIdType.ACCESSION,
			GenomeIdType.ACCESSION_CODE, 
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.ACCESSION_CODE_2_ACCESSION),
	
	ACCESSION_2_NCBI_GENEID (
			GenomeIdType.ACCESSION,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT,
			GenomeMappingType.NCBI_GENEID_2_ACCESSION),
			
	ACCESSION_2_MICROARRAY (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.MICROARRAY_2_ACCESSION),
		
			
	ACCESSION_CODE_2_NCBI_GENEID (
			GenomeIdType.ACCESSION_CODE,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT),
			
	/* --- KEGG Gene Id --- */
	/**
	 * @deprecated use NCBI_GENEID_2*
	 */
	KEGG_CODE_2_KEGG (
			GenomeIdType.KEGG,
			GenomeIdType.KEGG,
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.KEGG_2_KEGG_CODE),
			
	/**
	 * @deprecated use NCBI_GENEID_2*
	 */
	KEGG_2_KEGG_CODE (
			GenomeIdType.KEGG,
			GenomeIdType.KEGG,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.KEGG_CODE_2_KEGG),			
			
	/**
	 * @deprecated use NCBI_GENEID_2*
	 */
	KEGG_2_ENZYME (
			GenomeIdType.KEGG,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.MULTI_INT2INT,
			GenomeMappingType.ENZYME_2_KEGG),
			
	/**
	 * @deprecated use NCBI_GENEID_2*
	 */
	ENZYME_2_KEGG (
			GenomeIdType.KEGG,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.MULTI_INT2INT,
			GenomeMappingType.KEGG_2_ENZYME),
				
	/**
	 * @deprecated do not use KEGG Id; use NCBI_GENEID_2
	 * Note: this is the mapping between NCBI_GENEID and KEGG 
	 */
	NCBI_GENEID_2_KEGG  (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.KEGG,
			GenomeMappingDataType.INT2INT,
			GenomeMappingType.NON_MAPPING),
					
					
	/* --- NCBI GeneId --- */
	NCBI_GENEID_CODE_2_NCBI_GENEID  (
			GenomeIdType.NCBI_GENEID_CODE,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE),
	
	NCBI_GENEID_2_NCBI_GENEID_CODE  (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.NCBI_GENEID_CODE,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID),			
					
	NCBI_GENEID_2_ACCESSION (
			GenomeIdType.ACCESSION,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT,
			GenomeMappingType.ACCESSION_2_NCBI_GENEID),
	
	NCBI_GENEID_2_ENZYME  (
			GenomeIdType.NCBI_GENEID,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.INT2INT,
			GenomeMappingType.ENZYME_2_NCBI_GENEID),
					
			
	/* --- Pathway --- */
	PATHWAY_CODE_2_PATHWAY  (
			GenomeIdType.PATHWAY_CODE,
			GenomeIdType.PATHWAY,
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.PATHWAY_2_PATHWAY_CODE),
	
	PATHWAY_2_PATHWAY_CODE (
			GenomeIdType.PATHWAY,
			GenomeIdType.PATHWAY_CODE,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.PATHWAY_CODE_2_PATHWAY),			
			
	PATHWAY_2_NCBI_GENEID  (
			GenomeIdType.PATHWAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.INT2INT),
			
			
	/* --- Metabolit --- */
	METABOLIT_CODE_2_METABOLIT (
			GenomeIdType.METABOLIT_CODE,
			GenomeIdType.METABOLIT,
			GenomeMappingDataType.STRING2INT),
	
	METABOLIT_2_METABOLIT_CODE (
			GenomeIdType.METABOLIT,
			GenomeIdType.METABOLIT_CODE,
			GenomeMappingDataType.INT2STRING),			
			
	/* --- Microarray --- */
	MICROARRAY_CODE_2_MICROARRAY (
			GenomeIdType.MICROARRAY_CODE,
			GenomeIdType.MICROARRAY,
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.MICROARRAY_2_MICROARRAY_CODE),			

	MICROARRAY_2_MICROARRAY_CODE (
			GenomeIdType.MICROARRAY,
			GenomeIdType.MICROARRAY_CODE,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.MICROARRAY_CODE_2_MICROARRAY),			
			
	MICROARRAY_2_NCBI_GENEID (
			GenomeIdType.MICROARRAY,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.STRING2INT),
			
	MICROARRAY_2_ACCESSION (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.INT2INT,
			GenomeMappingType.ACCESSION_2_MICROARRAY),			
			
	MICROARRAY_CODE_2_ACCESSION (
			GenomeIdType.MICROARRAY,
			GenomeIdType.ACCESSION,
			GenomeMappingDataType.STRING2INT),
			
	MICROARRAY_CODE_2_ACCESSION_CODE (
			GenomeIdType.MICROARRAY_CODE,
			GenomeIdType.ACCESSION_CODE,
			GenomeMappingDataType.MULTI_STRING2STRING),

//  // Loader handles lookup table!
//	MICROARRAY_CODE_2_ACCESSION_CODE_USE_LUT (
//			GenomeIdType.MICROARRAY_CODE,
//			GenomeIdType.ACCESSION_CODE,
//			GenomeMappingDataType.MULTI_STRING2STRING_USE_LUT ),
				
	/* --- Enzyme --- */
	ENZYME_CODE_2_ENZYME  (
			GenomeIdType.ENZYME_CODE,
			GenomeIdType.ENZYME,
			GenomeMappingDataType.STRING2INT,
			GenomeMappingType.ENZYME_2_ENZYME_CODE),
	
	ENZYME_2_ENZYME_CODE (
			GenomeIdType.ENZYME,
			GenomeIdType.ENZYME_CODE,
			GenomeMappingDataType.INT2STRING,
			GenomeMappingType.ENZYME_CODE_2_ENZYME),
	
	ENZYME_2_NCBI_GENEID  (
			GenomeIdType.ENZYME,
			GenomeIdType.NCBI_GENEID,
			GenomeMappingDataType.MULTI_INT2INT,
			GenomeMappingType.NCBI_GENEID_2_ENZYME),
					
	NON_MAPPING(GenomeIdType.NONE,
			GenomeIdType.NONE,
			GenomeMappingDataType.NONE,
			GenomeMappingType.NON_MAPPING);
	
	private final GenomeIdType originType;
	
	private final GenomeIdType targetType;
	
	private final boolean bIsMultiMap;
	
	private final GenomeMappingDataType mappingDataType;
	
	private final GenomeMappingType reverseMappingType;
	
	private GenomeMappingType( GenomeIdType destination,
			GenomeIdType target,
			GenomeMappingDataType setDataMappingType,
			GenomeMappingType reverseMappingType) {
		
		mappingDataType = setDataMappingType;
		
		originType = destination;
		targetType = target;
		
		this.reverseMappingType = reverseMappingType;
		
		bIsMultiMap = setDataMappingType.isMultiMapUsed();
	}
	
	private GenomeMappingType( GenomeIdType destination,
			GenomeIdType target,
			GenomeMappingDataType setDataMappingType) {
		
		this(destination, 
				target, 
				setDataMappingType,
				GenomeMappingType.NON_MAPPING);
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
	
	public GenomeMappingDataType getDataMapppingType() {
		
		return mappingDataType;
	}
	
	public GenomeIdType getTypeOrigin() {
		
		return originType;
	}
	
	public GenomeIdType getTypeTarget() {
		
		return targetType;
	}

	public GenomeMappingType getReverseMappingType() {
		
		return reverseMappingType;
	}
}
