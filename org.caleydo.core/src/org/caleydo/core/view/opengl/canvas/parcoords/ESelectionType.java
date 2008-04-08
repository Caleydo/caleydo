package org.caleydo.core.view.opengl.canvas.parcoords;

/**
 * Enum for different selection Types.
 * 
 * @author Alexander Lex
 *
 */

public enum ESelectionType
{
	/**
	 * The selection type corresponding to an external selection 
	 * using SetSelections
	 */
	EXTERNAL_SELECTION,
	
	/**
	 * All elements in the storage (initially)
	 */
	COMPLETE_SELECTION,
	
	/**
	 * All storages (initially)
	 */
	STORAGE_SELECTION

}
