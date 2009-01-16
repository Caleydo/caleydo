package org.caleydo.core.data.selection;

/**
 * Enum that lists the possible selection types
 * 
 * @author Alexander Lex
 * 
 */
public enum ESelectionType
{
	NORMAL,
	SELECTION,
	MOUSE_OVER,
	DESELECTED,

	/**
	 * Pathway neighborhoods TODO: if needed more use the hash map in the
	 * selection manager
	 */
	NEIGHBORHOOD_1,
	NEIGHBORHOOD_2,
	NEIGHBORHOOD_3;
}
