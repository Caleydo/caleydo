package org.geneview.core.data.collection;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Specify one type of ISet.
 * 
 * @see cerberus.data.collection.ISet#getSetType()
 * @see cerberus.data.collection.ISet#getSetDataType()
 * 
 * @author Michael Kalkusch
 *
 */
public enum SetDataType
implements IGeneViewDefaultType <SetDataType> {

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
	
	/** 
	 * Set for handlein IViewCamera 
	 * 
	 * @see cerberus.data.view.camera.IViewCamera
	 * @see cerberus.data.collection.set.viewdata.ISetViewData
	 */
	SET_VIEWCAMERA,
	
	/** not specified  */
	SET_DATATYPE_NONE;
	
	/**
	 * Default Constructor
	 */
	private SetDataType() {
		
	}
	
	/**
	 * Convert a CommandQueueSaxType to SetDataType if a mappign exists.
	 * 
	 * @param type input type
	 * @return SetDataType if mapping with type exists
	 */
	public static final SetDataType convert( final CommandQueueSaxType type) {
		switch ( type ) {
		case SET_DATA_LINEAR:
			return SetDataType.SET_LINEAR;
			
		case SET_DATA_PLANAR:
			return SetDataType.SET_PLANAR;
			
		case SET_DATA_MULTIDIM:
			return SetDataType.SET_MULTI_DIM;
			
		case SET_DATA_MULTIDIM_VARIABLE:
			return SetDataType.SET_MULTI_DIM_VARIABLE;
			
		case SET_DATA_CUBIC:
			return SetDataType.SET_CUBIC;
			
		default:
			assert false : "unsupported type " + type.name();
			return null;
		}
	}
	

	/**
	 * Get the default type of SetDataType.
	 * 
	 * @return SetDataType.SET_LINEAR
	 * 
	 * @see cerberus.util.IGeneViewDefaultType#getDefault()
	 */
	public final SetDataType getTypeDefault() {
		return SetDataType.SET_LINEAR;
	}
	
	public static final SetDataType getDefault() {
		return SetDataType.SET_LINEAR;
	}
}
