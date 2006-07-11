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
 * Defines different types of sets.
 * 
 * @author Michael Kalkusch
 *
 */
public enum SetType 
implements CollectionType {

	/** 1-dimensional set */
	SET_LINEAR,
	/** 2-dimensional set */
	SET_PLANAR,
	/** 3-dimensional set */
	SET_CUBIC,
	
	/** n-dimensional set */
	SET_MULTI_DIM,
	
	/** variable n-dimensional set, were n may change. */
	SET_MULTI_DIM_VARIABLE,
	
	/** not specified  */
	SET_NONE;
	
	

	/**
	 * Convertion of String to SetType.
	 * 
	 * @param fromString String to parse
	 * @return SetType parsed from String, or null if type is unknown.
	 */
	public final static SetType getType( final String fromString ) {
		
		if ( fromString.equalsIgnoreCase( "SET_CUBIC" ) ) 
			return SetType.SET_CUBIC;
		if ( fromString.equalsIgnoreCase( "SET_LINEAR" ) ) 
			return SetType.SET_LINEAR;
		if ( fromString.equalsIgnoreCase( "SET_MULTI_DIM" ) ) 
			return SetType.SET_MULTI_DIM;
		if ( fromString.equalsIgnoreCase( "SET_NONE" ) ) 
			return SetType.SET_NONE;
		if ( fromString.equalsIgnoreCase( "SET_PLANAR" ) ) 
			return SetType.SET_PLANAR;
		if ( fromString.equalsIgnoreCase( "SET_MULTI_DIM_VARIABLE" ) ) 
			return SetType.SET_MULTI_DIM_VARIABLE;
		
		return null;
	}
	
	/**
	 * 
	 * @see prometheus.data.collection.CollectionType#isDataType()
	 */
	public boolean isDataType() {
		if ( this==SetType.SET_NONE) {
			return false;
		}
		return true;
	}
}
