package cerberus.data.collection;

import cerberus.command.CommandQueueSaxType;


public enum SetDataType
{

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
			return SetDataType.SET_LINEAR;
			
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
}
