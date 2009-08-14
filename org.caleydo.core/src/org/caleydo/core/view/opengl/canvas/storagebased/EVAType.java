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
	 * All elements in the storage, subject to filters
	 */
	CONTENT,

	/**
	 * Type that may not be used in event communication, only for private sub views
	 */
	CONTENT_EMBEDDED_HM,

	/**
	 * All storages (initially)
	 */
	STORAGE,

}
