package org.caleydo.core.data.datadomain;

/**
 * The level of data filtering. Determines whether all information contained in a dimension (meaning put in
 * the virtual array), or only information with some contextual information, such as mapping or occurence in
 * other data structures should be loaded.
 * 
 * @author Alexander Lex
 */
public enum EDataFilterLevel {
	/**
	 * All data in the dimension is used
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
