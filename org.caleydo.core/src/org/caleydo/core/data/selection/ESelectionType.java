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
	 * Type that is used only to signal initial addition (should only be in
	 * {@link SelectionDelta}), do not use this for element associations
	 */
	ADD,
	/**
	 * Type that is used to signal removal of an element. Elements moved to
	 * REMOVE are not recoverable.
	 */
	REMOVE;
}
