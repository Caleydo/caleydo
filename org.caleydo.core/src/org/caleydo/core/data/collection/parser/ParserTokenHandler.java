package org.caleydo.core.data.collection.parser;

import org.caleydo.core.data.collection.StorageType;

//import prometheus.data.MultiDataTypeEnum;


/**
 * Define the type's of data stored in MultiData storage.
 * Also used to address MultiData storages, becaus the index is stored.
 * 
 * @author Michael Kalkusch
 */
public final class ParserTokenHandler {
	/**
	 * Define the type of data stored in MultiData storage
	 */
	
	// =====   private variables  =====

	
	/**
	 * Index to address one array
	 */
	private final int iIndex;
	
	private StorageType enumStorageType;
	
	/**
	 * Defines the current data typ.
	 */
	//private ParserTokenType enumType;
	//TODO: remove the next line if not needed any more...
	// private final short iCurrentType;
	
	/**
	 * Defines, if this should be a ArrayList or an Vector.
	 */
	private boolean bArrayListNotVector;
	
	
	//private final ParserTokenType enumDataType;
	
	
//		 =====   public methods  =====
	
	
	/**
	 * Constructor
	 */
	public ParserTokenHandler() {
		this.bArrayListNotVector = true;
		this.iIndex = 0;
		this.enumStorageType = StorageType.SKIP;
	}
	
	/**
	 * Constructor
	 */
	public ParserTokenHandler( final StorageType setType) {
		this.bArrayListNotVector = true;
		this.iIndex = 0;
		this.enumStorageType = setType;
	}
	
	/*
	 * Constructor
	 */
	public ParserTokenHandler( final StorageType setType,
			final int iSetIndex) {
		this.bArrayListNotVector = true;
		this.iIndex = iSetIndex;
		this.enumStorageType = setType;
	}

	
	
	/**
	 * Constructor
	 */
	/*
	public void setEnumType(ParserTokenType setType,
			int setIndex,
			boolean bSetArrayListNotVector ) {
		bArrayListNotVector = bSetArrayListNotVector;
		iIndex = setIndex;
		this.iCurrentType = setType;
	}
	*/
	
	
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
	 * ISet the current index.
	 */
	/*
	public void setIndex( int iSetIndex ) {
		if ( iSetIndex < 0) {
			System.err.println("ERROR: ParserTokenType::setIndex("+ 
					iSetIndex + ") is below lower bound of 0.");
		}
		this.iIndex = iSetIndex;
	}
	*/
	
	/**
	 * ISet the current index.
	 */
	/*
	public void setTypeAndIndex( ParserTokenType eSetType,
			int iSetIndex ) {
		this.iIndex = iSetIndex;
	}
	*/
	
	
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
	 * ISet the type for this MultiData object.
	 */
	/*
	public void setType( ParserTokenType eSetType ) {
		try {
			this.enumType = eSetType.getType();
		} catch (Exception e) {
			System.err.println("ParserTokenHandler::setType() failed with "
					+ e.toString() );
		}
	}
	*/
	
	
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
	
	
//		 =====   private methods  =====
	
}
