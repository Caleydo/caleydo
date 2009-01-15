package org.caleydo.core.data.selection;

/**
 * List of operations allowed in virtual array deltas ({@link VADeltaItem}
 * 
 * @author Alexander Lex
 * 
 */
public enum EVAOperation
{
	/**
	 * Append an element at the end of a virtual array
	 */
	APPEND,
	/**
	 * Append an element at the end of a virtual array, if the element is not
	 * yet contained in the list
	 */
	APPEND_UNIQUE,
	/**
	 * Add an element at a specified index
	 */
	ADD,
	/**
	 * Remove an element at a specified index
	 */
	REMOVE,
	/**
	 * Remove a specific element
	 */
	REMOVE_ELEMENT
}
