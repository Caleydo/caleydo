package org.caleydo.core.data.mapping;

/**
 * Enum defines possible combinations of genome entity types that can be mapped
 * to each other.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public enum EMappingType
{
	DAVID_2_REFSEQ_MRNA(EIDType.DAVID, EIDType.REFSEQ_MRNA, EMappingDataType.MULTI_INT2STRING),
	DAVID_2_REFSEQ_MRNA_INT(EIDType.DAVID, EIDType.REFSEQ_MRNA_INT, EMappingDataType.MULTI_INT2INT),
	REFSEQ_MRNA_INT_2_DAVID(EIDType.REFSEQ_MRNA_INT, EIDType.DAVID, EMappingDataType.INT2INT),
	REFSEQ_MRNA_2_DAVID(EIDType.REFSEQ_MRNA, EIDType.DAVID, EMappingDataType.STRING2INT),
	REFSEQ_MRNA_2_REFSEQ_MRNA_INT(EIDType.REFSEQ_MRNA, EIDType.REFSEQ_MRNA_INT, EMappingDataType.STRING2INT),
	REFSEQ_MRNA_INT_2_REFSEQ_MRNA(EIDType.REFSEQ_MRNA_INT, EIDType.REFSEQ_MRNA, EMappingDataType.INT2STRING),	
	DAVID_2_ENTREZ_GENE_ID(EIDType.DAVID, EIDType.ENTREZ_GENE_ID, EMappingDataType.INT2INT),
	ENTREZ_GENE_ID_2_DAVID(EIDType.ENTREZ_GENE_ID, EIDType.DAVID, EMappingDataType.INT2INT),
//	DAVID_2_EXPRESSION_INDEX(EIDType.DAVID, EIDType.EXPRESSION_INDEX, EMappingDataType.MULTI_INT2INT),
	REFSEQ_MRNA_INT_2_EXPRESSION_INDEX(EIDType.REFSEQ_MRNA_INT, EIDType.EXPRESSION_INDEX, EMappingDataType.MULTI_INT2INT),
	REFSEQ_MRNA_2_EXPRESSION_INDEX(EIDType.REFSEQ_MRNA, EIDType.EXPRESSION_INDEX, EMappingDataType.MULTI_STRING2INT),
//	EXPRESSION_INDEX_2_DAVID(EIDType.EXPRESSION_INDEX, EIDType.DAVID, EMappingDataType.INT2INT),
	EXPRESSION_INDEX_2_REFSEQ_MRNA_INT(EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA_INT, EMappingDataType.INT2INT),
	DAVID_2_BIOCARTA_GENE_ID(EIDType.DAVID, EIDType.BIOCARTA_GENE_ID, EMappingDataType.MULTI_INT2STRING),
	BIOCARTA_GENE_ID_2_DAVID(EIDType.BIOCARTA_GENE_ID, EIDType.DAVID, EMappingDataType.MULTI_STRING2INT),
	BIOCARTA_GENE_ID_2_REFSEQ_MRNA(EIDType.BIOCARTA_GENE_ID, EIDType.REFSEQ_MRNA, EMappingDataType.MULTI_STRING2STRING),
//	BIOCARTA_GENE_ID_2_REFSEQ_MRNA_INT(EIDType.BIOCARTA_GENE_ID, EIDType.REFSEQ_MRNA_INT, EMappingDataType.MULTI_STRING2INT),
	DAVID_2_GENE_SYMBOL(EIDType.DAVID, EIDType.GENE_SYMBOL, EMappingDataType.INT2STRING),
	GENE_SYMBOL_2_DAVID(EIDType.GENE_SYMBOL, EIDType.DAVID, EMappingDataType.STRING2INT),
	DAVID_2_GENE_NAME(EIDType.DAVID, EIDType.GENE_NAME, EMappingDataType.INT2STRING),
	DAVID_2_CELL_COMPONENT(EIDType.DAVID, EIDType.CELL_COMPONENT, EMappingDataType.INT2STRING),
	CELL_COMPONENT_2_DAVID(EIDType.CELL_COMPONENT, EIDType.DAVID, EMappingDataType.STRING2INT),

	EXPERIMENT_2_EXPERIMENT_INDEX(EIDType.EXPERIMENT, EIDType.EXPERIMENT_INDEX, EMappingDataType.STRING2INT),
	EXPERIMENT_INDEX_2_EXPERIMENT(EIDType.EXPERIMENT_INDEX, EIDType.EXPERIMENT, EMappingDataType.INT2STRING),

	// FIXME: Make this general! Needed for Asslaber data
	OLIGO_2_EXPRESSION_INDEX(EIDType.OLIGO, EIDType.EXPRESSION_INDEX, EMappingDataType.STRING2INT),
	DAVID_2_OLIGO(EIDType.DAVID, EIDType.OLIGO, EMappingDataType.INT2STRING),
	OLIGO_2_DAVID(EIDType.OLIGO, EIDType.DAVID, EMappingDataType.STRING2INT),
	REFSEQ_MRNA_2_OLIGO(EIDType.REFSEQ_MRNA, EIDType.OLIGO, EMappingDataType.STRING2STRING);

	private final EIDType originType;
	private final EIDType targetType;

	private final boolean bIsMultiMap;

	private final EMappingDataType mappingDataType;

	/**
	 * Constructor.
	 * 
	 */
	private EMappingType(EIDType destination, EIDType target,
			EMappingDataType setDataMappingType)
	{
		mappingDataType = setDataMappingType;

		originType = destination;
		targetType = target;

		bIsMultiMap = setDataMappingType.isMultiMapUsed();
	}

	/**
	 * TRUE if this is a MultiMap <Integer,ArrayList<Integer>> FALSE if it is a
	 * HashMap. Two types of HashMaps are in use: <String,Integer> and
	 * <Integer,String> see isReversHashMap()
	 * 
	 * @return
	 */
	public boolean isMultiMap()
	{
		return bIsMultiMap;
	}

	public EMappingDataType getDataMapppingType()
	{
		return mappingDataType;
	}

	public EIDType getTypeOrigin()
	{
		return originType;
	}

	public EIDType getTypeTarget()
	{
		return targetType;
	}
}
