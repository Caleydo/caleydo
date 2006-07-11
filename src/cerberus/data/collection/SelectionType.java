/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import cerberus.data.collection.CollectionType;

/**
 * Types of selections.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.set.SelectionInterface
 */
public enum SelectionType 
implements CollectionType {

	SELECTION_ABSTRACT(false,
			"Abstract Selection, that has not been instaniated"),
	
	// --- instanitated Virtual Array's ---
	SELECTION_SINGLE_BLOCK(true,
			"Virtual Array of a single block"),
	
	SELECTION_MULTI_BLOCK(true,
			"Virtual Array of a multiple blocks, all with the same size"),
	
	SELECTION_MULTI_BLOCK_RLE(true,
			"Virtual Array of a multiple RLE (run length encoded) blocks"),
	
	SELECTION_RANDOM_BLOCK(true,
			"Virtual Array of a random block"),
	
	SELECTION_FABRIK(false,
			"Virtual Array Fabrik, that has not been initialized"),
	
	SELECTION_NEW_TYPE(false, "A new type of selection");
	
	
	/**
	 * Brief description, what the Selection does.
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
	private SelectionType(final boolean bSetDataType, 
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
	 * Convertion of a String to a SelectionType including abstract types.
	 * All known types are converted to a SelectionType.
	 * 
	 * @param fromString input String
	 * @return SelectionType parsed form String
	 */
	public final static SelectionType getTypeAll( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "SELECTION_SINGLE_BLOCK" ) ) 
			return SELECTION_SINGLE_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK" ) ) 
			return SELECTION_MULTI_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK_RLE" ) ) 
			return SELECTION_MULTI_BLOCK_RLE;
		if ( fromString.equalsIgnoreCase( "SELECTION_RANDOM_BLOCK" ) ) 
			return SELECTION_RANDOM_BLOCK;
		
		if ( fromString.equalsIgnoreCase( "SELECTION_ABSTRACT" ) ) 
			return SELECTION_ABSTRACT;
		if ( fromString.equalsIgnoreCase( "SELECTION_FABRIK" ) ) 
			return SELECTION_FABRIK;
		if ( fromString.equalsIgnoreCase( "SELECTION_NEW_TYPE" ) ) 
			return SELECTION_NEW_TYPE;
		
		return SELECTION_ABSTRACT;
	}
	
	
	/**
	 * Convertion of a String to a SelectionType excluding abstract type.
	 * 
	 * Note: The following parameters are mapped to SELECTION_ABSTRACT:
	 *  
	 * SELECTION_ABSTRACT, 
	 * VIRTUAL_ARRAY_ABSTRACT, 
	 * SELECTION_FABRIK,
	 * SELECTION_NEW_TYPE
	 * 
	 * @param fromString input String
	 * @return SelectionType parsed form String
	 */
	public final static SelectionType getType( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "SELECTION_SINGLE_BLOCK" ) ) 
			return SELECTION_SINGLE_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK" ) ) 
			return SELECTION_MULTI_BLOCK;
		if ( fromString.equalsIgnoreCase( "SELECTION_MULTI_BLOCK_RLE" ) ) 
			return SELECTION_MULTI_BLOCK_RLE;
		if ( fromString.equalsIgnoreCase( "SELECTION_RANDOM_BLOCK" ) ) 
			return SELECTION_RANDOM_BLOCK;
		
		return SELECTION_ABSTRACT;
	}
}
