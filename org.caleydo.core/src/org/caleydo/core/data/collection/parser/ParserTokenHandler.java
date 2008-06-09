package org.caleydo.core.data.collection.parser;

import org.caleydo.core.data.collection.StorageType;

/**
 * Define the type's of data stored in MultiData storage.
 * Also used to address MultiData storages, because the index is stored.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public final class ParserTokenHandler {
	
	/**
	 * Index to address one array
	 */
	private final int iIndex;
	
	private StorageType enumStorageType;
	
	/**
	 * Defines, if this should be a ArrayList or an Vector.
	 */
	private boolean bArrayListNotVector;
	
	/**
	 * Constructor.
	 */
	public ParserTokenHandler() {
		this.bArrayListNotVector = true;
		this.iIndex = 0;
		this.enumStorageType = StorageType.SKIP;
	}
	
	/**
	 * Constructor.
	 */
	public ParserTokenHandler( final StorageType setType) {
		this.bArrayListNotVector = true;
		this.iIndex = 0;
		this.enumStorageType = setType;
	}
	
	/**
	 * Constructor.
	 */
	public ParserTokenHandler( final StorageType setType,
			final int iSetIndex) {
		this.bArrayListNotVector = true;
		this.iIndex = iSetIndex;
		this.enumStorageType = setType;
	}
	
	/**
	 * Return the index.
	 */
	public int getIndex() {
		return this.iIndex;
	}
	
	
	/*
	 * Test if ParserTokenHandler is set.
	 * 
	 * Returns TRUE if eCurrentType equals eMultiDataType.mdnone
	 */
	public boolean isEmpty() {
		
		return enumStorageType.isDataType();
		
	}
	
	/**
	 * Return the current list-type.
	 * 
	 * ArrayList == TRUE. 
	 * Vector == FALSE.
	 */
	public boolean isArrayListNotVector() {
		return this.bArrayListNotVector;
	}
	
	
	/**
	 * ISet the current type to ArrayList == TRUE or Vector == FALSE.
	 * 
	 * \sa isArrayListNotVector()
	 */
	public void setArrayListNotVector( boolean bSetArrayListNotVector ) {
		this.bArrayListNotVector = bSetArrayListNotVector;
	}
	
	/**
	 * Get the type for this MultiData object.
	 */
	public StorageType getType( ) {
		return this.enumStorageType;
	}
	
	
	/*
	 * Test if other type is equal to this type.
	 */
	public boolean isEqualType( final StorageType testType ) {
		if ( testType == this.enumStorageType ) {
			return true;
		}
		return false;
	}
	
	/*
	 * Test if other type is equal to this type.
	 */
	public boolean isEqualType( final ParserTokenHandler testType ) {
		return testType.isEqualType( this.enumStorageType );
	}
	
	/**
	 * Get the type for this MultiData object casted to an "int".
	 * 
	 * \sa ParserTokenType getType() 
	 */
	public int getTypeOrdinal( ) {
		return this.enumStorageType.ordinal();
	}

	/**
	 * Debug info on this class
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String(
				"(" + this.enumStorageType.toString() +
				" " + this.iIndex +
				")");
	}
}
