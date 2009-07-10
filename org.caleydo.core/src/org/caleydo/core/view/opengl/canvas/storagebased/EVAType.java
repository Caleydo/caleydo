package org.caleydo.core.view.opengl.canvas.storagebased;

/**
 * Enum for VA selection.
 * 
 * @author Alexander Lex
 */

public enum EVAType {
	/**
	 * Used for contextual views, for example in the bucket, where only a subset of the data based on for
	 * example the pathways are used
	 */
	CONTENT_CONTEXT,

	/**
	 * All elements in the storage (initially)
	 */
	CONTENT,

	/**
	 * All elements in the storage (clustered)
	 */
	// COMPLETE_CLUSTERED_SELECTION,

	/**
	 * All storages (initially)
	 */
	STORAGE,

	/**
	 * All storages (clustered)
	 */
	// STORAGE_CLUSTERED_SELECTION

	/**
	 * Content bookmark VA used for the bookmark views
	 */
	CONTENT_BOOKMARKS

}
