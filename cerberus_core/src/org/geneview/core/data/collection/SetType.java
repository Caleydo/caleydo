/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection;

import org.geneview.core.data.collection.ICollectionType;
import org.geneview.core.data.collection.SetDataType;

/**
 * Defines different types of sets.
 * 
 * @author Michael Kalkusch
 *
 */
public enum SetType 
implements ICollectionType <SetType> {
	
	/** variable n-dimensional set, were n may change. */
	SET_RAW_DATA(SetDataType.SET_LINEAR),
	
	SET_SELECTION(SetDataType.SET_LINEAR),
	
	SET_VIEW_DATA(SetDataType.SET_VIEWCAMERA),
	
	/** Pathway related data */
	SET_PATHWAY_DATA(SetDataType.SET_LINEAR),
	
	/** gene expression data */
	SET_GENE_EXPRESSION_DATA(SetDataType.SET_LINEAR),
	
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
	public final boolean isDataType() {
		return bRawDataType;
	}
	
	public final void setDataType( final SetDataType setType) {
		assert setType != SetDataType.SET_DATATYPE_NONE : "Can not set no data type";
		
		setDataType = setType;
	}
	
	public final SetDataType getDataType() {
		return setDataType;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public SetType getTypeDefault() {

		return SetType.SET_RAW_DATA;
	}
}
