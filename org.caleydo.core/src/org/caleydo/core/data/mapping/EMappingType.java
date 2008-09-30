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
	DAVID_2_REFSEQ_MRNA(EIDType.DAVID, EIDType.REFSEQ_MRNA, EMappingDataType.INT2STRING),
	REFSEQ_MRNA_2_DAVID(EIDType.REFSEQ_MRNA, EIDType.DAVID, EMappingDataType.STRING2INT),
	DAVID_2_ENTREZ_GENE_ID(EIDType.DAVID, EIDType.ENTREZ_GENE_ID, EMappingDataType.INT2INT),
	ENTREZ_GENE_ID_2_DAVID(EIDType.ENTREZ_GENE_ID, EIDType.DAVID, EMappingDataType.INT2INT),
	DAVID_2_EXPRESSION_INDEX(EIDType.DAVID, EIDType.EXPRESSION_INDEX, EMappingDataType.INT2INT),
	REFSEQ_MRNA_2_EXPRESSION_INDEX(EIDType.REFSEQ_MRNA, EIDType.EXPRESSION_INDEX, EMappingDataType.STRING2INT),
	EXPRESSION_INDEX_2_DAVID(EIDType.EXPRESSION_INDEX, EIDType.DAVID, EMappingDataType.INT2INT),
	BIOCARTA_GENE_ID_2_DAVID(EIDType.BIOCARTA_GENE_ID, EIDType.DAVID, EMappingDataType.MULTI_STRING2STRING),
	BIOCARTA_GENE_ID_2_REFSEQ_MRNA(EIDType.BIOCARTA_GENE_ID, EIDType.REFSEQ_MRNA, EMappingDataType.MULTI_STRING2STRING),
	DAVID_2_GENE_SYMBOL(EIDType.DAVID, EIDType.GENE_SYMBOL, EMappingDataType.INT2STRING),
	GENE_SYMBOL_2_DAVID(EIDType.GENE_SYMBOL, EIDType.DAVID, EMappingDataType.STRING2INT),
	DAVID_2_GENE_NAME(EIDType.DAVID, EIDType.GENE_NAME, EMappingDataType.INT2STRING);

	private final EIDType originType;
	private final EIDType targetType;

	private final boolean bIsMultiMap;

	private final EMappingDataType mappingDataType;

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
