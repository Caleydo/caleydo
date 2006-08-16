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
 * Collection of diferent data types used in stograge.
 * 
 * @author Michael Kalkusch
 *
 */
public enum StorageType 
implements ICollectionType {

	/*
	 * Note: When adding a type also check all methodes!
	 */
	
	/** string */
	STRING(true),
	/** integer */
	INT(true),
	/** long */
	LONG(true),
	/** float */
	FLOAT(true),
	/** double */
	DOUBLE(true),
	/** vec2f, contains of (float,float) */
	VEC2F(true),
	/** vec3f, contains of (float,float,float) */ 
	VEC3F(true),
	/** vec4f, contains of (float,float,float,float) */
	VEC4F(true),
	/** boolean */
	BOOLEAN(true),
	/** short */
	SHORT(true),
	
	/** a java object is stored */
	OBJECT(true),
	
	/** not specified as data type. skip value in tokenize. */
	SKIP(false),
	/** not specified as data type. abort in tokenizer. */
	ABORT(false),
	
	/** not specified as data type. not any specified. */
	NONE(false),
	
	/** not specified as data type. used by prometheus.statistic.*.
	 * Defines an iterator as data type. */
	ITERATOR(false);
	
	/*
	 * Note: When adding a type also check all methodes!
	 */
	
	
	/**
	 * Defines if this type contains data or is only for parsing.
	 * TRUE for
	 */
	private final boolean bIsValue;
	
	/**
	 * Private constructopr.
	 * 
	 * @param bSetValueType treu for values containing data, false for parsing parametes.
	 */
	private StorageType( final boolean bSetValueType ) {
		bIsValue = bSetValueType;
	}
	
	/**
	 * Convertion of String to SetType including abstract types.
	 * 
	 * @param fromString String to parse
	 * @return SetType parsed from String, or null if type is unknown
	 */
	public final static StorageType getTypeAll( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "ABORT" ) ) 
			return StorageType.ABORT;
		if ( fromString.equalsIgnoreCase( "BOOLEAN" ) ) 
			return StorageType.BOOLEAN;
		if ( fromString.equalsIgnoreCase( "DOUBLE" ) ) 
			return StorageType.DOUBLE;
		if ( fromString.equalsIgnoreCase( "FLOAT" ) ) 
			return StorageType.FLOAT;
		if ( fromString.equalsIgnoreCase( "INTEGER" ) ) 
			return StorageType.INT;
		if ( fromString.equalsIgnoreCase( "ITERATOR" ) ) 
			return StorageType.ITERATOR;
		if ( fromString.equalsIgnoreCase( "LONG" ) ) 
			return StorageType.LONG;
		if ( fromString.equalsIgnoreCase( "SET_NONE" ) ) 
			return StorageType.NONE;
		if ( fromString.equalsIgnoreCase( "OBJECT" ) ) 
			return StorageType.OBJECT;
		if ( fromString.equalsIgnoreCase( "SHORT" ) ) 
			return StorageType.SHORT;
		if ( fromString.equalsIgnoreCase( "SKIP" ) ) 
			return StorageType.SKIP;
		if ( fromString.equalsIgnoreCase( "STRING" ) ) 
			return StorageType.STRING;
		if ( fromString.equalsIgnoreCase( "VEC2F" ) ) 
			return StorageType.VEC2F;
		if ( fromString.equalsIgnoreCase( "VEC3F" ) ) 
			return StorageType.VEC3F;
		if ( fromString.equalsIgnoreCase( "VEC4F" ) ) 
			return StorageType.VEC4F;
		
		return null;
	}
	
	/**
	 * Test if a this type contains data and values or is only used for parsing.
	 * 
	 * @return false for OBJECT, ABORT, ITERATOR, SET_NONE, SKIP and true for all other
	 * 
	 * @param true for data types, false for parsing types
	 */
	public boolean isDataType() {
		
		/**
		 * Exception for tpye OBJECT
		 */
		if ( this == OBJECT ) {
			return false;
		}
		
		return this.bIsValue;
	}
	
	/**
	 * Convertion of String to SetType excluding abstract types.
	 * 
	 * Note: ignored types are:
	 * ABORT,
	 * ITERATOR,
	 * SKIP
	 * 
	 * @param fromString String to parse
	 * @return SetType parsed from String, or SET_NONE if type is abstract or unknown
	 */
	public final static StorageType getType( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "BOOLEAN" ) ) 
			return StorageType.BOOLEAN;
		if ( fromString.equalsIgnoreCase( "DOUBLE" ) ) 
			return StorageType.DOUBLE;
		if ( fromString.equalsIgnoreCase( "FLOAT" ) ) 
			return StorageType.FLOAT;
		if ( fromString.equalsIgnoreCase( "INTEGER" ) ) 
			return StorageType.INT;
		if ( fromString.equalsIgnoreCase( "LONG" ) ) 
			return StorageType.LONG;
		if ( fromString.equalsIgnoreCase( "OBJECT" ) ) 
			return StorageType.OBJECT;
		if ( fromString.equalsIgnoreCase( "SHORT" ) ) 
			return StorageType.SHORT;
		if ( fromString.equalsIgnoreCase( "STRING" ) ) 
			return StorageType.STRING;
		if ( fromString.equalsIgnoreCase( "VEC2F" ) ) 
			return StorageType.VEC2F;
		if ( fromString.equalsIgnoreCase( "VEC3F" ) ) 
			return StorageType.VEC3F;
		if ( fromString.equalsIgnoreCase( "VEC4F" ) ) 
			return StorageType.VEC4F;
		
		return StorageType.NONE;
	}
	
}
