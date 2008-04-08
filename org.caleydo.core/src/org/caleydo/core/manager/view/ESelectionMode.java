package org.caleydo.core.manager.view;

/**
 * 
 * Enum to specify the picking mode
 * 
 * @author Alexander Lex
 *
 */

public enum ESelectionMode
{

	// Flag for adding the pick to the current selection
	AddPick,
	// Flag for replacing the pick with all stored picks per view and type
	ReplacePick,
	// Flag for removing the picked object from the pick list
	RemovePick
}
