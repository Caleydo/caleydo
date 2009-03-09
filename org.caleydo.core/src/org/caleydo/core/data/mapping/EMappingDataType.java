package org.caleydo.core.data.mapping;

/**
 * Enum that defines data mapping types for genome ID mapping.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @TODO: replace this type by generics
 */
public enum EMappingDataType {
	INT2INT(false, false), INT2STRING(false, false), STRING2INT(false, false), STRING2STRING(false, false),

	MULTI_INT2INT(true, false), MULTI_STRING2STRING(true, false), MULTI_STRING2INT(true, false), MULTI_INT2STRING(
		true, false),

	NONE(false, false);

	private final boolean bUseMultiMap;

	private final boolean bIsLokupTable;

	private EMappingDataType(boolean bEnableUseMultiMap, boolean bEnableIsLokupTable) {
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
}
