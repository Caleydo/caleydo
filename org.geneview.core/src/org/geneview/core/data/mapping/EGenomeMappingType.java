package org.geneview.core.data.mapping;

import org.geneview.core.data.mapping.EGenomeMappingDataType;
import org.geneview.core.data.mapping.EGenomeIdType;
import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Enum defines possible combinations of 
 * genome entity types that can be mapped to each other.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum EGenomeMappingType
implements IGeneViewDefaultType <EGenomeMappingType> {
	
	/* --- Accession Id --- */
	ACCESSION_CODE_2_ACCESSION (
			EGenomeIdType.ACCESSION_CODE,
			EGenomeIdType.ACCESSION, 
			EGenomeMappingDataType.STRING2INT),
			
	ACCESSION_2_ACCESSION_CODE (
			EGenomeIdType.ACCESSION,
			EGenomeIdType.ACCESSION_CODE, 
			EGenomeMappingDataType.INT2STRING),
			
	ACCESSION_2_MICROARRAY (
			EGenomeIdType.ACCESSION,
			EGenomeIdType.MICROARRAY,
			EGenomeMappingDataType.MULTI_INT2INT),
		
	ACCESSION_CODE_2_NCBI_GENEID (
			EGenomeIdType.ACCESSION_CODE,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.STRING2INT),
			
	ACCESSION_2_NCBI_GENEID (
			EGenomeIdType.ACCESSION,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.INT2INT),	
			
	ACCESSION_2_MICROARRAY_EXPRESSION (
			EGenomeIdType.ACCESSION,
			EGenomeIdType.MICROARRAY_EXPRESSION,
			EGenomeMappingDataType.INT2INT),
			
	ACCESSION_2_GENE_NAME (
			EGenomeIdType.ACCESSION,
			EGenomeIdType.GENE_NAME,
			EGenomeMappingDataType.INT2STRING),			
			
	/* --- KEGG Gene Id --- */
	/**
	 * use NCBI_GENEID_2* if possible
	 */
	KEGG_CODE_2_KEGG (
			EGenomeIdType.KEGG,
			EGenomeIdType.KEGG,
			EGenomeMappingDataType.STRING2INT),
			
	/**
	 * use NCBI_GENEID_2* if possible
	 */
	KEGG_2_KEGG_CODE (
			EGenomeIdType.KEGG,
			EGenomeIdType.KEGG,
			EGenomeMappingDataType.INT2STRING),			
			
	/**
	 * use NCBI_GENEID_2* if possible
	 */
	KEGG_2_ENZYME (
			EGenomeIdType.KEGG,
			EGenomeIdType.ENZYME,
			EGenomeMappingDataType.MULTI_INT2INT),
			
	/**
	 * use NCBI_GENEID_2* if possible
	 */
	ENZYME_2_KEGG (
			EGenomeIdType.KEGG,
			EGenomeIdType.ENZYME,
			EGenomeMappingDataType.MULTI_INT2INT),
				
	/* --- NCBI GeneId --- */		
			
	/**
	 * do not use KEGG Id; use NCBI_GENEID_2
	 * Note: this is the mapping between NCBI_GENEID and KEGG 
	 */
	NCBI_GENEID_2_KEGG  (
			EGenomeIdType.NCBI_GENEID,
			EGenomeIdType.KEGG,
			EGenomeMappingDataType.INT2INT),
					
	NCBI_GENEID_CODE_2_NCBI_GENEID  (
			EGenomeIdType.NCBI_GENEID_CODE,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.STRING2INT),
	
	NCBI_GENEID_2_NCBI_GENEID_CODE  (
			EGenomeIdType.NCBI_GENEID,
			EGenomeIdType.NCBI_GENEID_CODE,
			EGenomeMappingDataType.INT2STRING),			
					
	NCBI_GENEID_2_ACCESSION (
			EGenomeIdType.NCBI_GENEID,
			EGenomeIdType.ACCESSION,
			EGenomeMappingDataType.INT2INT),
			
	NCBI_GENEID_2_ENZYME  (
			EGenomeIdType.NCBI_GENEID,
			EGenomeIdType.ENZYME,
			EGenomeMappingDataType.MULTI_INT2INT),	
			
	NCBI_GENEID_2_GENE_SHORT_NAME (
			EGenomeIdType.NCBI_GENEID,
			EGenomeIdType.GENE_SHORT_NAME,
			EGenomeMappingDataType.INT2STRING),			
			
	GENE_SHORT_NAME_2_NCBI_GENEID (
			EGenomeIdType.GENE_SHORT_NAME,
			EGenomeIdType.NCBI_GENEID,			
			EGenomeMappingDataType.STRING2INT),				
						
	/* --- Pathway --- */
	PATHWAY_CODE_2_PATHWAY  (
			EGenomeIdType.PATHWAY_CODE,
			EGenomeIdType.PATHWAY,
			EGenomeMappingDataType.STRING2INT),
	
	PATHWAY_2_PATHWAY_CODE (
			EGenomeIdType.PATHWAY,
			EGenomeIdType.PATHWAY_CODE,
			EGenomeMappingDataType.INT2STRING),			
			
	PATHWAY_2_NCBI_GENEID  (
			EGenomeIdType.PATHWAY,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.INT2INT),
			
			
	/* --- Metabolit --- */
	METABOLIT_CODE_2_METABOLIT (
			EGenomeIdType.METABOLIT_CODE,
			EGenomeIdType.METABOLIT,
			EGenomeMappingDataType.STRING2INT),
	
	METABOLIT_2_METABOLIT_CODE (
			EGenomeIdType.METABOLIT,
			EGenomeIdType.METABOLIT_CODE,
			EGenomeMappingDataType.INT2STRING),			
			
	/* --- Microarray --- */
	MICROARRAY_CODE_2_MICROARRAY (
			EGenomeIdType.MICROARRAY_CODE,
			EGenomeIdType.MICROARRAY,
			EGenomeMappingDataType.STRING2INT),			

	MICROARRAY_2_MICROARRAY_CODE (
			EGenomeIdType.MICROARRAY,
			EGenomeIdType.MICROARRAY_CODE,
			EGenomeMappingDataType.INT2STRING),			
			
	MICROARRAY_2_NCBI_GENEID (
			EGenomeIdType.MICROARRAY,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.STRING2INT),
			
	MICROARRAY_2_ACCESSION (
			EGenomeIdType.MICROARRAY,
			EGenomeIdType.ACCESSION,
			EGenomeMappingDataType.MULTI_INT2INT),			
			
	MICROARRAY_CODE_2_ACCESSION (
			EGenomeIdType.MICROARRAY,
			EGenomeIdType.ACCESSION,
			EGenomeMappingDataType.STRING2INT),
			
	MICROARRAY_CODE_2_ACCESSION_CODE (
			EGenomeIdType.MICROARRAY_CODE,
			EGenomeIdType.ACCESSION_CODE,
			EGenomeMappingDataType.MULTI_STRING2STRING),
			
	MICROARRAY_2_MICROARRAY_EXPRESSION (
			EGenomeIdType.MICROARRAY,
			EGenomeIdType.MICROARRAY_EXPRESSION,
			EGenomeMappingDataType.INT2INT),	
				
	/* --- Enzyme --- */
	ENZYME_CODE_2_ENZYME  (
			EGenomeIdType.ENZYME_CODE,
			EGenomeIdType.ENZYME,
			EGenomeMappingDataType.STRING2INT),
	
	ENZYME_2_ENZYME_CODE (
			EGenomeIdType.ENZYME,
			EGenomeIdType.ENZYME_CODE,
			EGenomeMappingDataType.INT2STRING),
	
	ENZYME_2_NCBI_GENEID  (
			EGenomeIdType.ENZYME,
			EGenomeIdType.NCBI_GENEID,
			EGenomeMappingDataType.MULTI_INT2INT),
						
	/* --------------------- */
	NON_MAPPING(EGenomeIdType.NONE,
			EGenomeIdType.NONE,
			EGenomeMappingDataType.NONE);			
	
	private final EGenomeIdType originType;
	
	private final EGenomeIdType targetType;
	
	private final boolean bIsMultiMap;
	
	private final EGenomeMappingDataType mappingDataType;
	
	private EGenomeMappingType( EGenomeIdType destination,
			EGenomeIdType target,
			EGenomeMappingDataType setDataMappingType) {
		
		mappingDataType = setDataMappingType;
		
		originType = destination;
		targetType = target;
		
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
	
	public EGenomeMappingDataType getDataMapppingType() {
		
		return mappingDataType;
	}
	
	public EGenomeIdType getTypeOrigin() {
		
		return originType;
	}
	
	public EGenomeIdType getTypeTarget() {
		
		return targetType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public EGenomeMappingType getTypeDefault() {
		
		return EGenomeMappingType.NON_MAPPING;
	}
}
