package org.caleydo.core.view.opengl.canvas.storagebased;

/**
 * The level of data filtering. Determines whether all information contained in a storage (meaning put in the
 * virtual array), or only information with some contextual information, such as mapping or occurence in other
 * data structures should be loaded.
 * 
 * @author Alexander Lex
 */
public enum EDataFilterLevel {
	/**
	 * All data in the storage is used
	 */
	COMPLETE,

	/**
	 * Only data that has a mapping is used
	 */
	ONLY_MAPPING,

	/**
	 * Only data that has a mapping and occurs in another view is used
	 */
	ONLY_CONTEXT
}
