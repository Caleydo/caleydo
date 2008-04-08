/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.collection;

import org.caleydo.core.data.collection.ICollectionType;

/**
 * Types of selections.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.set.SelectionInterface
 */
public enum VirtualArrayType
implements ICollectionType <VirtualArrayType> {

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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public VirtualArrayType getTypeDefault() {

		return VirtualArrayType.VIRTUAL_ARRAY_SINGLE_BLOCK;
	}
}
