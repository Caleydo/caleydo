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
import cerberus.data.collection.SetDataType;

/**
 * Defines different types of sets.
 * 
 * @author Michael Kalkusch
 *
 */
public enum SetType 
implements ICollectionType {
	
	/** variable n-dimensional set, were n may change. */
	SET_RAW_DATA(SetDataType.SET_LINEAR),
	
	SET_PATHWAY(SetDataType.SET_DATATYPE_NONE),
	
	SET_SELECTION(SetDataType.SET_DATATYPE_NONE),
	
	/** not specified  */
	SET_NONE(SetDataType.SET_DATATYPE_NONE);
	
	
	private final boolean bRawDataType;
	
	private SetDataType setDataType;
	
	/**
	 * Default Constructor
	 */
	private SetType(final SetDataType defineDataType) {
				
		this.setDataType = defineDataType;
		
		if ( defineDataType == SetDataType.SET_DATATYPE_NONE )
		{
			this.bRawDataType = false;
		}
		else
		{
			this.bRawDataType = true;
		}
	}
	
	/**
	 * TRUE if it is a RAW_DATA type, FALSE else
	 */
	public boolean isDataType() {
		return bRawDataType;
	}
	
	public void setDataType( final SetDataType setType) {
		assert setType != SetDataType.SET_DATATYPE_NONE : "Can not set no data type";
		
		setDataType = setType;
	}
	
	public SetDataType getDataType() {
		return setDataType;
	}
}
