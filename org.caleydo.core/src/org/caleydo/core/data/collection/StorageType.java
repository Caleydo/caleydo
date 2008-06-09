package org.caleydo.core.data.collection;

/**
 * Collection of different data types used in storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum StorageType 
implements ICollectionType <StorageType> {

	/*
	 * Note: When adding a type also check all methods!
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
	
	/** */
	ID_LIST(true,6),
	
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
	 * Note: When adding a type also check all methods!
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
	 * Private constructor.
	 * 
	 * @param bSetValueType true for values containing data, false for parsing parameters.
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
		else if ( iIndex == BOOLEAN.getIndexInArray() ) {
			return BOOLEAN;
		}
		else if ( iIndex == OBJECT.getIndexInArray() ) {
			return OBJECT;
		}
		
		assert false : "unsupported type!";
		
		return NONE;
	}
	

	
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public StorageType getTypeDefault() {

		return StorageType.NONE;
	}
	
}
