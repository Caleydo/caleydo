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
import java.util.LinkedList;
import java.util.Iterator;

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
	
	/** integer */
	INT(true,0),
	/** float */
	FLOAT(true,1),
	/** string */
	STRING(true,2),
	/** boolean */
	BOOLEAN(true,3),
	/** double */
	DOUBLE(true,4),
	/** a java object is stored */
	OBJECT(true,5),
	
	/** long */
	LONG(true,-1),
	/** vec2f, contains of (float,float) */
	VEC2F(true,-1),
	/** vec3f, contains of (float,float,float) */ 
	VEC3F(true,-1),
	/** vec4f, contains of (float,float,float,float) */
	VEC4F(true,-1),
	/** short */
	SHORT(true,-1),
	
	/** not specified as data type. skip value in tokenize. */
	SKIP(false,-1),
	/** not specified as data type. abort in tokenizer. */
	ABORT(false,-1),
	
	/** not specified as data type. not any specified. */
	NONE(false,-1),
	
	/** not specified as data type. used by prometheus.statistic.*.
	 * Defines an iterator as data type. */
	ITERATOR(false,-1);
	
	/*
	 * Note: When adding a type also check all methodes!
	 */
	
	
	/**
	 * Defines if this type contains data or is only for parsing.
	 * TRUE for
	 */
	private final boolean bIsValue;
	
	/**
	 * Define the index inside the array.
	 * If not applicable -1 is used!
	 */
	private final int iIndexInArray;
	
	/**
	 * Private constructopr.
	 * 
	 * @param bSetValueType treu for values containing data, false for parsing parametes.
	 */
	private StorageType( final boolean bSetValueType,
			final int iIndexInArray ) {
		this.bIsValue = bSetValueType;
		this.iIndexInArray = iIndexInArray;
	}
	
	/**
	 * Returns StorageType by index. Does a reverse lookup.
	 * 
	 * @param iIndex that shall be converted to type
	 * @return type or NULL if type is unknown
	 */
	public static StorageType getTypeByIndex( final int iIndex ) {
		if ( iIndex == INT.getIndexInArray() ) {
			return INT;
		}
		else if ( iIndex == FLOAT.getIndexInArray() ) {
			return FLOAT;
		}
		else if ( iIndex == STRING.getIndexInArray() ) {
			return STRING;
		}
		else if ( iIndex == DOUBLE.getIndexInArray() ) {
			return DOUBLE;
		}
		else if ( iIndex == OBJECT.getIndexInArray() ) {
			return OBJECT;
		}
		
		assert false : "unsupported type!";
		
		return NONE;
	}
	
//	/**
//	 * Convertion of String to SetType including abstract types.
//	 * 
//	 * @param fromString String to parse
//	 * @return SetType parsed from String, or null if type is unknown
//	 */
//	public final static StorageType getTypeAll( final String fromString ) {
//		
//		if ( fromString.equalsIgnoreCase( "ABORT" ) ) 
//			return StorageType.ABORT;
//		if ( fromString.equalsIgnoreCase( "BOOLEAN" ) ) 
//			return StorageType.BOOLEAN;
//		if ( fromString.equalsIgnoreCase( "DOUBLE" ) ) 
//			return StorageType.DOUBLE;
//		if ( fromString.equalsIgnoreCase( "FLOAT" ) ) 
//			return StorageType.FLOAT;
//		if ( fromString.equalsIgnoreCase( "INTEGER" ) ) 
//			return StorageType.INT;
//		if ( fromString.equalsIgnoreCase( "ITERATOR" ) ) 
//			return StorageType.ITERATOR;
//		if ( fromString.equalsIgnoreCase( "LONG" ) ) 
//			return StorageType.LONG;
//		if ( fromString.equalsIgnoreCase( "SET_NONE" ) ) 
//			return StorageType.NONE;
//		if ( fromString.equalsIgnoreCase( "OBJECT" ) ) 
//			return StorageType.OBJECT;
//		if ( fromString.equalsIgnoreCase( "SHORT" ) ) 
//			return StorageType.SHORT;
//		if ( fromString.equalsIgnoreCase( "SKIP" ) ) 
//			return StorageType.SKIP;
//		if ( fromString.equalsIgnoreCase( "STRING" ) ) 
//			return StorageType.STRING;
//		if ( fromString.equalsIgnoreCase( "VEC2F" ) ) 
//			return StorageType.VEC2F;
//		if ( fromString.equalsIgnoreCase( "VEC3F" ) ) 
//			return StorageType.VEC3F;
//		if ( fromString.equalsIgnoreCase( "VEC4F" ) ) 
//			return StorageType.VEC4F;
//		
//		return null;
//	}
	
	/**
	 * Return the index of this enum in an array. 
	 * "-1" is returened it this enum should not be stored inside an array.
	 * @return index in array or -1 in case that the enum sould not be stored inside an array
	 */
	public int getIndexInArray() {
		return iIndexInArray;
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
	
//	/**
//	 * Convertion of String to SetType excluding abstract types.
//	 * 
//	 * Note: ignored types are:
//	 * ABORT,
//	 * ITERATOR,
//	 * SKIP
//	 * 
//	 * @param fromString String to parse
//	 * @return SetType parsed from String, or SET_NONE if type is abstract or unknown
//	 */
//	public final static StorageType getType( final String fromString ) {
//		
//		if ( fromString.equalsIgnoreCase( "BOOLEAN" ) ) 
//			return StorageType.BOOLEAN;
//		if ( fromString.equalsIgnoreCase( "DOUBLE" ) ) 
//			return StorageType.DOUBLE;
//		if ( fromString.equalsIgnoreCase( "FLOAT" ) ) 
//			return StorageType.FLOAT;
//		if ( fromString.equalsIgnoreCase( "INTEGER" ) ) 
//			return StorageType.INT;
//		if ( fromString.equalsIgnoreCase( "LONG" ) ) 
//			return StorageType.LONG;
//		if ( fromString.equalsIgnoreCase( "OBJECT" ) ) 
//			return StorageType.OBJECT;
//		if ( fromString.equalsIgnoreCase( "SHORT" ) ) 
//			return StorageType.SHORT;
//		if ( fromString.equalsIgnoreCase( "STRING" ) ) 
//			return StorageType.STRING;
//		if ( fromString.equalsIgnoreCase( "VEC2F" ) ) 
//			return StorageType.VEC2F;
//		if ( fromString.equalsIgnoreCase( "VEC3F" ) ) 
//			return StorageType.VEC3F;
//		if ( fromString.equalsIgnoreCase( "VEC4F" ) ) 
//			return StorageType.VEC4F;
//		
//		return StorageType.NONE;
//	}
}
