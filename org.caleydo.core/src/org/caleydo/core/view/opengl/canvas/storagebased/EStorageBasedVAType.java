package org.caleydo.core.view.opengl.canvas.storagebased;

/**
 * Enum for VA selection.
 * 
 * @author Alexander Lex
 */

public enum EStorageBasedVAType {
	/**
	 * The selection type corresponding to an external selection using SetSelections
	 */
	EXTERNAL_SELECTION,

	/**
	 * All elements in the storage (initially)
	 */
	COMPLETE_SELECTION,

	/**
	 * All elements in the storage (clustered)
	 */
	COMPLETE_CLUSTERED_SELECTION,

	/**
	 * All storages (initially)
	 */
	STORAGE_SELECTION

}
