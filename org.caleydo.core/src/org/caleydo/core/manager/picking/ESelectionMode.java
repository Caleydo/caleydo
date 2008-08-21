package org.caleydo.core.manager.picking;

/**
 * Enum to specify the picking mode
 * 
 * @author Alexander Lex
 */

public enum ESelectionMode
{

	// Flag for adding the pick to the current selection
	ADD_PICK,
	// Flag for replacing the pick with all stored picks per view and type
	REPLACE_PICK,
	// Flag for removing the picked object from the pick list
	REMOVE_PICK
}
