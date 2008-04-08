package org.geneview.core.data.mapping;

import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Enum that defines data mapping types for genome ID mapping.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum EGenomeMappingDataType
implements IGeneViewDefaultType <EGenomeMappingDataType> {

	INT2INT ( false, false ),
	INT2STRING ( false, false ),
	STRING2INT ( false, false ),
	STRING2STRING ( false, false ),
	
	MULTI_INT2INT ( true, false ),
	MULTI_STRING2STRING ( true, false ),
	
	NONE( false, false );
	
	
	private final boolean bUseMultiMap;
	
	private final boolean bIsLokupTable;
	
	
	private EGenomeMappingDataType( boolean bEnableUseMultiMap,
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
	 * @see org.geneview.core.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public EGenomeMappingDataType getTypeDefault() {

		return EGenomeMappingDataType.NONE;
	}
}
