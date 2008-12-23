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
	 * Add an element at a specified index
	 */
	ADD,
	/**
	 * Remove an element at a specified index
	 */
	REMOVE
}
