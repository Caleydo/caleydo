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
 * Defines different types of sets.
 * 
 * @author Michael Kalkusch
 *
 */
public enum SetType 
implements ICollectionType {

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
	 * Default Constructor
	 */
	private SetType() {
		
	}
	
	/**
	 * 
	 * @see prometheus.data.collection.ICollectionType#isDataType()
	 */
	public boolean isDataType() {
		if ( this==SetType.SET_NONE) {
			return false;
		}
		return true;
	}
}
