/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import cerberus.data.collection.ICollectionType;

/**
 * Types of selections.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.set.SelectionInterface
 */
public enum VirtualArrayType 
implements ICollectionType {

	VIRTUAL_ARRAY_ABSTRACT(false,
			"Abstract IVirtualArray, that has not been instaniated"),
	
	// --- instanitated Virtual Array's ---
	VIRTUAL_ARRAY_SINGLE_BLOCK(true,
			"Virtual Array of a single block"),
	
	VIRTUAL_ARRAY_MULTI_BLOCK(true,
			"Virtual Array of a multiple blocks, all with the same size"),
	
	VIRTUAL_ARRAY_MULTI_BLOCK_RLE(true,
			"Virtual Array of a multiple RLE (run length encoded) blocks"),
	
	VIRTUAL_ARRAY_RANDOM_BLOCK(true,
			"Virtual Array of a random block"),
	
	VIRTUAL_ARRAY_FABRIK(false,
			"Virtual Array Fabrik, that has not been initialized"),
	
	VIRTUAL_ARRAY_NEW_TYPE(false, "A new type of selection");
	
	
	/**
	 * Brief description, what the IVirtualArray does.
	 */
	private final String sDescription;
	
	/**
	 * Defines if this type is a data type.
	 */
	private final boolean bIsDataType;
	
	/**
	 * Constructor for the Enumeration.
	 * 
	 * @param sSetDescription
	 */
	private VirtualArrayType(final boolean bSetDataType, 
			final String sSetDescription) {
		sDescription = sSetDescription;
		bIsDataType = bSetDataType;
	}
	
	/**
	 * Get the description of an enumeration.
	 * 
	 * @return description
	 */
	public final String getDescription() {
		return sDescription;
	}
	
	public final boolean isDataType() {
		return bIsDataType;
	}
	
	/**
	 * Convertion of a String to a VirtualArrayType including abstract types.
	 * All known types are converted to a VirtualArrayType.
	 * 
	 * @param fromString input String
	 * @return VirtualArrayType parsed form String
	 */
	public final static VirtualArrayType getTypeAll( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_SINGLE_BLOCK" ) ) 
			return VIRTUAL_ARRAY_SINGLE_BLOCK;
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_MULTI_BLOCK" ) ) 
			return VIRTUAL_ARRAY_MULTI_BLOCK;
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_MULTI_BLOCK_RLE" ) ) 
			return VIRTUAL_ARRAY_MULTI_BLOCK_RLE;
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_RANDOM_BLOCK" ) ) 
			return VIRTUAL_ARRAY_RANDOM_BLOCK;
		
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_ABSTRACT" ) ) 
			return VIRTUAL_ARRAY_ABSTRACT;
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_FABRIK" ) ) 
			return VIRTUAL_ARRAY_FABRIK;
		if ( fromString.equalsIgnoreCase( "VIRTUAL_ARRAY_NEW_TYPE" ) ) 
			return VIRTUAL_ARRAY_NEW_TYPE;
		
		return VIRTUAL_ARRAY_ABSTRACT;
	}
	
	
	/**
	 * Convertion of a String to a VirtualArrayType excluding abstract type.
	 * 
	 * Note: The following parameters are mapped to SELECTION_ABSTRACT:
	 *  
	 * SELECTION_ABSTRACT, 
	 * VIRTUAL_ARRAY_ABSTRACT, 
	 * SELECTION_FABRIK,
	 * SELECTION_NEW_TYPE
	 * 
	 * @param fromString input String
	 * @return VirtualArrayType parsed form String
	 */
	public final static VirtualArrayType getType( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "SELECTION_SINGLE_BLOCK" ) ) 
			return VIRTUAL_ARRAY_SINGLE_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK" ) ) 
			return VIRTUAL_ARRAY_MULTI_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK_RLE" ) ) 
			return VIRTUAL_ARRAY_MULTI_BLOCK_RLE;
		if ( fromString.equalsIgnoreCase( "SELECTION_RANDOM_BLOCK" ) ) 
			return VIRTUAL_ARRAY_RANDOM_BLOCK;
		
		return VIRTUAL_ARRAY_ABSTRACT;
	}
}
