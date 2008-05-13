package org.caleydo.core.data.mapping;

import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.data.mapping.EGenomeIdType;
import org.caleydo.core.util.ICaleydoDefaultType;

/**
 * Enum defines possible combinations of 
 * genome entity types that can be mapped to each other.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum EGenomeMappingType
implements ICaleydoDefaultType <EGenomeMappingType> {
	
	/* --------------------- */
	NON_MAPPING(EGenomeIdType.NONE,
			EGenomeIdType.NONE,
			EGenomeMappingDataType.NONE),	
	
	/* --------------------- */
	DAVID_2_REFSEQ_MRNA(EGenomeIdType.DAVID,
			EGenomeIdType.REFSEQ_MRNA,
			EGenomeMappingDataType.INT2STRING),
	
	REFSEQ_MRNA_2_DAVID(EGenomeIdType.REFSEQ_MRNA,
			EGenomeIdType.DAVID,
			EGenomeMappingDataType.STRING2INT),			
			
	DAVID_2_ENTREZ_GENE_ID(EGenomeIdType.DAVID,
			EGenomeIdType.ENTREZ_GENE_ID,
			EGenomeMappingDataType.INT2INT),
	
	ENTREZ_GENE_ID_2_DAVID(EGenomeIdType.ENTREZ_GENE_ID,
			EGenomeIdType.DAVID,
			EGenomeMappingDataType.INT2INT),
			
	DAVID_2_EXPRESSION_STORAGE_ID(EGenomeIdType.DAVID,
			EGenomeIdType.EXPRESSION_STORAGE_ID,
			EGenomeMappingDataType.INT2INT),
			
	EXPRESSION_STORAGE_ID_2_DAVID(EGenomeIdType.EXPRESSION_STORAGE_ID,
			EGenomeIdType.DAVID,
			EGenomeMappingDataType.INT2INT),			
			
	BIOCARTA_GENE_ID_2_DAVID(EGenomeIdType.BIOCARTA_GENE_ID,
			EGenomeIdType.DAVID,
			EGenomeMappingDataType.STRING2INT);
	
	
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
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public EGenomeMappingType getTypeDefault() {
		
		return EGenomeMappingType.NON_MAPPING;
	}
}
