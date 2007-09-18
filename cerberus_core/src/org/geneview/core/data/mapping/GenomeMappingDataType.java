package org.geneview.core.data.mapping;

import org.geneview.core.util.IGeneViewDefaultType;

/**
 * @author Michael Kalkusch
 *
 */
public enum GenomeMappingDataType
implements IGeneViewDefaultType <GenomeMappingDataType> {

	INT2INT ( false, false ),
	INT2STRING ( false, false ),
	STRING2INT ( false, false ),
	STRING2STRING ( false, false ),
	
	MULTI_INT2INT ( true, false ),
	
//	/**
//	 * Reads a Integer-2-Integer Multimap and uses a 
//	 * lookup table to create the final Integer Multimap.
//	 */
//	MULTI_INT2INT_USE_LUT ( true, true ),
	
	MULTI_STRING2STRING ( true, false ),
	
//	/**
//	 * Reads a String-2-String Multimap and uses a 
//	 * lookup table to create the final String Multimap.
//	 */
//	MULTI_STRING2STRING_USE_LUT ( true, true ),
//	
//	MULTI_STRING2STRING_CREATE_REVERSE ( true, false ),
	
	NONE( false, false );
	
	
	private final boolean bUseMultiMap;
	
	private final boolean bIsLokupTable;
	
	
	private GenomeMappingDataType( boolean bEnableUseMultiMap,
			boolean bEnableIsLokupTable) {
		this.bUseMultiMap = bEnableUseMultiMap;
		this.bIsLokupTable = bEnableIsLokupTable;
	}
	
	/**
	 * Test if a multi map is used.
	 * 
	 * @return TRUE if it is a multi map
	 */
	public boolean isMultiMapUsed() {
		return bUseMultiMap;
	}
	
	/**
	 * Test if a lookup table shall be used.
	 * 
	 * @return TRUE for lookup tables
	 */
	public boolean isLookupTable() {
		return bIsLokupTable;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public GenomeMappingDataType getTypeDefault() {

		return GenomeMappingDataType.NONE;
	}
}
