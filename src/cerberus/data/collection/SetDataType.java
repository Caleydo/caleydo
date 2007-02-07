package cerberus.data.collection;


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
}
