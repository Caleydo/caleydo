package org.caleydo.core.data.mapping;

/**
 * Enum defines possible combinations of genome entity types that can be mapped to each other.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public enum EMappingType {
	DAVID_2_REFSEQ_MRNA(EIDType.DAVID, EIDType.REFSEQ_MRNA, true),
	DAVID_2_REFSEQ_MRNA_INT(EIDType.DAVID, EIDType.REFSEQ_MRNA_INT, true),
	REFSEQ_MRNA_INT_2_DAVID(EIDType.REFSEQ_MRNA_INT, EIDType.DAVID, false),
	REFSEQ_MRNA_2_DAVID(EIDType.REFSEQ_MRNA, EIDType.DAVID, false),
	REFSEQ_MRNA_2_REFSEQ_MRNA_INT(EIDType.REFSEQ_MRNA, EIDType.REFSEQ_MRNA_INT, false),
	REFSEQ_MRNA_INT_2_REFSEQ_MRNA(EIDType.REFSEQ_MRNA_INT, EIDType.REFSEQ_MRNA, false),
	DAVID_2_ENTREZ_GENE_ID(EIDType.DAVID, EIDType.ENTREZ_GENE_ID, false),
	ENTREZ_GENE_ID_2_DAVID(EIDType.ENTREZ_GENE_ID, EIDType.DAVID, false),
	// DAVID_2_EXPRESSION_INDEX(EIDType.DAVID, EIDType.EXPRESSION_INDEX,
	// EMappingDataType.MULTI_INT2INT),
	REFSEQ_MRNA_INT_2_EXPRESSION_INDEX(
		EIDType.REFSEQ_MRNA_INT,
		EIDType.EXPRESSION_INDEX,
		true),
	REFSEQ_MRNA_2_EXPRESSION_INDEX(
		EIDType.REFSEQ_MRNA,
		EIDType.EXPRESSION_INDEX,
		true),
	// EXPRESSION_INDEX_2_DAVID(EIDType.EXPRESSION_INDEX, EIDType.DAVID,
	// EMappingDataType.INT2INT),
	EXPRESSION_INDEX_2_REFSEQ_MRNA_INT(
		EIDType.EXPRESSION_INDEX,
		EIDType.REFSEQ_MRNA_INT,
		false),
	DAVID_2_BIOCARTA_GENE_ID(EIDType.DAVID, EIDType.BIOCARTA_GENE_ID, true),
	BIOCARTA_GENE_ID_2_DAVID(EIDType.BIOCARTA_GENE_ID, EIDType.DAVID, true),
	BIOCARTA_GENE_ID_2_REFSEQ_MRNA(
		EIDType.BIOCARTA_GENE_ID,
		EIDType.REFSEQ_MRNA,
		true),
	// BIOCARTA_GENE_ID_2_REFSEQ_MRNA_INT(EIDType.BIOCARTA_GENE_ID,
	// EIDType.REFSEQ_MRNA_INT,
	// EMappingDataType.MULTI_STRING2INT),
	DAVID_2_GENE_SYMBOL(EIDType.DAVID, EIDType.GENE_SYMBOL, false),
	GENE_SYMBOL_2_DAVID(EIDType.GENE_SYMBOL, EIDType.DAVID, false),

	DAVID_2_GENE_NAME(EIDType.DAVID, EIDType.GENE_NAME, false),
	GENE_NAME_2_DAVID(EIDType.GENE_NAME, EIDType.DAVID, false),

	DAVID_2_CELL_COMPONENT(EIDType.DAVID, EIDType.CELL_COMPONENT, false),
	CELL_COMPONENT_2_DAVID(EIDType.CELL_COMPONENT, EIDType.DAVID, false),

	EXPERIMENT_2_EXPERIMENT_INDEX(EIDType.EXPERIMENT, EIDType.EXPERIMENT_INDEX, false),
	EXPERIMENT_INDEX_2_EXPERIMENT(EIDType.EXPERIMENT_INDEX, EIDType.EXPERIMENT, false),

	// FIXME: Make this general! Needed for Asslaber data
	OLIGO_2_EXPRESSION_INDEX(EIDType.OLIGO, EIDType.EXPRESSION_INDEX, true),
	REFSEQ_MRNA_INT_2_OLIGO(EIDType.REFSEQ_MRNA_INT, EIDType.OLIGO, false),
	OLIGO_2_REFSEQ_MRNA_INT(EIDType.OLIGO, EIDType.REFSEQ_MRNA_INT, false),
	REFSEQ_MRNA_2_OLIGO(EIDType.REFSEQ_MRNA, EIDType.OLIGO, false),

	UNSPECIFIED_2_EXPRESSION_INDEX(EIDType.UNSPECIFIED, EIDType.EXPRESSION_INDEX, false),
	EXPRESSION_INDEX_2_UNSPECIFIED(EIDType.EXPRESSION_INDEX, EIDType.UNSPECIFIED, false);
	
	private final EIDType originType;
	private final EIDType targetType;

	private final boolean bIsMultiMap;

	/**
	 * Constructor.
	 */
	private EMappingType(EIDType destination, EIDType target, boolean isMultiMap) {

		originType = destination;
		targetType = target;

		this.bIsMultiMap = isMultiMap;
	}

	/**
	 * TRUE if this is a MultiMap <Integer,ArrayList<Integer>> FALSE if it is a HashMap. Two types of HashMaps
	 * are in use: <String,Integer> and <Integer,String> see isReversHashMap()
	 * 
	 * @return
	 */
	public boolean isMultiMap() {
		return bIsMultiMap;
	}

	public EIDType getTypeOrigin() {
		return originType;
	}

	public EIDType getTypeTarget() {
		return targetType;
	}
}
